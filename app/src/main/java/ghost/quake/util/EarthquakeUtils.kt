package ghost.quake.util

import kotlin.math.*

object EarthquakeUtils {
    private const val EARTH_RADIUS = 6371.0 // Radio de la Tierra en kilómetros

    /**
     * Calcula la distancia entre dos puntos usando la fórmula de Haversine
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    /**
     * Calcula el radio de impacto aproximado basado en la magnitud
     * Esta es una aproximación simplificada - los sismos más fuertes se sienten a mayor distancia
     */
    fun calculateImpactRadius(magnitude: Double): Double {
        return when {
            magnitude >= 7.0 -> 300.0  // Puede sentirse hasta 300km
            magnitude >= 6.0 -> 200.0  // Puede sentirse hasta 200km
            magnitude >= 5.0 -> 150.0  // Puede sentirse hasta 150km
            magnitude >= 4.0 -> 100.0  // Puede sentirse hasta 100km
            magnitude >= 3.0 -> 50.0   // Puede sentirse hasta 50km
            else -> 20.0              // Sismos menores tienen impacto local
        }
    }

    /**
     * Determina si un sismo puede sentirse en una ubicación dada
     */
    fun canBePerceivedAtLocation(
        earthquakeLat: Double,
        earthquakeLong: Double,
        earthquakeMagnitude: Double,
        userLat: Double,
        userLong: Double
    ): Boolean {
        val distance = calculateDistance(
            earthquakeLat, earthquakeLong,
            userLat, userLong
        )
        val impactRadius = calculateImpactRadius(earthquakeMagnitude)
        return distance <= impactRadius
    }
}