package ghost.quake.presentation.screens.home

import ghost.quake.domain.model.Earthquake

data class HomeState(
    val earthquakes: List<Earthquake> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)