package ghost.quake.di

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.hilt.work.HiltWorkerFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(
        workerFactory: HiltWorkerFactory
    ): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }
}