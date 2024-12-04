package ghost.quake.presentation.screens.home.components.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ghost.quake.domain.model.Earthquake
import ghost.quake.presentation.theme.DarkModeColors

@Composable
fun QuickStats(
    earthquakes: List<Earthquake>,
    colors: DarkModeColors
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Estadísticas últimas 24h",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val avgMagnitude = earthquakes.map { it.magnitude }.average()
            val strongestQuake = earthquakes.maxByOrNull { it.magnitude }
            val avgDepth = earthquakes.map { it.depth }.average()

            StatRow(
                icon = Icons.Rounded.Straighten,
                title = "Magnitud promedio",
                value = "%.1f".format(avgMagnitude),
                colors = colors
            )
            StatRow(
                icon = Icons.Rounded.CalendarMonth,
                title = "Sismo más fuerte",
                value = "%.1f".format(strongestQuake?.magnitude ?: 0.0),
                colors = colors
            )
            StatRow(
                icon = Icons.Rounded.LocationOn,
                title = "Profundidad promedio",
                value = "${avgDepth.toInt()} km",
                colors = colors
            )
        }
    }
}