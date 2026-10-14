package com.cb.widgets.feature.library

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.WidgetPreviewCard
import com.cb.widgets.core.design.icon
import com.cb.widgets.core.design.skeuRaised
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.core.widgetkit.WidgetCategory
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.core.widgetkit.WidgetSpec
import com.cb.widgets.core.widgetkit.defaultStyle

@Composable
fun WidgetCardRow(
    specs: List<WidgetSpec>,
    favorites: Set<String>,
    defaultMaterial: WidgetMaterial,
    cardWidth: Dp,
    onOpen: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 14.dp)) {
        items(specs, key = { it.id }) { spec ->
            WidgetPreviewCard(
                spec = spec,
                style = spec.defaultStyle(defaultMaterial),
                isFavorite = spec.id in favorites,
                onClick = { onOpen(spec.id) },
                onToggleFavorite = { onToggleFavorite(spec.id) },
                modifier = Modifier.width(cardWidth)
            )
        }
    }
}

@Composable
fun CategoryTile(
    category: WidgetCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSkeu.current
    val source = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .padding(10.dp)
            .width(104.dp)
            .skeuRaised(radius = 22.dp, depth = 7.dp, gloss = true)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(category.icon(), contentDescription = null, tint = palette.text, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(category.label, style = MaterialTheme.typography.labelLarge, color = palette.text, maxLines = 1)
        Text(
            "${WidgetCatalog.countIn(category)} widgets",
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
            maxLines = 1
        )
    }
}
