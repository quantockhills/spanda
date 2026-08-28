package com.madhav.bhairava.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsSystemDaydream
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.sync.OneDriveSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onThemeChanged: () -> Unit) {
    val context = LocalContext.current
    var currentMode by remember { mutableStateOf(AppSettings.getThemeMode(context)) }

    val themeOptions = listOf(
        Triple("system", "System default", Icons.Outlined.SettingsSystemDaydream),
        Triple("light", "Light", Icons.Outlined.LightMode),
        Triple("dark", "Dark", Icons.Outlined.DarkMode)
    )

    // ----- OneDrive sync state -----
    val scope = rememberCoroutineScope()
    var signedIn by remember { mutableStateOf(OneDriveSync.isSignedIn(context)) }
    var syncBusy by remember { mutableStateOf(false) }
    var syncStatus by remember { mutableStateOf("") }
    var pendingCode by remember { mutableStateOf<OneDriveSync.DeviceCodeInfo?>(null) }
    val pollCancelled = remember { AtomicBoolean(false) }

    fun startSignIn() {
        syncStatus = ""
        pollCancelled.set(false)
        scope.launch {
            syncBusy = true
            try {
                val info = withContext(Dispatchers.IO) { OneDriveSync.requestDeviceCode() }
                pendingCode = info
                val error = withContext(Dispatchers.IO) {
                    OneDriveSync.pollForToken(context, info, pollCancelled)
                }
                pendingCode = null
                if (error == null) {
                    signedIn = true
                    syncStatus = "Signed in ✓ — syncing…"
                    val result = withContext(Dispatchers.IO) { OneDriveSync.syncNow(context) }
                    syncStatus = result
                } else {
                    syncStatus = error
                }
            } catch (e: Exception) {
                pendingCode = null
                syncStatus = "Sign-in failed: ${e.message ?: "network error"}"
            }
            syncBusy = false
        }
    }

    fun runSync() {
        syncStatus = ""
        scope.launch {
            syncBusy = true
            val result = withContext(Dispatchers.IO) { OneDriveSync.syncNow(context) }
            signedIn = OneDriveSync.isSignedIn(context)
            syncStatus = result
            syncBusy = false
        }
    }

    fun signOut() {
        OneDriveSync.signOut(context)
        signedIn = false
        syncStatus = "Signed out — local notes & favorites kept"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(pad),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    "Appearance",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }

            items(themeOptions.size) { i ->
                val (mode, label, icon) = themeOptions[i]
                val selected = currentMode == mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            AppSettings.setThemeMode(context, mode)
                            currentMode = mode
                            onThemeChanged()
                        },
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(16.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.weight(1f))
                        if (selected) {
                            Text("✓", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    "OneDrive Sync",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            if (signedIn) "Syncing notes & favorites via OneDrive"
                            else "Sync notes & favorites across devices",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Notes and favorites are stored in the app's private OneDrive folder — nothing else is touched.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        if (signedIn) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(onClick = ::runSync, enabled = !syncBusy) {
                                    Text(if (syncBusy) "Syncing…" else "Sync now")
                                }
                                Spacer(Modifier.width(12.dp))
                                TextButton(onClick = ::signOut, enabled = !syncBusy) { Text("Sign out") }
                            }
                            val lastSync = AppSettings.getLastSyncTime(context)
                            if (lastSync > 0L) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Last synced: ${
                                        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                            .format(Date(lastSync))
                                    }",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Button(onClick = ::startSignIn, enabled = !syncBusy) {
                                Text(if (syncBusy) "Contacting Microsoft…" else "Sign in with Microsoft")
                            }
                        }
                        if (syncStatus.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                syncStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    pendingCode?.let { info ->
        AlertDialog(
            onDismissRequest = {
                pollCancelled.set(true)
                pendingCode = null
            },
            title = { Text("Sign in to Microsoft") },
            text = {
                Column {
                    Text("On your phone or computer, open:")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        info.verificationUri,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("…and enter this code:")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        info.userCode,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Waiting for approval…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    pollCancelled.set(true)
                    pendingCode = null
                }) { Text("Cancel") }
            }
        )
    }
}
