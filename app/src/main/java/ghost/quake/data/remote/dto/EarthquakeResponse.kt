package ghost.quake.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EarthquakeResponse(
    @SerializedName("data")
    val data: List<EarthquakeDto>
)