package ghost.quake.data.repository

import ghost.quake.data.remote.api.EarthquakeApi
import ghost.quake.data.remote.mapper.EarthquakeMapper
import ghost.quake.domain.model.Earthquake
import ghost.quake.domain.repository.EarthquakeRepository
import java.io.IOException
import javax.inject.Inject

class EarthquakeRepositoryImpl @Inject constructor(
    private val api: EarthquakeApi,
    private val mapper: EarthquakeMapper
) : EarthquakeRepository {
    override suspend fun getRecentEarthquakes(): Result<List<Earthquake>> = runCatching {
        val response = api.getRecentEarthquakes()
        if (response.isSuccessful) {
            response.body()?.data?.map { dto ->
                mapper.toDomain(dto)
            } ?: throw IOException("Respuesta vacía del servidor")
        } else {
            throw IOException("Error: ${response.code()}")
        }
    }
}