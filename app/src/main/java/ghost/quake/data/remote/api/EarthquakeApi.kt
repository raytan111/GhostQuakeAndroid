package ghost.quake.data.remote.api

import ghost.quake.data.remote.dto.EarthquakeResponse
import retrofit2.Response
import retrofit2.http.GET

interface EarthquakeApi {
    @GET("recent.json")
    suspend fun getRecentEarthquakes(): Response<EarthquakeResponse>
}
