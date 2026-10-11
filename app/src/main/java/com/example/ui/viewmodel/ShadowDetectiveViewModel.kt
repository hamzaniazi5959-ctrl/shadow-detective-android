package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AiMediaEntity
import com.example.data.local.DetectiveDatabase
import com.example.data.local.DetectiveRepository
import com.example.data.local.EvidenceEntity
import com.example.data.local.JailInmateEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.DetectiveTask
import com.example.data.model.ShopDisguise
import com.example.data.model.ShopDress
import com.example.data.model.ShopWeapon
import com.example.data.model.StoryTasksCatalog
import com.example.data.model.WorldAndShopsCatalog
import com.example.data.remote.GeminiDetectiveService
import com.example.data.remote.GroundedSourceLink
import com.example.util.VoiceActingManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class ActiveMissionSession(
    val task: DetectiveTask,
    val discoveredClueIds: Set<String> = emptySet(),
    val currentStage: Int = 0, // 0: Cutscene, 1: Clues/Investigation, 2: Combat/Chase, 3: Interrogation & Choice
    val criminalCurrentHp: Int = task.criminalHp,
    val interrogationBroken: Boolean = !task.requiresInterrogation,
    val combatLog: String = "Officer Sara, Partner Ryan & K9 Rex arrived at ${task.locationName}.",
    val hintRevealed: Boolean = false
)

data class AiLabUiState(
    val isGeneratingImage: Boolean = false,
    val generatedBitmap: Bitmap? = null,
    val imageStatusText: String = "Use gemini-nano-banana-2.1 to create or edit forensic sketches, suspect posters, or noir scenes.",
    val isGeneratingVideo: Boolean = false,
    val videoProgressText: String = "Upload any photo and animate a 16:9 or 9:16 cutscene using veo-3.1-fast-generate-preview.",
    val latestVideoFilePath: String? = null,
    val latestVideoAspectRatio: String = "16:9",
    val isSearchingNews: Boolean = false,
    val groundedNewsReport: String = "Tap any topic or enter a search query below to pull live Google Search grounded crime & forensics intelligence using gemini-3.5-flash.",
    val groundedSources: List<GroundedSourceLink> = emptyList()
)

class ShadowDetectiveViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DetectiveDatabase.getInstance(application)
    private val repository = DetectiveRepository(db.detectiveDao())
    val voiceManager = VoiceActingManager(application)

    val profileState: StateFlow<PlayerProfileEntity> = repository.profileFlow
        .map { it ?: PlayerProfileEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerProfileEntity()
        )

    val evidenceList: StateFlow<List<EvidenceEntity>> = repository.evidenceFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val jailInmates: StateFlow<List<JailInmateEntity>> = repository.inmatesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val aiMediaGallery: StateFlow<List<AiMediaEntity>> = repository.aiMediaFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _toastBanner = MutableStateFlow("Welcome to Shadow Detective! Officer Sara on duty (9 AM – 5 PM).")
    val toastBanner: StateFlow<String> = _toastBanner.asStateFlow()

    private val _autoWalkDestination = MutableStateFlow<String?>(null)
    val autoWalkDestination: StateFlow<String?> = _autoWalkDestination.asStateFlow()

    private val _bankRobberyActive = MutableStateFlow(true)
    val bankRobberyActive: StateFlow<Boolean> = _bankRobberyActive.asStateFlow()

    private val _activeMission = MutableStateFlow<ActiveMissionSession?>(null)
    val activeMission: StateFlow<ActiveMissionSession?> = _activeMission.asStateFlow()

    private val _aiLabState = MutableStateFlow(AiLabUiState())
    val aiLabState: StateFlow<AiLabUiState> = _aiLabState.asStateFlow()

    private var autoWalkJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    fun postBanner(msg: String) {
        _toastBanner.value = msg
    }

    fun speak(speaker: String, roleTag: String, text: String) {
        voiceManager.speakLine(
            speaker = speaker,
            roleTag = roleTag,
            text = text,
            enabled = profileState.value.voiceActingEnabled
        )
        _toastBanner.value = "$speaker: \"$text\""
    }

    fun toggleVoiceActing() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val next = !p.voiceActingEnabled
                _toastBanner.value = if (next) "Voice Acting: ON" else "Voice Acting: Muted"
                p.copy(voiceActingEnabled = next)
            }
        }
    }

    fun moveSara(dx: Float, dy: Float) {
        autoWalkJob?.cancel()
        _autoWalkDestination.value = null
        viewModelScope.launch {
            repository.updateProfile { p ->
                val speedMult = if (p.isDriving) 2.2f else 1.0f
                val nx = (p.posX + dx * speedMult).coerceIn(0.06f, 0.94f)
                val ny = (p.posY + dy * speedMult).coerceIn(0.08f, 0.92f)
                p.copy(
                    posX = nx,
                    posY = ny,
                    hunger = (p.hunger - 1).coerceAtLeast(15),
                    thirst = (p.thirst - 1).coerceAtLeast(15)
                )
            }
        }
    }

    fun startAutoWalkTo(targetX: Float, targetY: Float, destinationName: String) {
        autoWalkJob?.cancel()
        _autoWalkDestination.value = destinationName
        val mode = if (profileState.value.isDriving) "Auto-Driving Cruiser" else "Auto-Walking"
        _toastBanner.value = "$mode to $destinationName with Ryan & Rex..."
        autoWalkJob = viewModelScope.launch {
            var steps = 0
            while (steps < 45) {
                val p = profileState.value
                val dist = hypot(targetX - p.posX, targetY - p.posY)
                if (dist < 0.025f) {
                    repository.updateProfile { it.copy(posX = targetX, posY = targetY) }
                    break
                }
                val angle = atan2(targetY - p.posY, targetX - p.posX)
                val stepSize = if (p.isDriving) 0.032f else 0.020f
                repository.updateProfile {
                    it.copy(
                        posX = (it.posX + cos(angle) * stepSize).coerceIn(0.05f, 0.95f),
                        posY = (it.posY + sin(angle) * stepSize).coerceIn(0.06f, 0.94f)
                    )
                }
                steps++
                delay(70L)
            }
            _autoWalkDestination.value = null
            _toastBanner.value = "Arrived at $destinationName!"
        }
    }

    fun toggleDriving() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val driving = !p.isDriving
                val ach = if (driving) addCsvItem(p.unlockedAchievementsCsv, "ach_cruiser") else p.unlockedAchievementsCsv
                _toastBanner.value = if (driving) {
                    "Entered NKPD Police Cruiser! Sirens ready."
                } else {
                    "Stepped out of Police Cruiser with Ryan & Rex."
                }
                p.copy(isDriving = driving, unlockedAchievementsCsv = ach)
            }
        }
    }

    fun toggleRain() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val nextRain = !p.isRaining
                _toastBanner.value = if (nextRain) "Noir Rain (Barish) started across the city." else "Skies cleared over Noir City."
                p.copy(isRaining = nextRain)
            }
        }
    }

    fun advanceTimeOfDay() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val nextHour = (p.currentHour + 3) % 24
                val shiftLabel = if (nextHour in 9..17) "Day Duty Shift (9 AM - 5 PM)" else "Night Undercover Shift"
                _toastBanner.value = "Clock advanced to ${formatHour(nextHour)} — $shiftLabel"
                p.copy(currentHour = nextHour)
            }
        }
    }

    fun useUnlimitedMedkit() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                _toastBanner.value = "Used Unlimited Medkit (+HP ∞)! Sara restored to ${p.maxHp} HP."
                p.copy(hp = p.maxHp, energy = (p.energy + 25).coerceAtMost(100))
            }
            _activeMission.value?.let { session ->
                _activeMission.value = session.copy(
                    combatLog = "Officer Sara used an Unlimited Medkit! Full health restored!"
                )
            }
        }
    }

    fun commandRexBark() {
        val currentTask = StoryTasksCatalog.getTaskById(profileState.value.activeTaskId)
        speak("Rex (K9)", "K9", "Woof! Woof! Scent locked onto ${currentTask.criminalName} at ${currentTask.locationName}!")
        viewModelScope.launch {
            repository.updateProfile { p ->
                p.copy(
                    unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_k9_partner"),
                    rexLoyalty = 100
                )
            }
        }
    }

    fun stopBankRobberyEvent() {
        if (!_bankRobberyActive.value) {
            _toastBanner.value = "Central Bank is secure! Next armored truck arrives soon."
            _bankRobberyActive.value = true
            return
        }
        _bankRobberyActive.value = false
        speak("Sara", "HERO", "NKPD! Drop the vault drills! Central Bank robbery stopped cold!")
        viewModelScope.launch {
            repository.updateProfile { p ->
                val newCash = p.cash + 750
                val newXp = p.xp + 120
                val newLevel = 1 + (newXp / 250)
                val newRank = ((newLevel - 1) / 2).coerceIn(0, 4)
                p.copy(
                    cash = newCash,
                    xp = newXp,
                    level = newLevel,
                    rankIndex = maxOf(p.rankIndex, newRank),
                    justiceScore = (p.justiceScore + 6).coerceIn(0, 100),
                    bankRobberiesStopped = p.bankRobberiesStopped + 1,
                    unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_bank_hero")
                )
            }
        }
    }

    fun advanceTutorialStep() {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val next = if (p.tutorialStep >= 9) 0 else p.tutorialStep + 1
                p.copy(tutorialStep = next)
            }
        }
    }

    fun restartTutorial() {
        viewModelScope.launch {
            repository.updateProfile { it.copy(tutorialStep = 1) }
        }
    }

    // --- 20 Story Missions Lifecycle ---

    fun launchMission(task: DetectiveTask) {
        val session = ActiveMissionSession(task = task)
        _activeMission.value = session
        viewModelScope.launch {
            repository.updateProfile { it.copy(activeTaskId = task.id, posX = task.mapX, posY = task.mapY) }
        }
        val firstLine = task.dialogues.firstOrNull()
        if (firstLine != null) {
            speak(firstLine.speaker, firstLine.roleTag, firstLine.text)
        }
    }

    fun closeMissionOverlay() {
        _activeMission.value = null
    }

    fun setMissionStage(stage: Int) {
        _activeMission.value = _activeMission.value?.copy(currentStage = stage.coerceIn(0, 3))
    }

    fun revealMissionHint() {
        val current = _activeMission.value ?: return
        _activeMission.value = current.copy(hintRevealed = true)
        speak("Ryan", "PARTNER", "Hint: ${current.task.ryanHint}")
    }

    fun inspectClue(clueId: String) {
        val current = _activeMission.value ?: return
        val clue = current.task.clues.find { it.id == clueId } ?: return
        val updatedSet = current.discoveredClueIds + clueId
        _activeMission.value = current.copy(
            discoveredClueIds = updatedSet,
            combatLog = "Clue Added to Evidence Board: ${clue.label} -> ${clue.forensicResult}"
        )
        viewModelScope.launch {
            repository.addEvidence(
                EvidenceEntity(
                    clueId = clue.id,
                    taskId = current.task.id,
                    title = clue.label,
                    description = clue.detail,
                    suspectName = current.task.criminalName,
                    locationFound = current.task.locationName,
                    forensicNote = clue.forensicResult
                )
            )
            if (clue.requiresRex) {
                repository.updateProfile { p ->
                    p.copy(unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_k9_partner"))
                }
            }
        }
        _toastBanner.value = "Evidence logged: ${clue.label}"
    }

    fun performCombatAttack(actionType: String) {
        val current = _activeMission.value ?: return
        val p = profileState.value
        val weapon = WorldAndShopsCatalog.weapons.find { it.id == p.equippedWeaponId }
            ?: WorldAndShopsCatalog.weapons.first()

        val (damageDealt, logMessage) = when (actionType) {
            "WEAPON" -> weapon.damage to "Sara fired ${weapon.name} (-${weapon.damage} Suspect Willpower)!"
            "RYAN" -> 28 to "Partner Ryan flanked ${current.task.criminalName} with tactical cover (-28)!"
            "REX" -> 35 to "K9 Dog Rex leaped and pinned ${current.task.criminalName}'s arm (-35)!"
            else -> 25 to "Sara executed a rapid martial takedown (-25)!"
        }

        val newCriminalHp = (current.criminalCurrentHp - damageDealt).coerceAtLeast(0)
        val counterDamage = if (newCriminalHp > 0) 8 else 0

        viewModelScope.launch {
            repository.updateProfile { prof ->
                prof.copy(hp = (prof.hp - counterDamage).coerceAtLeast(20))
            }
        }

        _activeMission.value = current.copy(
            criminalCurrentHp = newCriminalHp,
            combatLog = if (newCriminalHp == 0) {
                "${current.task.criminalName} is subdued and handcuffed! Proceed to Final Resolution!"
            } else {
                "$logMessage (${current.task.criminalName} HP: $newCriminalHp)"
            },
            currentStage = if (newCriminalHp == 0) 3 else current.currentStage
        )
    }

    fun breakSuspectInInterrogation() {
        val current = _activeMission.value ?: return
        val prompt = current.task.interrogation
        if (prompt != null) {
            speak(current.task.criminalName, "CRIMINAL", prompt.confessionLine)
        }
        _activeMission.value = current.copy(
            interrogationBroken = true,
            combatLog = "Alibi broken in Police Station Interrogation! Choose Sara's verdict."
        )
        viewModelScope.launch {
            repository.updateProfile { p ->
                p.copy(unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_interrogator"))
            }
        }
    }

    /**
     * Final Moral Choice: "ARREST", "BRIBE", "BLACKMAIL", or "REFORM"
     */
    fun completeMissionWithChoice(choice: String) {
        val session = _activeMission.value ?: return
        val task = session.task

        viewModelScope.launch {
            // Ensure all clues from this task are on the Evidence Board
            task.clues.forEach { clue ->
                repository.addEvidence(
                    EvidenceEntity(
                        clueId = clue.id,
                        taskId = task.id,
                        title = clue.label,
                        description = clue.detail,
                        suspectName = task.criminalName,
                        locationFound = task.locationName,
                        forensicNote = clue.forensicResult
                    )
                )
            }

            val baseBonus = task.cashReward // $500 standard or $1000 hard
            val extraCash = when (choice) {
                "BRIBE" -> 800
                "BLACKMAIL" -> 1200
                else -> 0
            }
            val totalEarned = baseBonus + extraCash

            repository.addJailInmate(
                JailInmateEntity(
                    taskId = task.id,
                    criminalName = task.criminalName,
                    alias = task.criminalAlias,
                    crimeTitle = task.title,
                    resolution = choice,
                    cellNumber = "Block B - Cell #${100 + task.id}",
                    inmateQuote = task.jailQuote,
                    rewardEarned = totalEarned
                )
            )

            repository.updateProfile { p ->
                val newCompleted = addCsvItem(p.completedTasksCsv, task.id.toString())
                val completedCount = newCompleted.split(",").filter { it.isNotBlank() }.size
                val newXp = p.xp + if (task.isHardMission) 240 else 150
                val newLevel = 1 + (newXp / 250)
                val newRank = maxOf(p.rankIndex, (completedCount / 4).coerceIn(0, 4))

                val justiceDelta = when (choice) {
                    "ARREST" -> 8
                    "REFORM" -> 12
                    "BRIBE" -> -10
                    "BLACKMAIL" -> -15
                    else -> 5
                }
                val corruptionDelta = when (choice) {
                    "ARREST" -> -5
                    "REFORM" -> -8
                    "BRIBE" -> 12
                    "BLACKMAIL" -> 18
                    else -> 0
                }
                val wantedDelta = when (choice) {
                    "BRIBE" -> 1
                    "BLACKMAIL" -> 2
                    else -> -1
                }

                val newJustice = (p.justiceScore + justiceDelta).coerceIn(0, 100)
                val newCorruption = (p.corruptionScore + corruptionDelta).coerceIn(0, 100)

                var ach = p.unlockedAchievementsCsv
                if (task.id == 1) ach = addCsvItem(ach, "ach_first_arrest")
                if (completedCount >= 10) ach = addCsvItem(ach, "ach_halfway")
                if (choice == "REFORM") ach = addCsvItem(ach, "ach_compassion")
                if (choice == "BRIBE" || choice == "BLACKMAIL") ach = addCsvItem(ach, "ach_shadow_broker")
                if (newJustice >= 85) ach = addCsvItem(ach, "ach_clean_justice")
                if (newRank >= 4) ach = addCsvItem(ach, "ach_inspector")
                if (task.id == 20) ach = addCsvItem(ach, "ach_savior")

                val nextTaskId = (task.id + 1).coerceAtMost(20)
                p.copy(
                    cash = p.cash + totalEarned,
                    xp = newXp,
                    level = newLevel,
                    rankIndex = newRank,
                    justiceScore = newJustice,
                    corruptionScore = newCorruption,
                    wantedLevel = (p.wantedLevel + wantedDelta).coerceIn(0, 5),
                    completedTasksCsv = newCompleted,
                    taskOutcomesCsv = addCsvItem(p.taskOutcomesCsv, "${task.id}:$choice"),
                    unlockedAchievementsCsv = ach,
                    activeTaskId = nextTaskId
                )
            }

            _toastBanner.value = "Case #${task.id} (${task.title}) Resolved via $choice! Earned $${baseBonus + extraCash}!"
            _activeMission.value = null
        }
    }

    // --- Shops & City Life ---

    fun buyOrEquipWeapon(weapon: ShopWeapon) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val owned = p.ownedWeapons()
                if (weapon.id in owned) {
                    _toastBanner.value = "Equipped ${weapon.name}!"
                    return@updateProfile p.copy(equippedWeaponId = weapon.id)
                }
                if (p.rankIndex < weapon.requiredRankIndex) {
                    _toastBanner.value = "Requires higher Police Rank to unlock ${weapon.name}!"
                    return@updateProfile p
                }
                if (p.cash < weapon.price) {
                    _toastBanner.value = "Need $${weapon.price} to buy ${weapon.name}."
                    return@updateProfile p
                }
                _toastBanner.value = "Purchased & equipped ${weapon.name}!"
                p.copy(
                    cash = p.cash - weapon.price,
                    ownedWeaponsCsv = addCsvItem(p.ownedWeaponsCsv, weapon.id),
                    equippedWeaponId = weapon.id
                )
            }
        }
    }

    fun buyOrEquipDisguise(disguise: ShopDisguise) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val owned = p.ownedDisguises()
                if (disguise.id in owned) {
                    val nextId = if (p.equippedDisguiseId == disguise.id) "none" else disguise.id
                    _toastBanner.value = if (nextId == "none") "Removed disguise (Back in Uniform)." else "Undercover Disguise Active: ${disguise.name}!"
                    return@updateProfile p.copy(
                        equippedDisguiseId = nextId,
                        wantedLevel = (p.wantedLevel - disguise.wantedReduction).coerceAtLeast(0),
                        unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_undercover")
                    )
                }
                if (p.cash < disguise.price) {
                    _toastBanner.value = "Need $${disguise.price} for ${disguise.name}."
                    return@updateProfile p
                }
                _toastBanner.value = "Bought & wore undercover disguise: ${disguise.name}!"
                p.copy(
                    cash = p.cash - disguise.price,
                    ownedDisguisesCsv = addCsvItem(p.ownedDisguisesCsv, disguise.id),
                    equippedDisguiseId = disguise.id,
                    wantedLevel = (p.wantedLevel - disguise.wantedReduction).coerceAtLeast(0),
                    unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_undercover")
                )
            }
        }
    }

    fun buyOrEquipDress(dress: ShopDress, withFriendsZaraLeo: Boolean = false) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                val owned = p.ownedDresses()
                if (dress.id in owned) {
                    _toastBanner.value = "Sara changed into ${dress.name} (${dress.styleBonus})!"
                    return@updateProfile p.copy(equippedDressId = dress.id)
                }
                val finalPrice = if (withFriendsZaraLeo) (dress.price * 80) / 100 else dress.price
                if (p.cash < finalPrice) {
                    _toastBanner.value = "Need $$finalPrice for ${dress.name}."
                    return@updateProfile p
                }
                val friendMsg = if (withFriendsZaraLeo) " with Zara & Leo (20% Friend Discount!)" else ""
                _toastBanner.value = "Bought & wore ${dress.name}$friendMsg!"
                p.copy(
                    cash = p.cash - finalPrice,
                    ownedDressesCsv = addCsvItem(p.ownedDressesCsv, dress.id),
                    equippedDressId = dress.id,
                    unlockedAchievementsCsv = if (withFriendsZaraLeo) {
                        addCsvItem(p.unlockedAchievementsCsv, "ach_friends")
                    } else p.unlockedAchievementsCsv
                )
            }
        }
    }

    fun hangoutWithFriendsShopping() {
        speak("Sara", "HERO", "Hanging out at the Mall with Zara and Leo! Thanks for the coffee and outfit tips, guys!")
        viewModelScope.launch {
            repository.updateProfile { p ->
                p.copy(
                    hunger = 100,
                    thirst = 100,
                    energy = 100,
                    unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_friends")
                )
            }
        }
    }

    fun performCityLifeAction(venue: String, itemName: String, cost: Int, hungerGain: Int, thirstGain: Int, hpGain: Int, maxHpGain: Int = 0) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                if (p.cash < cost) {
                    _toastBanner.value = "Not enough cash ($cost required) for $itemName."
                    return@updateProfile p
                }
                _toastBanner.value = "$venue: Enjoyed $itemName! Needs replenished."
                val newMaxHp = (p.maxHp + maxHpGain).coerceAtMost(200)
                p.copy(
                    cash = p.cash - cost,
                    hunger = (p.hunger + hungerGain).coerceIn(0, 100),
                    thirst = (p.thirst + thirstGain).coerceIn(0, 100),
                    hp = (p.hp + hpGain).coerceIn(1, newMaxHp),
                    maxHp = newMaxHp,
                    energy = 100
                )
            }
        }
    }

    fun playCasinoSpin(betAmount: Int) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                if (p.cash < betAmount) {
                    _toastBanner.value = "Need $$betAmount to spin the Starlight Wheel!"
                    return@updateProfile p
                }
                val multipliers = listOf(0, 2, 3, 0, 2, 4)
                val mult = multipliers.random()
                val payout = betAmount * mult
                val net = payout - betAmount
                _toastBanner.value = if (mult > 0) {
                    "Starlight Casino JACKPOT (${mult}x)! Sara won $$payout!"
                } else {
                    "Wheel landed on House Zero. Lost $$betAmount."
                }
                p.copy(cash = (p.cash + net).coerceAtLeast(0))
            }
        }
    }

    fun inspectCctvCamera(linkedTaskId: Int, locationName: String) {
        viewModelScope.launch {
            repository.updateProfile { p ->
                p.copy(unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_cctv_hawk"))
            }
        }
        _toastBanner.value = "CCTV Feed Locked: $locationName (Task #$linkedTaskId Intel Tagged!)"
    }

    // --- Gemini AI Capabilities ---

    fun generateOrEditPhoto(prompt: String, aspectRatio: String, inputBitmap: Bitmap?) {
        if (prompt.isBlank()) {
            _toastBanner.value = "Enter a prompt for AI Photo Mode."
            return
        }
        _aiLabState.value = _aiLabState.value.copy(
            isGeneratingImage = true,
            imageStatusText = "Calling ${GeminiDetectiveService.IMAGE_MODEL} ($aspectRatio)..."
        )
        viewModelScope.launch {
            val result = GeminiDetectiveService.createOrEditImage(
                context = getApplication(),
                prompt = prompt,
                aspectRatio = aspectRatio,
                inputBitmap = inputBitmap
            )
            if (result.bitmap != null && result.savedFilePath != null) {
                repository.addAiMedia(
                    AiMediaEntity(
                        mediaType = "IMAGE",
                        prompt = prompt,
                        modelUsed = GeminiDetectiveService.IMAGE_MODEL,
                        localFilePath = result.savedFilePath,
                        aspectRatio = aspectRatio
                    )
                )
                repository.updateProfile { p ->
                    p.copy(unlockedAchievementsCsv = addCsvItem(p.unlockedAchievementsCsv, "ach_ai_forensics"))
                }
                _aiLabState.value = _aiLabState.value.copy(
                    isGeneratingImage = false,
                    generatedBitmap = result.bitmap,
                    imageStatusText = result.captionText
                )
            } else {
                _aiLabState.value = _aiLabState.value.copy(
                    isGeneratingImage = false,
                    imageStatusText = result.error ?: "Image generation completed."
                )
            }
        }
    }

    fun animatePhotoIntoVeoVideo(sourceBitmap: Bitmap, prompt: String, aspectRatio: String) {
        val ratio = if (aspectRatio == "9:16") "9:16" else "16:9"
        _aiLabState.value = _aiLabState.value.copy(
            isGeneratingVideo = true,
            latestVideoAspectRatio = ratio,
            videoProgressText = "Submitting photo to ${GeminiDetectiveService.VIDEO_MODEL} ($ratio)..."
        )
        viewModelScope.launch {
            val result = GeminiDetectiveService.animateImageWithVeo(
                context = getApplication(),
                sourceBitmap = sourceBitmap,
                prompt = prompt.ifBlank { "Cinematic neo-noir detective cutscene with rain and flashing police lights" },
                aspectRatio = ratio,
                onProgress = { progressMsg ->
                    _aiLabState.value = _aiLabState.value.copy(videoProgressText = progressMsg)
                }
            )
            if (result.videoFilePath != null || result.remoteVideoUrl != null) {
                repository.addAiMedia(
                    AiMediaEntity(
                        mediaType = "VIDEO",
                        prompt = prompt,
                        modelUsed = GeminiDetectiveService.VIDEO_MODEL,
                        localFilePath = result.videoFilePath ?: "",
                        remoteUri = result.remoteVideoUrl ?: "",
                        aspectRatio = ratio
                    )
                )
            }
            _aiLabState.value = _aiLabState.value.copy(
                isGeneratingVideo = false,
                latestVideoFilePath = result.videoFilePath ?: result.remoteVideoUrl,
                latestVideoAspectRatio = ratio,
                videoProgressText = result.error ?: result.statusMessage
            )
        }
    }

    fun searchLiveCrimeNews(query: String) {
        if (query.isBlank()) return
        _aiLabState.value = _aiLabState.value.copy(
            isSearchingNews = true,
            groundedNewsReport = "Searching live web data via ${GeminiDetectiveService.SEARCH_MODEL} + Google Search tool..."
        )
        viewModelScope.launch {
            val p = profileState.value
            val contextStr = "Officer Sara (${p.rankTitle}), ${p.completedTaskIds().size}/20 cases solved"
            val result = GeminiDetectiveService.queryCrimeNewsWithGoogleSearch(query, contextStr)
            _aiLabState.value = _aiLabState.value.copy(
                isSearchingNews = false,
                groundedNewsReport = result.text,
                groundedSources = result.sources
            )
        }
    }

    private fun addCsvItem(csv: String, newItem: String): String {
        val set = csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
        set.add(newItem)
        return set.joinToString(",")
    }

    fun formatHour(hour: Int): String {
        val h = hour % 24
        val amPm = if (h < 12) "AM" else "PM"
        val displayH = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return "%02d:00 %s".format(displayH, amPm)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
