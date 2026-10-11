package com.example.data.model

data class ClueHotspot(
    val id: String,
    val label: String,
    val detail: String,
    val forensicResult: String,
    val requiresRex: Boolean = false
)

data class DialogueLine(
    val speaker: String, // "Sara", "Ryan", "Rex", or Criminal Name
    val roleTag: String, // "HERO", "PARTNER", "K9", "CRIMINAL"
    val text: String
)

data class InterrogationPrompt(
    val question: String,
    val suspectLie: String,
    val evidenceCounter: String,
    val confessionLine: String
)

data class DetectiveTask(
    val id: Int,
    val title: String,
    val hindiSubtitle: String,
    val criminalName: String,
    val criminalAlias: String,
    val locationId: String,
    val locationName: String,
    val mapX: Float,
    val mapY: Float,
    val isHardMission: Boolean, // $500 standard, $1000 hard
    val isNightMission: Boolean,
    val requiresDisguise: Boolean = false,
    val requiresInterrogation: Boolean = false, // "41. Sirf zaroori interrogation"
    val allowCompassionateReform: Boolean = false, // For Tasks 9, 13, 16
    val briefing: String,
    val howSaraCatches: String,
    val ryanHint: String,
    val clues: List<ClueHotspot>,
    val dialogues: List<DialogueLine>,
    val interrogation: InterrogationPrompt?,
    val criminalHp: Int = 100,
    val jailQuote: String
) {
    val cashReward: Int get() = if (isHardMission) 1000 else 500
}

