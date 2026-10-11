package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.NoirDeepNavy
import com.example.ui.theme.PoliceCyan

/**
 * Locked, 100% consistent character renderer for Heroine Officer Sara.
 * Sara's face, skin tone, signature dark shoulder-length layered hair,
 * expressive eyes, and golden NKPD badge NEVER change across tasks,
 * cutscenes, or outfit switches.
 */
@Composable
fun SaraConsistentPortrait(
    outfitColorHex: Long = 0xFF00E5FF,
    isDisguised: Boolean = false,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "sara_portrait")
    val rimGlow by infinite.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "rim"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        PoliceCyan.copy(alpha = 0.28f * rimGlow),
                        NoirDeepNavy
                    )
                )
            )
            .border(1.5.dp, PoliceCyan.copy(alpha = 0.8f), CircleShape)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = this.size.width
            val h = this.size.height
            drawConsistentSaraAvatar(
                centerX = w * 0.5f,
                topY = h * 0.10f,
                scale = w / 100f,
                outfitColor = Color(outfitColorHex),
                isDisguised = isDisguised,
                headOnly = true
            )
        }
    }
}

/**
 * Shared Canvas drawing routine for Officer Sara so her character model
 * is identical in the HUD portrait, Open World 3D street view, Cutscenes,
 * Interrogation Room, and Boutique preview.
 */
fun DrawScope.drawConsistentSaraAvatar(
    centerX: Float,
    topY: Float,
    scale: Float,
    outfitColor: Color,
    isDisguised: Boolean = false,
    headOnly: Boolean = false
) {
    // 1. Signature Back Hair (Dark Espresso / Raven shoulder-length waves)
    val hairColor = Color(0xFF17131A)
    val hairHighlight = Color(0xFF342B3D)
    drawRoundRect(
        color = hairColor,
        topLeft = Offset(centerX - 24f * scale, topY + 8f * scale),
        size = Size(48f * scale, 56f * scale),
        cornerRadius = CornerRadius(20f * scale, 20f * scale)
    )

    // 2. Torso & Jacket (reflects equipped dress/uniform while keeping Sara's build)
    val bodyTop = topY + 52f * scale
    val bodyHeight = if (headOnly) 38f * scale else 48f * scale
    drawRoundRect(
        brush = Brush.verticalGradient(
            listOf(outfitColor, Color(0xFF0E1626))
        ),
        topLeft = Offset(centerX - 26f * scale, bodyTop),
        size = Size(52f * scale, bodyHeight),
        cornerRadius = CornerRadius(14f * scale, 14f * scale)
    )

    // Tactical Inner Shirt & Collar
    drawRoundRect(
        color = Color(0xFF121824),
        topLeft = Offset(centerX - 10f * scale, bodyTop),
        size = Size(20f * scale, bodyHeight * 0.65f),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )

    // Golden NKPD Detective Shield on Jacket
    drawCircle(
        color = BadgeGold,
        radius = 4.5f * scale,
        center = Offset(centerX + 12f * scale, bodyTop + 14f * scale)
    )
    drawCircle(
        color = Color(0xFFFFF8E1),
        radius = 2.0f * scale,
        center = Offset(centerX + 12f * scale, bodyTop + 14f * scale)
    )

    // 3. Neck & 3D-shaded Face (Warm natural complexion, never changes)
    val skinLight = Color(0xFFF3C6A5)
    val skinShade = Color(0xFFD9A17C)
    drawRoundRect(
        color = skinShade,
        topLeft = Offset(centerX - 6f * scale, topY + 42f * scale),
        size = Size(12f * scale, 14f * scale),
        cornerRadius = CornerRadius(4f * scale, 4f * scale)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skinShade),
            center = Offset(centerX - 3f * scale, topY + 26f * scale),
            radius = 22f * scale
        ),
        topLeft = Offset(centerX - 17f * scale, topY + 12f * scale),
        size = Size(34f * scale, 36f * scale)
    )

    // 4. Expressive Detective Eyes, Eyebrows & Confident Expression
    val eyeY = topY + 28f * scale
    // Eyebrows
    drawLine(
        color = hairColor,
        start = Offset(centerX - 11f * scale, eyeY - 4.5f * scale),
        end = Offset(centerX - 3.5f * scale, eyeY - 3.5f * scale),
        strokeWidth = 2.2f * scale
    )
    drawLine(
        color = hairColor,
        start = Offset(centerX + 3.5f * scale, eyeY - 3.5f * scale),
        end = Offset(centerX + 11f * scale, eyeY - 4.5f * scale),
        strokeWidth = 2.2f * scale
    )

    if (isDisguised) {
        // Sleek undercover visor/sunglasses over Sara's eyes when disguised
        drawRoundRect(
            color = Color(0xFF0A0E17),
            topLeft = Offset(centerX - 14f * scale, eyeY - 3f * scale),
            size = Size(12f * scale, 6.5f * scale),
            cornerRadius = CornerRadius(2f * scale, 2f * scale)
        )
        drawRoundRect(
            color = Color(0xFF0A0E17),
            topLeft = Offset(centerX + 2f * scale, eyeY - 3f * scale),
            size = Size(12f * scale, 6.5f * scale),
            cornerRadius = CornerRadius(2f * scale, 2f * scale)
        )
        drawLine(
            color = BadgeGold,
            start = Offset(centerX - 2f * scale, eyeY),
            end = Offset(centerX + 2f * scale, eyeY),
            strokeWidth = 1.6f * scale
        )
    } else {
        // Left & Right Hazel/Amber Eyes with white catchlight
        drawCircle(color = Color(0xFF231815), radius = 2.6f * scale, center = Offset(centerX - 7f * scale, eyeY))
        drawCircle(color = Color(0xFF231815), radius = 2.6f * scale, center = Offset(centerX + 7f * scale, eyeY))
        drawCircle(color = Color.White, radius = 0.9f * scale, center = Offset(centerX - 6.3f * scale, eyeY - 0.8f * scale))
        drawCircle(color = Color.White, radius = 0.9f * scale, center = Offset(centerX + 7.7f * scale, eyeY - 0.8f * scale))
    }

    // Confident lips
    drawLine(
        color = Color(0xFFB85D65),
        start = Offset(centerX - 4.5f * scale, topY + 40f * scale),
        end = Offset(centerX + 4.5f * scale, topY + 40f * scale),
        strokeWidth = 2.0f * scale
    )

    // 5. Signature Side-Swept Front Bangs (Identical silhouette every time)
    val bangsPath = Path().apply {
        moveTo(centerX - 18f * scale, topY + 22f * scale)
        quadraticTo(centerX - 10f * scale, topY + 6f * scale, centerX + 4f * scale, topY + 9f * scale)
        quadraticTo(centerX + 18f * scale, topY + 12f * scale, centerX + 18f * scale, topY + 25f * scale)
        quadraticTo(centerX + 6f * scale, topY + 16f * scale, centerX - 4f * scale, topY + 18f * scale)
        close()
    }
    drawPath(path = bangsPath, color = hairColor)
    drawLine(
        color = hairHighlight,
        start = Offset(centerX - 10f * scale, topY + 13f * scale),
        end = Offset(centerX + 6f * scale, topY + 14f * scale),
        strokeWidth = 1.5f * scale
    )

    // Full-body legs & boots when rendered in the Open World
    if (!headOnly) {
        val legTop = bodyTop + bodyHeight - 4f * scale
        drawRoundRect(
            color = Color(0xFF101726),
            topLeft = Offset(centerX - 16f * scale, legTop),
            size = Size(13f * scale, 34f * scale),
            cornerRadius = CornerRadius(5f * scale, 5f * scale)
        )
        drawRoundRect(
            color = Color(0xFF101726),
            topLeft = Offset(centerX + 3f * scale, legTop),
            size = Size(13f * scale, 34f * scale),
            cornerRadius = CornerRadius(5f * scale, 5f * scale)
        )
        // Tactical Boots
        drawRoundRect(
            color = Color(0xFF080B11),
            topLeft = Offset(centerX - 17f * scale, legTop + 28f * scale),
            size = Size(15f * scale, 9f * scale),
            cornerRadius = CornerRadius(3f * scale, 3f * scale)
        )
        drawRoundRect(
            color = Color(0xFF080B11),
            topLeft = Offset(centerX + 2f * scale, legTop + 28f * scale),
            size = Size(15f * scale, 9f * scale),
            cornerRadius = CornerRadius(3f * scale, 3f * scale)
        )
    }
}

