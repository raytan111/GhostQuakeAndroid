package ghost.quake.presentation.screens.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()
}