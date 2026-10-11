package com.example.data.model

data class CityLocation(
    val id: String,
    val name: String,
    val category: String, // "HQ", "MISSION", "SHOP", "LIFE", "HOME"
    val mapX: Float,
    val mapY: Float,
    val colorHex: Long,
    val storiesHeight: Int,
    val description: String
)

data class ShopWeapon(
    val id: String,
    val name: String,
    val damage: Int,
    val accuracy: Int,
    val requiredRankIndex: Int, // 0..4
    val price: Int,
    val description: String
)

data class ShopDisguise(
    val id: String,
    val name: String,
    val stealthBonus: Int,
    val wantedReduction: Int,
    val price: Int,
    val description: String
)

data class ShopDress(
    val id: String,
    val name: String,
    val styleBonus: String,
    val primaryColorHex: Long,
    val price: Int,
    val description: String
)

data class AchievementMedal(
    val id: String,
    val title: String,
    val description: String,
    val rewardCash: Int
)

data class TutorialStepInfo(
    val step: Int,
    val title: String,
    val instruction: String
)

data class PhoneMessage(
    val sender: String,
    val role: String,
    val time: String,
    val text: String
)

data class CctvFeedInfo(
    val id: String,
    val cameraCode: String,
    val locationName: String,
    val statusText: String,
    val linkedTaskId: Int,
    val isAlertActive: Boolean
)

data class GameEndingInfo(
    val id: String,
    val title: String,
    val conditionText: String,
    val epilogueStory: String,
    val saraQuote: String
)

object WorldAndShopsCatalog {
    val cityLocations: List<CityLocation> = listOf(
        CityLocation("loc_precinct", "Police Station HQ", "HQ", 0.22f, 0.28f, 0xFF00E5FF, 5, "Interrogation room, Evidence Board, Armory & Chief's Briefing."),
        CityLocation("loc_bank", "Central Bank", "MISSION", 0.35f, 0.25f, 0xFFFFD54F, 6, "High-security vault, CCTV room & live Bank Robbery defense."),
        CityLocation("loc_hospital", "City Hospital", "LIFE", 0.42f, 0.52f, 0xFF00E676, 5, "Full medical treatment (Ilaj), Pharmacy Store Room & Medkit supply."),
        CityLocation("loc_bus_stop", "Metro Bus Terminal", "LIFE", 0.16f, 0.20f, 0xFF2979FF, 2, "City transit hub, fast travel & Highway Bus Hijack intercept."),
        CityLocation("loc_jewelry", "Royal Diamond Shop", "MISSION", 0.66f, 0.22f, 0xFFE040FB, 3, "Luxury diamond showroom & back-alley footprint crime scene."),
        CityLocation("loc_school", "City High School", "MISSION", 0.18f, 0.48f, 0xFFFFAB00, 3, "School gate stakeout & Principal's office handwriting case."),
        CityLocation("loc_park", "Greenview City Park", "MISSION", 0.28f, 0.65f, 0xFF69F0AE, 1, "Fountain park walkways for Kidnapping & Plainclothes Purse Bait."),
        CityLocation("loc_club", "Neon Velvet Club", "LIFE", 0.74f, 0.42f, 0xFFD500F9, 4, "Sparkling mocktail lounge, informants & undercover backroom raid."),
        CityLocation("loc_restaurant", "Golden Spice Restaurant", "LIFE", 0.50f, 0.45f, 0xFFFF6D00, 3, "Restore Hunger/Thirst with hot meals, feed Sami & inspect kitchen."),
        CityLocation("loc_gym", "Iron Pulse Gym", "LIFE", 0.38f, 0.74f, 0xFFFF1744, 3, "Train Sara's stamina, max HP & combat power."),
        CityLocation("loc_casino", "Starlight Casino", "LIFE", 0.82f, 0.58f, 0xFFFFD700, 6, "Play High-Roller Wheel, monitor VIP card CCTV & win cash."),
        CityLocation("loc_weapons", "Tactical Weapon Shop", "SHOP", 0.60f, 0.34f, 0xFFFF5252, 2, "Rank-unlocked police firearms, tasers & non-lethal gear."),
        CityLocation("loc_disguise", "Shadow Disguise Shop", "SHOP", 0.78f, 0.28f, 0xFF7C4DFF, 2, "Undercover disguises to infiltrate clubs & clear Wanted stars."),
        CityLocation("loc_boutique", "Zara & Leo Dress Boutique", "SHOP", 0.55f, 0.55f, 0xFFFF4081, 3, "Buy & change Sara's outfits and shop together with Zara & Leo!"),
        CityLocation("loc_jail", "County Jail", "HQ", 0.88f, 0.76f, 0xFF90A4AE, 4, "Visit arrested criminals from all 20 cases in high-security cells."),
        CityLocation("loc_cctv_tower", "CCTV & Cyber Cell Tower", "HQ", 0.84f, 0.24f, 0xFF00B0FF, 7, "City-wide CCTV cameras, IP tracer & WNKP News Channel 7."),
        CityLocation("loc_hotel", "Grand Noir Hotel", "MISSION", 0.68f, 0.72f, 0xFFFFC400, 7, "Luxury hotel suites & Suite 404 gold smuggling raid."),
        CityLocation("loc_home", "Sara's Apartment (Ghar)", "HOME", 0.14f, 0.78f, 0xFF18FFFF, 4, "Rest to switch Day/Night shift, change dresses & play with Rex."),
        CityLocation("loc_station_finale", "Central Train Station", "MISSION", 0.50f, 0.82f, 0xFFFF1744, 5, "Main rail terminal & Task 20 Bomb Defusal Finale.")
    )

