package com.example.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeColors

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onPermissionGranted: () -> Unit,
    onOpenPicker: () -> Unit
) {
    val theme = LocalThemeColors.current

    val permissionsToRequest = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_AUDIO)
            add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val audioGranted = results[Manifest.permission.READ_MEDIA_AUDIO] == true ||
                results[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        if (audioGranted) {
            onPermissionGranted()
        }
        onDismiss()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(theme.pillGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color(0xFF130924),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Full Audio & Files Access",
                    color = theme.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "WOOLY needs access to your device's audio files so you can import, preview, transform, and export high-quality music offline.",
                    color = theme.textSecondary,
                    fontSize = 14.sp
                )

                PermissionFeatureRow(
                    icon = Icons.Default.FolderOpen,
                    title = "Device Music Library",
                    description = "Instant access to all songs on your storage"
                )

                PermissionFeatureRow(
                    icon = Icons.Default.Security,
                    title = "100% Offline & Private",
                    description = "Zero internet upload. Your files stay on device"
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionFeatureRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Export Completion Alerts",
                        description = "Notifies you when background audio master is ready"
                    )
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(theme.pillGradient)
                    .clickable {
                        permissionLauncher.launch(permissionsToRequest)
                    }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Grant Full Access",
                    color = Color(0xFF130924),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = {
                    onDismiss()
                    onOpenPicker()
                }) {
                    Text("Select File", color = theme.primary)
                }
                TextButton(onClick = onDismiss) {
                    Text("Later", color = theme.textMuted)
                }
            }
        },
        containerColor = theme.surfaceElevated,
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
private fun PermissionFeatureRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    val theme = LocalThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(theme.glassBg.copy(alpha = 0.15f))
            .border(1.dp, theme.glassBorder, CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = theme.primary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = title,
                color = theme.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = theme.textMuted,
                fontSize = 11.sp
            )
        }
    }
}
