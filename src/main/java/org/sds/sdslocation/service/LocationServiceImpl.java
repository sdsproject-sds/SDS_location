package org.sds.sdslocation.service;

import lombok.extern.slf4j.Slf4j;
import org.sds.sdslocation.model.PolygonGeometry;
import org.sds.sdslocation.model.PolygonRequest;
import org.sds.sdslocation.model.RegionResponse;
import org.sds.sdslocation.repository.DataRepository;
import org.sds.sdslocation.repository.TblCountryDivisions;
import org.sds.sdslocation.repository.TblCountrySubDivisions;
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

    // private final CountrySubDivisionRepo countrySubDivisionRepo;
    private final DataRepository dataRepository;

    public LocationServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }


    public RegionResponse getSubDivision(Double lon, Double lat) {
        List<TblCountrySubDivisions> regions = dataRepository.getSubDivision(lon, lat);
        if (!regions.isEmpty()) {
            log.info("regions {}", regions.size());
            log.info("name {}", regions.get(0).getCountrySubDivisionName());
            return RegionResponse.builder()
                    .locationArea(regions.get(0).getCountrySubDivisionName())
                    .supported(true)
                    .build();
        }
        return RegionResponse.builder()
                .supported(false)
                .available(false)
                .build();
    }

    public RegionResponse getDivision(Double lon, Double lat) {
        List<TblCountryDivisions> regions = dataRepository.getDivision(lon, lat);
        if (!regions.isEmpty()) {
            log.info("regions {}", regions.size());
            log.info("name {}", regions.get(0).getDivision());
            return RegionResponse.builder()
                    .locationArea(regions.get(0).getDivision())
                    .available(true)
                    .supported(regions.get(0).isSupported())
                    .build();
        }
        return RegionResponse.builder()
                .supported(false)
                .available(false)
                .build();
    }

    public String createSubDivision(PolygonRequest request) throws Exception {
        PolygonGeometry geom = request.getPolygonGeometry();

        List<List<List<Double>>> cleaned = geom.getCoordinates()
                .stream()
                .map(ring -> ring.stream()
                        .map(cord -> List.of(cord.get(0), cord.get(1)))
                        .toList()
                ).toList();

        Map<String, Object> geoJson = Map.of(
                "type", "Polygon",
                "coordinates", cleaned
        );
        String geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        log.info("geo String {}", geoJsonString);
        dataRepository.saveSubDivision(geoJsonString, request.getDivisionCode(), request.getProperties().get("Name"));
        return geoJsonString;
    }

    public String createDivision(PolygonRequest request) {
        PolygonGeometry geom = request.getPolygonGeometry();

        List<List<List<Double>>> cleaned = geom.getCoordinates()
                .stream()
                .map(ring -> ring.stream()
                        .map(cord -> List.of(cord.get(0), cord.get(1)))
                        .toList()
                ).toList();

        Map<String, Object> geoJson = Map.of(
                "type", "Polygon",
                "coordinates", cleaned
        );
        String geoJsonString = new ObjectMapper().writeValueAsString(geoJson);
        log.info("geo String {}", geoJsonString);
        dataRepository.saveDivision(request.getCountryIsoCode2(), request.getDivisionCode(), request.getDivision(), geoJsonString);
        return geoJsonString;
    }

}
