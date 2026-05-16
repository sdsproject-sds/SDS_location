package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author samwel.wafula
 * Created on 4/15/2026
 * Time 4:15 PM
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PolygonGeometry {
    private String type;
    private List<List<List<Double>>> coordinates;

    /**
     * Create PolygonGeometry from JTS Geometry object
     */
    public static PolygonGeometry fromGeometry(Geometry geometry) {
        if (geometry == null) {
            return null;
        }

        PolygonGeometry polygonGeometry = new PolygonGeometry();
        polygonGeometry.setType(geometry.getGeometryType());
        polygonGeometry.setCoordinates(extractCoordinates(geometry));
        
        return polygonGeometry;
    }

    /**
     * Extract coordinates from JTS Geometry based on geometry type
     */
    private static List<List<List<Double>>> extractCoordinates(Geometry geometry) {
        String type = geometry.getGeometryType();
        
        switch (type) {
            case "Polygon":
                return extractPolygonCoordinates((Polygon) geometry);
            
            case "MultiPolygon":
                return extractMultiPolygonCoordinates((MultiPolygon) geometry);
                
            case "Point":
                // Convert point to a simple polygon (square around the point)
                return convertPointToPolygon((Point) geometry);
                
            case "LineString":
                // Convert linestring to a polygon by buffering
                return convertLineStringToPolygon((LineString) geometry);
                
            default:
                // For other geometries, try to get the envelope as a polygon
                return extractPolygonCoordinates((Polygon) geometry.getEnvelope());
        }
    }
    
    private static List<List<List<Double>>> extractPolygonCoordinates(Polygon polygon) {
        List<List<List<Double>>> rings = new ArrayList<>();
        
        // Exterior ring
        rings.add(coordinatesToList(polygon.getExteriorRing().getCoordinates()));
        
        // Interior rings (holes)
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            rings.add(coordinatesToList(polygon.getInteriorRingN(i).getCoordinates()));
        }
        
        return rings;
    }
    
    private static List<List<List<Double>>> extractMultiPolygonCoordinates(MultiPolygon multiPolygon) {
        List<List<List<Double>>> allRings = new ArrayList<>();
        
        // Take the first polygon from the multipolygon
        // If you need all polygons, you might want to return a different structure
        if (multiPolygon.getNumGeometries() > 0) {
            Polygon firstPolygon = (Polygon) multiPolygon.getGeometryN(0);
            return extractPolygonCoordinates(firstPolygon);
        }
        
        return allRings;
    }
    
    private static List<List<List<Double>>> convertPointToPolygon(Point point) {
        // Create a small square around the point (0.001 degree buffer)
        double buffer = 0.001;
        double x = point.getX();
        double y = point.getY();
        
        List<List<Double>> ring = List.of(
            List.of(x - buffer, y - buffer),
            List.of(x + buffer, y - buffer),
            List.of(x + buffer, y + buffer),
            List.of(x - buffer, y + buffer),
            List.of(x - buffer, y - buffer)  // Close the ring
        );
        
        return List.of(ring);
    }
    
    private static List<List<List<Double>>> convertLineStringToPolygon(LineString lineString) {
        // Create a simple polygon from linestring coordinates
        // This is a basic conversion - you might want to implement proper buffering
        Coordinate[] coords = lineString.getCoordinates();
        List<List<Double>> ring = new ArrayList<>();
        
        // Add all coordinates
        for (Coordinate coord : coords) {
            ring.add(List.of(coord.x, coord.y));
        }
        
        // Close the ring if not already closed
        if (!coords[0].equals(coords[coords.length - 1])) {
            ring.add(List.of(coords[0].x, coords[0].y));
        }
        
        return List.of(ring);
    }
    
    /**
     * Convert JTS Coordinate array to nested list structure
     */
    private static List<List<Double>> coordinatesToList(Coordinate[] coordinates) {
        List<List<Double>> coords = new ArrayList<>();
        for (Coordinate coord : coordinates) {
            coords.add(List.of(coord.x, coord.y));
        }
        return coords;
    }
    
    /**
     * Create an empty PolygonGeometry
     */
    public static PolygonGeometry empty() {
        PolygonGeometry empty = new PolygonGeometry();
        empty.setType("Polygon");
        empty.setCoordinates(new ArrayList<>());
        return empty;
    }
}