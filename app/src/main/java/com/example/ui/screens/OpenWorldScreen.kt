package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.DetectiveTask
import com.example.data.model.StoryTasksCatalog
import com.example.data.model.WorldAndShopsCatalog
import com.example.ui.components.SaraConsistentPortrait
import com.example.ui.components.drawConsistentSaraAvatar
import com.example.ui.components.drawPartnerRyanAndRex
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.CorruptionMagenta
import com.example.ui.theme.JusticeEmerald
import com.example.ui.theme.NoirCard
import com.example.ui.theme.NoirDeepNavy
import com.example.ui.theme.NoirObsidian
import com.example.ui.theme.PoliceCyan
import com.example.ui.theme.SirenCrimson
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShadowDetectiveViewModel
import kotlin.math.hypot

@Composable
fun OpenWorldScreen(
    profile: PlayerProfileEntity,
    toastBanner: String,
    autoWalkDest: String?,
    bankRobberyActive: Boolean,
    viewModel: ShadowDetectiveViewModel,
    onOpenTaskMission: (DetectiveTask) -> Unit,
    onJumpToShopTab: () -> Unit
) {
    var showFullMapDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }

    val activeTask = remember(profile.activeTaskId) {
        StoryTasksCatalog.getTaskById(profile.activeTaskId)
    }
    val equippedDress = remember(profile.equippedDressId) {
        WorldAndShopsCatalog.dresses.find { it.id == profile.equippedDressId }
            ?: WorldAndShopsCatalog.dresses.first()
    }

    val infinite = rememberInfiniteTransition(label = "world_loop")
    val trafficPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "traffic"
    )
    val rainPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(950, easing = LinearEasing), RepeatMode.Restart),
        label = "rain"
    )
    val sirenFlash by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "siren"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-Bleed 3D-Perspective Open World City Canvas (Tap anywhere on map to Auto-Walk/Drive!)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("open_world_canvas")
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val normX = (tapOffset.x / size.width).coerceIn(0.06f, 0.94f)
                        val normY = (tapOffset.y / size.height).coerceIn(0.10f, 0.90f)
                        val nearLoc = WorldAndShopsCatalog.cityLocations.minByOrNull {
                            hypot(it.mapX - normX, it.mapY - normY)
                        }
                        val label = if (nearLoc != null && hypot(nearLoc.mapX - normX, nearLoc.mapY - normY) < 0.12f) {
                            nearLoc.name
                        } else {
                            "Street Sector (${(normX * 100).toInt()}, ${(normY * 100).toInt()})"
                        }
                        viewModel.startAutoWalkTo(normX, normY, label)
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val isDay = profile.isOnDutyHours

            // Realistic Sky & Horizon (Feature #16 & #20: Realistic Sky + Day/Night cycle)
            val skyColors = if (isDay) {
                listOf(Color(0xFF1A3A6E), Color(0xFF2E5C9A), Color(0xFF182844), Color(0xFF0E1525))
            } else {
                listOf(Color(0xFF04070E), Color(0xFF0B1224), Color(0xFF141D36), Color(0xFF0A0F1D))
            }
            drawRect(brush = Brush.verticalGradient(skyColors, startY = 0f, endY = h * 0.36f))

            // Sun or Moon + Stars
            if (isDay) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFFFF59D), Color(0x55FFB300), Color.Transparent),
                        center = Offset(w * 0.50f, h * 0.08f),
                        radius = w * 0.14f
                    ),
                    center = Offset(w * 0.50f, h * 0.08f),
                    radius = w * 0.14f
                )
            } else {
                drawCircle(
                    color = Color(0xFFE1F5FE),
                    radius = 18f,
                    center = Offset(w * 0.48f, h * 0.08f)
                )
                for (i in 0..18) {
                    val sx = ((i * 73) % 90 + 5) / 100f * w
                    val sy = ((i * 29) % 18 + 2) / 100f * h
                    drawCircle(color = Color.White.copy(alpha = 0.65f), radius = 2f, center = Offset(sx, sy))
                }
            }

            // Ground / City Asphalt Grid
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        if (isDay) Color(0xFF1B2436) else Color(0xFF0B101B),
                        Color(0xFF080C15)
                    ),
                    startY = h * 0.16f,
                    endY = h
                ),
                topLeft = Offset(0f, h * 0.16f),
                size = Size(w, h * 0.84f)
            )

            // City Roads & Multi-Lane Highways (Feature #17: Roads/Traffic/Cars)
            val roadColor = Color(0xFF151C28)
            val laneLineColor = BadgeGold.copy(alpha = 0.45f)
            val horizontalRoads = listOf(0.26f, 0.48f, 0.72f)
            val verticalRoads = listOf(0.22f, 0.50f, 0.80f)

            horizontalRoads.forEach { ry ->
                val yPx = h * ry
                drawRect(
                    color = roadColor,
                    topLeft = Offset(0f, yPx - 22f),
                    size = Size(w, 44f)
                )
                for (dash in 0..16) {
                    drawLine(
                        color = laneLineColor,
                        start = Offset(dash * (w / 16f), yPx),
                        end = Offset(dash * (w / 16f) + (w / 32f), yPx),
                        strokeWidth = 2.5f
                    )
                }
            }

            verticalRoads.forEach { rx ->
                val xPx = w * rx
                drawRect(
                    color = roadColor,
                    topLeft = Offset(xPx - 22f, h * 0.16f),
                    size = Size(44f, h * 0.84f)
                )
                for (dash in 3..18) {
                    drawLine(
                        color = laneLineColor,
                        start = Offset(xPx, dash * (h / 20f)),
                        end = Offset(xPx, dash * (h / 20f) + (h / 40f)),
                        strokeWidth = 2.5f
                    )
                }
            }

            // Animated Traffic Cars on Roads (#17)
            horizontalRoads.forEachIndexed { idx, ry ->
                val carX = ((trafficPhase + idx * 0.33f) % 1f) * w
                val carY = h * ry - 8f
                drawRoundRect(
                    color = if (idx % 2 == 0) Color(0xFF00B0FF) else Color(0xFFFF5252),
                    topLeft = Offset(carX, carY),
                    size = Size(34f, 16f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                // Headlights
                drawCircle(
                    color = Color(0xFFFFF9C4).copy(alpha = 0.75f),
                    radius = 6f,
                    center = Offset(carX + 36f, carY + 8f)
                )
            }

            // Walking City NPCs (#19: NPCs)
            for (npcIdx in 0..7) {
                val npcX = w * (0.15f + ((npcIdx * 0.11f + trafficPhase * 0.25f) % 0.75f))
                val npcY = h * (0.24f + (npcIdx % 3) * 0.23f) + 24f
                drawCircle(color = Color(0xFF90CAF9), radius = 5.5f, center = Offset(npcX, npcY - 8f))
                drawRoundRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(npcX - 5f, npcY - 2f),
                    size = Size(10f, 14f),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }

            // 3D Extruded City Buildings, Houses, Hospital, Bank, Bus Stop & Shops (#15, #18, #22)
            WorldAndShopsCatalog.cityLocations.forEach { loc ->
                val bx = w * loc.mapX
                val by = h * loc.mapY
                val bWidth = 56f
                val bHeight = 28f + loc.storiesHeight * 6f
                val accent = Color(loc.colorHex)

                // 3D Roof Shadow & Side Extrusion
                drawRoundRect(
                    color = Color(0xFF070A12),
                    topLeft = Offset(bx - bWidth / 2f + 6f, by - bHeight + 6f),
                    size = Size(bWidth, bHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Building Facade
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.36f),
                            Color(0xFF121A2B)
                        )
                    ),
                    topLeft = Offset(bx - bWidth / 2f, by - bHeight),
                    size = Size(bWidth, bHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = accent.copy(alpha = 0.85f),
                    topLeft = Offset(bx - bWidth / 2f, by - bHeight),
                    size = Size(bWidth, bHeight),
                    cornerRadius = CornerRadius(6f, 6f),
                    style = Stroke(width = 2f)
                )

                // Illuminated Windows
                val rows = loc.storiesHeight.coerceAtMost(5)
                for (r in 0 until rows) {
                    for (c in 0..2) {
                        drawRect(
                            color = accent.copy(alpha = 0.55f),
                            topLeft = Offset(bx - 18f + c * 14f, by - bHeight + 8f + r * 9f),
                            size = Size(7f, 5f)
                        )
                    }
                }

                // Bank Robbery Flashing Siren Alert over Central Bank (#49: Bank robbery rokna)
                if (loc.id == "loc_bank" && bankRobberyActive) {
                    drawCircle(
                        color = if (sirenFlash > 0.5f) SirenCrimson.copy(alpha = 0.55f) else PoliceCyan.copy(alpha = 0.55f),
                        radius = 38f,
                        center = Offset(bx, by - bHeight / 2f)
                    )
                }
            }

            // Active Mission Target Beacon Ring
            val targetX = w * activeTask.mapX
            val targetY = h * activeTask.mapY
            drawCircle(
                color = SirenCrimson.copy(alpha = 0.35f + 0.35f * sirenFlash),
                radius = 42f,
                center = Offset(targetX, targetY)
            )
            drawCircle(
                color = BadgeGold,
                radius = 28f,
                center = Offset(targetX, targetY),
                style = Stroke(width = 3f)
            )

            // Heroine Officer Sara + Partner Ryan + K9 Dog Rex (or NKPD Police Cruiser when driving)
            val saraPx = w * profile.posX
            val saraPy = h * profile.posY

            if (profile.isDriving) {
                // Sleek NKPD Police Cruiser with Flashing Red/Blue Lightbar (#42: Gari chalana)
                drawRoundRect(
                    color = Color(0xFF0A101D),
                    topLeft = Offset(saraPx - 38f, saraPy - 18f),
                    size = Size(76f, 36f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = PoliceCyan,
                    topLeft = Offset(saraPx - 38f, saraPy - 18f),
                    size = Size(76f, 36f),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 2.5f)
                )
                // Red & Blue Roof Lightbar
                drawCircle(
                    color = if (sirenFlash > 0.5f) SirenCrimson else PoliceCyan,
                    radius = 8f,
                    center = Offset(saraPx - 8f, saraPy)
                )
                drawCircle(
                    color = if (sirenFlash > 0.5f) PoliceCyan else SirenCrimson,
                    radius = 8f,
                    center = Offset(saraPx + 8f, saraPy)
                )
            } else {
                // Partner Ryan & K9 Rex following right beside Officer Sara
                drawPartnerRyanAndRex(
                    ryanX = saraPx - 36f,
                    ryanY = saraPy - 42f,
                    rexX = saraPx + 34f,
                    rexY = saraPy - 10f,
                    scale = 0.52f
                )
                // Selection Halo under Sara
                drawCircle(
                    color = PoliceCyan.copy(alpha = 0.38f),
                    radius = 26f,
                    center = Offset(saraPx, saraPy + 12f)
                )
                // Locked Consistent 3D Avatar of Officer Sara ("Sara ka character change nhi hona chaiye")
                drawConsistentSaraAvatar(
                    centerX = saraPx,
                    topY = saraPy - 48f,
                    scale = 0.62f,
                    outfitColor = Color(equippedDress.primaryColorHex),
                    isDisguised = profile.equippedDisguiseId != "none",
                    headOnly = false
                )
            }

            // Rain Overlay (#21: Barish)
            if (profile.isRaining) {
                for (drop in 0..48) {
                    val rx = ((drop * 67 + (rainPhase * 220).toInt()) % 100) / 100f * w
                    val ry = ((drop * 43 + (rainPhase * 500).toInt()) % 100) / 100f * h
                    drawLine(
                        color = PoliceCyan.copy(alpha = 0.30f),
                        start = Offset(rx, ry),
                        end = Offset(rx - 8f, ry + 22f),
                        strokeWidth = 1.5f
                    )
                }
            }
        }

        // =========================================================================
        // CORNER HUD ("12. Screen clear (buttons konon me)" — Clean Center Viewport!)
        // =========================================================================

        // 1. TOP-LEFT CORNER: Officer Sara Badge, Rank, Duty Clock, Needs & Unlimited Medkit
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .widthIn(max = 225.dp),
            color = NoirObsidian.copy(alpha = 0.86f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SaraConsistentPortrait(
                        outfitColorHex = equippedDress.primaryColorHex,
                        isDisguised = profile.equippedDisguiseId != "none",
                        size = 44.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SARA • ${profile.rankTitle.uppercase()}",
                            style = MaterialTheme.typography.labelLarge,
                            color = PoliceCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$${profile.cash}  •  Lv.${profile.level}",
                            style = MaterialTheme.typography.titleMedium,
                            color = BadgeGold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${viewModel.formatHour(profile.currentHour)} ${if (profile.isOnDutyHours) "DUTY" else "NIGHT"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (profile.isOnDutyHours) JusticeEmerald else CorruptionMagenta
                            )
                            if (profile.wantedLevel > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                repeat(profile.wantedLevel) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Wanted Star",
                                        tint = SirenCrimson,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Compact Needs Row: HP, Hunger, Thirst, Justice/Corruption
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("HP ${profile.hp}", style = MaterialTheme.typography.labelSmall, color = JusticeEmerald)
                    Text("Food ${profile.hunger}%", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                    Text("Water ${profile.thirst}%", style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { profile.justiceScore / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = JusticeEmerald,
                    trackColor = CorruptionMagenta
                )
                Text(
                    text = "Justice ${profile.justiceScore}% | Corrupt ${profile.corruptionScore}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))
                // Unlimited Medkit + Weather/Clock Quick Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.useUnlimitedMedkit() },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("unlimited_medkit_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JusticeEmerald.copy(alpha = 0.22f))
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = "Unlimited Medkit", tint = JusticeEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Medkit ∞", style = MaterialTheme.typography.labelSmall, color = JusticeEmerald)
                    }
                    IconButton(
                        onClick = { viewModel.advanceTimeOfDay() },
                        modifier = Modifier
                            .size(34.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("cycle_time_button")
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Advance Day/Night Clock", tint = BadgeGold, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { viewModel.toggleRain() },
                        modifier = Modifier
                            .size(34.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("toggle_rain_button")
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = "Toggle Rain", tint = PoliceCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 2. TOP-RIGHT CORNER: Interactive Mini-Map -> Tap to open Full City Map & Auto-Walk (#23, #24)
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .width(138.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { showFullMapDialog = true }
                .testTag("mini_map_card"),
            color = NoirObsidian.copy(alpha = 0.88f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PoliceCyan)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MINI-MAP", style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                    Icon(Icons.Default.Map, contentDescription = "Expand Full Map", tint = BadgeGold, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1220))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val mw = size.width
                        val mh = size.height
                        // Mini roads
                        drawLine(Color(0xFF26354D), Offset(0f, mh * 0.48f), Offset(mw, mh * 0.48f), strokeWidth = 2f)
                        drawLine(Color(0xFF26354D), Offset(mw * 0.5f, 0f), Offset(mw * 0.5f, mh), strokeWidth = 2f)
                        // Locations blips
                        WorldAndShopsCatalog.cityLocations.forEach { loc ->
                            drawCircle(
                                color = Color(loc.colorHex).copy(alpha = 0.7f),
                                radius = 2.5f,
                                center = Offset(mw * loc.mapX, mh * loc.mapY)
                            )
                        }
                        // Active task red target
                        drawCircle(
                            color = SirenCrimson,
                            radius = 5f,
                            center = Offset(mw * activeTask.mapX, mh * activeTask.mapY)
                        )
                        // Officer Sara cyan blip
                        drawCircle(
                            color = PoliceCyan,
                            radius = 4.5f,
                            center = Offset(mw * profile.posX, mh * profile.posY)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = autoWalkDest?.let { "Auto -> $it" } ?: "Tap: Full Map / Auto-Walk",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = BadgeGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Subtle Top-Center Radio Dispatch Ticker + Tutorial Banner
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 148.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = NoirObsidian.copy(alpha = 0.82f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.toggleVoiceActing() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (profile.voiceActingEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Voice Acting",
                            tint = PoliceCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = toastBanner,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 9-Step Interactive Tutorial Pill (if active)
            val currentTut = WorldAndShopsCatalog.tutorialSteps.find { it.step == profile.tutorialStep }
            AnimatedVisibility(visible = currentTut != null) {
                if (currentTut != null) {
                    Surface(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .widthIn(max = 360.dp)
                            .clickable { viewModel.advanceTutorialStep() }
                            .testTag("tutorial_banner"),
                        color = NoirCard.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BadgeGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(currentTut.title, style = MaterialTheme.typography.labelLarge, color = BadgeGold)
                                Text(currentTut.instruction, style = MaterialTheme.typography.bodyMedium, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("NEXT >", style = MaterialTheme.typography.labelLarge, color = PoliceCyan)
                        }
                    }
                }
            }
        }

        // 3. BOTTOM-LEFT CORNER: D-Pad + Police Cruiser Drive + K9 Rex Track (#12, #42, #50)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
            color = NoirObsidian.copy(alpha = 0.85f),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // D-Pad Cross
                IconButton(
                    onClick = { viewModel.moveSara(0f, -0.05f) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(NoirCard, CircleShape)
                        .testTag("dpad_up")
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move North", tint = PoliceCyan)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    IconButton(
                        onClick = { viewModel.moveSara(-0.05f, 0f) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("dpad_left")
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Move West", tint = PoliceCyan)
                    }
                    IconButton(
                        onClick = { viewModel.moveSara(0.05f, 0f) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("dpad_right")
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Move East", tint = PoliceCyan)
                    }
                }
                IconButton(
                    onClick = { viewModel.moveSara(0f, 0.05f) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(NoirCard, CircleShape)
                        .testTag("dpad_down")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move South", tint = PoliceCyan)
                }

                Spacer(modifier = Modifier.height(6.dp))
                // Cruiser & Rex Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { viewModel.toggleDriving() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (profile.isDriving) SirenCrimson else NoirCard, CircleShape)
                            .testTag("drive_cruiser_button")
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = "Drive Police Cruiser",
                            tint = if (profile.isDriving) Color.White else BadgeGold
                        )
                    }
                    IconButton(
                        onClick = { viewModel.commandRexBark() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("rex_k9_button")
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = "Command K9 Dog Rex", tint = PoliceCyan)
                    }
                }
            }
        }

        // 4. BOTTOM-RIGHT CORNER: Action Cluster (Play Task, Stop Bank Robbery, Phone, Hint, Tutorial)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .widthIn(max = 195.dp),
            color = NoirObsidian.copy(alpha = 0.88f),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BadgeGold.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Primary Mission Action Button
                Button(
                    onClick = { onOpenTaskMission(activeTask) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("start_active_task_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Task #${activeTask.id}: ${activeTask.title}",
                        style = MaterialTheme.typography.labelLarge,
                        color = NoirObsidian,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Stop Bank Robbery Dynamic Button (#49: Bank robbery rokna)
                Button(
                    onClick = { viewModel.stopBankRobberyEvent() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("stop_bank_robbery_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bankRobberyActive) SirenCrimson else NoirCard
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (bankRobberyActive) "STOP BANK ROBBERY!" else "Bank Vault Secure",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1
                    )
                }

                // Corner Utility Icon Row: Hint, Phone, Shops, Tutorial
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            viewModel.speak("Ryan", "PARTNER", activeTask.ryanHint)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("world_hint_button")
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Hint System", tint = BadgeGold, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { showPhoneDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("open_phone_button")
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = "Sara's Phone", tint = PoliceCyan, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onJumpToShopTab,
                        modifier = Modifier
                            .size(38.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("quick_shops_button")
                    ) {
                        Icon(Icons.Default.GpsFixed, contentDescription = "Quick Shops & Wardrobe", tint = JusticeEmerald, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = { viewModel.restartTutorial() },
                        modifier = Modifier
                            .size(38.dp)
                            .background(NoirCard, CircleShape)
                            .testTag("restart_tutorial_button")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "9-Step Tutorial", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    // FULL CITY MAP MODAL (#23, #24: Mini-map -> Full Map -> Auto-walk / Auto-drive)
    if (showFullMapDialog) {
        Dialog(onDismissRequest = { showFullMapDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(540.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NoirDeepNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NOIR CITY FULL GPS MAP", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
                            Text(
                                "Tap any building or task to Auto-${if (profile.isDriving) "Drive Cruiser" else "Walk"} there!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BadgeGold
                            )
                        }
                        IconButton(onClick = { showFullMapDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Map", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(WorldAndShopsCatalog.cityLocations) { loc ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.startAutoWalkTo(loc.mapX, loc.mapY, loc.name)
                                        showFullMapDialog = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = NoirCard)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .background(Color(loc.colorHex), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(loc.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                        Text(loc.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("AUTO-GO >", style = MaterialTheme.typography.labelLarge, color = PoliceCyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SARA'S SMARTPHONE MODAL (#47: Phone — Messages from Ryan, Zara, Leo, Chief & Informant)
    if (showPhoneDialog) {
        Dialog(onDismissRequest = { showPhoneDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NoirObsidian),
                border = androidx.compose.foundation.BorderStroke(2.dp, PoliceCyan)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SARA'S NKPD SMARTPHONE", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
                            Text("Messages • Ryan • Friends Zara & Leo • Dispatch", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                        }
                        IconButton(onClick = { showPhoneDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Phone", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.hangoutWithFriendsShopping()
                                showPhoneDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorruptionMagenta)
                        ) {
                            Text("Call Zara & Leo (Shop)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                        Button(
                            onClick = {
                                viewModel.speak("Ryan", "PARTNER", "On your six, Sara! Task #${activeTask.id} hint: ${activeTask.ryanHint}")
                                showPhoneDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                        ) {
                            Text("Call Partner Ryan", style = MaterialTheme.typography.labelSmall, color = NoirObsidian)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(WorldAndShopsCatalog.phoneMessages) { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NoirCard),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.speak(
                                            msg.sender,
                                            if (msg.role == "PARTNER") "PARTNER" else "HERO",
                                            msg.text
                                        )
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(msg.sender, style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                                        Text(msg.time, style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(msg.text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
