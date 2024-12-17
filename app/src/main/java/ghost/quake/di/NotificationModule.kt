package ghost.quake.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ghost.quake.util.NotificationManagerHelper
import ghost.quake.util.NotificationStateManager
import ghost.quake.util.WorkManagerHelper
import ghost.quake.data.local.preferences.PreferencesManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerHelper = NotificationManagerHelper(context)

    @Provides
    @Singleton
    fun provideNotificationStateManager(
        workManagerHelper: WorkManagerHelper,
        preferencesManager: PreferencesManager
    ): NotificationStateManager = NotificationStateManager(
        workManagerHelper = workManagerHelper,
        preferencesManager = preferencesManager
    )
}