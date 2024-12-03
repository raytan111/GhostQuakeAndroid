package ghost.quake.presentation.screens.main

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ghost.quake.presentation.navigation.NavigationGraph
import ghost.quake.presentation.navigation.Screen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(56.dp)  // Altura de la barra de navegación
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Mapa
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = "Mapa",
                            modifier = Modifier.size(22.dp) // Icono más pequeño
                        )
                    },
                    label = {
                        Text(
                            "Mapa",
                            fontSize = 11.sp // Texto más pequeño
                        )
                    },
                    selected = currentRoute == Screen.Map.route,
                    onClick = {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Inicio
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Inicio",
                            modifier = Modifier.size(22.dp) // Icono más pequeño
                        )
                    },
                    label = {
                        Text(
                            "Inicio",
                            fontSize = 11.sp // Texto más pequeño
                        )
                    },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Ajustes
                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            modifier = Modifier.size(22.dp) // Icono más pequeño
                        )
                    },
                    label = {
                        Text(
                            "Ajustes",
                            fontSize = 11.sp // Texto más pequeño
                        )
                    },
                    selected = currentRoute == Screen.Settings.route,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
