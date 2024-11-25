package ghost.quake.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.res.Configuration
import androidx.compose.ui.graphics.Shadow
import ghost.quake.presentation.theme.DarkModeColors
import ghost.quake.presentation.theme.getColorsTheme
import ghost.quake.presentation.theme.getMagnitudeColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val state = viewModel.state.value
    val colors = getColorsTheme()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.earthquakes, state.isLoading) {
        isVisible = state.earthquakes.isNotEmpty() && !state.isLoading
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = {
            scope.launch {
                viewModel.refreshData()
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(colors.backgroundColor)
    ) {
        if (state.isLoading) {
            EarthquakeSkeletonLoading(colors = colors)
        } else {
            if (isLandscape) {
                LandscapeLayout(
                    state = state,
                    colors = colors,
                    isVisible = isVisible
                )
            } else {
                PortraitLayout(
                    state = state,
                    colors = colors,
                    isVisible = isVisible
                )
            }
        }

        // Error Snackbar
        if (state.error.isNotBlank()) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                containerColor = colors.errorBackground,
                contentColor = colors.errorText
            ) {
                Text(text = state.error)
            }
        }

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = colors.cardBackground,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PortraitLayout(
    state: HomeState,
    colors: DarkModeColors,
    isVisible: Boolean
) {
    LazyColumn {
        // Último sismo
        if (state.earthquakes.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(
                        initialAlpha = 0f,
                        animationSpec = tween(durationMillis = 500)
                    ) + slideInVertically(
                        initialOffsetY = { -100 },
                        animationSpec = tween(durationMillis = 500)
                    )
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
                            isLastEarthquake = true
                        )
                    }
                }
            }
        }

        // Estadísticas
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    initialAlpha = 0f,
                    animationSpec = tween(durationMillis = 500, delayMillis = 200)
                )
            ) {
                QuickStats(
                    earthquakes = state.earthquakes,
                    colors = colors
                )
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
                    isLastEarthquake = false
                )
            }
        }
    }
}

@Composable
private fun LandscapeLayout(
    state: HomeState,
    colors: DarkModeColors,
    isVisible: Boolean
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Panel izquierdo
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
                                isLastEarthquake = true
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

        // Separador vertical
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(colors.cardBackground)
        )

        // Panel derecho
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it })
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = "Últimos Sismos",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = colors.textColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        items(
                            items = state.earthquakes.drop(1),
                            key = { earthquake -> "${earthquake.date}${earthquake.hour}${earthquake.magnitude}" }
                        ) { earthquake ->
                            EarthquakeItem(
                                earthquake = earthquake,
                                colors = colors,
                                isLastEarthquake = false
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun QuickStats(
    earthquakes: List<Earthquake>,
    colors: DarkModeColors
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        shape = RoundedCornerShape(16.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estadísticas últimas 24h",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textColor,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    val avgMagnitude = earthquakes.map { it.magnitude }.average()
                    val strongestQuake = earthquakes.maxByOrNull { it.magnitude }
                    val avgDepth = earthquakes.map { it.depth }.average()

                    StatRow(
                        icon = Icons.Rounded.Straighten,
                        title = "Magnitud promedio",
                        value = "%.1f".format(avgMagnitude),
                        colors = colors
                    )
                    StatRow(
                        icon = Icons.Rounded.CalendarMonth,
                        title = "Sismo más fuerte",
                        value = "%.1f".format(strongestQuake?.magnitude ?: 0.0),
                        colors = colors
                    )
                    StatRow(
                        icon = Icons.Rounded.LocationOn,
                        title = "Profundidad promedio",
                        value = "${avgDepth.toInt()} km",
                        colors = colors
                    )
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    icon: ImageVector,
    title: String,
    value: String,
    colors: DarkModeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.secondaryText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = colors.secondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = colors.textColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LastEarthquakeCard(
    earthquake: Earthquake,
    colors: DarkModeColors,
    isLastEarthquake: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedMagnitudeBox(
                    magnitude = earthquake.magnitude,
                    large = true,
                    isLastEarthquake = isLastEarthquake
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = earthquake.place,
                        color = colors.textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = colors.secondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${earthquake.date} ${earthquake.hour}",
                            color = colors.secondaryText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Straighten,
                    contentDescription = null,
                    tint = colors.secondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Profundidad: ${earthquake.depth} km",
                    color = colors.secondaryText,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun EarthquakeItem(
    earthquake: Earthquake,
    colors: DarkModeColors,
    isLastEarthquake: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedMagnitudeBox(
                magnitude = earthquake.magnitude,
                large = false,
                isLastEarthquake = isLastEarthquake
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = earthquake.place,
                    color = colors.textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint = colors.secondaryText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${earthquake.date} ${earthquake.hour}",
                        color = colors.secondaryText,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedMagnitudeBox(
    magnitude: Double,
    large: Boolean,
    isLastEarthquake: Boolean
) {
    val size = if (large) 80.dp else 60.dp
    val fontSize = if (large) 32.sp else 24.sp

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLastEarthquake) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val magnitudeColor = getMagnitudeColor(magnitude)
    val shadowColor = remember(magnitudeColor) {
        magnitudeColor.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isLastEarthquake) scale else 1f)
            .shadow(
                elevation = if (isLastEarthquake) 8.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = shadowColor
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        magnitudeColor,
                        magnitudeColor.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isLastEarthquake) 2.dp else 0.dp,
                color = Color(0xFF81C784).copy(alpha = if (isLastEarthquake) 1f else 0f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "%.1f".format(magnitude),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.5f),
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            )
        )
    }
}

@Composable
private fun EarthquakeSkeletonLoading(colors: DarkModeColors) {
    LazyColumn {
        // Último Sismo skeleton
        item {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    .width(150.dp)
                    .height(32.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(8.dp))
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shimmerEffect()
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(24.dp)
                                    .shimmerEffect()
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(16.dp)
                                    .shimmerEffect()
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .shimmerEffect()
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // Estadísticas skeleton con acordeón
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header del acordeón
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(24.dp)
                                .shimmerEffect()
                                .clip(RoundedCornerShape(4.dp))
                        )
                        // Indicador de expandir
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .shimmerEffect()
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    // Contenido del acordeón (mostrar en estado colapsado por defecto)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.dp)  // Altura 0 para simular estado colapsado
                    ) {
                        repeat(3) {
                            StatRowSkeleton()
                        }
                    }
                }
            }

            // Título lista skeleton
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .width(250.dp)
                    .height(32.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Lista de sismos skeleton
        items(5) {
            EarthquakeItemSkeleton(colors = colors)
        }
    }
}

@Composable
private fun StatRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono
        Box(
            modifier = Modifier
                .size(24.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Título
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
        // Valor
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun EarthquakeItemSkeleton(colors: DarkModeColors) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(20.dp)
                        .shimmerEffect()
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(150.dp)
                        .height(16.dp)
                        .shimmerEffect()
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// Extensión para el efecto Shimmer
fun Modifier.shimmerEffect(isDarkMode: Boolean = true): Modifier = composed {
    val shimmerColors = if (isDarkMode) {
        listOf(
            Color(0xFF3C3C3C),
            Color(0xFF4C4C4C),
            Color(0xFF3C3C3C)
        )
    } else {
        listOf(
            Color(0xFFE6E8EB),
            Color(0xFFD4D7DC),
            Color(0xFFE6E8EB)
        )
    }

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - 1000f, y = 0f),
            end = Offset(x = translateAnimation.value, y = 0f)
        )
    )
}