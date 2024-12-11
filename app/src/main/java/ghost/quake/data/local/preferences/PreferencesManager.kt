package ghost.quake.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, false)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun isLocationEnabled(): Boolean {
        return preferences.getBoolean(KEY_LOCATION_ENABLED, false)
    }

    fun setLocationEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_LOCATION_ENABLED, enabled).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "quake_preferences"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LOCATION_ENABLED = "location_enabled"
    }
}