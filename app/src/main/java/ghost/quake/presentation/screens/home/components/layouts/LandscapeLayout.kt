package ghost.quake.presentation.screens.home.components.layouts

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import ghost.quake.presentation.screens.home.components.QuickStats
import ghost.quake.presentation.theme.DarkModeColors

@Composable
fun LandscapeLayout(
    state: HomeState,
    colors: DarkModeColors,
    isVisible: Boolean,
    onEarthquakeClick: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            if (state.earthquakes.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInHorizontally()
                    ) {
                        Column {
                            Text(
                                text = "Último Sismo",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = colors.textColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                            LastEarthquakeCard(
                                earthquake = state.earthquakes.first(),
                                colors = colors,
                                isLastEarthquake = true,
                                onCardClick = onEarthquakeClick
                            )
                            QuickStats(
                                earthquakes = state.earthquakes,
                                colors = colors
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(colors.cardBackground)
        )

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it })
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    Text(
                        text = "Últimos Sismos",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = colors.textColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(
                            items = state.earthquakes.drop(1),
                            key = { earthquake -> "${earthquake.date}${earthquake.hour}${earthquake.magnitude}" }
                        ) { earthquake ->
                            EarthquakeItem(
                                earthquake = earthquake,
                                colors = colors,
                                isLastEarthquake = false,
                                onCardClick = onEarthquakeClick
                            )
                        }
                    }
                }
            }
        }
    }
}