package ghost.quake.presentation.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Home : Screen("home")
    object Settings : Screen("settings")
}