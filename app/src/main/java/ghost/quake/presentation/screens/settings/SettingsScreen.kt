package ghost.quake.presentation.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import ghost.quake.util.*

@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(initialAlpha = 0f) +
                    slideInVertically(initialOffsetY = { it / 3 }),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Ajustes",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )

                SettingsSection(
                    title = "Permisos de Ubicación",
                    icon = Icons.Default.LocationOn
                ) {
                    UbicationContent()
                }

                SettingsSection(
                    title = "Notificaciones",
                    icon = Icons.Default.Notifications
                ) {
                    NotificationsContent()
                }

                SettingsSection(
                    title = "Apariencia",
                    icon = Icons.Default.Settings
                ) {
                    AppearanceContent()
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Developed by Ghost\nVersion 2.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun UbicationContent() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (!isGranted) {
                showPermissionDialog = true
            }
        }
    )

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de ubicación requerido") },
            text = { Text("Para mostrar sismos cercanos, necesitamos acceder a tu ubicación. Por favor, habilita el permiso en la configuración.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Ir a Ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    SettingItem(
        title = "Permitir Ubicación",
        subtitle = "Necesitamos tu ubicación para mostrarte sismos cercanos",
        checked = hasLocationPermission,
        onCheckedChange = { enabled ->
            if (enabled) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                showPermissionDialog = true
            }
        }
    )
}

@Composable
private fun NotificationsContent() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    val notificationManager = remember { NotificationManagerHelper(context) }

    // Estado para los permisos de notificación
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                // Mostrar notificación de confirmación
                notificationManager.showNotification(
                    "Notificaciones Activadas",
                    "Recibirás alertas sobre actividad sísmica"
                )
            } else {
                showPermissionDialog = true
            }
        }
    )

    // Diálogo para cuando se deniegan los permisos
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de notificaciones requerido") },
            text = { Text("Para recibir alertas sobre actividad sísmica, necesitamos tu permiso. Por favor, habilita las notificaciones en la configuración.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                        } else {
                            // Para versiones anteriores a Android 8.0
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Ir a Ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    SettingItem(
        title = "Activar Notificaciones",
        subtitle = "Recibe alertas sobre actividad sísmica",
        checked = hasNotificationPermission,
        onCheckedChange = { enabled ->
            if (enabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // En versiones anteriores a Android 13, no se necesita permiso explícito
                    hasNotificationPermission = true
                    notificationManager.showNotification(
                        "Notificaciones Activadas",
                        "Recibirás alertas sobre actividad sísmica"
                    )
                }
            } else {
                showPermissionDialog = true
            }
        }
    )
}

@Composable
private fun AppearanceContent() {
    var darkModeEnabled by remember { mutableStateOf(false) }
    SettingItem(
        title = "Modo Oscuro",
        subtitle = "Cambia el tema de la aplicación",
        checked = darkModeEnabled,
        onCheckedChange = { darkModeEnabled = it }
    )
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}