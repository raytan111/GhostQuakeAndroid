package ghost.quake.domain.model

data class Earthquake(
    val id: String,
    val date: String,
    val hour: String,
    val place: String,
    val magnitude: Double,
    val depth: Int,
    val latitude: Double,
    val longitude: Double,
    val image: String,
    val info: String
)
