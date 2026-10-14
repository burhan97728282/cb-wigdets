package com.cb.widgets.core.design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SimCard
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.cb.widgets.core.widgetkit.WidgetCategory

fun WidgetCategory.icon(): ImageVector = when (this) {
    WidgetCategory.CLOCK -> Icons.Outlined.Schedule
    WidgetCategory.WEATHER -> Icons.Outlined.WbSunny
    WidgetCategory.BATTERY -> Icons.Outlined.BatteryFull
    WidgetCategory.NETWORK -> Icons.Outlined.NetworkCheck
    WidgetCategory.SIM -> Icons.Outlined.SimCard
    WidgetCategory.STORAGE -> Icons.Outlined.Storage
    WidgetCategory.MEMORY -> Icons.Outlined.Memory
    WidgetCategory.CALENDAR -> Icons.Outlined.CalendarMonth
    WidgetCategory.PRODUCTIVITY -> Icons.Outlined.TaskAlt
    WidgetCategory.DEVICE -> Icons.Outlined.PhoneAndroid
    WidgetCategory.SYSTEM -> Icons.Outlined.Dashboard
}
