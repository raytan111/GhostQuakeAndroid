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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ghost.quake.presentation.theme.getMagnitudeColor
import ghost.quake.presentation.theme.LightThemeColors

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
                    icon = getMarkerIcon(earthquake.magnitude)
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
                    .height(250.dp)
                    .background(LightThemeColors.CardBackground)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Información de Sismos",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightThemeColors.PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Earthquake Details
                    state.earthquakes.take(3).forEach { earthquake ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(getMagnitudeColor(earthquake.magnitude))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = earthquake.place,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LightThemeColors.PrimaryText
                                )
                                Text(
                                    text = "Magnitud: ${earthquake.magnitude}, Profundidad: ${earthquake.depth}km",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightThemeColors.SecondaryText
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Floating Action Button for Info
        FloatingActionButton(
            onClick = {
                isAccordionExpanded = !isAccordionExpanded
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = if (isAccordionExpanded) 260.dp else 16.dp,
                    end = 16.dp
                )
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                modifier = Modifier.rotate(rotationState)
            )
        }

        // Floating Action Button for Location Permissions
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

// Helper function to get marker icon based on magnitude
private fun getMarkerIcon(magnitude: Double): BitmapDescriptor {
    // Convert the color to its hue value for BitmapDescriptorFactory
    val hue = when (val color = getMagnitudeColor(magnitude)) {
        Color(0xFF388E3C) -> BitmapDescriptorFactory.HUE_GREEN     // Low
        Color(0xFFF57F17) -> BitmapDescriptorFactory.HUE_ORANGE    // Medium
        Color(0xFFFF5722) -> BitmapDescriptorFactory.HUE_ORANGE    // Medium-High
        Color(0xFFD32F2F) -> BitmapDescriptorFactory.HUE_RED       // High
        Color(0xFFB71C1C) -> BitmapDescriptorFactory.HUE_RED       // Severe
        else -> BitmapDescriptorFactory.HUE_GREEN
    }
    return BitmapDescriptorFactory.defaultMarker(hue)
}