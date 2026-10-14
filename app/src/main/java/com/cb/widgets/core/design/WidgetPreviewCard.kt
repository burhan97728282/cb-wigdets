package com.cb.widgets.core.design

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.widgetkit.WidgetSpec
import com.cb.widgets.core.widgetkit.WidgetStyle

@Composable
fun WidgetPreviewCard(
    spec: WidgetSpec,
    style: WidgetStyle,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSkeu.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val factor by animateFloatAsState(if (pressed) 0.35f else 1f, tween(100), label = "cardPress")

    // Outer padding leaves room for the soft shadow, which lazy containers would otherwise clip.
    Box(modifier.padding(10.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .skeuRaised(radius = 24.dp, depth = 8.dp * factor, pressed = false)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClickLabel = "Open ${spec.name}",
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(10.dp)
        ) {
            Box {
                PreviewStage(spec = spec, style = style, modifier = Modifier.fillMaxWidth())
                SkeuIconButton(
                    icon = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove ${spec.name} from favorites" else "Add ${spec.name} to favorites",
                    onClick = onToggleFavorite,
                    tint = if (isFavorite) palette.accent else null,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = spec.name,
                style = MaterialTheme.typography.titleMedium,
                color = palette.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = spec.category.label + " · " + spec.sizes.joinToString(" · ") { it.grid },
                style = MaterialTheme.typography.labelMedium,
                color = palette.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}
