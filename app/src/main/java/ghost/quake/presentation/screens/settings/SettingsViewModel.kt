package ghost.quake.presentation.screens.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ghost.quake.data.local.preferences.PreferencesManager
import ghost.quake.util.WorkManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        checkInitialPermissions()
    }

    private fun checkInitialPermissions() {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        _state.value = _state.value.copy(
            locationEnabled = hasLocationPermission,
            notificationsEnabled = hasNotificationPermission && preferencesManager.areNotificationsEnabled()
        )

        // Si las notificaciones estaban habilitadas, reiniciar el monitoreo
        if (state.value.notificationsEnabled) {
            workManagerHelper.startEarthquakeMonitoring()
        }
    }

    fun setLocationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(locationEnabled = enabled)
            preferencesManager.setLocationEnabled(enabled)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(notificationsEnabled = enabled)
            preferencesManager.setNotificationsEnabled(enabled)

            if (enabled) {
                workManagerHelper.startEarthquakeMonitoring()
            } else {
                workManagerHelper.stopEarthquakeMonitoring()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Error obteniendo ubicación", e)
            null
        }
    }
}