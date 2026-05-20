package org.sds.sdslocation.service;

import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.sds.sdslocation.model.*;
import org.sds.sdslocation.exeption.SdsLocationNotFoundException;
import org.sds.sdslocation.model.*;
import org.sds.sdslocation.model.CountryDivisionLookupResponse;
import org.sds.sdslocation.model.request.CountryDivisionRequest;
import org.sds.sdslocation.model.request.CountryDivisionUpdateRequest;
import org.sds.sdslocation.model.request.SubDivisionRequest;
import org.sds.sdslocation.model.request.SubDivisionUpdateRequest;
import org.sds.sdslocation.repository.DataRepository;
import org.sds.sdslocation.repository.TblCountryDivisions;
import org.sds.sdslocation.repository.TblCountrySubDivisions;
import org.sds.sdslocation.repository.TblDeviceLocation;
import org.sds.sdslocation.utility.ULIDRef;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:20 PM
 */
@Service
@Slf4j
public class LocationServiceImpl {

    public static final String POLYGON = "Polygon";
    public static final String COORDINATES = "coordinates";
    private final DataRepository dataRepository;

    public LocationServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }


    public DeviceLocation saveUserLocation(DeviceLocation location) {
        return dataRepository.saveUserLocation(location);
    }

    public List<TblDeviceLocation> getActiveUsers(Coordinates2D coordinates2D) {
        return dataRepository.findNearbyUsers(coordinates2D.getLat(), coordinates2D.getLon());
    }

    public CountryDivision updateDivision(String divisionCode, CountryDivisionUpdateRequest request, String updatedBy) {
        // Check if division exists
        if (!dataRepository.divisionExists(divisionCode)) {
            throw new SdsLocationException("Division with code " + divisionCode + " not found");
        }

        try {
            // Preprocess values - convert empty strings to null
            String countryIso2 = preprocessString(request.getCountryCode() != null ? request.getCountryCode().getAlpha2() : null);
            String divisionName = preprocessString(request.getDivision());
            String geoString = null;
            Boolean supported = request.getSupported();

            // Process geometry if provided
            if (request.getPolygonGeometry() != null &&
                    request.getPolygonGeometry().getCoordinates() != null &&
                    !request.getPolygonGeometry().getCoordinates().isEmpty()) {

                List<List<List<Double>>> cleaned = request.getPolygonGeometry().getCoordinates()
                        .stream()
                        .map(ring -> ring.stream()
                                .map(cord -> List.of(cord.get(0), cord.get(1)))
                                .toList()
                        ).toList();

                Map<String, Object> geoJson = Map.of(
                        "type", POLYGON,
                        COORDINATES, cleaned
                );

                geoString = new ObjectMapper().writeValueAsString(geoJson);
                log.info("Updated geo String {}", geoString);
            }

            // Check if any update is provided
            if (countryIso2 == null && divisionName == null && geoString == null && supported == null) {
                throw new SdsLocationException("No valid update data provided. All fields are null or empty.");
            }

            // Perform single update
            boolean updated = dataRepository.updateDivision(
                    divisionCode,
                    countryIso2,
                    divisionName,
                    geoString,
                    supported,
                    updatedBy != null ? updatedBy : "system"
            );

            if (!updated) {
                throw new SdsLocationException("Updated failed. Division may not exist or be inactive.");
            }

            return dataRepository.getDivisionById(divisionCode)
                    .toCountryDivision();

        } catch (Exception e) {
            log.error("Error updating division with code: {}", divisionCode, e);
            throw new SdsLocationException("Failed to update division: " + e.getMessage());
        }
    }

    public SubDivision updateSubDivision(Long id, SubDivisionUpdateRequest request, String updatedBy) {
        if (!dataRepository.subDivisionExists(id)) {
            throw new SdsLocationException("Sub-division with ID " + id + " not found");
        }

        try {
            String divisionCode = preprocessString(request.getDivisionId());
            String subDivisionName = preprocessString(request.getSubDivisionName());
            String geoString = null;

            // Validate division exists if provided
            if (divisionCode != null && !dataRepository.isCountryDivisionAvailable(divisionCode)) {
                throw new SdsLocationNotFoundException("Division with code " + divisionCode + " not available");
            }

            if (request.getPolygonGeometry() != null &&
                    request.getPolygonGeometry().getCoordinates() != null &&
                    !request.getPolygonGeometry().getCoordinates().isEmpty()) {

                List<List<List<Double>>> cleaned = request.getPolygonGeometry().getCoordinates()
                        .stream()
                        .map(ring -> ring.stream()
                                .map(cord -> List.of(cord.get(0), cord.get(1)))
                                .toList()
                        ).toList();

                Map<String, Object> geoJson = Map.of(
                        "type", POLYGON,
                        COORDINATES, cleaned
                );

                geoString = new ObjectMapper().writeValueAsString(geoJson);
                log.info("Updated subdivision geo String {}", geoString);
            }

            if (divisionCode == null && subDivisionName == null && geoString == null) {
                throw new SdsLocationException("No valid update data provided. All fields are null or empty.");
            }

            boolean updated = dataRepository.updateSubDivision(
                    id,
                    divisionCode,
                    subDivisionName,
                    geoString,
                    updatedBy != null ? updatedBy : "system"
            );

            if (updated) {
                return dataRepository.getSubDivisionById(id);
            } else {
                throw new SdsLocationException("No rows were updated. Sub-division may not exist or be inactive.");
            }

        } catch (Exception e) {
            log.error("Error updating sub-division with ID: {}", id, e);
            throw new SdsLocationException("Failed to update sub-division: " + e.getMessage());
        }
    }

    private String preprocessString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }


    public SubDivisionLookupResponse getSubDivision(Double lon, Double lat) {
        List<TblCountrySubDivisions> regions = dataRepository.getSubDivision(lon, lat);
        if (!regions.isEmpty()) {
            return SubDivisionLookupResponse.builder()
                    .subDivision(regions.get(0).toSubDivision())
                    .available(true)
                    .supported(true)
                    .build();
        }
        return SubDivisionLookupResponse.builder()
                .supported(false)
                .available(false)
                .build();
    }

    public CountryDivisionLookupResponse getDivision(Double lon, Double lat) {
        List<TblCountryDivisions> regions = dataRepository.getDivision(lon, lat);
        if (!regions.isEmpty()) {
            return CountryDivisionLookupResponse.builder()
                    .countryDivision(regions.get(0).toCountryDivision())
                    .available(true)
                    .supported(regions.get(0).isSupported())
                    .build();
        }
        return CountryDivisionLookupResponse.builder()
                .supported(false)
                .available(false)
                .build();
    }

    public CountryDivision getDivisionById(String id) {
        try {
            TblCountryDivisions division = dataRepository.getDivisionById(id);
            return division.toCountryDivision();
        } catch (Exception e) {
            throw new SdsLocationException("Failed to retrieve division by ID: "+ id);
        }
    }

    public SubDivision getSubDivisionById(Long id) {
        var subDivision = dataRepository.getSubDivisionById(id);

        if (subDivision == null) {
            throw new SdsLocationNotFoundException("Sub-division with ID " + id + " not found");
        }
        return subDivision;
    }

    public boolean deleteSubDivision(Long id, String deletedBy) {
        // Check if subdivision exists
        if (!dataRepository.subDivisionExists(id)) {
            throw new SdsLocationNotFoundException("Sub-division with ID " + id + " not found or already deleted");
        }

        try {
            return dataRepository.deleteSubDivision(id, deletedBy);
        } catch (Exception e) {
            log.error("Error deleting sub-division with ID: {}", id, e);
            throw new SdsLocationException("Failed to delete sub-division: " + e.getMessage());
        }
    }

    public boolean hardDeleteSubDivision(Long id) {
        // Check if subdivision exists first
        if (!dataRepository.subDivisionExists(id)) {
            throw new SdsLocationNotFoundException("Sub-division with ID " + id + " not found");
        }

        try {
            return dataRepository.hardDeleteSubDivision(id);
        } catch (Exception e) {
            log.error("Error hard deleting sub-division with ID: {}", id, e);
            throw new SdsLocationException("Failed to hard delete sub-division: " + e.getMessage());
        }
    }

    public void deleteDivision(String divisionCode, String deletedBy) {
        try {
            // Check if division exists
            if (!dataRepository.divisionExists(divisionCode)) {
                throw new SdsLocationException("Division with code " + divisionCode + " not found or already deleted");
            }

            // Check if there are active subdivisions
            long activeSubDivisions = dataRepository.countActiveSubDivisions(divisionCode);
            if (activeSubDivisions > 0) {
                throw new SdsLocationException("Cannot delete division " + divisionCode +
                        ". It has " + activeSubDivisions + " active sub-divisions. Please delete them first.");
            }

            if (!dataRepository.deleteDivision(divisionCode, deletedBy)) {
                throw new SdsLocationException("Failed to delete division with code "+ divisionCode);
            }
        } catch (Exception e) {
            log.error("Error deleting division with code: {}", divisionCode, e);
            throw new SdsLocationException("Failed to delete division: " + e.getMessage());
        }
    }

    public void hardDeleteDivision(String divisionCode) {
        try {
            // Check if division exists
            if (!dataRepository.divisionExists(divisionCode)) {
                throw new SdsLocationNotFoundException("Division with code " + divisionCode + " not found");
            }

            // Check if there are any subdivisions (active or inactive)
            long totalSubDivisions = dataRepository.countActiveSubDivisions(divisionCode);
            if (totalSubDivisions > 0) {
                throw new SdsLocationException("Cannot permanently delete division " + divisionCode +
                        ". It has " + totalSubDivisions + " sub-divisions. Delete them first.");
            }

            if (!dataRepository.hardDeleteDivision(divisionCode)) {
                throw new SdsLocationException("Division with code " + divisionCode + " not found");
            }
        } catch (Exception e) {
            log.error("Error hard deleting division with code: {}", divisionCode, e);
            throw new SdsLocationException("Failed to hard delete division: " + e.getMessage());
        }
    }


    public SubDivision createSubDivision(SubDivisionRequest subDivision) {
        PolygonGeometry geom = subDivision.getPolygonGeometry();

        if (!dataRepository.isCountryDivisionAvailable(subDivision.getDivisionId())) {
            throw new SdsLocationNotFoundException("Division not available");
        }

        List<List<List<Double>>> cleaned = geom.getCoordinates()
                .stream()
                .map(ring -> ring.stream()
                        .map(cord -> List.of(cord.get(0), cord.get(1)))
                        .toList()
                ).toList();

        Map<String, Object> geoJson = Map.of(
                "type", POLYGON,
                COORDINATES, cleaned
        );

        String geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        return dataRepository.saveSubDivision(geoJsonString, subDivision.getDivisionId(), subDivision.getSubDivisionName());
    }

    public CountryDivision createDivision(CountryDivisionRequest request) {
        PolygonGeometry geom = request.getPolygonGeometry();

        List<List<List<Double>>> cleaned = geom.getCoordinates()
                .stream()
                .map(ring -> ring.stream()
                        .map(cord -> List.of(cord.get(0), cord.get(1)))
                        .toList()
                ).toList();

        Map<String, Object> geoJson = Map.of(
                "type", POLYGON,
                COORDINATES, cleaned
        );
        String geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        log.info("geo String {}", geoJsonString);
        return dataRepository.saveDivision(request.getCountryCode().getAlpha2(), ULIDRef.get(), request.getDivision(), geoJsonString);
    }

}
