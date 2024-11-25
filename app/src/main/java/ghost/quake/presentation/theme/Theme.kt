package ghost.quake.presentation.theme

import android.app.Activity
import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightThemeColors.Background,
    surface = LightThemeColors.CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = LightThemeColors.PrimaryText,
    onSurface = LightThemeColors.PrimaryText
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkThemeColors.Background,
    surface = DarkThemeColors.CardBackground,
    onPrimary = DarkThemeColors.PrimaryText,
    onSecondary = DarkThemeColors.PrimaryText,
    onBackground = DarkThemeColors.PrimaryText,
    onSurface = DarkThemeColors.PrimaryText
)

@SuppressLint("NewApi")
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun getColorsTheme(): DarkModeColors {
    val isDarkMode = isSystemInDarkTheme()
    return DarkModeColors(
        coloricon = if (isDarkMode) DarkThemeColors.IconTint else LightThemeColors.IconTint,
        backgroundColor = if (isDarkMode) DarkThemeColors.Background else LightThemeColors.Background,
        textColor = if (isDarkMode) DarkThemeColors.PrimaryText else LightThemeColors.PrimaryText,
        cardBackground = if (isDarkMode) DarkThemeColors.CardBackground else LightThemeColors.CardBackground,
        secondaryText = if (isDarkMode) DarkThemeColors.SecondaryText else LightThemeColors.SecondaryText,
        errorBackground = if (isDarkMode) DarkThemeColors.ErrorBackground else LightThemeColors.ErrorBackground,
        errorText = if (isDarkMode) DarkThemeColors.ErrorText else LightThemeColors.ErrorText,
        successColor = if (isDarkMode) DarkThemeColors.SuccessColor else LightThemeColors.SuccessColor
    )
}