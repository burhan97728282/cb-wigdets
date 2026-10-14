package com.cb.widgets.core.design

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SkeuSurface(
    modifier: Modifier = Modifier,
    radius: Dp = SkeuDims.CardRadius,
    depth: Dp = SkeuDims.DepthMedium,
    gloss: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .skeuRaised(radius = radius, depth = depth, gloss = gloss)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun RecessedWell(
    modifier: Modifier = Modifier,
    radius: Dp = SkeuDims.ControlRadius,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .skeuRecessed(radius = radius)
            .padding(contentPadding),
        content = content
    )
}

/** Dark LCD-style recessed display used for digital read-outs. */
@Composable
fun LcdDisplay(
    modifier: Modifier = Modifier,
    radius: Dp = 14.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val palette = LocalSkeu.current
    Box(
        modifier = modifier
            .skeuRecessed(radius = radius, depth = 6.dp, colors = palette.lcdWell)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SkeuDims.ScreenPadding, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = LocalSkeu.current.text,
            modifier = Modifier.semantics { heading() }
        )
        trailing?.invoke()
    }
}

/** Small recessed label, e.g. widget size or status. */
@Composable
fun SkeuTag(text: String, modifier: Modifier = Modifier, led: Boolean = false) {
    val palette = LocalSkeu.current
    Row(
        modifier = modifier
            .skeuRecessed(radius = 12.dp, depth = 3.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (led) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.accent)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = palette.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SkeuDivider(modifier: Modifier = Modifier) {
    val palette = LocalSkeu.current
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(androidx.compose.ui.graphics.Color(palette.raised.lowlight).copy(alpha = 0.35f))
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(androidx.compose.ui.graphics.Color(palette.raised.highlight).copy(alpha = 0.6f))
        )
    }
}
