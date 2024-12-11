
package ghost.quake.data.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ghost.quake.domain.usecase.GetRecentEarthquakesUseCase
import ghost.quake.util.EarthquakeUtils
import ghost.quake.util.NotificationManagerHelper
import ghost.quake.data.local.preferences.PreferencesManager

@HiltWorker
class EarthquakeWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getRecentEarthquakesUseCase: GetRecentEarthquakesUseCase,
    private val notificationManager: NotificationManagerHelper,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var lastProcessedEarthquakeId: String? = null

    override suspend fun doWork(): Result {
        try {
            if (!preferencesManager.areNotificationsEnabled()) {
                return Result.success()
            }

            val location = getCurrentLocation()
            val hasLocationPermission = hasLocationPermission()
            val magnitudeThreshold = 5.5

            getRecentEarthquakesUseCase()
                .onSuccess { earthquakes ->
                    val relevantEarthquakes = if (hasLocationPermission && location != null) {
                        earthquakes.filter { earthquake ->
                            earthquake.magnitude >= magnitudeThreshold &&
                                    EarthquakeUtils.canBePerceivedAtLocation(
                                        earthquakeLat = earthquake.latitude,
                                        earthquakeLong = earthquake.longitude,
                                        earthquakeMagnitude = earthquake.magnitude,
                                        userLat = location.latitude,
                                        userLong = location.longitude
                                    )
                        }
                    } else {
                        earthquakes.filter { it.magnitude >= magnitudeThreshold }
                    }

                    relevantEarthquakes
                        .firstOrNull { it.id != lastProcessedEarthquakeId }
                        ?.let { earthquake ->
                            lastProcessedEarthquakeId = earthquake.id
                            notificationManager.showEarthquakeNotification(
                                title = "¡Sismo Significativo Detectado!",
                                message = "Magnitud ${earthquake.magnitude} en ${earthquake.place}",
                                earthquake = earthquake
                            )
                        }
                }
                .onFailure { exception ->
                    Log.e("EarthquakeWorker", "Error checking earthquakes", exception)
                }

            return Result.success()
        } catch (e: Exception) {
            Log.e("EarthquakeWorker", "Worker failed", e)
            return Result.retry()
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
            Log.e("EarthquakeWorker", "Error getting location", e)
            null
        }
    }
}