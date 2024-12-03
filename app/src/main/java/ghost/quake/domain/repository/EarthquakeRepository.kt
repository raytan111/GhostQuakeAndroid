package ghost.quake.domain.repository

import ghost.quake.domain.model.Earthquake

interface EarthquakeRepository {
    suspend fun getRecentEarthquakes(): Result<List<Earthquake>>
    suspend fun getEarthquakeById(id: String): Earthquake? // Añade esta línea
}