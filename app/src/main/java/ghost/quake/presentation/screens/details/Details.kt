package ghost.quake.presentation.screens.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import ghost.quake.domain.model.Earthquake
import ghost.quake.domain.repository.EarthquakeRepository
import ghost.quake.presentation.theme.DarkModeColors
import ghost.quake.presentation.theme.EarthquakeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: EarthquakeRepository
) : ViewModel() {
    private val _state = mutableStateOf(DetailState())
    val state: State<DetailState> = _state

    fun getEarthquake(id: String) {
        viewModelScope.launch {
            repository.getEarthquakeById(id)?.let { earthquake ->
                _state.value = _state.value.copy(
                    earthquake = earthquake
                )
            }
        }
    }
}

data class DetailState(
    val earthquake: Earthquake? = null
)

private fun getMagnitudeColor(magnitude: Double): Color {
    return when {
        magnitude >= 7.0 -> EarthquakeColors.Severe
        magnitude >= 6.0 -> EarthquakeColors.High
        magnitude >= 5.0 -> EarthquakeColors.MediumHigh
        magnitude >= 4.0 -> EarthquakeColors.Medium
        else -> EarthquakeColors.Low
    }
}

@Composable
private fun InformacionUtil(magnitude: Double, colors: DarkModeColors) {
    val (titulo, informacion) = when {
        magnitude >= 7.0 -> Pair(
            "Sismo Mayor - Precaución Extrema",
            "• Evacúe inmediatamente a un lugar seguro y aléjese de edificios\n" +
                    "• Corte el gas, agua y electricidad\n" +
                    "• Manténgase informado por canales oficiales\n" +
                    "• Prepare un kit de emergencia con agua, alimentos y medicamentos\n" +
                    "• Siga las instrucciones de autoridades de emergencia"
        )
        magnitude >= 5.5 -> Pair(
            "Sismo Moderado - Precaución Alta",
            "• Mantenga la calma y ubíquese en zonas seguras\n" +
                    "• Aléjese de ventanas y objetos que puedan caer\n" +
                    "• Tenga lista una mochila de emergencia\n" +
                    "• Esté atento a las réplicas\n" +
                    "• Revise si hay daños estructurales visibles"
        )
        else -> Pair(
            "Sismo Menor - Precaución Normal",
            "• Mantenga la calma\n" +
                    "• Identifique las zonas seguras de su hogar\n" +
                    "• Revise su plan de emergencia familiar\n" +
                    "• Tenga números de emergencia a mano\n" +
                    "• Asegure objetos que puedan caer"
        )
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = getMagnitudeColor(magnitude)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = informacion,
                fontSize = 16.sp,
                color = colors.textColor,
                lineHeight = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarthquakeDetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    colors: DarkModeColors,
    navController: NavController,
    id: String
) {
    val state by viewModel.state
    var showImageDialog by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getEarthquake(id)
        delay(100) // Esperar a que los datos se carguen
        visible = false
        launch {
            delay(200)
            visible = true
        }
    }

    state.earthquake?.let { earthquake ->
        if (showImageDialog) {
            Dialog(onDismissRequest = { showImageDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        AsyncImage(
                            model = earthquake.image,
                            contentDescription = "Mapa del sismo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { showImageDialog = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Cerrar",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Detalles del Sismo",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Regresar")
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = colors.cardBackground,
                        titleContentColor = colors.textColor,
                        navigationIconContentColor = colors.textColor
                    )
                )
            },
            containerColor = colors.backgroundColor
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(
                        initialAlpha = 0f,
                        animationSpec = tween(durationMillis = 500)
                    ) + expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(16.dp)
                        ) {
                            AsyncImage(
                                model = earthquake.image,
                                contentDescription = "Mapa del sismo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { showImageDialog = true },
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.3f)
                                            )
                                        )
                                    )
                            )
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(
                                initialAlpha = 0f,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 100
                                )
                            ) + expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(durationMillis = 500, delayMillis = 100)
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = getMagnitudeColor(earthquake.magnitude)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = earthquake.magnitude.toString(),
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Magnitud",
                                            fontSize = 16.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(
                                initialAlpha = 0f,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 200
                                )
                            ) + expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(durationMillis = 500, delayMillis = 200)
                            )
                        ) {
                            Card(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .animateContentSize(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    DetailRow(
                                        icon = Icons.Rounded.LocationOn,
                                        label = "Ubicación",
                                        value = earthquake.place,
                                        colors = colors
                                    )

                                    HorizontalDivider(color = colors.secondaryText.copy(alpha = 0.1f))

                                    DetailRow(
                                        icon = Icons.Rounded.CalendarMonth,
                                        label = "Fecha",
                                        value = earthquake.date,
                                        colors = colors
                                    )

                                    HorizontalDivider(color = colors.secondaryText.copy(alpha = 0.1f))

                                    DetailRow(
                                        icon = Icons.Rounded.Schedule,
                                        label = "Hora",
                                        value = earthquake.hour,
                                        colors = colors
                                    )

                                    HorizontalDivider(color = colors.secondaryText.copy(alpha = 0.1f))

                                    DetailRow(
                                        icon = Icons.Rounded.Straighten,
                                        label = "Profundidad",
                                        value = "${earthquake.depth} km",
                                        colors = colors
                                    )

                                    HorizontalDivider(color = colors.secondaryText.copy(alpha = 0.1f))

                                    DetailRow(
                                        icon = Icons.Rounded.MyLocation,
                                        label = "Coordenadas",
                                        value = "${earthquake.latitude}, ${earthquake.longitude}",
                                        colors = colors
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(
                                initialAlpha = 0f,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 300
                                )
                            ) + expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(durationMillis = 500, delayMillis = 300)
                            )
                        ) {
                            InformacionUtil(earthquake.magnitude, colors)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    colors: DarkModeColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.secondaryText,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = colors.secondaryText
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = colors.textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}