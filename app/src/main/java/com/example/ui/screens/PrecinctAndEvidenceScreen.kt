package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.EvidenceEntity
import com.example.data.local.JailInmateEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.StoryTasksCatalog
import com.example.data.model.WorldAndShopsCatalog
import com.example.ui.components.SaraConsistentPortrait
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.CorruptionMagenta
import com.example.ui.theme.JusticeEmerald
import com.example.ui.theme.NoirCard
import com.example.ui.theme.NoirDeepNavy
import com.example.ui.theme.NoirObsidian
import com.example.ui.theme.PoliceCyan
import com.example.ui.theme.SirenCrimson
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShadowDetectiveViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrecinctAndEvidenceScreen(
    profile: PlayerProfileEntity,
    evidenceList: List<EvidenceEntity>,
    inmates: List<JailInmateEntity>,
    viewModel: ShadowDetectiveViewModel,
    onJumpToCasesTab: () -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Evidence Board, 1: CCTV Grid, 2: County Jail, 3: 16 Medals & 3 Endings
    val unlockedSet = remember(profile.unlockedAchievementsCsv) { profile.unlockedAchievements() }
    val equippedDress = remember(profile.equippedDressId) {
        WorldAndShopsCatalog.dresses.find { it.id == profile.equippedDressId }
            ?: WorldAndShopsCatalog.dresses.first()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NoirCard)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_evidence_board_1791737387521),
                        contentDescription = "Precinct Evidence Board and CCTV Monitors",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = NoirObsidian.copy(alpha = 0.80f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SaraConsistentPortrait(
                                outfitColorHex = equippedDress.primaryColorHex,
                                isDisguised = profile.equippedDisguiseId != "none",
                                size = 46.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("NKPD PRECINCT HQ & EVIDENCE BOARD", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
                                Text(
                                    "Rank: ${profile.rankTitle} (Constable → Inspector) • Clues: ${evidenceList.size} • Jail Inmates: ${inmates.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BadgeGold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("Evidence Board") }
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("CCTV Cameras (6)") }
                )
                FilterChip(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    label = { Text("County Jail (${inmates.size})") }
                )
                FilterChip(
                    selected = subTab == 3,
                    onClick = { subTab = 3 },
                    label = { Text("16 Medals & 3 Endings") }
                )
            }
        }

        // SUB-TAB 0: RED-YARN EVIDENCE BOARD (#39)
        if (subTab == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SirenCrimson.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CONSPIRACY RED-STRING LINK BOARD", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1A1412))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val nodes = listOf(
                                    Offset(w * 0.15f, h * 0.28f),
                                    Offset(w * 0.48f, h * 0.22f),
                                    Offset(w * 0.82f, h * 0.32f),
                                    Offset(w * 0.28f, h * 0.75f),
                                    Offset(w * 0.68f, h * 0.72f)
                                )
                                for (i in 0 until nodes.size - 1) {
                                    drawLine(
                                        color = SirenCrimson,
                                        start = nodes[i],
                                        end = nodes[i + 1],
                                        strokeWidth = 3.5f
                                    )
                                }
                                drawLine(SirenCrimson, nodes.first(), nodes.last(), strokeWidth = 2.5f)
                                nodes.forEachIndexed { idx, pt ->
                                    drawCircle(color = BadgeGold, radius = 12f, center = pt)
                                    drawCircle(color = SirenCrimson, radius = 5f, center = pt)
                                }
                            }
                            Text(
                                text = "Connected Case Nodes: ${evidenceList.size} Forensic Clues Pinned",
                                style = MaterialTheme.typography.labelSmall,
                                color = BadgeGold,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            if (evidenceList.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("No clues pinned yet!", style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                            Text(
                                "Inspect crime scenes in the 20 Cases (CCTV tapes, Size 10 boot prints, cigarette DNA, ransom notes) to pin clues here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(evidenceList, key = { it.clueId }) { ev ->
                    Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Task #${ev.taskId}: ${ev.title}", style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                                Text(ev.suspectName, style = MaterialTheme.typography.labelLarge, color = SirenCrimson)
                            }
                            Text(ev.description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            Text("Forensic Result: ${ev.forensicNote}", style = MaterialTheme.typography.labelSmall, color = JusticeEmerald)
                        }
                    }
                }
            }
        }

        // SUB-TAB 1: 6-SECTOR CCTV SURVEILLANCE GRID (#53: CCTV)
        if (subTab == 1) {
            items(WorldAndShopsCatalog.cctvFeeds, key = { it.id }) { cam ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (cam.isAlertActive) SirenCrimson else TerminalGreen
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = TerminalGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${cam.cameraCode} • ${cam.locationName}", style = MaterialTheme.typography.titleMedium, color = TerminalGreen)
                                    Text(cam.statusText, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                            if (cam.isAlertActive) {
                                Surface(color = SirenCrimson, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        "REC • ALERT",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.inspectCctvCamera(cam.linkedTaskId, cam.locationName)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NoirCard)
                            ) {
                                Text("Log CCTV Stamp", color = TerminalGreen, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = {
                                    val task = StoryTasksCatalog.getTaskById(cam.linkedTaskId)
                                    viewModel.launchMission(task)
                                    onJumpToCasesTab()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Launch Task #${cam.linkedTaskId}", color = NoirObsidian, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 2: COUNTY JAIL VISITS (#55: Jail)
        if (subTab == 2) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirDeepNavy)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("COUNTY JAIL — CELL BLOCK VISITATION", style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                        Text(
                            "Every criminal Officer Sara arrests (or reforms/blackmails) across the 20 cases is logged below. Tap any cell to hear their voice-acted line!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (inmates.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cells are waiting for your first arrest!", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                            Text(
                                "Complete Task #1 (Cleaner Ramu) or any of the 20 cases to lock criminals inside County Jail.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(inmates, key = { it.taskId }) { inmate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.speak(inmate.criminalName, "CRIMINAL", inmate.inmateQuote)
                            },
                        colors = CardDefaults.cardColors(containerColor = NoirCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = BadgeGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("${inmate.criminalName} (${inmate.alias})", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                        Text("${inmate.cellNumber} • Case #${inmate.taskId}: ${inmate.crimeTitle}", style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                                    }
                                }
                                Surface(
                                    color = when (inmate.resolution) {
                                        "ARREST" -> JusticeEmerald.copy(alpha = 0.2f)
                                        "REFORM" -> PoliceCyan.copy(alpha = 0.2f)
                                        else -> CorruptionMagenta.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = inmate.resolution,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "Speak", tint = BadgeGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("\"${inmate.inmateQuote}\"", style = MaterialTheme.typography.bodyMedium, color = BadgeGold)
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 3: RANK LADDER, 16 ACHIEVEMENTS & 3 ENDINGS THEATER (#6, #37, #57)
        if (subTab == 3) {
            // Rank Ladder Card
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirDeepNavy)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("POLICE RANK PROGRESSION (CONSTABLE → INSPECTOR)", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                        val ranks = listOf("Constable", "Corporal", "Sergeant", "Sub-Inspector", "Inspector")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ranks.forEachIndexed { idx, rName ->
                                val reached = idx <= profile.rankIndex
                                Text(
                                    text = rName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (reached) JusticeEmerald else TextSecondary
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { (profile.rankIndex + 1) / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BadgeGold,
                            trackColor = NoirCard
                        )
                    }
                }
            }

            // 3 Endings Theater (#6: 3 Endings)
            item {
                Text("3 STORY ENDINGS THEATER", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
            }
            items(WorldAndShopsCatalog.endings, key = { it.id }) { ending ->
                val isCurrentPath = when (ending.id) {
                    "ending_justice" -> profile.justiceScore >= 70
                    "ending_corruption" -> profile.corruptionScore >= 60
                    else -> profile.justiceScore < 70 && profile.corruptionScore < 60
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isCurrentPath) BadgeGold else PoliceCyan.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ending.title, style = MaterialTheme.typography.titleMedium, color = Color.White, modifier = Modifier.weight(1f))
                            if (isCurrentPath) {
                                Surface(color = BadgeGold, shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        "ACTIVE PATH",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NoirObsidian,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(ending.conditionText, style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                        Text(ending.epilogueStory, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Button(
                            onClick = { viewModel.speak("Sara", "HERO", ending.saraQuote) },
                            colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Sara's Ending Monologue", color = BadgeGold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // 16 Achievements & Medals (#57)
            item {
                Text(
                    "16 ACHIEVEMENTS & MEDALS (${unlockedSet.size}/16 UNLOCKED)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BadgeGold
                )
            }
            items(WorldAndShopsCatalog.achievements, key = { it.id }) { ach ->
                val unlocked = ach.id in unlockedSet
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (unlocked) Color(0xFF112622) else NoirCard
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (unlocked) BadgeGold else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ach.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(ach.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Text(
                            text = if (unlocked) "UNLOCKED" else "+$${ach.rewardCash}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (unlocked) JusticeEmerald else BadgeGold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
