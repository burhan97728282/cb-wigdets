package com.cb.widgets.core.design

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import kotlin.math.roundToInt

@Composable
fun SkeuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val palette = LocalSkeu.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val factor by animateFloatAsState(if (pressed) 0.25f else 1f, tween(90), label = "buttonPress")
    val face = if (primary) palette.primaryRaised else palette.raised
    val ink = if (primary) palette.onAccent else palette.text

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.55f)
            .skeuRaised(
                radius = SkeuDims.ButtonRadius,
                depth = SkeuDims.DepthMedium * factor,
                gloss = true,
                pressed = pressed,
                colors = face
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .heightIn(min = 52.dp)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = ink, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SkeuIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color? = null,
    enabled: Boolean = true
) {
    val palette = LocalSkeu.current
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val factor by animateFloatAsState(if (pressed) 0.25f else 1f, tween(90), label = "iconPress")
    val inner = size - 8.dp
    Box(
        modifier = modifier
            .size(size)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(inner)
                .skeuRaised(radius = inner / 2, depth = 4.dp * factor, gloss = true, pressed = pressed),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint ?: palette.text,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun SkeuToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val palette = LocalSkeu.current
    val trackWidth = 58.dp
    val trackHeight = 34.dp
    val knob = 28.dp
    val knobOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - knob - 3.dp else 3.dp,
        animationSpec = tween(160),
        label = "toggleKnob"
    )
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.55f)
            .size(width = 64.dp, height = 48.dp)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(trackWidth, trackHeight)
                .skeuRecessed(radius = trackHeight / 2, depth = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(if (checked) palette.accent.copy(alpha = 0.40f) else Color.Transparent)
            )
            Box(
                Modifier
                    .offset(x = knobOffset)
                    .size(knob)
                    .skeuRaised(radius = knob / 2, depth = 4.dp, gloss = true)
            )
        }
    }
}

@Composable
fun SkeuSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true
) {
    val palette = LocalSkeu.current
    val density = LocalDensity.current
    var widthPx by remember { mutableIntStateOf(0) }
    val knobDp = 30.dp
    val knobPx = with(density) { knobDp.toPx() }
    val currentChange by rememberUpdatedState(onValueChange)
    val currentRange by rememberUpdatedState(valueRange)
    val span = valueRange.endInclusive - valueRange.start
    val fraction = if (span == 0f) 0f else ((value - valueRange.start) / span).coerceIn(0f, 1f)

    fun update(x: Float) {
        val usable = (widthPx - knobPx).coerceAtLeast(1f)
        val f = ((x - knobPx / 2f) / usable).coerceIn(0f, 1f)
        val r = currentRange
        currentChange(r.start + f * (r.endInclusive - r.start))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(if (enabled) 1f else 0.55f)
            .onSizeChanged { widthPx = it.width }
            .pointerInput(enabled) {
                if (enabled) detectTapGestures { offset -> update(offset.x) }
            }
            .pointerInput(enabled) {
                if (enabled) detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    update(change.position.x)
                }
            }
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange)
                setProgress { target ->
                    onValueChange(target.coerceIn(valueRange.start, valueRange.endInclusive))
                    true
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .skeuRecessed(radius = 7.dp, depth = 4.dp)
        )
        val fillPx = knobPx / 2f + fraction * (widthPx - knobPx).coerceAtLeast(0f)
        Box(
            Modifier
                .width(with(density) { fillPx.toDp() })
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(palette.accent.copy(alpha = 0.65f))
        )
        Box(
            Modifier
                .offset { IntOffset((fraction * (widthPx - knobPx).coerceAtLeast(0f)).roundToInt(), 0) }
                .size(knobDp)
                .skeuRaised(radius = knobDp / 2, depth = 5.dp, gloss = true)
        )
    }
}

@Composable
fun SkeuSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSkeu.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .skeuRecessed(radius = 20.dp, depth = 5.dp)
            .padding(4.dp)
            .selectableGroup()
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val source = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .then(
                        if (selected) Modifier.skeuRaised(radius = 16.dp, depth = 3.dp, gloss = true)
                        else Modifier
                    )
                    .selectable(
                        selected = selected,
                        interactionSource = source,
                        indication = null,
                        role = Role.Tab,
                        onClick = { onSelect(index) }
                    )
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) palette.text else palette.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SkeuChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSkeu.current
    val source = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .heightIn(min = 44.dp)
            .then(
                if (selected) Modifier.skeuRecessed(radius = 16.dp, depth = 4.dp)
                else Modifier.skeuRaised(radius = 16.dp, depth = 3.dp, gloss = true)
            )
            .selectable(
                selected = selected,
                interactionSource = source,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(palette.accent)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) palette.text else palette.textSecondary,
            maxLines = 1
        )
    }
}

@Composable
fun ColorWell(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    description: String,
    modifier: Modifier = Modifier
) {
    val ink = if (color.red * 0.299f + color.green * 0.587f + color.blue * 0.114f > 0.6f) Color.Black else Color.White
    Box(
        modifier = modifier
            .size(48.dp)
            .semantics { this.contentDescription = description }
            .selectable(
                selected = selected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(40.dp)
                .skeuRaised(radius = 20.dp, depth = if (selected) 1.dp else 4.dp, pressed = selected),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = ink, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SkeuSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalSkeu.current
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = palette.text),
        cursorBrush = SolidColor(palette.accent),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .skeuRecessed(radius = 22.dp, depth = 5.dp),
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(start = 18.dp, end = if (value.isEmpty()) 18.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.textSecondary)
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = palette.textSecondary)
                    }
                    inner()
                }
                if (value.isNotEmpty()) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .semantics { this.contentDescription = "Clear search" }
                            .clickable(role = Role.Button, onClick = { onValueChange("") }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = palette.textSecondary)
                    }
                }
            }
        }
    )
}
