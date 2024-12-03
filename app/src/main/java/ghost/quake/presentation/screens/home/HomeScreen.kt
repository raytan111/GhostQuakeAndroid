package ghost.quake.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ghost.quake.presentation.screens.home.components.layouts.LandscapeLayout
import ghost.quake.presentation.screens.home.components.layouts.PortraitLayout
import ghost.quake.presentation.screens.home.components.loading.EarthquakeSkeletonLoading
import ghost.quake.presentation.screens.home.components.utils.ErrorSnackbar
import ghost.quake.presentation.theme.getColorsTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController // Nuevo parámetro
) {
    val state = viewModel.state.value
    val colors = getColorsTheme()
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.earthquakes, state.isLoading) {
        isVisible = state.earthquakes.isNotEmpty() && !state.isLoading
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = {
            scope.launch {
                viewModel.refreshData()
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(colors.backgroundColor)
    ) {
        if (state.isLoading) {
            EarthquakeSkeletonLoading(colors = colors)
        } else {
            if (isLandscape) {
                LandscapeLayout(
                    state = state,
                    colors = colors,
                    isVisible = isVisible,
                    onEarthquakeClick = { earthquakeId ->
                        navController.navigate("detail/$earthquakeId")
                    }
                )
            } else {
                PortraitLayout(
                    state = state,
                    colors = colors,
                    isVisible = isVisible,
                    onEarthquakeClick = { earthquakeId ->
                        navController.navigate("detail/$earthquakeId")
                    }
                )
            }
        }

        // Error Snackbar
        if (state.error.isNotBlank()) {
            ErrorSnackbar(
                error = state.error,
                colors = colors
            )
        }

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = colors.cardBackground,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}