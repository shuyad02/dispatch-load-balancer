package com.freightfox.dispatch.util;

/**
 * Utility class providing Haversine distance calculation between two geographical coordinates on Earth.
 */
public final class HaversineDistanceCalculator {

    // Earth's mean radius in kilometers
    public static final double EARTH_RADIUS_KM = 6371.0;

    private HaversineDistanceCalculator() {
        // Private constructor for utility class
    }

    /**
     * Calculates the great-circle distance between two coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of first point in degrees (-90 to 90)
     * @param lon1 Longitude of first point in degrees (-180 to 180)
     * @param lat2 Latitude of second point in degrees (-90 to 90)
     * @param lon2 Longitude of second point in degrees (-180 to 180)
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if (Double.compare(lat1, lat2) == 0 && Double.compare(lon1, lon2) == 0) {
            return 0.0;
        }

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        // Protect against rounding errors yielding a > 1.0
        a = Math.min(1.0, Math.max(0.0, a));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Formats distance in km according to requirement specification:
     * e.g., "5 km"
     */
    public static String formatDistance(double distanceKm) {
        return Math.round(distanceKm) + " km";
    }
}
