package ghost.quake.presentation.screens.settings

data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notificationsEnabled: Boolean = false,
    val locationEnabled: Boolean = false,
    val magnitudeThreshold: Double = 5.0
)