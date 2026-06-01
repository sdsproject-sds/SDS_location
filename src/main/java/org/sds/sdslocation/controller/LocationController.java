package org.sds.sdslocation.controller;

import com.sds.integration.commons.model.AbstractBaseApiResponse;
import org.sds.sdslocation.model.*;
import org.sds.sdslocation.model.request.CountryDivisionRequest;
import org.sds.sdslocation.model.request.CountryDivisionUpdateRequest;
import org.sds.sdslocation.model.request.SubDivisionRequest;
import org.sds.sdslocation.model.request.SubDivisionUpdateRequest;
import org.sds.sdslocation.service.LocationService;
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

    public static final String SUCCESS = "Success";
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
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

        res = locationService.createDivision(polygonRequest);
        return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                "200",
                "Successfully created",
                res));
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
        CountryDivision updatedDivision = locationService.updateDivision(divisionCode, updateRequest, updatedBy);
        return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                "200",
                "Division updated successfully",
                updatedDivision
        ));
    }


    /**
     * Division support search
     * {"lon":36.89326,"lat":-1.21326}
     *
     * @param coordinates {@link Coordinates2D}
     * @return {@link CountryDivisionLookupResponse}
     */
    @GetMapping(value = "/division")
    public ResponseEntity<AbstractBaseApiResponse<CountryDivisionLookupResponse>> getDivision(@ParameterObject Coordinates2D coordinates) {

        CountryDivisionLookupResponse response = locationService.getDivision(coordinates.getLon(), coordinates.getLat());
        return ResponseEntity.ok(new ApiResponse<CountryDivisionLookupResponse>().success(
                "200",
                SUCCESS,
                response
        ));
    }

    @GetMapping(value = "/division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<CountryDivision>> getDivisionById(@PathVariable String id) {

        CountryDivision division = locationService.getDivisionById(id);

        return ResponseEntity.ok(new ApiResponse<CountryDivision>().success(
                "200",
                SUCCESS,
                division
        ));
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

        locationService.deleteDivision(divisionCode, deletedBy);
        return ResponseEntity.ok(new ApiResponse<String>().success(
                "200",
                "Division deleted successfully",
                "Division with code " + divisionCode + " has been deleted"
        ));
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

        locationService.hardDeleteDivision(divisionCode);

        return ResponseEntity.ok(new ApiResponse<String>().success(
                "200",
                "Division permanently deleted",
                "Division with code " + divisionCode + " has been permanently removed"
        ));

    }

    /**
     * Adding subDivisions
     * { "type": "Feature", "properties": { "Name": "Roysambu zimmerman Area" }, "geometry": { "type": "Polygon", "coordinates": [ [ [ 36.8876376, -1.2221954, 0.0 ], [ 36.8950619, -1.2145582, 0.0 ],
     * 8 [ 36.9085374, -1.2098815, 0.0 ], [ 36.8989672, -1.2017294, 0.0 ], [ 36.8827023, -1.2072643, 0.0 ], [ 36.8801703, -1.2185485, 0.0 ], [ 36.8876376, -1.2221954, 0.0 ] ] ] } }
     */

    @PostMapping(value = "sub-division")
    public ResponseEntity<AbstractBaseApiResponse<SubDivision>> createSubDivision(@RequestBody SubDivisionRequest polygonRequest) {
        SubDivision res;

        res = locationService.createSubDivision(polygonRequest);

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

    }


    /**
     * Get sub division Name
     * {"lon":36.89326,"lat":-1.21326}
     *
     * @param coordinates {@link Coordinates2D}
     * @return {@link SubDivisionLookupResponse}
     */
    @GetMapping(value = "sub-division")
    public ResponseEntity<AbstractBaseApiResponse<SubDivisionLookupResponse>> getSubDivision(@ParameterObject Coordinates2D coordinates) {

        SubDivisionLookupResponse response = locationService.getSubDivision(coordinates.getLon(), coordinates.getLat());
        return ResponseEntity.ok(new ApiResponse<SubDivisionLookupResponse>().success(
                "200",
                SUCCESS,
                response
        ));

    }

    @GetMapping(value = "/sub-division/{id}")
    public ResponseEntity<AbstractBaseApiResponse<SubDivision>> getSubDivisionById(@PathVariable Long id) {

        SubDivision subDivision = locationService.getSubDivisionById(id);
        return ResponseEntity.ok(new ApiResponse<SubDivision>().success(
                "200",
                SUCCESS,
                subDivision
        ));

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

    }

}
