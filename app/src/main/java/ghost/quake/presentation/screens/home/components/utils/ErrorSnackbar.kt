package ghost.quake.presentation.screens.home.components.utils

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ghost.quake.presentation.theme.DarkModeColors

@Composable
fun ErrorSnackbar(
    error: String,
    colors: DarkModeColors
) {
    Snackbar(
        modifier = Modifier
            .padding(16.dp),
        containerColor = colors.errorBackground,
        contentColor = colors.errorText
    ) {
        Text(text = error)
    }
}