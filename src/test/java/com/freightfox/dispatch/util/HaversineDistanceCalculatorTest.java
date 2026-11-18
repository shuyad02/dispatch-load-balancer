package com.freightfox.dispatch.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaversineDistanceCalculatorTest {

    @Test
    @DisplayName("Distance between identical coordinates should be zero")
    void distanceBetweenIdenticalCoordinatesShouldBeZero() {
        double dist = HaversineDistanceCalculator.calculateDistance(28.6139, 77.2090, 28.6139, 77.2090);
        assertEquals(0.0, dist, 0.0001);
    }

    @Test
    @DisplayName("Distance calculation should be symmetric: dist(A,B) == dist(B,A)")
    void distanceShouldBeSymmetric() {
        double lat1 = 28.6139, lon1 = 77.2090; // Connaught Place
        double lat2 = 28.7041, lon2 = 77.1025; // Karol Bagh

        double dist1 = HaversineDistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        double dist2 = HaversineDistanceCalculator.calculateDistance(lat2, lon2, lat1, lon1);

        assertEquals(dist1, dist2, 0.0001);
        assertTrue(dist1 > 0.0);
    }

    @Test
    @DisplayName("Calculates accurate distance for Bangalore coordinates")
    void calculatesAccurateBangaloreDistance() {
        // Indiranagar: 12.9716, 77.6413
        // MG Road: 12.9716, 77.5946
        double dist = HaversineDistanceCalculator.calculateDistance(12.9716, 77.6413, 12.9716, 77.5946);

        // Expected distance is ~5.06 km, formatting to "5 km"
        assertEquals(5.06, dist, 0.1);
        assertEquals("5 km", HaversineDistanceCalculator.formatDistance(dist));
    }

    @Test
    @DisplayName("Calculates accurate distance for Chennai coordinates")
    void calculatesAccurateChennaiDistance() {
        // T Nagar: 13.0674, 80.2376
        // Anna Salai: 13.0827, 80.2707
        double dist = HaversineDistanceCalculator.calculateDistance(13.0674, 80.2376, 13.0827, 80.2707);

        assertEquals(3.97, dist, 0.1);
        assertEquals("4 km", HaversineDistanceCalculator.formatDistance(dist));
    }

    @Test
    @DisplayName("Format distance handles integer rounding")
    void testFormatDistance() {
        assertEquals("0 km", HaversineDistanceCalculator.formatDistance(0.0));
        assertEquals("5 km", HaversineDistanceCalculator.formatDistance(5.2));
        assertEquals("12 km", HaversineDistanceCalculator.formatDistance(12.4));
    }
}
