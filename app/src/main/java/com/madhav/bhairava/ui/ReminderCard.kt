package com.madhav.bhairava.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.madhav.bhairava.notify.Meditation
import com.madhav.bhairava.notify.ReminderScheduler
import com.madhav.bhairava.notify.ReminderSlot

private fun hm(h: Int, m: Int) = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"

/** The daily-reminder card on the home screen: a list of times, each with a chosen or random verse. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(pool: List<Meditation>) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(ReminderScheduler.isEnabled(context)) }
    var slots by remember { mutableStateOf(ReminderScheduler.slots(context)) }

    // Editor state: editorOpen with editorSlot == null means "add a new time".
    var editorOpen by remember { mutableStateOf(false) }
    var editorSlot by remember { mutableStateOf<ReminderSlot?>(null) }
    var draftRoute by remember { mutableStateOf("") }
    var pickerOpen by remember { mutableStateOf(false) }

    fun refresh() {
        enabled = ReminderScheduler.isEnabled(context)
        slots = ReminderScheduler.slots(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ReminderScheduler.enable(context)
            refresh()
        }
    }

    fun toggle(on: Boolean) {
        if (on) {
            val needPermission = Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            if (needPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ReminderScheduler.enable(context)
                refresh()
            }
        } else {
            ReminderScheduler.disable(context)
            refresh()
        }
    }

    fun verseTitle(route: String): String =
        if (route.isEmpty()) "Random verse each day"
        else pool.firstOrNull { it.route == route }?.title ?: "Random verse each day"

    if (editorOpen) {
        val initial = editorSlot
        val tp = rememberTimePickerState(
            initialHour = initial?.hour ?: ReminderScheduler.DEFAULT_HOUR,
            initialMinute = initial?.minute ?: ReminderScheduler.DEFAULT_MINUTE,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { editorOpen = false },
            title = { Text("Reminder time", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    TimePicker(state = tp)
                    Spacer(Modifier.height(12.dp))
                    Text("Verse", style = MaterialTheme.typography.labelMedium)
                    TextButton(onClick = { pickerOpen = true }) {
                        Text(verseTitle(draftRoute), color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (initial == null) {
                        ReminderScheduler.addSlot(context, tp.hour, tp.minute, draftRoute)
                    } else {
                        ReminderScheduler.updateSlot(context, initial.copy(hour = tp.hour, minute = tp.minute, route = draftRoute))
                    }
                    editorOpen = false
                    refresh()
                }) { Text("Save", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { editorOpen = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (pickerOpen) {
        AlertDialog(
            onDismissRequest = { pickerOpen = false },
            title = { Text("Choose a verse", style = MaterialTheme.typography.titleMedium) },
            text = {
                LazyColumn(Modifier.heightIn(max = 420.dp)) {
                    item {
                        VerseChoice(
                            label = "Random verse each day",
                            selected = draftRoute.isEmpty(),
                            onClick = { draftRoute = ""; pickerOpen = false }
                        )
                    }
                    items(pool) { m ->
                        VerseChoice(
                            label = m.title,
                            selected = draftRoute == m.route,
                            onClick = { draftRoute = m.route; pickerOpen = false }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { pickerOpen = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Daily reminder", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Choose the times, and a verse for each or random",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = { toggle(it) },
                    colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                )
            }

            slots.forEach { slot ->
                Row(
                    Modifier.fillMaxWidth().padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .clickable {
                                editorSlot = slot
                                draftRoute = slot.route
                                editorOpen = true
                            }
                    ) {
                        Text(
                            hm(slot.hour, slot.minute),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            verseTitle(slot.route),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                    TextButton(onClick = {
                        ReminderScheduler.removeSlot(context, slot.id)
                        refresh()
                    }) { Text("Remove", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }

            if (slots.size < ReminderScheduler.MAX_SLOTS) {
                TextButton(
                    onClick = {
                        editorSlot = null
                        draftRoute = ""
                        editorOpen = true
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) { Text("+ Add a time", color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}

@Composable
private fun VerseChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
