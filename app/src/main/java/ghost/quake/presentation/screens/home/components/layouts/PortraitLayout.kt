package ghost.quake.presentation.screens.home.components.layouts

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ghost.quake.presentation.screens.home.HomeState
import ghost.quake.presentation.screens.home.components.EarthquakeItem
import ghost.quake.presentation.screens.home.components.LastEarthquakeCard
import ghost.quake.presentation.theme.DarkModeColors

@Composable
fun PortraitLayout(
    state: HomeState,
    colors: DarkModeColors,
    isVisible: Boolean,
    onEarthquakeClick: (String) -> Unit // Nuevo parámetro
) {
    LazyColumn {
        if (state.earthquakes.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(initialAlpha = 0f, animationSpec = tween(durationMillis = 500)) +
                            slideInVertically(initialOffsetY = { -100 }, animationSpec = tween(durationMillis = 500))
                ) {
                    Column {
                        Text(
                            text = "Último Sismo",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = colors.textColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        LastEarthquakeCard(
                            earthquake = state.earthquakes.first(),
                            colors = colors,
                            isLastEarthquake = true,
                            onCardClick = onEarthquakeClick
                        )
                    }
                }
            }
        }

        // Título lista
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500, delayMillis = 300)
                ) + slideInVertically(
                    initialOffsetY = { 100 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 300)
                )
            ) {
                Text(
                    text = "Sismos Recientes",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = colors.textColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Lista de sismos
        items(
            items = state.earthquakes.drop(1),
            key = { earthquake -> "${earthquake.date}${earthquake.hour}${earthquake.magnitude}" }
        ) { earthquake ->
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500, delayMillis = 400)
                ) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 400)
                )
            ) {
                EarthquakeItem(
                    earthquake = earthquake,
                    colors = colors,
                    isLastEarthquake = false,
                    onCardClick = onEarthquakeClick  // Add this line
                )
            }
        }
    }
}
