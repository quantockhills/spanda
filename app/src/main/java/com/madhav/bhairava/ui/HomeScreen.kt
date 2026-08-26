package com.madhav.bhairava.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.notify.Meditation
import com.madhav.bhairava.notify.ReminderScheduler
import com.madhav.bhairava.notify.buildPool
import com.madhav.bhairava.ui.theme.CardBg
import com.madhav.bhairava.ui.theme.Crimson
import com.madhav.bhairava.ui.theme.DevanagariFont
import com.madhav.bhairava.ui.theme.Gold
import com.madhav.bhairava.ui.theme.GoldLight
import com.madhav.bhairava.ui.theme.Ink
import com.madhav.bhairava.ui.theme.Line
import com.madhav.bhairava.ui.theme.MutedInk
import com.madhav.bhairava.ui.theme.SerifFont
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSivabodha: () -> Unit,
    onOpenAmrta: () -> Unit,
    onOpenRoute: (String) -> Unit
) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val pool = remember(lib) { buildPool(lib) }
    var todayIdx by remember { mutableIntStateOf(if (pool.isEmpty()) 0 else Random.nextInt(pool.size)) }
    val cover = rememberAssetImage("cover.jpg")

    var reminderEnabled by remember { mutableStateOf(ReminderScheduler.isEnabled(context)) }
    var reminderTime by remember { mutableStateOf(ReminderScheduler.currentTime(context)) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            ReminderScheduler.schedule(context, reminderTime.first, reminderTime.second)
            reminderEnabled = true
        }
    }

    fun toggleReminder(on: Boolean) {
        if (on) {
            val needPermission = Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            if (needPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ReminderScheduler.schedule(context, reminderTime.first, reminderTime.second)
                reminderEnabled = true
            }
        } else {
            ReminderScheduler.cancel(context)
            reminderEnabled = false
        }
    }

    if (showTimeDialog) {
        val tp = rememberTimePickerState(
            initialHour = reminderTime.first,
            initialMinute = reminderTime.second,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Daily meditation", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text("A random meditation from both texts, every day.", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    TimePicker(state = tp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    reminderTime = tp.hour to tp.minute
                    showTimeDialog = false
                    if (reminderEnabled) ReminderScheduler.schedule(context, tp.hour, tp.minute)
                }) { Text("Save", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showTimeDialog = false }) { Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ---- header ----
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color(0xFF241C12))
            ) {
                if (cover != null) {
                    Image(
                        bitmap = cover,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(235.dp)
                            .clip(RoundedCornerShape(0.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x221E150D), Color(0xE6241C12))
                            )
                        )
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        "Bhairava Bodha",
                        fontFamily = SerifFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color(0xFFFAF8F2)
                    )
                    Text(
                        "शिवबोधविंशिका · अमृतादिस्तवः",
                        fontFamily = DevanagariFont,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        "Śivabodhaviṃśikā · Amṛtādistavaḥ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCBBFA8)
                    )
                }
            }
        }

        // ---- today's meditation ----
        if (pool.isNotEmpty()) {
            item {
                val m: Meditation = pool[todayIdx % pool.size]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("TODAY'S MEDITATION", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { todayIdx = Random.nextInt(pool.size) }) {
                                Icon(Icons.Outlined.Refresh, contentDescription = "Another", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(m.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            m.sanskrit,
                            fontFamily = DevanagariFont,
                            fontSize = 17.sp,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            m.body,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { onOpenRoute(m.route) }) {
                            Text("Open", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ---- reminder ----
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Daily reminder", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "A random meditation notification",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "${reminderTime.first.toString().padStart(2, '0')}:${reminderTime.second.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(0.dp))
                                .clickableNoRipple { showTimeDialog = true }
                                .padding(top = 2.dp, bottom = 2.dp, end = 4.dp)
                        )
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = ::toggleReminder,
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        // ---- the two texts ----
        item {
            BookCard(
                devTitle = "शिवबोधविंशिका",
                title = "Śivabodhaviṃśikā",
                subtitle = "Twenty contemplations on the realization of Śiva — each stanza a bhāvanā, in order, with word-by-word glosses.",
                thumb = null,
                onClick = onOpenSivabodha
            )
        }
        item {
            BookCard(
                devTitle = "अमृतादिस्तवः",
                title = "Amṛtādistavaḥ",
                subtitle = "Abhinavagupta's hidden hymn — the fifty Bhairavas of the alphabet, each with its maṇḍala and translation.",
                thumb = rememberAssetImage("mandala_01.jpg"),
                onClick = onOpenAmrta
            )
        }

        // ---- attribution ----
        item {
            Text(
                "Text by Śaivācārya Abhinavagupta · Translated by Śaivācārya Sthaneshwar Timalsina\nVimarsha Foundation, San Diego, 2021 — for personal study",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 16.dp)
            )
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)
