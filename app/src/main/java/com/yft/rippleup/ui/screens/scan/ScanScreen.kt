package com.yft.rippleup.ui.screens.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.yft.rippleup.data.model.ScanPreset
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.components.ecoIcon
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.Stroke

/** QR scan screen — real camera via ML Kit + the 3 demo presets as fallback. */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(vm: StatsViewModel) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var scanEnabled by remember { mutableStateOf(true) }
    var lastResult by remember { mutableStateOf<ScanResult?>(null) }

    // Re-enable scanning a moment after a result is dismissed.
    LaunchedEffect(lastResult) {
        if (lastResult != null) {
            scanEnabled = false
            kotlinx.coroutines.delay(2200)
            lastResult = null
            scanEnabled = true
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SectionHeader(
                title = "Scan to Verify",
                subtitle = "Scan a RippleUp QR sticker at a refill station or partner vendor — or simulate one below.",
            )

            // Camera card (only meaningful if permission granted).
            if (cameraPermission.status.isGranted) {
                CameraCard(
                    enabled = scanEnabled,
                    onScanned = { raw ->
                        val preset = matchPreset(raw) ?: ScanPreset.CampusRefill
                        logScan(preset, vm)
                        lastResult = ScanResult(preset)
                    },
                )
            } else {
                PermissionCard(
                    granted = false,
                    onRequest = { cameraPermission.launchPermissionRequest() },
                )
            }

            // Preset section — always available, matches the web's preset-btns.
            Text("Or pick a simulated station:",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ScanPreset.entries.forEach { preset ->
                PresetCard(preset = preset) {
                    logScan(preset, vm)
                    lastResult = ScanResult(preset)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Success overlay (the web's view-success overlay).
        AnimatedVisibility(visible = lastResult != null, modifier = Modifier.fillMaxSize()) {
            lastResult?.let { SuccessOverlay(it) }
        }
    }
}

@Composable
private fun CameraCard(enabled: Boolean, onScanned: (String) -> Unit) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22, contentPadding = PaddingValues(0.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            CameraPreview(enabled = enabled, onScanned = onScanned)
            ScanFrameOverlay()
        }
    }
}

@Composable
private fun PermissionCard(granted: Boolean, onRequest: () -> Unit) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.size(64.dp).clip(CircleShape).background(Emerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Emerald, modifier = Modifier.size(32.dp)) }
            Spacer(Modifier.height(12.dp))
            Text("Camera access needed", style = MaterialTheme.typography.titleMedium)
            Text("RippleUp needs your camera to scan QR codes. Your photos are never accessed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald),
                onClick = onRequest,
            ) {
                Text("Grant camera access",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    color = Color(0xFF0A1410), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PresetCard(preset: ScanPreset, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(preset.color.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) { Icon(ecoIcon(preset.iconName), contentDescription = null, tint = preset.color) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(preset.title, style = MaterialTheme.typography.titleMedium)
                Text("+${preset.points} Points  •  −${preset.co2Kg} kg CO₂",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ScanFrameOverlay() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Transparent)
                .scanCorners(),
        )
        Text("Align QR sticker inside the frame",
            modifier = Modifier.padding(top = 150.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun SuccessOverlay(result: ScanResult) {
    val preset = result.preset
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center,
    ) {
        GlassPanel(modifier = Modifier.padding(32.dp), cornerRadius = 24, glow = true) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.size(72.dp).clip(CircleShape).background(Emerald),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.CheckCircle, contentDescription = null,
                    tint = Color(0xFF0A1410), modifier = Modifier.size(40.dp)) }
                Spacer(Modifier.height(14.dp))
                Text("Action Verified!", style = MaterialTheme.typography.headlineMedium,
                    color = Emerald)
                Spacer(Modifier.height(8.dp))
                Text("🍃 Verified! You avoided carbon offset by ${"%.2f".format(preset.co2Kg)} kg.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text("+${preset.points} Ripple Points",
                    style = MaterialTheme.typography.headlineMedium, color = preset.color)
            }
        }
    }
}

private data class ScanResult(val preset: ScanPreset)

/** Match a scanned QR text to a preset (by id substring); fallback to Campus Refill. */
private fun matchPreset(raw: String): ScanPreset? =
    ScanPreset.entries.firstOrNull { raw.contains(it.id, ignoreCase = true) }

private fun logScan(preset: ScanPreset, vm: StatsViewModel) {
    vm.logAction(
        title = "${preset.title} Scan",
        points = preset.points,
        co2Kg = preset.co2Kg,
        colorTag = when (preset) {
            ScanPreset.CampusRefill -> "blue"
            ScanPreset.GreenGrocer -> "red"
            ScanPreset.RecycleBin -> "green"
        },
        iconTag = preset.iconName,
    )
}
