package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.DetectiveTask
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
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ActiveMissionSession
import com.example.ui.viewmodel.ShadowDetectiveViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CasesAndMissionScreen(
    profile: PlayerProfileEntity,
    activeMission: ActiveMissionSession?,
    viewModel: ShadowDetectiveViewModel
) {
    var filterMode by remember { mutableIntStateOf(0) } // 0: All 20, 1: Day (9-5), 2: Night/Undercover, 3: Hard ($1000)
    val completedSet = remember(profile.completedTasksCsv) { profile.completedTaskIds() }
    val equippedDress = remember(profile.equippedDressId) {
        WorldAndShopsCatalog.dresses.find { it.id == profile.equippedDressId }
            ?: WorldAndShopsCatalog.dresses.first()
    }

    val filteredTasks = remember(filterMode) {
        when (filterMode) {
            1 -> StoryTasksCatalog.allTasks.filter { !it.isNightMission }
            2 -> StoryTasksCatalog.allTasks.filter { it.isNightMission || it.requiresDisguise }
            3 -> StoryTasksCatalog.allTasks.filter { it.isHardMission }
            else -> StoryTasksCatalog.allTasks
        }
    }

    if (activeMission != null) {
        ActiveMissionRunnerView(
            profile = profile,
            session = activeMission,
            outfitColorHex = equippedDress.primaryColorHex,
            viewModel = viewModel
        )
        return
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
            // Hero Banner Card with consistent Officer Sara, Ryan & K9 Rex
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NoirCard)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_noir_city_banner_1791737366230),
                        contentDescription = "Officer Sara, Ryan and K9 Rex in Noir City",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, NoirObsidian.copy(alpha = 0.92f))
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SaraConsistentPortrait(
                            outfitColorHex = equippedDress.primaryColorHex,
                            isDisguised = profile.equippedDisguiseId != "none",
                            size = 52.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "20 UNIQUE DETECTIVE CASES",
                                style = MaterialTheme.typography.headlineMedium,
                                color = PoliceCyan
                            )
                            Text(
                                text = "Solved: ${completedSet.size}/20 • $500 Standard / $1000 Hard Bonus • Hero: Officer Sara (Locked Character)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BadgeGold
                            )
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
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("All 20 Tasks") }
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("9AM–5PM Duty") }
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = { Text("Night & Undercover") }
                )
                FilterChip(
                    selected = filterMode == 3,
                    onClick = { filterMode = 3 },
                    label = { Text("$1000 Hard Cases") }
                )
            }
        }

        items(filteredTasks, key = { it.id }) { task ->
            val isCompleted = task.id in completedSet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.launchMission(task) }
                    .testTag("task_card_${task.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) Color(0xFF102422) else NoirCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isCompleted) JusticeEmerald else if (task.isHardMission) BadgeGold else PoliceCyan.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                color = if (task.isHardMission) BadgeGold else PoliceCyan,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "#${task.id}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NoirObsidian,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = task.hindiSubtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PoliceCyan
                                )
                            }
                        }

                        Surface(
                            color = if (task.isHardMission) SirenCrimson.copy(alpha = 0.22f) else JusticeEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$${task.cashReward}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (task.isHardMission) BadgeGold else JusticeEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "How Sara Catches ${task.criminalName}: ${task.howSaraCatches}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.92f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (task.isNightMission) Icons.Default.NightlightRound else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = if (task.isNightMission) CorruptionMagenta else BadgeGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${task.criminalName} • ${task.locationName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = { viewModel.launchMission(task) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) JusticeEmerald else PoliceCyan
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NoirObsidian,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCompleted) "Replay Case" else "Investigate",
                                style = MaterialTheme.typography.labelLarge,
                                color = NoirObsidian
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ActiveMissionRunnerView(
    profile: PlayerProfileEntity,
    session: ActiveMissionSession,
    outfitColorHex: Long,
    viewModel: ShadowDetectiveViewModel
) {
    val task = session.task

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirObsidian)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header with Officer Sara Locked Character Portrait
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PoliceCyan)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            SaraConsistentPortrait(
                                outfitColorHex = outfitColorHex,
                                isDisguised = profile.equippedDisguiseId != "none",
                                size = 50.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CASE #${task.id}: ${task.title.uppercase()}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = PoliceCyan
                                )
                                Text(
                                    text = "${task.hindiSubtitle} • Bonus: $${task.cashReward}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BadgeGold
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.closeMissionOverlay() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Mission", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = task.howSaraCatches,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    // 4-Stage Stepper Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val stages = listOf("1. Cutscene", "2. Clues", "3. Capture", "4. Verdict")
                        stages.forEachIndexed { idx, label ->
                            val selected = session.currentStage == idx
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setMissionStage(idx) },
                                color = if (selected) PoliceCyan else NoirCard,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) NoirObsidian else Color.White,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // STAGE 0: STORY CUTSCENE & VOICE ACTING
        if (session.currentStage == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("CINEMATIC CUTSCENE & VOICE ACTING", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                        Text(
                            "Tap any dialogue line below to hear Voice Acting for Officer Sara, Partner Ryan, K9 Rex, or ${task.criminalName}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        task.dialogues.forEach { line ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.speak(line.speaker, line.roleTag, line.text) },
                                color = NoirDeepNavy,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (line.roleTag) {
                                        "HERO" -> PoliceCyan
                                        "PARTNER" -> BadgeGold
                                        "K9" -> JusticeEmerald
                                        else -> SirenCrimson
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (line.roleTag == "HERO") {
                                        SaraConsistentPortrait(outfitColorHex = outfitColorHex, size = 38.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                    } else {
                                        Icon(
                                            Icons.Default.RecordVoiceOver,
                                            contentDescription = "Speak Line",
                                            tint = PoliceCyan,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(line.speaker, style = MaterialTheme.typography.labelLarge, color = BadgeGold)
                                        Text(line.text, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.setMissionStage(1) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                        ) {
                            Text("Proceed to Crime Scene Clues >", color = NoirObsidian, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // STAGE 1: INVESTIGATION, CLUES, K9 REX & HINT SYSTEM
        if (session.currentStage == 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "CRIME SCENE CLUES (${session.discoveredClueIds.size}/${task.clues.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = PoliceCyan
                            )
                            OutlinedButton(
                                onClick = { viewModel.revealMissionHint() },
                                modifier = Modifier.testTag("mission_hint_button")
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ryan's Hint", color = BadgeGold)
                            }
                        }

                        if (session.hintRevealed) {
                            Surface(
                                color = Color(0xFF2B220B),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BadgeGold)
                            ) {
                                Text(
                                    text = "Partner Ryan's Hint: ${task.ryanHint}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = BadgeGold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        task.clues.forEach { clue ->
                            val found = clue.id in session.discoveredClueIds
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.inspectClue(clue.id) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (found) Color(0xFF0E2925) else NoirDeepNavy
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (found) JusticeEmerald else PoliceCyan.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (found) Icons.Default.CheckCircle else if (clue.requiresRex) Icons.Default.Pets else Icons.Default.Search,
                                        contentDescription = null,
                                        tint = if (found) JusticeEmerald else BadgeGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = clue.label + if (clue.requiresRex) " [K9 Rex Scent]" else "",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White
                                        )
                                        Text(clue.detail, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                        if (found) {
                                            Text(
                                                text = "Forensic Match: ${clue.forensicResult}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = JusticeEmerald
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.setMissionStage(2) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                        ) {
                            Text("Engage & Catch ${task.criminalName} >", color = NoirObsidian, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // STAGE 2: COMBAT / CHASE / TAKEDOWN + UNLIMITED MEDKITS
        if (session.currentStage == 2) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("TACTICAL PURSUIT & CAPTURE", style = MaterialTheme.typography.titleMedium, color = SirenCrimson)
                        Text(session.combatLog, style = MaterialTheme.typography.bodyMedium, color = BadgeGold)

                        // Suspect Willpower / HP Bar
                        Text(
                            "${task.criminalName} (${task.criminalAlias}) Resistance: ${session.criminalCurrentHp}/${task.criminalHp}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        LinearProgressIndicator(
                            progress = { session.criminalCurrentHp.toFloat() / task.criminalHp.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = SirenCrimson,
                            trackColor = NoirDeepNavy
                        )

                        // Officer Sara HP + Unlimited Medkit
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Officer Sara HP: ${profile.hp}/${profile.maxHp}", style = MaterialTheme.typography.labelLarge, color = JusticeEmerald)
                            OutlinedButton(onClick = { viewModel.useUnlimitedMedkit() }) {
                                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = JusticeEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unlimited Medkit (+HP ∞)", color = JusticeEmerald)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.performCombatAttack("WEAPON") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sara Weapon", color = NoirObsidian, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { viewModel.performCombatAttack("RYAN") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BadgeGold)
                            ) {
                                Text("Ryan Cover", color = NoirObsidian, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { viewModel.performCombatAttack("REX") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JusticeEmerald)
                            ) {
                                Icon(Icons.Default.Pets, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Rex Pounce", color = NoirObsidian, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Button(
                            onClick = { viewModel.setMissionStage(3) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SirenCrimson)
                        ) {
                            Text("Skip to Interrogation & Final Verdict >", color = Color.White, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // STAGE 3: POLICE STATION INTERROGATION (#40, #41) & JUSTICE / CORRUPTION CHOICE (#4, #35, #36)
        if (session.currentStage == 3) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (task.requiresInterrogation && task.interrogation != null) {
                            Text(
                                "POLICE STATION INTERROGATION (ESSENTIAL CASE)",
                                style = MaterialTheme.typography.titleMedium,
                                color = PoliceCyan
                            )
                            Surface(
                                color = NoirDeepNavy,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Sara's Question: \"${task.interrogation.question}\"", style = MaterialTheme.typography.bodyMedium, color = PoliceCyan)
                                    Text("${task.criminalName}'s Excuse: \"${task.interrogation.suspectLie}\"", style = MaterialTheme.typography.bodyMedium, color = SirenCrimson)
                                    if (!session.interrogationBroken) {
                                        Button(
                                            onClick = { viewModel.breakSuspectInInterrogation() },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = BadgeGold)
                                        ) {
                                            Text(
                                                text = "Present Clues: ${task.interrogation.evidenceCounter}",
                                                color = NoirObsidian,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Confession: \"${task.interrogation.confessionLine}\"",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = JusticeEmerald
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                "Caught Red-Handed at Scene! (No station interrogation required — #41 Sirf zaroori interrogation)",
                                style = MaterialTheme.typography.labelLarge,
                                color = JusticeEmerald
                            )
                        }

                        Text(
                            "FINAL CHOICE — JUSTICE OR CORRUPTION METER:",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BadgeGold
                        )

                        // Choice 1: Official Arrest (Justice)
                        Button(
                            onClick = { viewModel.completeMissionWithChoice("ARREST") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("choice_arrest_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = JusticeEmerald)
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = NoirObsidian)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ARREST ${task.criminalName.uppercase()} (+Justice, +$${task.cashReward} Bonus, Send to Jail)",
                                color = NoirObsidian,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Special Compassionate Reform Option for Tasks 9 (Ayan), 13 (Miss Nadia), 16 (Hungry Kid Sami)
                        if (task.allowCompassionateReform) {
                            Button(
                                onClick = { viewModel.completeMissionWithChoice("REFORM") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("choice_reform_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                            ) {
                                Text(
                                    "COUNSEL & REFORM WITH KINDNESS (+12 Justice, +$${task.cashReward} Bonus)",
                                    color = NoirObsidian,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        // Choice 2: Accept Bribe / Rishwat (#35)
                        Button(
                            onClick = { viewModel.completeMissionWithChoice("BRIBE") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("choice_bribe_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BadgeGold)
                        ) {
                            Text(
                                "TAKE BRIBE / RISHWAT (+12% Corruption, +$${task.cashReward + 800} Cash)",
                                color = NoirObsidian,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Choice 3: Blackmail (#36)
                        Button(
                            onClick = { viewModel.completeMissionWithChoice("BLACKMAIL") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("choice_blackmail_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CorruptionMagenta)
                        ) {
                            Text(
                                "BLACKMAIL SUSPECT (+18% Corruption, +$${task.cashReward + 1200} Syndicate Cut)",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
