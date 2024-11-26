package ghost.quake.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EarthquakeDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("hour")
    val hour: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("magnitude")
    val magnitude: String,
    @SerializedName("depth")
    val depth: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("info")
    val info: String
)