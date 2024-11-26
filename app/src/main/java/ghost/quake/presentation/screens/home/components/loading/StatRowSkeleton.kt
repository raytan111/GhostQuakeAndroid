package ghost.quake.presentation.screens.home.components.loading

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ghost.quake.presentation.screens.home.components.utils.shimmerEffect

@Composable
fun StatRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
                .shimmerEffect()
                .clip(RoundedCornerShape(4.dp))
        )
    }
}