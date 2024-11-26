package ghost.quake.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.res.Configuration
import androidx.compose.ui.graphics.Shadow
import androidx.hilt.navigation.compose.hiltViewModel
import ghost.quake.presentation.screens.home.components.layouts.LandscapeLayout
import ghost.quake.presentation.screens.home.components.layouts.PortraitLayout
import ghost.quake.presentation.screens.home.components.loading.EarthquakeSkeletonLoading
import ghost.quake.presentation.screens.home.components.utils.ErrorSnackbar
import ghost.quake.presentation.theme.DarkModeColors
import ghost.quake.presentation.theme.getColorsTheme
import ghost.quake.presentation.theme.getMagnitudeColor
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
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
                    isVisible = isVisible
                )
            } else {
                PortraitLayout(
                    state = state,
                    colors = colors,
                    isVisible = isVisible
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