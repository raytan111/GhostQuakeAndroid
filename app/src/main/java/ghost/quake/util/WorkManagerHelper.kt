package ghost.quake.util

import android.content.Context
import android.util.Log
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import ghost.quake.data.worker.EarthquakeWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) {

    fun startEarthquakeMonitoring() {
        Log.d("WorkManagerHelper", "Starting earthquake monitoring")

        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<EarthquakeWorker>(
                15, TimeUnit.MINUTES
            ).setConstraints(constraints)
                .addTag(WORKER_TAG)
                .setInitialDelay(1, TimeUnit.MINUTES) // Empieza después de 1 minuto
                .build()

            Log.d("WorkManagerHelper", "Created work request: ${workRequest.id}")

            // Cancelar cualquier trabajo existente antes de encolar uno nuevo
            workManager.cancelUniqueWork(WORKER_NAME)

            workManager.enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )

            // Observar el estado del trabajo
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .observeForever { workInfo ->
                    Log.d("WorkManagerHelper", "Work ${workRequest.id} state changed to: ${workInfo?.state}")
                    if (workInfo?.state == WorkInfo.State.FAILED) {
                        Log.e("WorkManagerHelper", "Work failed: ${workInfo.outputData}")
                    }
                }

            Log.d("WorkManagerHelper", "Work request enqueued successfully")
        } catch (e: Exception) {
            Log.e("WorkManagerHelper", "Error starting monitoring", e)
        }
    }

    fun stopEarthquakeMonitoring() {
        Log.d("WorkManagerHelper", "Stopping earthquake monitoring")
        workManager.cancelUniqueWork(WORKER_NAME)
    }

    companion object {
        private const val WORKER_TAG = "earthquake_monitoring"
        private const val WORKER_NAME = "earthquake_periodic_check"
    }
}