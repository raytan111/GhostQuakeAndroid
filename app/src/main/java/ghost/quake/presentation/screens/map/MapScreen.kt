package ghost.quake.presentation.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state

    if (state.isLoading) {
        CircularProgressIndicator()
        return
    }

    state.error?.let { error ->
        Text("Error: $error")
        return
    }

    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val defaultLocation = LatLng(-33.4489, -70.6693)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            defaultLocation,
            6f
        )
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = hasLocationPermission,
        mapType = MapType.NORMAL
    )

    val mapUiSettings = MapUiSettings(
        zoomControlsEnabled = false
    )

    // Accordion State
    var isAccordionExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isAccordionExpanded) 180f else 0f,
        label = "info-button-rotation"
    )

    // Main Layout
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings
        ) {
            state.earthquakes.forEach { earthquake ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            earthquake.latitude,
                            earthquake.longitude
                        )
                    ),
                    title = earthquake.place,
                    snippet = "Magnitud: ${earthquake.magnitude}, Profundidad: ${earthquake.depth}km",
                    icon = when {
                        earthquake.magnitude < 4.0 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        earthquake.magnitude < 6.0 -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    }
                )
            }
        }

        // Bottom Sheet / Accordion
        AnimatedVisibility(
            visible = isAccordionExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Tamaño fijo del box
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Información de Terremotos",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Hola texto de prueba",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Botón flotante para mostrar/ocultar información
        FloatingActionButton(
            onClick = {
                isAccordionExpanded = !isAccordionExpanded
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Botón movido a la derecha
                .padding(
                    bottom = if (isAccordionExpanded) 210.dp else 16.dp, // Ajuste dinámico según el tamaño del acordeón
                    end = 16.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                modifier = Modifier.rotate(rotationState)
            )
        }

        // Botón flotante para solicitar permisos (si no se tienen)
        if (!hasLocationPermission) {
            FloatingActionButton(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Solicitar Permiso"
                )
            }
        }
    }
}