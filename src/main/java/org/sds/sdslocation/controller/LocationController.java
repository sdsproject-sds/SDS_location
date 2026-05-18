package org.sds.sdslocation.controller;

import com.sds.integration.commons.model.AbstractBaseApiResponse;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.sds.sdslocation.model.*;
import org.sds.sdslocation.model.request.CountryDivisionRequest;
import org.sds.sdslocation.model.request.CountryDivisionUpdateRequest;
import org.sds.sdslocation.model.request.SubDivisionRequest;
import org.sds.sdslocation.model.request.SubDivisionUpdateRequest;
import org.sds.sdslocation.service.LocationServiceImpl;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:18 PM
 */
@RestController
@RequestMapping(value = "/location")
class LocationController {

    public static final String SUB_DIVISION_NOT_FOUND = "Sub-division not found";
    public static final String SUCCESS = "Success";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    private final LocationServiceImpl locationService;

    public LocationController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }



    /**
     * Adding divisions
     * {"division":"NAIROBI","division_code":"NBO","iso2":"KE","type":"Feature","properties":
     * {"name":"Nairobi County","country":"Kenya"},"geometry":{"type":"Polygon","coordinates":[[[36.6640,-1.4440],[36.9150,-1.4440],[36.9150,-1.1500],[36.6640,-1.1500],[36.6640,-1.4440]]]}}
     **/
    @PostMapping(value = "/division")
    public ResponseEntity<AbstractBaseApiResponse<CountryDivision>> createCountryDivision(@RequestBody CountryDivisionRequest polygonRequest) {
        CountryDivision res;
        try {
            res = locationService.createDivision(polygonRequest);
        } catch (Exception e) {
            throw new SdsLocationException(e);
        }
        return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                "200",
                "Successfully created",
                res
        ));
    }

    /**
     * Update division by division code (partial update - only non-null fields)
     * PUT /location/division/{divisionCode}
     *
     * @param divisionCode  the division code to update
     * @param updateRequest the update request with fields to update
     * @param updatedBy     optional parameter to track who updated it
     * @return updated division or error response
     */
    @PutMapping(value = "/division/{divisionCode}")
    public ResponseEntity<AbstractBaseApiResponse<CountryDivision>> updateDivision(
            @PathVariable String divisionCode,
            @RequestBody CountryDivisionUpdateRequest updateRequest,
            @RequestParam(value = "updatedBy", defaultValue = "system") String updatedBy) {
        try {
            CountryDivision updatedDivision = locationService.updateDivision(divisionCode, updateRequest, updatedBy);

            if (updatedDivision != null) {
                return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                        "200",
                        "Division updated successfully",
                        updatedDivision
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<CountryDivision>().error(
                                "400",
                                "Update failed",
                                "Failed to update division with code: " + divisionCode
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<CountryDivision>().error(
                            "400",
                            "Cannot update division",
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<CountryDivision>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while updating the division: " + e.getMessage()
                    ));
        }
    }


    /**
     * Division support search
     * {"lon":36.89326,"lat":-1.21326}
     *
     * @param coordinates {@link Coordinates2D}
     * @return {@link RegionSupportResponse}
     */
    @GetMapping(value = "/division")
    public ResponseEntity<AbstractBaseApiResponse<RegionSupportResponse>> getDivision(@ParameterObject Coordinates2D coordinates) {

        try {
            RegionSupportResponse response = locationService.getDivision(coordinates.getLon(), coordinates.getLat());
            return ResponseEntity.ok(new ApiResponse<RegionSupportResponse>().success(
                    "200",
                    SUCCESS,
                    response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<RegionSupportResponse>().error(
                            "400",
                            "Failed to Fetch",
                            e.getLocalizedMessage()
                    ));
        }
    }

    @GetMapping(value = "/division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<CountryDivision>> getDivisionById(@PathVariable String id) {
        try {
            CountryDivision division = locationService.getDivisionById(id);
            if (division != null) {
                return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                        "200",
                        SUCCESS,
                        division
                ));
            } else {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<CountryDivision>().error(
                                "404",
                                "Division not found",
                                "No division found with ID: " + id
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<CountryDivision>().error(
                            "400",
                            "Failed to fetch division",
                            e.getLocalizedMessage()
                    ));
        }
    }

    /**
     * Delete division by division code (soft delete - marks as DELETED)
     * DELETE /location/division/{divisionCode}
     *
     * @param divisionCode the division code to delete
     * @param deletedBy    optional parameter to track who deleted it
     * @return success/error response
     */
    @DeleteMapping(value = "/division/{divisionCode}")
    public ResponseEntity<AbstractBaseApiResponse<String>> deleteDivision(
            @PathVariable String divisionCode,
            @RequestParam(value = "deletedBy", defaultValue = "system") String deletedBy) {
        try {
            boolean deleted = locationService.deleteDivision(divisionCode, deletedBy);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<String>().success(
                        "200",
                        "Division deleted successfully",
                        "Division with code " + divisionCode + " has been deleted"
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<String>().error(
                                "400",
                                "Failed to delete division",
                                "Unable to delete division with code: " + divisionCode
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<String>().error(
                            "400",
                            "Cannot delete division",
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<String>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while deleting the division: " + e.getMessage()
                    ));
        }
    }

    /**
     * Hard delete division by division code (permanent removal)
     * DELETE /location/division/{divisionCode}/permanent
     * Use with extreme caution - this cannot be undone!
     *
     * @param divisionCode the division code to permanently delete
     * @return success/error response
     */
    @DeleteMapping(value = "/division/{divisionCode}/permanent")
    public ResponseEntity<AbstractBaseApiResponse<String>> hardDeleteDivision(@PathVariable String divisionCode) {
        try {
            boolean deleted = locationService.hardDeleteDivision(divisionCode);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<String>().success(
                        "200",
                        "Division permanently deleted",
                        "Division with code " + divisionCode + " has been permanently removed"
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<String>().error(
                                "400",
                                "Failed to permanently delete division",
                                "Unable to permanently delete division with code: " + divisionCode
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<String>().error(
                            "400",
                            "Cannot delete division",
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<String>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while permanently deleting the division: " + e.getMessage()
                    ));
        }
    }

    /**
     * Adding subDivisions
     * { "type": "Feature", "properties": { "Name": "Roysambu zimmerman Area" }, "geometry": { "type": "Polygon", "coordinates": [ [ [ 36.8876376, -1.2221954, 0.0 ], [ 36.8950619, -1.2145582, 0.0 ],
     * 8 [ 36.9085374, -1.2098815, 0.0 ], [ 36.8989672, -1.2017294, 0.0 ], [ 36.8827023, -1.2072643, 0.0 ], [ 36.8801703, -1.2185485, 0.0 ], [ 36.8876376, -1.2221954, 0.0 ] ] ] } }
     */

    @PostMapping(value = "sub-division")
    public ResponseEntity<AbstractBaseApiResponse<SubDivision>> createSubDivision(@RequestBody SubDivisionRequest polygonRequest) {
        SubDivision res;
        try {
            res = locationService.createSubDivision(polygonRequest);
        } catch (Exception e) {
            throw new SdsLocationException(e);
        }
        return ResponseEntity.ok(new ApiResponse<SubDivision>().success(
                "200",
                SUCCESS,
                res
        ));
    }

    /**
     * Update subdivision by ID (partial update - only non-null fields)
     * PUT /location/subdivision/{id}
     *
     * @param id            the subdivision ID to update
     * @param updateRequest the update request with fields to update
     * @param updatedBy     optional parameter to track who updated it
     * @return updated subdivision or error response
     */
    @PutMapping(value = "/sub-division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<SubDivision>> updateSubDivision(
            @PathVariable Long id,
            @RequestBody SubDivisionUpdateRequest updateRequest,
            @RequestParam(value = "updatedBy", defaultValue = "system") String updatedBy) {
        try {
            SubDivision updatedSubDivision = locationService.updateSubDivision(id, updateRequest, updatedBy);

            if (updatedSubDivision != null) {
                return ResponseEntity.ok(new ApiResponse<SubDivision>().success(
                        "200",
                        "Sub-division updated successfully",
                        updatedSubDivision
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<SubDivision>().error(
                                "400",
                                "Update failed",
                                "Failed to update sub-division with ID: " + id
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<SubDivision>().error(
                            "400",
                            "Cannot update sub-division",
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<SubDivision>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while updating the sub-division: " + e.getMessage()
                    ));
        }
    }


    /**
     * Get sub division Name
     * {"lon":36.89326,"lat":-1.21326}
     *
     * @param coordinates {@link Coordinates2D}
     * @return {@link RegionSupportResponse}
     */
    @GetMapping(value = "sub-division")
    public ResponseEntity<AbstractBaseApiResponse<RegionSupportResponse>> getSubDivision(@ParameterObject Coordinates2D coordinates) {

        try {
            RegionSupportResponse response = locationService.getSubDivision(coordinates.getLon(), coordinates.getLat());
            return ResponseEntity.ok(new ApiResponse<RegionSupportResponse>().success(
                    "200",
                    SUCCESS,
                    response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    new ApiResponse<RegionSupportResponse>().error(
                            "400",
                            "Filed",
                            e.getLocalizedMessage()
                    ));
        }
    }

    @GetMapping(value = "/sub-division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<SubDivision>> getSubDivisionById(@PathVariable Long id) {
        try {
            SubDivision subDivision = locationService.getSubDivisionById(id);
            if (subDivision != null) {
                return ResponseEntity.ok(new ApiResponse<SubDivision>().success(
                        "200",
                        SUCCESS,
                        subDivision
                ));
            } else {
                return ResponseEntity.status(404)
                        .body(new ApiResponse<SubDivision>().error(
                                "404",
                                SUB_DIVISION_NOT_FOUND,
                                "No sub-division found with ID: " + id
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(new ApiResponse<SubDivision>().error(
                            "400",
                            "Failed to fetch sub-division",
                            e.getLocalizedMessage()
                    ));
        }
    }

    /**
     * Delete subdivision by ID (soft delete - marks as DELETED)
     * DELETE /location/subdivision/{id}
     *
     * @param id        the subdivision ID to delete
     * @param deletedBy optional parameter to track who deleted it
     * @return success/error response
     */
    @DeleteMapping(value = "/sub-division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<String>> deleteSubDivision(
            @PathVariable Long id,
            @RequestParam(value = "deletedBy", defaultValue = "system") String deletedBy) {
        try {
            boolean deleted = locationService.deleteSubDivision(id, deletedBy);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<String>().success(
                        "200",
                        "Sub-division deleted successfully",
                        "Sub-division with ID " + id + " has been deleted"
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<String>().error(
                                "400",
                                "Failed to delete sub-division",
                                "Unable to delete sub-division with ID: " + id
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<String>().error(
                            "404",
                            SUB_DIVISION_NOT_FOUND,
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<String>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while deleting the sub-division: " + e.getMessage()
                    ));
        }
    }

    /**
     * Hard delete subdivision by ID (permanent removal)
     * DELETE /location/subdivision/{id}/permanent
     * Use with extreme caution - this cannot be undone!
     *
     * @param id the subdivision ID to permanently delete
     * @return success/error response
     */
    @DeleteMapping(value = "/sub-division/{id}/permanent")
    public ResponseEntity<AbstractBaseApiResponse<String>> hardDeleteSubDivision(@PathVariable Long id) {
        try {
            boolean deleted = locationService.hardDeleteSubDivision(id);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse<String>().success(
                        "200",
                        "Sub-division permanently deleted",
                        "Sub-division with ID " + id + " has been permanently removed"
                ));
            } else {
                return ResponseEntity.status(400)
                        .body(new ApiResponse<String>().error(
                                "400",
                                "Failed to permanently delete sub-division",
                                "Unable to permanently delete sub-division with ID: " + id
                        ));
            }
        } catch (SdsLocationException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<String>().error(
                            "404",
                            SUB_DIVISION_NOT_FOUND,
                            e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<String>().error(
                            "500",
                            INTERNAL_SERVER_ERROR,
                            "An error occurred while permanently deleting the sub-division: " + e.getMessage()
                    ));
        }
    }

}
