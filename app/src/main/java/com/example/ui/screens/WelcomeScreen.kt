package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassPillButton
import com.example.ui.theme.LocalThemeColors

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val theme = LocalThemeColors.current

    BoxWithConstraints(
        modifier = Modifier
            .testTag("welcome_screen")
            .fillMaxSize()
            .background(theme.backgroundBrush)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val isCompact = maxHeight < 700.dp
        val orbSize = if (isCompact) 170.dp else 260.dp
        val titleSize = if (isCompact) 28.sp else 38.sp
        val subtitleSize = if (isCompact) 14.sp else 16.sp
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))

            // Central Glowing Glass Sphere Visual - completely circular with glowing rings
            Box(
                modifier = Modifier
                    .size(orbSize)
                    .shadow(36.dp, CircleShape, spotColor = theme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.wooly_glass_orb),
                    contentDescription = "WOOLY Glass Orb",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    theme.primary,
                                    theme.secondary,
                                    theme.tertiary,
                                    theme.primary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(if (isCompact) 12.dp else 24.dp))

            // Typography & Brand Copy
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WOOLY",
                    color = theme.textPrimary,
                    fontSize = titleSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Same Music. Different Vibes.",
                    color = theme.primary,
                    fontSize = subtitleSize,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Transform your music into lo-fi, reverb, vinyl, slowed and more. 100% offline, studio quality, your way.",
                    color = theme.textSecondary,
                    fontSize = if (isCompact) 13.sp else 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = if (isCompact) 18.sp else 21.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(if (isCompact) 14.dp else 24.dp))

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(theme.glassBorder)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp, 6.dp)
                            .clip(CircleShape)
                            .background(theme.pillGradient)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(theme.glassBorder)
                    )
                }
            }

            Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))

            // Bottom CTA Pill Button
            GlassPillButton(
                text = "Get Started",
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                testTag = "get_started_button"
            )
        }
    }
}
