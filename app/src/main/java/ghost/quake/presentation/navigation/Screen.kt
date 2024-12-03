package ghost.quake.presentation.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Details : Screen("detail/{earthquakeId}") {
        fun createRoute(earthquakeId: String) = "detail/$earthquakeId"
    }
}