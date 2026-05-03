package org.sds.sdslocation.controller;

import org.sds.sdslocation.model.PolygonRequest;
import org.sds.sdslocation.model.RegionResponse;
import org.sds.sdslocation.service.LocationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:18 PM
 */
@RestController
@RequestMapping(value = "/location")
public class LocationController {

    private final LocationServiceImpl locationService;

    public LocationController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }
    /* Adding divisions
    {"division":"NAIROBI","division_code":"NBO","iso2":"KE","type":"Feature","properties":
    {"name":"Nairobi County","country":"Kenya"},"geometry":{"type":"Polygon","coordinates":[[[36.6640,-1.4440],[36.9150,-1.4440],[36.9150,-1.1500],[36.6640,-1.1500],[36.6640,-1.4440]]]}}
     */

    @PostMapping(value = "/division")
    public ResponseEntity<?> createDivision(@RequestBody PolygonRequest polygonRequest) {
        String res = "";
        try {
            res = locationService.createDivision(polygonRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(res);
    }

     /* Get  division Name
    {"lon":36.89326,"lat":-1.21326}
     */

    @GetMapping(value = "/division")
    public ResponseEntity<?> getDivision(@RequestBody Map<String, Double> coordinates) {

        try {
            RegionResponse response = locationService.getDivision(coordinates.get("lon"), coordinates.get("lat"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(RegionResponse.builder().supported(false).build());
        }


    }


    /* Adding subDivisions
    { "type": "Feature", "properties": { "Name": "Roysambu zimmerman Area" }, "geometry": { "type": "Polygon", "coordinates": [ [ [ 36.8876376, -1.2221954, 0.0 ], [ 36.8950619, -1.2145582, 0.0 ],
     [ 36.9085374, -1.2098815, 0.0 ], [ 36.8989672, -1.2017294, 0.0 ], [ 36.8827023, -1.2072643, 0.0 ], [ 36.8801703, -1.2185485, 0.0 ], [ 36.8876376, -1.2221954, 0.0 ] ] ] } }
     */
     /*
    Location Entry
     */

    @PostMapping(value = "sub-division")
    public ResponseEntity<?> createSubDivision(@RequestBody PolygonRequest polygonRequest) {
        String res = "";
        try {
            res = locationService.createSubDivision(polygonRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(res);
    }

    /* Get sub division Name
    {"lon":36.89326,"lat":-1.21326}
     */

    @GetMapping(value = "sub-division")
    public ResponseEntity<?> getSubDivision(@RequestBody Map<String, Double> coordinates) {

        try {
            RegionResponse response = locationService.getSubDivision(coordinates.get("lon"), coordinates.get("lat"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(RegionResponse.builder().supported(false).build());
        }


    }
}