/**
 * Renders Partner Ryan and K9 Dog Rex alongside Officer Sara in the Open World and Cutscenes.
 */
fun DrawScope.drawPartnerRyanAndRex(
    ryanX: Float,
    ryanY: Float,
    rexX: Float,
    rexY: Float,
    scale: Float
) {
    // Partner Ryan (Tall detective in dark leather jacket & earpiece)
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(ryanX - 20f * scale, ryanY + 38f * scale),
        size = Size(40f * scale, 44f * scale),
        cornerRadius = CornerRadius(10f * scale, 10f * scale)
    )
    drawCircle(
        color = Color(0xFFE0B190),
        radius = 13f * scale,
        center = Offset(ryanX, ryanY + 24f * scale)
    )
    // Ryan's short brown hair & cyan tactical earpiece
    drawArc(
        color = Color(0xFF2D221E),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(ryanX - 14f * scale, ryanY + 9f * scale),
        size = Size(28f * scale, 22f * scale)
    )
    drawCircle(
        color = PoliceCyan,
        radius = 2.5f * scale,
        center = Offset(ryanX + 12f * scale, ryanY + 25f * scale)
    )

    // Police Dog Rex (Loyal German Shepherd K9 with Police Harness)
    // Body
    drawRoundRect(
        color = Color(0xFF8D5B34),
        topLeft = Offset(rexX - 22f * scale, rexY + 16f * scale),
        size = Size(42f * scale, 22f * scale),
        cornerRadius = CornerRadius(9f * scale, 9f * scale)
    )
    // Black saddle & K9 vest
    drawRoundRect(
        color = Color(0xFF141C2B),
        topLeft = Offset(rexX - 10f * scale, rexY + 15f * scale),
        size = Size(22f * scale, 16f * scale),
        cornerRadius = CornerRadius(5f * scale, 5f * scale)
    )
    drawCircle(
        color = BadgeGold,
        radius = 3f * scale,
        center = Offset(rexX, rexY + 22f * scale)
    )
    // Rex Head & pointed German Shepherd ears
    drawCircle(
        color = Color(0xFF9C673E),
        radius = 11f * scale,
        center = Offset(rexX + 18f * scale, rexY + 12f * scale)
    )
    val earPath = Path().apply {
        moveTo(rexX + 11f * scale, rexY + 5f * scale)
        lineTo(rexX + 15f * scale, rexY - 8f * scale)
        lineTo(rexX + 20f * scale, rexY + 4f * scale)
        moveTo(rexX + 19f * scale, rexY + 4f * scale)
        lineTo(rexX + 25f * scale, rexY - 7f * scale)
        lineTo(rexX + 27f * scale, rexY + 6f * scale)
        close()
    }
    drawPath(earPath, color = Color(0xFF2B1D14))
}
