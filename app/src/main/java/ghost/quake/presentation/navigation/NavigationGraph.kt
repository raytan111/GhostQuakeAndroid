package ghost.quake.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ghost.quake.presentation.screens.details.EarthquakeDetailScreen
import ghost.quake.presentation.screens.home.HomeScreen
import ghost.quake.presentation.screens.map.MapScreen
import ghost.quake.presentation.screens.settings.SettingsScreen
import ghost.quake.presentation.theme.getColorsTheme
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut


@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.Home.route,
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) }
        ) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Settings.route) { SettingsScreen() }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("earthquakeId") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) { backStackEntry ->
            val earthquakeId = backStackEntry.arguments?.getString("earthquakeId") ?: ""
            EarthquakeDetailScreen(
                id = earthquakeId,
                colors = getColorsTheme(),
                navController = navController
            )
        }
    }
}