package ghost.quake.presentation.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import ghost.quake.presentation.screens.home.components.utils.QuickStats
import ghost.quake.presentation.theme.getColorsTheme


@Composable
fun MapScreenSkeleton() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Map skeleton background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Animated loading markers
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .shimmerEffect()
                    .offset(
                        x = (index * 50).dp,
                        y = (index * 30).dp
                    )
            )
        }

        // FAB skeleton
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .shimmerEffect()
        )

        // Stats panel skeleton when expanded
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Column {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .padding(vertical = 4.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier = composed {
    var value by remember { mutableFloatStateOf(0f) }
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = shimmer.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    value = translateAnim.value

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Gray.copy(alpha = 0.2f),
                Color.Gray.copy(alpha = 0.4f),
                Color.Gray.copy(alpha = 0.2f),
            ),
            start = Offset(-value, 0f),
            end = Offset(value, 0f),
        )
    )
}

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state
    val colors = getColorsTheme()

    if (state.isLoading) {
        MapScreenSkeleton()
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
        position = CameraPosition.fromLatLngZoom(defaultLocation, 6f)
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = hasLocationPermission,
        mapType = MapType.NORMAL
    )

    val mapUiSettings = MapUiSettings(
        zoomControlsEnabled = false
    )

    var isAccordionExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isAccordionExpanded) 180f else 0f,
        label = "info-button-rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = cameraPositionState,
            uiSettings = mapUiSettings
        ) {
            state.earthquakes.forEach { earthquake ->
                Marker(
                    state = MarkerState(position = LatLng(earthquake.latitude, earthquake.longitude)),
                    title = earthquake.place,
                    snippet = "Magnitud: ${earthquake.magnitude}, Profundidad: ${earthquake.depth}km",
                    icon = getMarkerIcon(earthquake.magnitude)
                )
            }
        }

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
                    .background(colors.cardBackground)
                    .padding(16.dp)
            ) {
                QuickStats(
                    earthquakes = state.earthquakes,
                    colors = colors
                )
            }
        }

        FloatingActionButton(
            onClick = { isAccordionExpanded = !isAccordionExpanded },
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

        if (!hasLocationPermission) {
            FloatingActionButton(
                onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
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

private fun getMarkerIcon(magnitude: Double): BitmapDescriptor {
    return when {
        magnitude >= 7.0 -> BitmapDescriptorFactory.defaultMarker(340.0f)  // Severe - Darker Red
        magnitude >= 6.0 -> BitmapDescriptorFactory.defaultMarker(25.0f)   // High - Darker Orange-Red
        magnitude >= 5.0 -> BitmapDescriptorFactory.defaultMarker(32.0f)   // MediumHigh - Darker Orange
        magnitude >= 4.0 -> BitmapDescriptorFactory.defaultMarker(45.0f)   // Medium - Darker Yellow
        else -> BitmapDescriptorFactory.defaultMarker(85.0f)               // Low - Dark Green
    }
}