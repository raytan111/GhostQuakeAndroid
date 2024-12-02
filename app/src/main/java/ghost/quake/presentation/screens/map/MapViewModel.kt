package ghost.quake.presentation.screens.map

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghost.quake.domain.usecase.GetRecentEarthquakesUseCase
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRecentEarthquakesUseCase: GetRecentEarthquakesUseCase
) : ViewModel() {
    private val _state = mutableStateOf(MapState())
    val state: State<MapState> = _state

    init {
        loadEarthquakes()
    }

    fun refreshData() {
        loadEarthquakes()
    }

    private fun loadEarthquakes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            getRecentEarthquakesUseCase()
                .onSuccess { earthquakes ->
                    _state.value = _state.value.copy(
                        earthquakes = earthquakes,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    Log.e("MapViewModel", "Error cargando sismos", exception)
                    val errorMessage = when (exception) {
                        is IOException -> "Error de red: ${exception.message}"
                        else -> "Error desconocido: ${exception.message}"
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
        }
    }
}