    val weapons: List<ShopWeapon> = listOf(
        ShopWeapon("wpn_baton", "Tactical Stun Baton", 22, 95, 0, 0, "Standard Constable issue non-lethal shock baton."),
        ShopWeapon("wpn_pistol", "NKPD 9mm Service Pistol", 34, 90, 0, 0, "Reliable sidearm for patrol and street pursuits."),
        ShopWeapon("wpn_taser_pro", "Arc-Volt Dual Taser", 45, 96, 1, 450, "Corporal-rank high-voltage capture device for instant takedowns."),
        ShopWeapon("wpn_smg", "MP5-N Tactical SMG", 62, 88, 2, 900, "Sergeant-rank rapid suppression weapon for highway chases."),
        ShopWeapon("wpn_shotgun", "Enforcer Breacher", 78, 84, 3, 1500, "Sub-Inspector heavy raid weapon for warehouse sieges."),
        ShopWeapon("wpn_netgun", "Cyber Net-Launcher X9", 92, 98, 4, 2200, "Inspector prototype that immobilizes armed fugitives cleanly.")
    )

    val disguises: List<ShopDisguise> = listOf(
        ShopDisguise("disguise_plain", "Plainclothes Park Jacket", 25, 1, 0, "Casual hoodie & sunglasses for Park Purse Bait (Task 15) and school stakeouts."),
        ShopDisguise("disguise_vip", "Velvet Club VIP Suit", 55, 2, 400, "Glamorous undercover attire required to enter the Neon Club backroom (Task 6)."),
        ShopDisguise("disguise_medic", "Hospital Night Scrubs", 65, 2, 550, "Blend seamlessly into City Hospital corridors during midnight stakeouts."),
        ShopDisguise("disguise_cyber", "Grid Technician Jumpsuit", 75, 3, 800, "Access restricted server rooms, CCTV towers, and hotel maintenance floors."),
        ShopDisguise("disguise_syndicate", "Shadow Broker Trench", 95, 4, 1300, "Master disguise that fools high-roller casino guards and clears 4 Wanted stars.")
    )

    val dresses: List<ShopDress> = listOf(
        ShopDress("dress_patrol", "NKPD Patrol Uniform", "+10% Authority", 0xFF00E5FF, 0, "Crisp navy-and-cyan official police duty uniform."),
        ShopDress("dress_plainclothes", "Noir Leather Trenchcoat", "+15% Detective Grit", 0xFF78909C, 0, "Classic rain-slicked detective trenchcoat for night investigations."),
        ShopDress("dress_blazer", "Crimson Inspector Blazer", "+20% Interrogation", 0xFFFF1744, 350, "Sharp tailored crimson jacket worn by senior detectives."),
        ShopDress("dress_gala", "Starlight Emerald Gown", "+25% High-Society Charm", 0xFF00E676, 650, "Elegant evening outfit picked out with best friend Zara."),
        ShopDress("dress_stealth", "Phantom Kevlar Suit", "+25% Armor & Speed", 0xFFAA00FF, 950, "Armored night-ops tactical suit with neon trim picked with Leo.")
    )