object StoryTasksCatalog {
    val allTasks: List<DetectiveTask> = listOf(
        DetectiveTask(
            id = 1,
            title = "Vault at 2 AM",
            hindiSubtitle = "1. Paise Chori — Bank CCTV & Bed Stash",
            criminalName = "Ramu",
            criminalAlias = "Night Shift Cleaner",
            locationId = "loc_bank",
            locationName = "Central Bank -> Ramu's House",
            mapX = 0.35f,
            mapY = 0.25f,
            isHardMission = false,
            isNightMission = false,
            requiresInterrogation = true,
            briefing = "Central Bank's main vault was emptied overnight without forced entry. Review the 2:00 AM CCTV tape and raid the culprit's home at dawn.",
            howSaraCatches = "Sara checks Bank CCTV at 2:00 AM and spots night cleaner Ramu unlocking the safe! In the morning, Sara raids his house, finds cash bags under his bed, and arrests him!",
            ryanHint = "Check the 02:00 AM Vault CCTV timestamp first, then search underneath the wooden bed in Ramu's quarters.",
            clues = listOf(
                ClueHotspot("t1_c1", "02:00 AM Vault CCTV Tape", "Camera 4 shows night janitor Ramu entering the vault combination at 2:04 AM.", "Facial & Uniform Match: Ramu (Staff #14)"),
                ClueHotspot("t1_c2", "Master Key Logbook", "No alarm triggered; janitor mop cart blocked the motion sensor.", "Inside Job Confirmed"),
                ClueHotspot("t1_c3", "Under Ramu's Bed (Morning Raid)", "Rex sniffs crisp polymer banknotes stuffed inside two mattress sacks under the bed!", "Recovered $250,000 Marked Banknotes", requiresRex = true)
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Play back Camera 4 at 2:00 AM... Wait, zoom in! That's Ramu the cleaner opening the vault dial!"),
                DialogueLine("Ryan", "PARTNER", "Sharp eyes, Sara. Let's raid his house first thing in the morning before he moves the cash."),
                DialogueLine("Ramu", "CRIMINAL", "Officer Sara?! How did you find me—please, don't look under the bed!")
            ),
            interrogation = InterrogationPrompt(
                question = "Ramu, how did you know the 6-digit Central Bank vault combination at 2 AM?",
                suspectLie = "I was only mopping the lobby floor, Madam! I never touched the vault!",
                evidenceCounter = "Present 02:04 AM CCTV Freeze-Frame + Marked Bundles Found Under His Bed",
                confessionLine = "Okay, okay! I saw the manager write the code under his desk blotter. Greed blinded me!"
            ),
            criminalHp = 85,
            jailQuote = "I should have stuck to mopping floors... Sara's eyes miss nothing on CCTV."
        ),
        DetectiveTask(
            id = 2,
            title = "The Keymaker's Chop Shop",
            hindiSubtitle = "2. Car Chor — Parking CCTV & 10 Stolen Cars",
            criminalName = "Vicky",
            criminalAlias = "The Keymaker",
            locationId = "loc_parking",
            locationName = "Downtown Parking -> Hidden Garage",
            mapX = 0.52f,
            mapY = 0.30f,
            isHardMission = false,
            isNightMission = true,
            requiresInterrogation = false,
            briefing = "Luxury cars are vanishing from Downtown Parking. Plant a covert night camera, tail the key cloner, and bust the hidden garage.",
            howSaraCatches = "Sara installs covert CCTV in the parking lot. At night, Vicky crafts a duplicate car key! Sara tails him to a secret garage holding 10 stolen cars and calls police backup to arrest him!",
            ryanHint = "Mount the covert pinhole camera on Pillar B, then follow the tire tracks to the shuttered garage.",
            clues = listOf(
                ClueHotspot("t2_c1", "Covert Parking CCTV Feed", "Night vision captures a youth using a digital transponder key programmer.", "Suspect Identified: Vicky"),
                ClueHotspot("t2_c2", "Cloned Key Fob Shavings", "Brass key filings and RF relay device dropped near Spot #19.", "Key-Cloning Tool Signature"),
                ClueHotspot("t2_c3", "Hidden Garage Warehouse", "Inside the locked shutter: 10 stolen luxury cars with swapped plates!", "10 Stolen Vehicles Recovered")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Camera is live. Look—he just forged a car key in forty seconds. Don't arrest him yet, let's tail him."),
                DialogueLine("Ryan", "PARTNER", "Ten stolen cars inside one garage! Calling all precinct units to surround the exits!"),
                DialogueLine("Vicky", "CRIMINAL", "Ten rides ready for export and you tailed me all the way from the lot?!")
            ),
            interrogation = null,
            criminalHp = 95,
            jailQuote = "I could unlock any car in sixty seconds, but I can't unlock this cell door."
        ),
        DetectiveTask(
            id = 3,
            title = "Size 10 Diamond Trail",
            hindiSubtitle = "3. Heere Ka Chor — Size 10 Boot Prints",
            criminalName = "Kamran",
            criminalAlias = "The Jeweler's Brother",
            locationId = "loc_jewelry",
            locationName = "Royal Diamond Shop -> Brother's Home",
            mapX = 0.66f,
            mapY = 0.22f,
            isHardMission = false,
            isNightMission = false,
            requiresInterrogation = true,
            briefing = "Uncut diamonds were stolen from the back vault of Royal Jewelers. Check the alley behind the shop for footprints.",
            howSaraCatches = "Behind the shop, Sara finds muddy shoe prints — Size 10! The shopkeeper's brother wears the exact same Size 10 boots! Sara searches his house and recovers the diamonds!",
            ryanHint = "Measure the mud tread behind the back window: Size 10 custom sole. Compare it with the brother's boots.",
            clues = listOf(
                ClueHotspot("t3_c1", "Muddy Alley Boot Prints", "Clear shoe impression in wet alley clay behind the shop window: Exact Size 10.", "Footwear Cast: Size 10 Zig-Zag Tread"),
                ClueHotspot("t3_c2", "Brother's Shoe Rack", "The shopkeeper's brother Kamran has Size 10 boots with wet alley clay on the heel!", "100% Tread & Size 10 Match"),
                ClueHotspot("t3_c3", "Velvet Diamond Pouch", "Rex barks at a false ceiling tile in Kamran's bedroom—32 uncut diamonds inside!", "All Stolen Diamonds Recovered", requiresRex = true)
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Look behind the shop window—deep footprints, Size 10. Wait... aren't your brother's shoes the exact same pattern?"),
                DialogueLine("Rex", "K9", "Woof! Grrr! (Scents diamond pouch in Kamran's wardrobe!)"),
                DialogueLine("Kamran", "CRIMINAL", "My own brother's shop... I thought nobody would check my shoe size!")
            ),
            interrogation = InterrogationPrompt(
                question = "Kamran, why do your Size 10 boots have the exact red clay from the alley behind your brother's diamond shop?",
                suspectLie = "Lots of people wear Size 10 shoes in this city, Officer Sara!",
                evidenceCounter = "Present Plaster Cast of Chipped Left Heel + Velvet Diamond Pouch from His Ceiling",
                confessionLine = "I had gambling debts! I knew my brother turned off the back sensor at 8 PM!"
            ),
            criminalHp = 90,
            jailQuote = "Betraying my own brother over diamonds... and caught by a Size 10 footprint."
        ),
        DetectiveTask(
            id = 4,
            title = "School Gate Poison",
            hindiSubtitle = "4. Drug Dealer — Canteen Vendor & Supplier Phone",
            criminalName = "Tariq & Nadeem",
            criminalAlias = "Canteen Pusher & Syndicate Supplier",
            locationId = "loc_school",
            locationName = "City High School -> Supplier Depot",
            mapX = 0.18f,
            mapY = 0.48f,
            isHardMission = true,
            isNightMission = false,
            requiresInterrogation = true,
            briefing = "Someone is slipping illicit pills to high school students. Stake out outside the school gates and trace the supply chain.",
            howSaraCatches = "Sara sits on stakeout outside the school. She catches the canteen vendor handing pills to kids! Sara checks his phone, finds the supplier's number, and arrests both of them!",
            ryanHint = "Observe the canteen stall at recess, confiscate Tariq's unlocked phone, and trace the pinned number 'Boss N'.",
            clues = listOf(
                ClueHotspot("t4_c1", "Binocular Stakeout Observation", "Canteen vendor Tariq slips foil packets inside snack wrappers.", "Eyewitness Video Recorded"),
                ClueHotspot("t4_c2", "Tariq's Confiscated Phone", "Encrypted chat log reveals main supplier Nadeem's warehouse address & phone number.", "Supplier Number Traced: Nadeem"),
                ClueHotspot("t4_c3", "Supplier Crate Bust", "Raid on Nadeem's depot uncovers 500 contraband boxes.", "Full Supply Chain Dismantled")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Got you red-handed at the school gate, Tariq! Hand over your phone right now."),
                DialogueLine("Ryan", "PARTNER", "Look at his call log—'Supplier Nadeem' called ten minutes ago. Let's take them both down!"),
                DialogueLine("Tariq & Nadeem", "CRIMINAL", "You traced my burner number from the canteen phone?!")
            ),
            interrogation = InterrogationPrompt(
                question = "Tariq, we have your chat messages with Supplier Nadeem. Confirm the drop schedule!",
                suspectLie = "Those were just vitamin tablets, Officer!",
                evidenceCounter = "Present Lab Analysis + Encrypted Phone Call Logs to Nadeem's Warehouse",
                confessionLine = "Nadeem forced me to sell them at the canteen! Here is his main vault key!"
            ),
            criminalHp = 120,
            jailQuote = "Targeting a school was our downfall. Sara took down both vendor and supplier in one sweep."
        ),
        DetectiveTask(
            id = 5,
            title = "Ransom in the Park",
            hindiSubtitle = "5. Kidnapping — Schoolbag Note & Decoy Cash Trap",
            criminalName = "Javed 'Viper'",
            criminalAlias = "Ransom Kidnapper",
            locationId = "loc_park",
            locationName = "Greenview Park -> Old Pier Drop",
            mapX = 0.28f,
            mapY = 0.65f,
            isHardMission = true,
            isNightMission = false,
            requiresInterrogation = false,
            briefing = "A child vanished near Greenview Park. Search the park benches for clues, prepare decoy ransom bills, and spring a trap with Ryan.",
            howSaraCatches = "Sara finds the child's bag in the park with a note: 'Bring the money!' Sara brings fake decoy money to the drop. When the kidnapper arrives, Sara and Ryan surround and arrest him — saving the child!",
            ryanHint = "Inspect the blue schoolbag by the fountain, read the ransom note, then signal Ryan when Javed reaches for the decoy briefcase.",
            clues = listOf(
                ClueHotspot("t5_c1", "Child's Schoolbag in Park", "Found near the park swing set with a torn note inside: 'Bring $50,000 cash alone!'", "Ransom Note Recovered"),
                ClueHotspot("t5_c2", "UV-Marked Decoy Briefcase", "Prepared prop banknotes dusted with invisible fluorescent tracker powder.", "Trap Briefcase Armed"),
                ClueHotspot("t5_c3", "Rex Scent Trail to Van", "Rex tracks the child's sneaker scent to a parked van behind the pier—child rescued unharmed!", "Hostage Child Safe!", requiresRex = true)
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Look in the child's bag—a note: 'Bring the money!' We'll pack fake decoy cash and surround the drop point."),
                DialogueLine("Ryan", "PARTNER", "I've got the rear exit blocked, Sara. Rex already found the kid safe in the van—move in!"),
                DialogueLine("Javed 'Viper'", "CRIMINAL", "Wait—this money is fake! And I'm completely surrounded!")
            ),
            interrogation = null,
            criminalHp = 130,
            jailQuote = "Sara's fake ransom cash fooled me completely, and Ryan cut off my escape route."
        ),
        DetectiveTask(
            id = 6,
            title = "Undercover Velvet Raid",
            hindiSubtitle = "6. Club Raid — Disguise, Backroom Gambling & Bribe Choice",
            criminalName = "Viktor Boss",
            criminalAlias = "Neon Club Kingpin",
            locationId = "loc_club",
            locationName = "Neon Velvet Club (Backroom)",
            mapX = 0.74f,
            mapY = 0.42f,
            isHardMission = false,
            isNightMission = true,
            requiresDisguise = true,
            requiresInterrogation = false,
            briefing = "Slip into an undercover disguise and enter the Neon Club. Expose the illegal backroom gambling ring—will you take Viktor's bribe or arrest him?",
            howSaraCatches = "Sara wears a disguise into the club and discovers illegal gambling in the back room! The owner offers Sara a heavy bribe — Sara chooses to arrest him or take the cash!",
            ryanHint = "Wear an Evening/VIP Disguise so the bouncer lets you into the back parlor.",
            clues = listOf(
                ClueHotspot("t6_c1", "VIP Bouncer Velvet Rope", "Undercover disguise fools the doorman; hidden door behind the jukebox spotted.", "Backroom Access Gained"),
                ClueHotspot("t6_c2", "Rigged Roulette & Cash Ledger", "Millions in untaxed chips and underground betting ledgers on the table.", "Illegal Gambling Ring Filmed"),
                ClueHotspot("t6_c3", "Viktor's Bribe Suitcase", "Viktor slides a suitcase of cash toward Sara: 'Take this and walk away, Detective.'", "Moral Choice Unlocked")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "My disguise worked. Look at this back room—illegal high-stakes tables wall to wall."),
                DialogueLine("Viktor Boss", "CRIMINAL", "Easy, Officer Sara! Why ruin a profitable night? Take this cash bribe and forget what you saw!"),
                DialogueLine("Ryan", "PARTNER", "Your call, Sara—slap the cuffs on him for Justice, or pocket the envelope?")
            ),
            interrogation = null,
            criminalHp = 100,
            jailQuote = "I offered her a fortune in cash right there in the club backroom!"
        ),
        DetectiveTask(
            id = 7,
            title = "Midnight Pharmacy Ambush",
            hindiSubtitle = "7. Dawa Chori — Hiding in Hospital Store Room",
            criminalName = "Nurse Sonia",
            criminalAlias = "Black-Market Medic",
            locationId = "loc_hospital",
            locationName = "City Hospital Store Room",
            mapX = 0.42f,
            mapY = 0.52f,
            isHardMission = false,
            isNightMission = true,
            requiresInterrogation = false,
            briefing = "Life-saving hospital medicines are disappearing every night. Hide inside the medical store room after lights-out and catch the thief red-handed.",
            howSaraCatches = "Sara hides inside the hospital store room at night. At midnight, Nurse Sonia sneaks in to steal medicines — Sara catches her red-handed!",
            ryanHint = "Conceal yourself behind Supply Rack C and wait for the midnight keycard swipe.",
            clues = listOf(
                ClueHotspot("t7_c1", "Missing Inventory Audit", "38 vials of rare surgical medicine missing exclusively during the night shift.", "Night Shift Pattern Confirmed"),
                ClueHotspot("t7_c2", "Store Room Ambush Spot", "Sara waits silently behind the sterile supply shelves at 12:15 AM.", "Visual Contact Established"),
                ClueHotspot("t7_c3", "Duffel Bag of Stolen Vials", "Caught Nurse Sonia loading sealed medicine boxes into a private duffel bag!", "Caught Red-Handed!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Freeze right there, Sonia! Drop the medicine bag—you're caught red-handed!"),
                DialogueLine("Nurse Sonia", "CRIMINAL", "Officer Sara?! You were hiding inside the store room the whole time?!"),
                DialogueLine("Ryan", "PARTNER", "Every single stolen vial is back where it belongs—for the patients who need it.")
            ),
            interrogation = null,
            criminalHp = 80,
            jailQuote = "Stealing hospital medicine in the dark... I never knew Sara was waiting behind the shelves."
        ),
        DetectiveTask(
            id = 8,
            title = "Speeding Bus Hijack",
            hindiSubtitle = "8. Bus Hijack — Police Car Chase & Roadblock Rescue",
            criminalName = "Roka",
            criminalAlias = "Highway Hijacker",
            locationId = "loc_bus_stop",
            locationName = "Metro Bus Stop -> Highway Roadblock",
            mapX = 0.16f,
            mapY = 0.20f,
            isHardMission = true,
            isNightMission = false,
            requiresInterrogation = false,
            briefing = "City Bus #42 has been hijacked with 24 passengers aboard! Chase the bus with Ryan in the police cruiser, set up a roadblock, and storm inside!",
            howSaraCatches = "Sara and Ryan chase the hijacked bus in their police car! A police roadblock forces the bus to halt — Sara boards the bus, overpowers Roka, and saves every passenger!",
            ryanHint = "Engage Cruiser Siren Boost to overtake the bus before the bridge and deploy the barricade.",
            clues = listOf(
                ClueHotspot("t8_c1", "Bus GPS Emergency Beacon", "Transit Bus #42 deviated from Route 7 at 75 MPH.", "Live Intercept Vector Locked"),
                ClueHotspot("t8_c2", "Cruiser Roadblock Barricade", "Ryan angles the police cruisers across the highway, forcing the driver to slam the brakes!", "Bus Immobilized"),
                ClueHotspot("t8_c3", "Front Door Tactical Entry", "Sara breaches the folding bus door and disarms Roka in two seconds flat!", "All 24 Passengers Rescued!")
            ),
            dialogues = listOf(
                DialogueLine("Ryan", "PARTNER", "Hold on tight, Sara! Roadblock is set two hundred meters ahead—he's braking!"),
                DialogueLine("Sara", "HERO", "Stay in your seats, everyone! Police! Drop the weapon, Roka—it's over!"),
                DialogueLine("Roka", "CRIMINAL", "How did your cruiser beat us to the highway exit?!")
            ),
            interrogation = null,
            criminalHp = 135,
            jailQuote = "Sara boarded that bus like lightning the second we hit the roadblock."
        ),
        DetectiveTask(
            id = 9,
            title = "Trace the Teen Hacker",
            hindiSubtitle = "9. Hacker — Cyber Cell IP Trace & Second Chance",
            criminalName = "Ayan (Age 19)",
            criminalAlias = "PhantomKey_19",
            locationId = "loc_cctv_tower",
            locationName = "Cyber Cell -> Ayan's Apartment",
            mapX = 0.84f,
            mapY = 0.24f,
            isHardMission = false,
            isNightMission = true,
            allowCompassionateReform = true,
            briefing = "Someone breached the city traffic grid firewall. Have the Cyber Cell trace the IP address to the hacker's home.",
            howSaraCatches = "Sara traces the IP address via the Cyber Cell and finds a 19-year-old boy on his laptop at home! Sara counsels him on using his talent for good — he apologizes tearfully!",
            ryanHint = "Run the packet tracer in the Cyber Cell to triangulate Apartment 3B, then talk to Ayan.",
            clues = listOf(
                ClueHotspot("t9_c1", "Cyber Cell IP Triangulation", "Reverse proxy peeled back: static residential IP 192.0.2.88 at Sector 4.", "Home Address Located"),
                ClueHotspot("t9_c2", "Open Terminal Laptop", "19-year-old engineering student Ayan caught mid-script at his bedroom desk.", "Zero Malicious Transfer—Just a Dare"),
                ClueHotspot("t9_c3", "Signed Apology & Cyber Cadet Pledge", "Sara explains the law and offers him a chance to help the Police Cyber Cell.", "Reformed Youth")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Cyber Cell traced your IP straight to this desk, Ayan. You're only nineteen—why ruin your future?"),
                DialogueLine("Ayan (Age 19)", "CRIMINAL", "I'm so sorry, Officer Sara! It was a college challenge—I never wanted to hurt anyone!"),
                DialogueLine("Sara", "HERO", "Use your mind to protect people, not break systems. Promise me you'll never cross this line again.")
            ),
            interrogation = null,
            criminalHp = 65,
            jailQuote = "Officer Sara gave me guidance instead of destroying my life. I'm helping Cyber Cell secure the grid now!"
        ),
        DetectiveTask(
            id = 10,
            title = "Ashes of Betrayal",
            hindiSubtitle = "10. Murder — Cigarette Butt DNA Match",
            criminalName = "Farhan",
            criminalAlias = "The Victim's Best Friend",
            locationId = "loc_alley",
            locationName = "Rainy Alley -> Farhan's House",
            mapX = 0.58f,
            mapY = 0.62f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = true,
            briefing = "A businessman was found murdered in a rainy alley. Bag the cigarette butt near the crime scene, run forensic DNA, and confront the killer.",
            howSaraCatches = "Sara picks up a cigarette butt near the body and runs a DNA test — it matches the victim's own friend Farhan! Sara arrests him at his house and he confesses!",
            ryanHint = "Use forensic tweezers on the imported clove cigarette filter by the puddle and run a DNA match.",
            clues = listOf(
                ClueHotspot("t10_c1", "Cigarette Butt by Crime Scene", "Half-smoked gold-ring cigarette preserved under an overhang near the victim.", "Saliva DNA Sample Extracted"),
                ClueHotspot("t10_c2", "Forensic Lab DNA Sequencer", "99.99% DNA profile match to Farhan—the victim's childhood friend and business partner!", "Killer Identified: Farhan"),
                ClueHotspot("t10_c3", "Bloodstained Cufflink at Farhan's Home", "Rex finds the missing cufflink in Farhan's coat pocket during the arrest.", "Physical Link Confirmed", requiresRex = true)
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "One cigarette butt left in the rain, Farhan. The DNA lab matched it directly to you."),
                DialogueLine("Ryan", "PARTNER", "You were supposed to be his best friend. Why did you do it?"),
                DialogueLine("Farhan", "CRIMINAL", "He was going to cut me out of the company! Yes... I did it. I confess.")
            ),
            interrogation = InterrogationPrompt(
                question = "Farhan, you claimed you weren't in the alley that night. Explain your DNA on this cigarette butt next to the body!",
                suspectLie = "I haven't been near that alley in months, Sara!",
                evidenceCounter = "Present Forensic DNA Report (99.99% Match) + Victim's Cufflink Found in His Coat",
                confessionLine = "Stop... I can't lie anymore. We argued over the contract and I lost control!"
            ),
            criminalHp = 125,
            jailQuote = "A single cigarette butt in the rain gave me away to Detective Sara."
        ),
        DetectiveTask(
            id = 11,
            title = "Fingerprints on the Frame",
            hindiSubtitle = "11. Painting Chori — Wall Fingerprints & Guard's Locker",
            criminalName = "Guard Sikandar",
            criminalAlias = "Inside Gallery Guard",
            locationId = "loc_museum",
            locationName = "City Art Gallery -> Guard Locker",
            mapX = 0.46f,
            mapY = 0.18f,
            isHardMission = false,
            isNightMission = false,
            requiresInterrogation = true,
            briefing = "A priceless heritage painting was cut from its frame at the Art Gallery. Dust the wall for fingerprints and search the staff lockers.",
            howSaraCatches = "Sara lifts fingerprints from the gallery wall — they match Guard Sikandar! She opens his locker, recovers the rolled-up painting, and arrests him!",
            ryanHint = "Apply silver fingerprint powder to the empty wall mount, then unlock Staff Locker #07.",
            clues = listOf(
                ClueHotspot("t11_c1", "Latent Fingerprints on Wall", "UV powder reveals four clear thumbprints on the gilded frame hook.", "AFIS Match: Guard Sikandar"),
                ClueHotspot("t11_c2", "Disabled Laser Switch", "Gallery Room 3 sensor was switched off using Sikandar's master badge.", "Badge Log Verified"),
                ClueHotspot("t11_c3", "Guard Sikandar's Metal Locker", "Inside Locker #07: the stolen masterpiece rolled inside a blueprint tube!", "Masterpiece Recovered!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Your fingerprints were right on the gallery wall, Sikandar. Open your locker now."),
                DialogueLine("Guard Sikandar", "CRIMINAL", "Wait—don't open Locker 7! I was only holding the painting until tonight!"),
                DialogueLine("Ryan", "PARTNER", "Guarding the museum by day, robbing it by night. You're under arrest!")
            ),
            interrogation = InterrogationPrompt(
                question = "Sikandar, how did the stolen canvas end up inside your personal locker with your thumbprints on the wall?",
                suspectLie = "Someone framed me and planted it in my locker!",
                evidenceCounter = "Present Wall Thumbprint Lift + Master Badge Disarm Timestamp",
                confessionLine = "An art collector offered me fifty grand! I took it off the wall myself."
            ),
            criminalHp = 95,
            jailQuote = "I forgot to wear gloves when I lifted the frame... Sara dusted the wall in minutes."
        ),
        DetectiveTask(
            id = 12,
            title = "Rigged Deck at Starlight",
            hindiSubtitle = "12. Casino Dhokha — CCTV Card Swap & Double Arrest",
            criminalName = "Dealer Marco & Zafar",
            criminalAlias = "Card Mechanic Duo",
            locationId = "loc_casino",
            locationName = "Starlight Casino VIP Floor",
            mapX = 0.82f,
            mapY = 0.58f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = false,
            briefing = "Starlight Casino suspects a sophisticated cheating conspiracy at Table 1. Monitor the overhead CCTV and bust the dealer and player in the act.",
            howSaraCatches = "Sara watches the Casino CCTV and catches the dealer swapping cards from his sleeve for his accomplice! Sara raids the table and arrests both of them together!",
            ryanHint = "Slow down Overhead Camera 2 to 0.25x speed when Dealer Marco deals the river card.",
            clues = listOf(
                ClueHotspot("t12_c1", "4K Overhead Table CCTV", "Slow-motion replay shows Dealer Marco using a mechanical sleeve holdout to swap Aces.", "Card Swap Caught on Tape"),
                ClueHotspot("t12_c2", "Coded Chip Signals", "High-roller Zafar taps his ring twice to signal which suit he needs.", "Accomplice Confirmed: Zafar"),
                ClueHotspot("t12_c3", "Spring-Loaded Sleeve Rig", "Confiscated hidden card-injector strapped to Marco's wrist.", "Physical Rig Seized")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Look at the CCTV monitor—the dealer just swapped the Ace from his cuff! Let's grab both of them at once!"),
                DialogueLine("Ryan", "PARTNER", "Hands on the felt, Marco and Zafar! Nobody leaves the table!"),
                DialogueLine("Dealer Marco & Zafar", "CRIMINAL", "How did you spot the sleeve swap through the overhead dome?!")
            ),
            interrogation = null,
            criminalHp = 115,
            jailQuote = "Our card trick fooled the pit bosses, but it couldn't fool Sara on the CCTV monitor."
        ),
        DetectiveTask(
            id = 13,
            title = "Ink of Tears",
            hindiSubtitle = "13. School Letter — Handwriting Match & Dismissed Teacher",
            criminalName = "Miss Nadia",
            criminalAlias = "Wrongfully Dismissed Teacher",
            locationId = "loc_school",
            locationName = "School Principal's Office",
            mapX = 0.22f,
            mapY = 0.44f,
            isHardMission = false,
            isNightMission = false,
            allowCompassionateReform = true,
            briefing = "Mysterious warning letters were slipped under the Principal's door. Match the handwriting against staff records to uncover the truth.",
            howSaraCatches = "Sara matches the letter's handwriting to Miss Nadia, an upset teacher who was unfairly fired! She breaks into tears, and Sara counsels her and helps resolve her grievance peacefully!",
            ryanHint = "Compare the cursive 'g' and 'y' loops in the letter against the archived staff grading notebooks.",
            clues = listOf(
                ClueHotspot("t13_c1", "Fountain Pen Warning Letter", "Written in violet ink with distinctive slanted calligraphy.", "Handwriting Sample Scanned"),
                ClueHotspot("t13_c2", "Staff Grading Ledger Match", "98% handwriting match to Miss Nadia, terminated without notice last month.", "Author Identified: Miss Nadia"),
                ClueHotspot("t13_c3", "Unfair Dismissal File", "Records show she was blamed for a clerical error she didn't commit.", "Motive: Desperation, Not Malice")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Miss Nadia, the handwriting on the letter matches your grading notebook. Why did you write it?"),
                DialogueLine("Miss Nadia", "CRIMINAL", "(Crying) I taught here for ten years and they fired me without hearing the truth! I was heartbroken!"),
                DialogueLine("Sara", "HERO", "Threats aren't the way, Miss Nadia. Come—I'll present the clerical proof to the board so you get justice the right way.")
            ),
            interrogation = null,
            criminalHp = 60,
            jailQuote = "Officer Sara listened to my tears and helped clear my name while teaching me a lesson."
        ),
        DetectiveTask(
            id = 14,
            title = "Bitter Recipe",
            hindiSubtitle = "14. Zehar — Poison Vial in the Chef's Bag",
            criminalName = "Chef Bashir",
            criminalAlias = "Vengeful Head Cook",
            locationId = "loc_restaurant",
            locationName = "Golden Spice Restaurant Kitchen",
            mapX = 0.50f,
            mapY = 0.45f,
            isHardMission = true,
            isNightMission = false,
            requiresInterrogation = true,
            briefing = "The restaurant owner fell critically ill after tasting the evening soup. Search the kitchen for the poison bottle before evidence is washed away.",
            howSaraCatches = "Sara searches the kitchen and finds a glass vial of poison hidden inside Chef Bashir's bag! He admits he poisoned the owner over an old grudge — arrested!",
            ryanHint = "Have Rex sniff the staff lockers in the back of the kitchen for bitter almond toxin.",
            clues = listOf(
                ClueHotspot("t14_c1", "Tainted Soup Ladle", "Chemical swab turns crimson—alkaloid toxin added after cooking.", "Poison Confirmed in Kitchen"),
                ClueHotspot("t14_c2", "Chef Bashir's Leather Bag", "Rex alerts on Bashir's bag—a half-empty amber glass vial of poison inside!", "Poison Vial Found in Chef's Bag", requiresRex = true),
                ClueHotspot("t14_c3", "Torn Partnership Dispute Letter", "Letter proving a bitter personal feud between Chef Bashir and the restaurant owner.", "Motive Established")
            ),
            dialogues = listOf(
                DialogueLine("Rex", "K9", "Woof! (Paws at the black canvas bag under the prep counter!)"),
                DialogueLine("Sara", "HERO", "A glass vial of poison right inside your bag, Chef Bashir! The hospital already has the antidote from this label."),
                DialogueLine("Chef Bashir", "CRIMINAL", "The owner insulted my family and stole my recipes! I wanted revenge!")
            ),
            interrogation = InterrogationPrompt(
                question = "Chef Bashir, this exact poison vial was found zipped inside your personal bag in the kitchen!",
                suspectLie = "That's just rat medicine for the pantry, Officer!",
                evidenceCounter = "Present Chemical Match to Owner's Soup + Bashir's Fingerprints on the Glass Vial",
                confessionLine = "Fine! I hated the owner for years and slipped three drops into his bowl!"
            ),
            criminalHp = 110,
            jailQuote = "Rex sniffed out that vial in my kitchen bag in thirty seconds."
        ),
        DetectiveTask(
            id = 15,
            title = "Alleyway Decoy Sprint",
            hindiSubtitle = "15. Purse Snatching — Plainclothes Bait & Biker Chase",
            criminalName = "Billa 'Biker'",
            criminalAlias = "Speed Snatcher",
            locationId = "loc_park",
            locationName = "Central Park -> Narrow Back Alley",
            mapX = 0.32f,
            mapY = 0.70f,
            isHardMission = false,
            isNightMission = false,
            requiresDisguise = true,
            briefing = "A motorbike purse-snatcher is terrorizing women in the park. Walk into the park in plain clothes as bait, then chase him into the alley!",
            howSaraCatches = "Sara goes to the park in plain clothes carrying a decoy purse. When the biker snatches it, Sara chases him down and corners him inside a narrow alley!",
            ryanHint = "Equip Plainclothes Dress/Disguise, let Billa grab the GPS-tagged purse, and cut through the brick alley.",
            clues = listOf(
                ClueHotspot("t15_c1", "Plainclothes GPS Decoy Purse", "Sara walks along the park path in casual clothes holding a tracker purse.", "Biker Takes the Bait!"),
                ClueHotspot("t15_c2", "Skid Marks into Brick Alley", "Billa turns into a narrow dead-end lane where his bike handlebars jam!", "Suspect Cornered in Alley"),
                ClueHotspot("t15_c3", "Stash of 14 Stolen Purses", "Saddlebag on his motorcycle holds fourteen stolen wallets and phones!", "All Stolen Purses Recovered")
            ),
            dialogues = listOf(
                DialogueLine("Billa 'Biker'", "CRIMINAL", "Haha, easy purse! Wait—why is this girl running faster than my bike?!"),
                DialogueLine("Sara", "HERO", "Dead end, Billa! And I'm not a tourist—Officer Sara, NKPD! Off the bike!"),
                DialogueLine("Ryan", "PARTNER", "Plainclothes bait worked like a charm. Fourteen stolen purses recovered!")
            ),
            interrogation = null,
            criminalHp = 100,
            jailQuote = "I thought she was an easy target in plain clothes—turned out to be Officer Sara!"
        ),
        DetectiveTask(
            id = 16,
            title = "Bread Before Bars",
            hindiSubtitle = "16. Pickpocket — Hungry 14-Year-Old Boy in the Crowd",
            criminalName = "Sami (Age 14)",
            criminalAlias = "Hungry Street Kid",
            locationId = "loc_restaurant",
            locationName = "Crowded Bazaar -> Golden Spice",
            mapX = 0.48f,
            mapY = 0.38f,
            isHardMission = false,
            isNightMission = false,
            allowCompassionateReform = true,
            briefing = "Wallets are vanishing in the busy market crowd. Stand among the shoppers, catch the pickpocket's hand, and uncover the real story.",
            howSaraCatches = "Sara stands in the crowded bazaar and catches a 14-year-old boy picking a pocket! Discovering he hasn't eaten in two days, Sara buys him a hot meal and guides him with kindness!",
            ryanHint = "Watch the crowd near the fruit carts; when you catch the boy, choose compassion.",
            clues = listOf(
                ClueHotspot("t16_c1", "Crowd Surveillance", "A trembling 14-year-old boy reaches for a shopper's coat pocket.", "Caught by the Wrist"),
                ClueHotspot("t16_c2", "Empty Stomach & Torn Shoes", "The boy has no gang ties—he was trying to buy bread for his little sister.", "Compassion Case Verified"),
                ClueHotspot("t16_c3", "Hot Meal & School Enrollment", "Sara takes Sami to the restaurant for a warm meal and arranges school support.", "Heartwarming Resolution")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Got your wrist, little brother. You're only fourteen—why are you picking pockets in the crowd?"),
                DialogueLine("Sami (Age 14)", "CRIMINAL", "(Trembling) Please don't beat me, Didi! I haven't eaten in two days!"),
                DialogueLine("Sara", "HERO", "Come with me to the restaurant first. You'll eat a full meal today, and you'll never steal again.")
            ),
            interrogation = null,
            criminalHp = 50,
            jailQuote = "Officer Sara fed me when I was starving and put me back in school. She's my hero."
        ),
        DetectiveTask(
            id = 17,
            title = "Gold Out the Window",
            hindiSubtitle = "17. Smuggling — Hotel Raid, Gold Suitcase & Window Catch",
            criminalName = "Don Salazar",
            criminalAlias = "Gold Bullion Smuggler",
            locationId = "loc_hotel",
            locationName = "Grand Hotel Suite 404",
            mapX = 0.68f,
            mapY = 0.72f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = true,
            briefing = "Intel says an international smuggler is hiding contraband gold in Room 404 of the Grand Hotel. Position Ryan below the window and raid the suite!",
            howSaraCatches = "Sara raids the hotel room and finds solid gold bars inside a suitcase! The smuggler jumps out the window to flee — and Partner Ryan catches him right below!",
            ryanHint = "Station Ryan in the courtyard under the 4th-floor fire escape before breaching Suite 404.",
            clues = listOf(
                ClueHotspot("t17_c1", "Heavy Titanium Suitcase", "X-ray scanner shows 24 kilograms of unstamped solid gold bullion bars inside!", "Smuggled Gold Found!"),
                ClueHotspot("t17_c2", "Open Balcony Window", "Salazar vaults out the window onto the awning to escape Sara's raid!", "Suspect Jumped Window"),
                ClueHotspot("t17_c3", "Ryan's Courtyard Intercept", "Ryan tackles Salazar the moment he lands on the alley fire escape below!", "Caught by Partner Ryan!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "NKPD! The suitcase is full of smuggled gold bars! Wait—he's jumping out the window!"),
                DialogueLine("Ryan", "PARTNER", "Right into my handcuffs! Nice try, Salazar—Sara told me to wait down here!"),
                DialogueLine("Don Salazar", "CRIMINAL", "Impossible! You had the window covered too?!")
            ),
            interrogation = InterrogationPrompt(
                question = "Salazar, who financed the 24 kilos of smuggled gold bullion in Suite 404?",
                suspectLie = "Those gold bars are family heirlooms, Detective!",
                evidenceCounter = "Present Unstamped Smuggler Serial Numbers + Forged Customs Manifest",
                confessionLine = "Alright! The port syndicate paid me to move the gold through the hotel!"
            ),
            criminalHp = 140,
            jailQuote = "I jumped out the hotel window to escape Sara and landed straight into Ryan's cuffs!"
        ),
        DetectiveTask(
            id = 18,
            title = "Surround the Safehouse",
            hindiSubtitle = "18. Faraar Mujrim — Informant Tip & House Siege Surrender",
            criminalName = "Shera Khan",
            criminalAlias = "Most-Wanted Fugitive",
            locationId = "loc_safehouse",
            locationName = "Old Suburb Safehouse",
            mapX = 0.14f,
            mapY = 0.78f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = false,
            briefing = "An informant tipped off Sara that fugitive Shera Khan is hiding inside his own house. Surround the perimeter so he lays down his weapons!",
            howSaraCatches = "Sara gets a tip that the fugitive is hiding in his house! Sara, Ryan, and police units surround the house completely — seeing no escape, he drops his weapons and surrenders!",
            ryanHint = "Position cruisers at the front gate and garden door, then use the megaphone to force his surrender.",
            clues = listOf(
                ClueHotspot("t18_c1", "Informant Phone Tip", "Encrypted SMS: 'Shera returned to his family house at 11 PM.'", "Hideout Confirmed"),
                ClueHotspot("t18_c2", "360° Tactical Perimeter", "Sara, Ryan, and Rex lock down the roof, back door, and front street.", "Zero Escape Routes"),
                ClueHotspot("t18_c3", "Dropped Weapons on Porch", "Blinded by cruiser spotlights, Shera walks out with hands raised and drops his rifle!", "Bloodless Surrender!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "(Megaphone) Shera Khan! Your house is completely surrounded by NKPD! Throw down your weapons!"),
                DialogueLine("Ryan", "PARTNER", "Front, rear, and rooftop sealed, Sara. He has nowhere to run."),
                DialogueLine("Shera Khan", "CRIMINAL", "Don't shoot! I'm dropping my weapons—I surrender to Officer Sara!")
            ),
            interrogation = null,
            criminalHp = 145,
            jailQuote = "When I looked out the window and saw Sara had the whole house surrounded, I knew it was over."
        ),
        DetectiveTask(
            id = 19,
            title = "Triple Jailbreak Manhunt",
            hindiSubtitle = "19. Jailbreak — Club, Godown & Highway Car Chase",
            criminalName = "Raju, Kalia & Zorawar",
            criminalAlias = "The 3 Escaped Convicts",
            locationId = "loc_jail",
            locationName = "Club -> East Godown -> City Outskirts",
            mapX = 0.88f,
            mapY = 0.76f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = false,
            briefing = "3 prisoners escaped County Jail! Catch #1 in the Club, #2 in the East Godown, and chase down #3 fleeing out of the city by car!",
            howSaraCatches = "3 inmates escaped! Sara catches the first in the Club, corners the second in the Godown warehouse, and chases the third fleeing the city in a high-speed car pursuit!",
            ryanHint = "Sweep the Neon Club booth for Raju, send Rex into the Godown for Kalia, then hit the highway in the cruiser for Zorawar!",
            clues = listOf(
                ClueHotspot("t19_c1", "Capture #1: Neon Club VIP Booth", "Sara spots escaped convict Raju hiding under a hoodie in the Club and cuffs him!", "Prisoner 1/3 Captured (Club)"),
                ClueHotspot("t19_c2", "Capture #2: East Godown Crates", "Rex tracks Kalia behind stacked shipping crates in the dark Godown warehouse!", "Prisoner 2/3 Captured (Godown)", requiresRex = true),
                ClueHotspot("t19_c3", "Capture #3: Outskirts Highway PIT", "Sara & Ryan chase Zorawar's getaway sedan at 110 MPH on the highway and spin him out!", "All 3 Escaped Prisoners Captured!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "Raju caught in the Club, Kalia pinned in the Godown! Only Zorawar left—he's heading for the highway!"),
                DialogueLine("Ryan", "PARTNER", "Floor it, Sara! Pit maneuver on my mark—three, two, one!"),
                DialogueLine("Raju, Kalia & Zorawar", "CRIMINAL", "Three different hideouts and Sara still caught all three of us in one night?!")
            ),
            interrogation = null,
            criminalHp = 160,
            jailQuote = "Club, godown, highway—no matter where we ran, Officer Sara hunted all three of us down."
        ),
        DetectiveTask(
            id = 20,
            title = "Countdown at Central Station",
            hindiSubtitle = "20. Bomb — Clues, Bomb Defusal & Phone Trace Finale 🎉",
            criminalName = "Dr. Kabir 'Phantom'",
            criminalAlias = "Mastermind Bomber",
            locationId = "loc_station_finale",
            locationName = "Central Train Station",
            mapX = 0.50f,
            mapY = 0.82f,
            isHardMission = true,
            isNightMission = true,
            requiresInterrogation = true,
            briefing = "FINAL CASE: An anonymous caller planted a time bomb in the city! Piece together the clues to locate it at Central Station, defuse the bomb, and trace his phone call to save the city!",
            howSaraCatches = "Sara connects the clues and discovers the bomb is at Central Station! She defuses the ticking bomb with seconds left, traces the caller's phone signal, and arrests the mastermind — saving the entire city! 🎉",
            ryanHint = "Combine the acoustic train whistle clue with Platform 3 locker, cut the Blue-Gold trigger wire, then trace the live cell tower ping!",
            clues = listOf(
                ClueHotspot("t20_c1", "Acoustic Clue & Cipher Deduction", "Background train chime in the threat call matches Central Station Platform 3!", "Bomb Location Found: Central Station!"),
                ClueHotspot("t20_c2", "Ticking Bomb Defusal (00:07 Left)", "Rex locates Locker 99; Sara steadies her hands and snips the Blue-Gold detonator wire!", "BOMB DEFUSED! City Saved!", requiresRex = true),
                ClueHotspot("t20_c3", "Live Cell Tower Phone Trace", "Cyber trace locks onto Dr. Kabir's burner phone on the clocktower roof—arrested!", "Mastermind Captured!")
            ),
            dialogues = listOf(
                DialogueLine("Sara", "HERO", "The clues all point to Central Station Platform 3! Seven seconds on the timer—cutting the blue-gold wire... Defused!"),
                DialogueLine("Ryan", "PARTNER", "You saved the whole city, Sara! Look—he's calling again to gloat. Tracing his phone signal now!"),
                DialogueLine("Dr. Kabir 'Phantom'", "CRIMINAL", "You defused my bomb AND traced my encrypted call?! Inspector Sara... you truly are the Shadow Detective!")
            ),
            interrogation = InterrogationPrompt(
                question = "Dr. Kabir, the bomb at Central Station is defused and your burner phone is in evidence. Name your syndicate financiers!",
                suspectLie = "You may have stopped the timer, Inspector Sara, but you'll never prove I planted it!",
                evidenceCounter = "Present Defused Detonator Board + Voiceprint & Cell Tower Triangulation Log",
                confessionLine = "Incredible... Every clue, every wire, every trace. The city is safe because of you, Inspector Sara."
            ),
            criminalHp = 180,
            jailQuote = "She defused my bomb with seconds to spare and traced my call across the city. Legendary."
        )
    )

    fun getTaskById(id: Int): DetectiveTask =
        allTasks.find { it.id == id } ?: allTasks.first()
}
