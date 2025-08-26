package com.cb.th.claims.cmx.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeoUtil {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public static String pointToString(Point point) {
        if (point == null) return null;
        Coordinate c = point.getCoordinate();
        return c.y + "," + c.x; // lat,lng
    }

    public static Point stringToPoint(String latLng) {
        if (latLng == null || latLng.isEmpty()) return null;
        String[] parts = latLng.split(",");
        double lat = Double.parseDouble(parts[0].trim());
        double lng = Double.parseDouble(parts[1].trim());
        return geometryFactory.createPoint(new Coordinate(lng, lat));
    }
}