    val achievements: List<AchievementMedal> = listOf(
        AchievementMedal("ach_badge", "1. First Day on Duty", "Begin Officer Sara's journey on the force.", 100),
        AchievementMedal("ach_first_arrest", "2. First Collar", "Complete Task 1 and catch Ramu via Bank CCTV.", 200),
        AchievementMedal("ach_k9_partner", "3. Good Boy, Rex!", "Use K9 Dog Rex to sniff out hidden evidence.", 200),
        AchievementMedal("ach_cruiser", "4. Siren & Asphalt", "Drive the NKPD Police Cruiser across the city.", 150),
        AchievementMedal("ach_undercover", "5. Master of Disguise", "Use a disguise for an undercover operation.", 250),
        AchievementMedal("ach_friends", "6. Squad Shopping Spree", "Go shopping with friends Zara & Leo at the Boutique.", 250),
        AchievementMedal("ach_bank_hero", "7. Vault Guardian", "Stop an active Bank Robbery in the Open World.", 400),
        AchievementMedal("ach_halfway", "8. Sergeant's Star", "Solve 10 unique story cases.", 500),
        AchievementMedal("ach_compassion", "9. Heart of Gold", "Counsel and reform Ayan, Miss Nadia, or young Sami.", 350),
        AchievementMedal("ach_interrogator", "10. Truth Extractor", "Break 5 suspect alibis in Police Station Interrogation.", 400),
        AchievementMedal("ach_cctv_hawk", "11. Eye in the Sky", "Spot criminals using the CCTV Surveillance Grid.", 300),
        AchievementMedal("ach_clean_justice", "12. Incorruptible Shield", "Reach 85% or higher on the Justice Meter.", 500),
        AchievementMedal("ach_shadow_broker", "13. Whispering Bribes", "Test the Corruption path via Bribe or Blackmail.", 300),
        AchievementMedal("ach_ai_forensics", "14. AI Crime Lab Pioneer", "Create or edit a forensic photo in AI Photo Mode.", 400),
        AchievementMedal("ach_inspector", "15. Promoted to Inspector", "Reach the highest rank of Inspector.", 1000),
        AchievementMedal("ach_savior", "16. Savior of Noir City", "Defuse the Central Station bomb in Task 20!", 1500)
    )

    val tutorialSteps: List<TutorialStepInfo> = listOf(
        TutorialStepInfo(1, "Step 1/9: Corner HUD & Needs", "Top-left shows Sara's Rank, Cash, 9AM-5PM Duty Clock, Hunger/Thirst, and Unlimited Medkits (+HP ∞)."),
        TutorialStepInfo(2, "Step 2/9: Mini-Map & Auto-Walk", "Tap the Top-Right Mini-Map to open the Full City Map. Tap any building to Auto-Walk or Auto-Drive there!"),
        TutorialStepInfo(3, "Step 3/9: Driving & K9 Rex", "Use Bottom-Left controls to walk, toggle the Police Cruiser siren car, or command K9 Dog Rex to sniff clues."),
        TutorialStepInfo(4, "Step 4/9: 20 Unique Story Cases", "Open '20 Cases' to play each criminal's unique story ($500 standard bonus, $1000 Hard mission bonus)."),
        TutorialStepInfo(5, "Step 5/9: Clues & Hint System", "At every crime scene, inspect clue hotspots or tap 'Hint' for Partner Ryan's deduction."),
        TutorialStepInfo(6, "Step 6/9: Interrogation & Choices", "Bring major suspects to Police Station Interrogation, then choose: Arrest (Justice), Bribe, or Blackmail!"),
        TutorialStepInfo(7, "Step 7/9: Shops, Disguises & Friends", "Visit Weapon Shop, Disguise Shop, and Dress Boutique to shop with friends Zara & Leo."),
        TutorialStepInfo(8, "Step 8/9: CCTV, Jail & Bank Defense", "Monitor 6 live CCTV cameras, visit arrested criminals in County Jail, and stop Bank Robberies!"),
        TutorialStepInfo(9, "Step 9/9: Voice Acting, AI Photo & Veo", "Use Voice Acting in cutscenes, generate/edit images in Photo Mode, animate Veo videos, and check Live Crime News!")
    )

