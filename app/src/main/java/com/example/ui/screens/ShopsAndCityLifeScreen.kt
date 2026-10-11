package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.PlayerProfileEntity
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
import com.example.ui.viewmodel.ShadowDetectiveViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShopsAndCityLifeScreen(
    profile: PlayerProfileEntity,
    viewModel: ShadowDetectiveViewModel
) {
    var categoryTab by remember { mutableIntStateOf(0) } // 0: Weapons, 1: Disguises, 2: Dress & Friends, 3: City Life & Home
    var shopWithZaraLeo by remember { mutableStateOf(true) }

    val equippedDress = remember(profile.equippedDressId) {
        WorldAndShopsCatalog.dresses.find { it.id == profile.equippedDressId }
            ?: WorldAndShopsCatalog.dresses.first()
    }
    val ownedWeapons = remember(profile.ownedWeaponsCsv) { profile.ownedWeapons() }
    val ownedDisguises = remember(profile.ownedDisguisesCsv) { profile.ownedDisguises() }
    val ownedDresses = remember(profile.ownedDressesCsv) { profile.ownedDresses() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NoirObsidian)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Locked Officer Sara Character Identity & Needs Summary Card
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PoliceCyan)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SaraConsistentPortrait(
                        outfitColorHex = equippedDress.primaryColorHex,
                        isDisguised = profile.equippedDisguiseId != "none",
                        size = 64.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OFFICER SARA (CHARACTER LOCKED)",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PoliceCyan
                        )
                        Text(
                            text = "Outfit: ${equippedDress.name} • Cash: $${profile.cash} • Rank: ${profile.rankTitle}",
                            style = MaterialTheme.typography.labelLarge,
                            color = BadgeGold
                        )
                        Text(
                            text = "HP ${profile.hp}/${profile.maxHp} • Hunger ${profile.hunger}% • Thirst ${profile.thirst}% • Energy ${profile.energy}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = JusticeEmerald
                        )
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
                    selected = categoryTab == 0,
                    onClick = { categoryTab = 0 },
                    label = { Text("Weapon Shop") }
                )
                FilterChip(
                    selected = categoryTab == 1,
                    onClick = { categoryTab = 1 },
                    label = { Text("Disguise Shop") }
                )
                FilterChip(
                    selected = categoryTab == 2,
                    onClick = { categoryTab = 2 },
                    label = { Text("Dresses & Friends") }
                )
                FilterChip(
                    selected = categoryTab == 3,
                    onClick = { categoryTab = 3 },
                    label = { Text("Club, Food, Gym & Home") }
                )
            }
        }

        // TAB 0: WEAPON SHOP (RANK-UNLOCKED #43)
        if (categoryTab == 0) {
            items(WorldAndShopsCatalog.weapons, key = { it.id }) { weapon ->
                val isOwned = weapon.id in ownedWeapons
                val isEquipped = profile.equippedWeaponId == weapon.id
                val rankNames = listOf("Constable", "Corporal", "Sergeant", "Sub-Inspector", "Inspector")
                val reqRankName = rankNames.getOrElse(weapon.requiredRankIndex) { "Constable" }
                val rankUnlocked = profile.rankIndex >= weapon.requiredRankIndex

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEquipped) Color(0xFF10282A) else NoirCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isEquipped) PoliceCyan else Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = PoliceCyan, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(weapon.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(
                                "Damage: ${weapon.damage} • Accuracy: ${weapon.accuracy}% • Req Rank: $reqRankName",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (rankUnlocked) BadgeGold else SirenCrimson
                            )
                            Text(weapon.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.buyOrEquipWeapon(weapon) },
                            enabled = isOwned || rankUnlocked,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) JusticeEmerald else PoliceCyan
                            )
                        ) {
                            Text(
                                text = when {
                                    isEquipped -> "Equipped"
                                    isOwned -> "Equip"
                                    !rankUnlocked -> "Locked"
                                    else -> "Buy $${weapon.price}"
                                },
                                color = NoirObsidian,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // TAB 1: DISGUISE SHOP (#56: Disguise shop & #48: Undercover)
        if (categoryTab == 1) {
            items(WorldAndShopsCatalog.disguises, key = { it.id }) { disguise ->
                val isOwned = disguise.id in ownedDisguises
                val isEquipped = profile.equippedDisguiseId == disguise.id

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEquipped) Color(0xFF1F1635) else NoirCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isEquipped) CorruptionMagenta else Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TheaterComedy, contentDescription = null, tint = CorruptionMagenta, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(disguise.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(
                                "Stealth +${disguise.stealthBonus}% • Clears ${disguise.wantedReduction} Wanted Stars",
                                style = MaterialTheme.typography.labelSmall,
                                color = PoliceCyan
                            )
                            Text(disguise.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.buyOrEquipDisguise(disguise) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) CorruptionMagenta else BadgeGold
                            )
                        ) {
                            Text(
                                text = when {
                                    isEquipped -> "Take Off"
                                    isOwned -> "Wear"
                                    else -> "Buy $${disguise.price}"
                                },
                                color = if (isEquipped) Color.White else NoirObsidian,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // TAB 2: DRESS BOUTIQUE & FRIENDS ZARA / LEO SHOPPING (#33, #34)
        if (categoryTab == 2) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BadgeGold)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = BadgeGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("SHOP WITH FRIENDS ZARA & LEO", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                                    Text(
                                        "20% Friend Discount + Sara's face/character stays 100% locked while changing outfits!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Switch(
                                checked = shopWithZaraLeo,
                                onCheckedChange = { shopWithZaraLeo = it }
                            )
                        }

                        Button(
                            onClick = { viewModel.hangoutWithFriendsShopping() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("friends_shopping_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CorruptionMagenta)
                        ) {
                            Text("Hangout & Mall Spree with Zara & Leo (+100% Mood/Hunger/Thirst)", color = Color.White, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            items(WorldAndShopsCatalog.dresses, key = { it.id }) { dress ->
                val isOwned = dress.id in ownedDresses
                val isEquipped = profile.equippedDressId == dress.id
                val discountedPrice = if (shopWithZaraLeo) (dress.price * 80) / 100 else dress.price

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEquipped) Color(0xFF13262F) else NoirCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SaraConsistentPortrait(
                            outfitColorHex = dress.primaryColorHex,
                            size = 48.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dress.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(dress.styleBonus, style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                            Text(dress.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.buyOrEquipDress(dress, shopWithZaraLeo) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEquipped) JusticeEmerald else PoliceCyan
                            )
                        ) {
                            Icon(Icons.Default.Checkroom, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    isEquipped -> "Wearing"
                                    isOwned -> "Wear"
                                    else -> "Buy $$discountedPrice"
                                },
                                color = NoirObsidian,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // TAB 3: RESTAURANT, CLUB, GYM, CASINO, HOSPITAL & SARA'S HOME (#30, #31, #44, #46, #51, #52)
        if (categoryTab == 3) {
            // Golden Spice Restaurant (#31)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = BadgeGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GOLDEN SPICE RESTAURANT (KHANA)", style = MaterialTheme.typography.titleMedium, color = BadgeGold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.performCityLifeAction("Restaurant", "Royal Chicken Biryani", 25, 45, 15, 15) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                            ) {
                                Text("Biryani ($25)\n+45 Food", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.performCityLifeAction("Restaurant", "Noir Ramen & Tea", 18, 35, 30, 10) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                            ) {
                                Text("Ramen ($18)\n+35 Food/Water", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Neon Velvet Club (#30: Clean Mocktail Drinks)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalBar, contentDescription = null, tint = CorruptionMagenta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("NEON VELVET CLUB LOUNGE (SPARKLING DRINKS)", style = MaterialTheme.typography.titleMedium, color = CorruptionMagenta)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.performCityLifeAction("Neon Club", "Sparkling Berry Mocktail", 20, 5, 50, 10) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                            ) {
                                Text("Berry Fizz ($20)\n+50 Thirst", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.performCityLifeAction("Neon Club", "Detective Energy Tonic", 30, 10, 60, 25) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                            ) {
                                Text("Energy Tonic ($30)\n+60 Thirst/+25 HP", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Iron Pulse Gym (#51) & Starlight Casino (#52)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = SirenCrimson)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("IRON PULSE GYM & STARLIGHT CASINO", style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.performCityLifeAction("Iron Pulse Gym", "Combat & Stamina Training", 40, -10, -10, 40, maxHpGain = 10) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = JusticeEmerald)
                            ) {
                                Text("Gym Workout ($40)\n+10 Max HP!", style = MaterialTheme.typography.labelSmall, color = NoirObsidian)
                            }
                            Button(
                                onClick = { viewModel.playCasinoSpin(100) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BadgeGold)
                            ) {
                                Icon(Icons.Default.Casino, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Casino Spin ($100)", style = MaterialTheme.typography.labelSmall, color = NoirObsidian)
                            }
                        }
                    }
                }
            }

            // City Hospital (#44: Hospital Ilaj) & Sara's Home (#46: Ghar)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NoirCard)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = PoliceCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SARA'S APARTMENT (GHAR) & CITY HOSPITAL (ILAJ)", style = MaterialTheme.typography.titleMedium, color = PoliceCyan)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.useUnlimitedMedkit()
                                    viewModel.performCityLifeAction("City Hospital", "Full Medical Ilaj & Medkit Refill", 0, 20, 20, 100)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                            ) {
                                Icon(Icons.Default.LocalHospital, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hospital Ilaj (Free)", style = MaterialTheme.typography.labelSmall, color = NoirObsidian)
                            }
                            Button(
                                onClick = {
                                    viewModel.advanceTimeOfDay()
                                    viewModel.performCityLifeAction("Sara's Home", "Rest & Feed K9 Rex", 0, 50, 50, 100)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NoirDeepNavy)
                            ) {
                                Text("Sleep at Home (Next Shift)", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
