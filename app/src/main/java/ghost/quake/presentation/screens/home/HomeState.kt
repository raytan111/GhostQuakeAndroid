package ghost.quake.presentation.screens.home

data class HomeState(
    val earthquakes: List<Earthquake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

data class Earthquake(
    val id: String = "", // Nuevo campo para las animaciones
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