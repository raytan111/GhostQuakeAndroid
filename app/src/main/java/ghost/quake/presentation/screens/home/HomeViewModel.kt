package ghost.quake.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class HomeViewModel : ViewModel() {
    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state
    private val client = OkHttpClient()

    init {
        loadEarthquakes()
    }

    fun refreshData() {
        loadEarthquakes()
    }

    private fun loadEarthquakes() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                val request = Request.Builder()
                    .url("https://api.boostr.cl/earthquakes/recent.json")
                    .build()

                val earthquakes = withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Error: ${response.code}")

                        val jsonStr = response.body?.string()
                            ?: throw IOException("Respuesta vacía del servidor")

                        Log.d("HomeViewModel", "Response: $jsonStr")
                        parseEarthquakeData(jsonStr)
                    }
                }

                _state.value = _state.value.copy(
                    earthquakes = earthquakes,
                    isLoading = false,
                    error = ""
                )

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error cargando sismos", e)
                val errorMessage = when (e) {
                    is IOException -> "Error de red: ${e.message}"
                    is JSONException -> "Error procesando datos: ${e.message}"
                    else -> "Error desconocido: ${e.message}"
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    private fun parseEarthquakeData(jsonStr: String): List<Earthquake> {
        val jsonObject = JSONObject(jsonStr)
        val jsonArray = jsonObject.getJSONArray("data")
        val earthquakes = mutableListOf<Earthquake>()

        for (i in 0 until jsonArray.length()) {
            try {
                val item = jsonArray.getJSONObject(i)
                earthquakes.add(
                    Earthquake(
                        id = "${item.optString("date")}${item.optString("hour")}${item.optString("magnitude")}", // ID único para las animaciones
                        date = item.optString("date", ""),
                        hour = item.optString("hour", ""),
                        place = item.optString("place", ""),
                        magnitude = item.optString("magnitude", "0").toDoubleOrNull() ?: 0.0,
                        depth = item.optString("depth", "0 km")
                            .replace(" km", "")
                            .toIntOrNull() ?: 0,
                        latitude = item.optString("latitude", "0").toDoubleOrNull() ?: 0.0,
                        longitude = item.optString("longitude", "0").toDoubleOrNull() ?: 0.0,
                        image = item.optString("image", ""),
                        info = item.optString("info", "")
                    )
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error parsing earthquake ${i}: ${e.message}")
            }
        }

        return earthquakes
    }
}
