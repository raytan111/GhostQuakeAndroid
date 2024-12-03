package ghost.quake.presentation.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import ghost.quake.domain.model.Earthquake
import ghost.quake.domain.repository.EarthquakeRepository
import ghost.quake.presentation.theme.DarkModeColors
import ghost.quake.presentation.navigation.Screen
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

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
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = informacion,
                fontSize = 16.sp,
                color = colors.textColor
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

    LaunchedEffect(Unit) {
        viewModel.getEarthquake(id)
    }

    state.earthquake?.let { earthquake ->
        if (showImageDialog) {
            Dialog(onDismissRequest = { showImageDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                                .height(400.dp),
                            contentScale = ContentScale.Fit
                        )
                        IconButton(
                            onClick = { showImageDialog = false },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Cerrar",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles del Sismo") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Regresar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.cardBackground,
                        titleContentColor = colors.textColor
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
                AsyncImage(
                    model = earthquake.image,
                    contentDescription = "Mapa del sismo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showImageDialog = true },
                    contentScale = ContentScale.Crop
                )

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Magnitud ${earthquake.magnitude}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textColor
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailRow(
                            icon = Icons.Rounded.LocationOn,
                            label = "Ubicación",
                            value = earthquake.place,
                            colors = colors
                        )

                        DetailRow(
                            icon = Icons.Rounded.CalendarMonth,
                            label = "Fecha",
                            value = earthquake.date,
                            colors = colors
                        )

                        DetailRow(
                            icon = Icons.Rounded.Schedule,
                            label = "Hora",
                            value = earthquake.hour,
                            colors = colors
                        )

                        DetailRow(
                            icon = Icons.Rounded.Straighten,
                            label = "Profundidad",
                            value = "${earthquake.depth} km",
                            colors = colors
                        )

                        DetailRow(
                            icon = Icons.Rounded.MyLocation,
                            label = "Coordenadas",
                            value = "${earthquake.latitude}, ${earthquake.longitude}",
                            colors = colors
                        )
                    }
                }

                InformacionUtil(earthquake.magnitude, colors)
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.secondaryText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = colors.secondaryText
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = colors.textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}