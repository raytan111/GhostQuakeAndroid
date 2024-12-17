package ghost.quake.presentation.screens.settings

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ghost.quake.data.local.preferences.PreferencesManager
import ghost.quake.util.NotificationManagerHelper
import ghost.quake.util.NotificationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager,
    private val notificationManager: NotificationManagerHelper,
    private val notificationStateManager: NotificationStateManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        Log.d("SettingsViewModel", "Initializing SettingsViewModel")
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

        Log.d("SettingsViewModel", "Initial permissions check - Location: $hasLocationPermission, Notifications: $hasNotificationPermission")

        _state.value = _state.value.copy(
            locationEnabled = hasLocationPermission,
            notificationsEnabled = hasNotificationPermission && notificationStateManager.isNotificationsEnabled()
        )
    }

    fun setLocationEnabled(enabled: Boolean) {
        Log.d("SettingsViewModel", "Setting location enabled: $enabled")
        viewModelScope.launch {
            _state.value = _state.value.copy(locationEnabled = enabled)
            preferencesManager.setLocationEnabled(enabled)

            if (enabled) {
                notificationManager.showNotification(
                    "Ubicación Activada",
                    "Ahora recibirás notificaciones de sismos cercanos a tu ubicación"
                )
            } else {
                notificationManager.showNotification(
                    "Ubicación Desactivada",
                    "Recibirás notificaciones de sismos en todo Chile mayores a 5.5"
                )
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        Log.d("SettingsViewModel", "Setting notifications enabled: $enabled")
        viewModelScope.launch {
            _state.value = _state.value.copy(notificationsEnabled = enabled)
            notificationStateManager.updateNotificationState(enabled)

            if (enabled) {
                notificationManager.showNotification(
                    "Notificaciones Activadas",
                    if (state.value.locationEnabled)
                        "Recibirás alertas de sismos cercanos que podrías sentir"
                    else
                        "Recibirás alertas de sismos en Chile con magnitud mayor a 5.5"
                )
            } else {
                notificationManager.showNotification(
                    "Notificaciones Desactivadas",
                    "Ya no recibirás alertas de sismos"
                )
            }
        }
    }
}