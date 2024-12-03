package ghost.quake.presentation.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

object LightThemeColors {
    val Background = Color(0xFFE9ECEF)
    val CardBackground = Color.White
    val PrimaryText = Color(0xFF2C3E50)
    val SecondaryText = Color(0xFF6C7C8C)
    val IconTint = Color(0xFF311B92)
    val ErrorBackground = Color(0xFFFFDAD6)
    val ErrorText = Color(0xFF410002)
    val SuccessColor = Color(0xFF388E3C)
}

object DarkThemeColors {
    val Background = Color(0xFF121212)
    val CardBackground = Color(0xFF1E1E1E)
    val PrimaryText = Color.White
    val SecondaryText = Color(0xFFAAAAAA)
    val IconTint = Color(0xFF9FA8DA)
    val ErrorBackground = Color(0xFF470000)
    val ErrorText = Color(0xFFFFB4AB)
    val SuccessColor = Color(0xFF81C784)
}

object EarthquakeColors {
    val Low = Color(0xFF4CAF50)      // Verde para sismos bajos
    val Medium = Color(0xFFFBC02D)   // Amarillo para sismos medios
    val MediumHigh = Color(0xFFFFA000) // Naranja para sismos medio-altos
    val High = Color(0xFFF57F17)     // Naranja oscuro para sismos altos
    val Severe = Color(0xFFD32F2F)   // Rojo oscuro para sismos severos
}

data class DarkModeColors(
    val coloricon: Color,
    val backgroundColor: Color,
    val textColor: Color,
    val cardBackground: Color,
    val secondaryText: Color,
    // Nuevos campos opcionales con valores por defecto
    val errorBackground: Color = DarkThemeColors.ErrorBackground,
    val errorText: Color = DarkThemeColors.ErrorText,
    val successColor: Color = DarkThemeColors.SuccessColor
)

fun getMagnitudeColor(magnitude: Double): Color {
    return when {
        magnitude >= 7.0 -> EarthquakeColors.Severe
        magnitude >= 6.0 -> EarthquakeColors.High
        magnitude >= 5.0 -> EarthquakeColors.MediumHigh
        magnitude >= 4.0 -> EarthquakeColors.Medium
        else -> EarthquakeColors.Low
    }
}