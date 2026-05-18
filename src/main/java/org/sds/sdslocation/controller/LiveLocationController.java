package org.sds.sdslocation.controller;

import com.sds.integration.commons.model.AbstractBaseApiResponse;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.sds.sdslocation.model.Coordinates2D;
import org.sds.sdslocation.model.UserLocation;
import org.sds.sdslocation.repository.TblDeviceLocation;
import org.sds.sdslocation.service.LocationServiceImpl;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author samwel.wafula
 * Created on 5/18/2026
 * Time 1:10 PM
 */
@RestController
@RequestMapping("/live_location")
public class LiveLocationController {
    private final LocationServiceImpl locationService;

    public LiveLocationController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    @PostMapping()
    public ResponseEntity<AbstractBaseApiResponse<UserLocation>> createOrUpdateUserLocation(@RequestBody UserLocation userLocation) {
        UserLocation res;
        try {
            res = locationService.saveUserLocation(userLocation);
        } catch (Exception e) {
            throw new SdsLocationException(e);
        }
        return ResponseEntity.ok(new ApiResponse<UserLocation>().success(
                "200",
                "Successfully updated",
                res
        ));
    }

    @GetMapping()
    public ResponseEntity<AbstractBaseApiResponse<List<TblDeviceLocation>>> getActiveUsers(@ParameterObject Coordinates2D coordinates2D) {
        List<TblDeviceLocation> res;

        try {
            res = locationService.getActiveUsers(coordinates2D);
        } catch (Exception e) {
            throw new SdsLocationException(e);
        }
        return ResponseEntity.ok(new ApiResponse<List<TblDeviceLocation>>().success(
                "200",
                "successfully retrieved",
                res
        ));
    }
}
