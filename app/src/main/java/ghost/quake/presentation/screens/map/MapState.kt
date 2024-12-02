package ghost.quake.presentation.screens.map

import ghost.quake.domain.model.Earthquake

data class MapState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val earthquakes: List<Earthquake> = emptyList()
)