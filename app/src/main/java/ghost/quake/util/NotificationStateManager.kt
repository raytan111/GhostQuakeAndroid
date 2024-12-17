package ghost.quake.util

import ghost.quake.data.local.preferences.PreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationStateManager @Inject constructor(
    private val workManagerHelper: WorkManagerHelper,
    private val preferencesManager: PreferencesManager
) {
    fun updateNotificationState(enabled: Boolean) {
        if (enabled) {
            workManagerHelper.startEarthquakeMonitoring()
        } else {
            workManagerHelper.stopEarthquakeMonitoring()
        }
        preferencesManager.setNotificationsEnabled(enabled)
    }

    fun isNotificationsEnabled(): Boolean {
        return preferencesManager.areNotificationsEnabled()
    }
}