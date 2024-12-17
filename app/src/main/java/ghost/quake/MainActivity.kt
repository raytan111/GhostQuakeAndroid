package ghost.quake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ghost.quake.presentation.screens.main.MainScreen
import ghost.quake.presentation.theme.MyAppTheme
import ghost.quake.util.NotificationStateManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var notificationStateManager: NotificationStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val earthquakeId = intent?.getStringExtra("earthquakeId")

        setContent {
            MyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        initialEarthquakeId = earthquakeId,
                        notificationStateManager = notificationStateManager
                    )
                }
            }
        }
    }
}