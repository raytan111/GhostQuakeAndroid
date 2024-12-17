package ghost.quake.presentation.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ghost.quake.data.local.preferences.PreferencesManager
import ghost.quake.util.WorkManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        Log.d("MainViewModel", "Initializing MainViewModel")
        checkNotificationState()
    }

    fun checkNotificationState() {
        val notificationsEnabled = preferencesManager.areNotificationsEnabled()
        Log.d("MainViewModel", "Checking notification state: $notificationsEnabled")

        if (notificationsEnabled) {
            workManagerHelper.startEarthquakeMonitoring()
        }
    }

    // Este método será llamado cuando cambien las notificaciones en SettingsScreen
    fun updateNotificationState(enabled: Boolean) {
        Log.d("MainViewModel", "Updating notification state: $enabled")
        if (enabled) {
            workManagerHelper.startEarthquakeMonitoring()
        } else {
            workManagerHelper.stopEarthquakeMonitoring()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel", "MainViewModel cleared")
    }
}

data class MainState(
    val isLoading: Boolean = false
)