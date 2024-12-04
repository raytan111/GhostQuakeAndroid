package ghost.quake.presentation.screens.home.components.loading

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ghost.quake.presentation.screens.home.components.utils.shimmerEffect
import ghost.quake.presentation.theme.DarkModeColors

@Composable
fun EarthquakeSkeletonLoading(colors: DarkModeColors) {
    LazyColumn {
        // Último Sismo skeleton
        item {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    .width(150.dp)
                    .height(32.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(8.dp))
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shimmerEffect()
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(24.dp)
                                    .shimmerEffect()
                                    .clip(RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(16.dp)
                                    .shimmerEffect()
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .shimmerEffect()
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // Título lista skeleton
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .width(250.dp)
                    .height(32.dp)
                    .shimmerEffect()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Lista de sismos skeleton
        items(6) {
            EarthquakeItemSkeleton(colors = colors)
        }
    }
}
