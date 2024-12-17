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
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getRecentEarthquakesUseCase: GetRecentEarthquakesUseCase,
    private val notificationManager: NotificationManagerHelper,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(appContext, workerParams) {

    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    private var lastProcessedEarthquakeId: String? = null

    init {
        Log.d("EarthquakeWorker", "Worker initialized with dependencies: " +
                "UseCase=$getRecentEarthquakesUseCase, " +
                "NotificationManager=$notificationManager, " +
                "PreferencesManager=$preferencesManager")
    }

    override suspend fun doWork(): Result {
        Log.d("EarthquakeWorker", "Worker started")
        try {
            if (!preferencesManager.areNotificationsEnabled()) {
                Log.d("EarthquakeWorker", "Notifications are disabled, stopping worker")
                return Result.success()
            }

            val location = getCurrentLocation()
            val hasLocationPermission = hasLocationPermission()
            val magnitudeThreshold = 3.0

            Log.d("EarthquakeWorker", "Checking for earthquakes. Has location permission: $hasLocationPermission")

            getRecentEarthquakesUseCase()
                .onSuccess { earthquakes ->
                    Log.d("EarthquakeWorker", "Retrieved ${earthquakes.size} earthquakes from API")

                    val relevantEarthquakes = if (hasLocationPermission && location != null) {
                        Log.d("EarthquakeWorker", "Filtering earthquakes by location and magnitude")
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
                        Log.d("EarthquakeWorker", "Filtering earthquakes by magnitude only")
                        earthquakes.filter { it.magnitude >= magnitudeThreshold }
                    }

                    Log.d("EarthquakeWorker", "Found ${relevantEarthquakes.size} relevant earthquakes")

                    relevantEarthquakes
                        .firstOrNull { it.id != lastProcessedEarthquakeId }
                        ?.let { earthquake ->
                            Log.d("EarthquakeWorker", "Sending notification for earthquake: Magnitude ${earthquake.magnitude} at ${earthquake.place}")
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

            Log.d("EarthquakeWorker", "Worker completed successfully")
            return Result.success()
        } catch (e: Exception) {
            Log.e("EarthquakeWorker", "Worker failed", e)
            return Result.retry()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return try {
            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.e("EarthquakeWorker", "Error getting location", e)
            null
        }
    }
}