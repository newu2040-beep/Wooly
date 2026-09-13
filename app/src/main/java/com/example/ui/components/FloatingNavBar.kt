package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WoolyGlassBorder
import com.example.ui.theme.WoolyLavender
import com.example.ui.theme.WoolyPillGradient
import com.example.ui.theme.WoolyTextMuted

enum class NavTab {
    HOME,
    LIBRARY,
    SAVED,
    MORE
}

@Composable
fun FloatingNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onCenterAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barShape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .testTag("floating_nav_bar")
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main glass navigation container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(24.dp, barShape, ambientColor = Color(0x66000000), spotColor = Color(0x778B5CF6))
                .clip(barShape)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xE61C1433), Color(0xF2120C24))
                    )
                )
                .border(1.dp, WoolyGlassBorder, barShape)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 2 items
                NavItem(
                    label = "Home",
                    icon = if (currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    isSelected = currentTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) },
                    modifier = Modifier.weight(1f)
                )

                NavItem(
                    label = "Library",
                    icon = Icons.AutoMirrored.Filled.QueueMusic,
                    isSelected = currentTab == NavTab.LIBRARY,
                    onClick = { onTabSelected(NavTab.LIBRARY) },
                    modifier = Modifier.weight(1f)
                )

                // Space for Center button
                Box(modifier = Modifier.size(54.dp))

                // Right 2 items
                NavItem(
                    label = "Saved",
                    icon = if (currentTab == NavTab.SAVED) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    isSelected = currentTab == NavTab.SAVED,
                    onClick = { onTabSelected(NavTab.SAVED) },
                    modifier = Modifier.weight(1f)
                )

                NavItem(
                    label = "More",
                    icon = Icons.Default.MoreHoriz,
                    isSelected = currentTab == NavTab.MORE,
                    onClick = { onTabSelected(NavTab.MORE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center highlighted Action button (+)
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .size(54.dp)
                .shadow(16.dp, CircleShape, spotColor = Color(0xFFC084FC))
                .clip(CircleShape)
                .background(WoolyPillGradient)
                .border(1.5.dp, Color(0x88FFFFFF), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCenterAction
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Import or Convert Audio",
                tint = Color(0xFF130924),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) WoolyLavender else WoolyTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (isSelected) WoolyLavender else WoolyTextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
