package ghost.quake.util

import android.content.Context
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
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<EarthquakeWorker>(
            15, TimeUnit.MINUTES,  // Período mínimo es 15 minutos en WorkManager
            5, TimeUnit.MINUTES    // Flex interval
        )
            .setConstraints(constraints)
            .addTag(WORKER_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORKER_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun stopEarthquakeMonitoring() {
        workManager.cancelUniqueWork(WORKER_NAME)
    }

    companion object {
        private const val WORKER_TAG = "earthquake_monitoring"
        private const val WORKER_NAME = "earthquake_periodic_check"
    }
}