    val phoneMessages: List<PhoneMessage> = listOf(
        PhoneMessage("Partner Ryan", "PARTNER", "09:02 AM", "Morning Sara! Cruiser is fueled up outside Precinct HQ. Central Bank called about a vault heist—Task 1 is ready!"),
        PhoneMessage("Zara (Best Friend)", "FRIEND", "10:15 AM", "Hey Sara! Leo and I are at the Dress Boutique! Come shop with us after your shift for a 20% friend discount & coffee!"),
        PhoneMessage("Leo (Friend)", "FRIEND", "11:30 AM", "Sara, I spotted suspicious bikers near Greenview Park. Stay sharp out there—and bring Rex a treat!"),
        PhoneMessage("Chief Miller", "HQ COMMAND", "01:00 PM", "Officer Sara, remember duty hours are 9 AM to 5 PM, though undercover & night raids pay double XP. Keep up the great work."),
        PhoneMessage("Shadow Informant", "TIP LINE", "11:45 PM", "Psst... Check Camera 4 at Central Bank and Locker 7 at the Museum. And don't trust Chef Bashir's soup!")
    )

    val cctvFeeds: List<CctvFeedInfo> = listOf(
        CctvFeedInfo("cam_bank", "CAM-01 [VAULT]", "Central Bank Vault", "02:04 AM Clip: Cleaner Ramu at safe dial", 1, true),
        CctvFeedInfo("cam_parking", "CAM-02 [LOT-B]", "Downtown Parking Lot", "Night Feed: Vicky cloning transponder key", 2, true),
        CctvFeedInfo("cam_casino", "CAM-03 [VIP-1]", "Starlight Casino Table", "Overhead 4K: Dealer Marco sleeve card swap", 12, true),
        CctvFeedInfo("cam_club", "CAM-04 [NEON]", "Neon Velvet Backroom", "Thermal Feed: Illegal roulette & prisoner Raju", 6, false),
        CctvFeedInfo("cam_hospital", "CAM-05 [MED-3]", "Hospital Pharmacy Store", "Motion Alert: Midnight keycard entry logged", 7, false),
        CctvFeedInfo("cam_station", "CAM-06 [RAIL]", "Central Train Platform 3", "High Alert: Suspicious locker package detected", 20, true)
    )

    val endings: List<GameEndingInfo> = listOf(
        GameEndingInfo(
            id = "ending_justice",
            title = "Ending 1: Paragon of Justice (Commissioner Sara)",
            conditionText = "Unlocked at High Justice (Justice >= 70%)",
            epilogueStory = "Refusing every bribe and blackmail temptation, Inspector Sara dismantled all 20 criminal operations across Noir City. Alongside Partner Ryan, loyal K9 Rex, and friends Zara & Leo, Sara is awarded the Golden Shield of Honor and appointed the youngest Police Commissioner in city history!",
            saraQuote = "A badge isn't worn on the coat—it's worn on the conscience. This city belongs to its honest people now."
        ),
        GameEndingInfo(
            id = "ending_grey",
            title = "Ending 2: Grey Shadow Vigilante",
            conditionText = "Unlocked at Balanced Meter (Justice 40%–69%)",
            epilogueStory = "Walking the razor's edge between strict law and street pragmatism, Inspector Sara bent the rules when necessary to protect the innocent and reform the desperate. Neither politician nor syndicate boss dares cross the Shadow Detective who watches the rain-slicked streets at midnight.",
            saraQuote = "The law sees in black and white, Ryan. Out here in the rain, justice lives in the shadows."
        ),
        GameEndingInfo(
            id = "ending_corruption",
            title = "Ending 3: Syndicate Kingpin (Shadow Boss)",
            conditionText = "Unlocked at High Corruption (Corruption >= 60%)",
            epilogueStory = "Through strategic bribes at the Neon Club and blackmail folders locked in her apartment safe, Inspector Sara quietly took control of every underground empire she was sent to bust. Wearing the golden Inspector badge by day and ruling the city's vaults by night, nobody can touch her.",
            saraQuote = "Why arrest the kingpins when they can pay rent to the badge? The whole city answers to me now."
        )
    )
}
