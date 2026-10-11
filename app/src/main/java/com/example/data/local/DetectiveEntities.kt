package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val officerName: String = "Officer Sara",
    val rankIndex: Int = 0, // 0: Constable, 1: Corporal, 2: Sergeant, 3: Sub-Inspector, 4: Inspector
    val level: Int = 1,
    val xp: Int = 0,
    val cash: Int = 1200,
    val justiceScore: Int = 60, // 0..100
    val corruptionScore: Int = 10, // 0..100
    val hp: Int = 100,
    val maxHp: Int = 100,
    val hunger: Int = 90, // 0..100
    val thirst: Int = 90, // 0..100
    val energy: Int = 95, // 0..100
    val wantedLevel: Int = 0, // 0..5 stars
    val equippedWeaponId: String = "wpn_baton",
    val equippedDressId: String = "dress_patrol",
    val equippedDisguiseId: String = "none",
    val ownedWeaponsCsv: String = "wpn_baton,wpn_pistol",
    val ownedDressesCsv: String = "dress_patrol,dress_plainclothes",
    val ownedDisguisesCsv: String = "disguise_plain",
    val completedTasksCsv: String = "",
    val taskOutcomesCsv: String = "", // format: "1:ARREST,2:BRIBE"
    val unlockedAchievementsCsv: String = "ach_badge",
    val tutorialStep: Int = 1, // 1..9 (0 = completed)
    val currentHour: Int = 9, // 9 AM Duty start
    val isRaining: Boolean = true,
    val isDriving: Boolean = false,
    val posX: Float = 0.22f,
    val posY: Float = 0.28f,
    val activeTaskId: Int = 1,
    val voiceActingEnabled: Boolean = true,
    val bankRobberiesStopped: Int = 0,
    val rexLoyalty: Int = 100,
    val ryanTrust: Int = 90
) {
    val rankTitle: String
        get() = when (rankIndex.coerceIn(0, 4)) {
            0 -> "Constable"
            1 -> "Corporal"
            2 -> "Sergeant"
            3 -> "Sub-Inspector"
            else -> "Inspector"
        }

    val isOnDutyHours: Boolean
        get() = currentHour in 9..17

    fun completedTaskIds(): Set<Int> =
        completedTasksCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    fun ownedWeapons(): Set<String> =
        ownedWeaponsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun ownedDresses(): Set<String> =
        ownedDressesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun ownedDisguises(): Set<String> =
        ownedDisguisesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun unlockedAchievements(): Set<String> =
        unlockedAchievementsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
}

@Serializable
@Entity(tableName = "evidence_items")
data class EvidenceEntity(
    @PrimaryKey val clueId: String,
    val taskId: Int,
    val title: String,
    val description: String,
    val suspectName: String,
    val locationFound: String,
    val forensicNote: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "jail_inmates")
data class JailInmateEntity(
    @PrimaryKey val taskId: Int,
    val criminalName: String,
    val alias: String,
    val crimeTitle: String,
    val resolution: String, // ARRESTED, BRIBED, BLACKMAILED, REFORMED
    val cellNumber: String,
    val inmateQuote: String,
    val rewardEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "ai_media_gallery")
data class AiMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaType: String, // "IMAGE" or "VIDEO"
    val prompt: String,
    val modelUsed: String,
    val localFilePath: String,
    val remoteUri: String = "",
    val aspectRatio: String = "16:9",
    val timestamp: Long = System.currentTimeMillis()
)
