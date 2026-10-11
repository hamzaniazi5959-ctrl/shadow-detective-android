package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.data.local.JailInmateEntity
import com.example.data.local.PlayerProfileEntity
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.CorruptionMagenta
import com.example.ui.theme.JusticeEmerald
import com.example.ui.theme.NoirCard
import com.example.ui.theme.NoirDeepNavy
import com.example.ui.theme.NoirObsidian
import com.example.ui.theme.PoliceCyan
import com.example.ui.theme.SirenCrimson
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AiLabUiState
import com.example.ui.viewmodel.ShadowDetectiveViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCrimeLabAndNewsScreen(
    profile: PlayerProfileEntity,
    inmates: List<JailInmateEntity>,
    aiState: AiLabUiState,
    viewModel: ShadowDetectiveViewModel
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: Photo Mode (Nano Banana 2.1), 1: Veo Video Animator, 2: News Channel & Google Search

    var imagePrompt by remember {
        mutableStateOf("Officer Sara with dark shoulder-length hair in a neon noir alley beside her police cruiser and German Shepherd dog Rex")
    }
    var imageAspectRatio by remember { mutableStateOf("16:9") }
    var selectedEditBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var veoPrompt by remember {
        mutableStateOf("Cinematic rain falling on neon asphalt as police cruiser lights flash around Detective Sara and K9 Rex")
    }
    var veoAspectRatio by remember { mutableStateOf("16:9") } // Must be "16:9" or "9:16"
    var uploadedVeoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var searchQuery by remember {
        mutableStateOf("Latest forensic DNA & fingerprint breakthroughs used by modern police detectives")
    }

    // Zero-permission Photo Picker for Image Editing (gemini-nano-banana-2.1)
    val editPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    selectedEditBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
            }
        }
    }

    // Zero-permission Photo Picker for Veo Video Generation (veo-3.1-fast-generate-preview)
    val veoPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    uploadedVeoBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
            }
        }
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
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    label = { Text("Photo Mode (Nano Banana 2.1)") }
                )
                FilterChip(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    label = { Text("Veo Cutscene Video") }
                )
                FilterChip(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    label = { Text("News Channel & Search") }
                )
            }
        }

        // =========================================================================
        // TAB 0: CREATE & EDIT IMAGES USING gemini-nano-banana-2.1
        // =========================================================================
        if (activeTab == 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PoliceCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("PHOTO MODE & FORENSICS STUDIO", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
                                Text("Model: gemini-nano-banana-2.1 • Create or Edit Detective Photos", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                            }
                        }

                        OutlinedTextField(
                            value = imagePrompt,
                            onValueChange = { imagePrompt = it },
                            label = { Text("Text prompt to create or edit image") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nano_banana_prompt_input")
                        )

                        // Aspect Ratio Selector
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Aspect Ratio:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            listOf("16:9", "1:1", "9:16", "4:3").forEach { ratio ->
                                FilterChip(
                                    selected = imageAspectRatio == ratio,
                                    onClick = { imageAspectRatio = ratio },
                                    label = { Text(ratio) }
                                )
                            }
                        }

                        // Upload Photo to Edit or Clear for Fresh Generation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    editPhotoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pick_photo_for_edit_button")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PoliceCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (selectedEditBitmap != null) "Photo Attached" else "Upload Photo to Edit",
                                    color = PoliceCyan,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            if (selectedEditBitmap != null) {
                                OutlinedButton(onClick = { selectedEditBitmap = null }) {
                                    Text("Clear Photo", color = SirenCrimson, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.generateOrEditPhoto(
                                    prompt = imagePrompt,
                                    aspectRatio = imageAspectRatio,
                                    inputBitmap = selectedEditBitmap
                                )
                            },
                            enabled = !aiState.isGeneratingImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_nano_banana_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                        ) {
                            if (aiState.isGeneratingImage) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NoirObsidian, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Running gemini-nano-banana-2.1...", color = NoirObsidian)
                            } else {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = NoirObsidian)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (selectedEditBitmap != null) "Edit Image (gemini-nano-banana-2.1)" else "Create Image (gemini-nano-banana-2.1)",
                                    color = NoirObsidian,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Text(aiState.imageStatusText, style = MaterialTheme.typography.bodyMedium, color = BadgeGold)

                        val displayBmp = aiState.generatedBitmap ?: selectedEditBitmap
                        if (displayBmp != null) {
                            Image(
                                bitmap = displayBmp.asImageBitmap(),
                                contentDescription = "Generated or uploaded detective image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_noir_city_banner_1791737366230),
                                contentDescription = "Noir Photo Mode Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 1: ANIMATE UPLOADED PHOTO INTO VIDEO USING veo-3.1-fast-generate-preview
        // =========================================================================
        if (activeTab == 1) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CorruptionMagenta)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = CorruptionMagenta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("ANIMATE PHOTO INTO CUTSCENE VIDEO", style = MaterialTheme.typography.headlineMedium, color = PoliceCyan)
                                Text("Model: veo-3.1-fast-generate-preview • 16:9 Landscape or 9:16 Portrait", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                            }
                        }

                        // Step 1: Upload Photo Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    veoPhotoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_veo_photo_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = BadgeGold)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = NoirObsidian, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uploadedVeoBitmap != null) "Photo Uploaded ✓" else "Upload Photo for Veo",
                                    color = NoirObsidian,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    uploadedVeoBitmap = BitmapFactory.decodeResource(
                                        context.resources,
                                        R.drawable.img_noir_city_banner_1791737366230
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Use Sara Scene Photo", color = PoliceCyan, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        // Aspect Ratio MUST be 16:9 (Landscape) or 9:16 (Portrait)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Video Aspect Ratio:", style = MaterialTheme.typography.labelLarge, color = Color.White)
                            FilterChip(
                                selected = veoAspectRatio == "16:9",
                                onClick = { veoAspectRatio = "16:9" },
                                label = { Text("16:9 (Landscape)") },
                                modifier = Modifier.testTag("veo_ratio_16_9")
                            )
                            FilterChip(
                                selected = veoAspectRatio == "9:16",
                                onClick = { veoAspectRatio = "9:16" },
                                label = { Text("9:16 (Portrait)") },
                                modifier = Modifier.testTag("veo_ratio_9_16")
                            )
                        }

                        OutlinedTextField(
                            value = veoPrompt,
                            onValueChange = { veoPrompt = it },
                            label = { Text("Animation prompt for Veo 3.1") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("veo_prompt_input")
                        )

                        Button(
                            onClick = {
                                val bmpToUse = uploadedVeoBitmap ?: aiState.generatedBitmap ?: BitmapFactory.decodeResource(
                                    context.resources,
                                    R.drawable.img_noir_city_banner_1791737366230
                                )
                                viewModel.animatePhotoIntoVeoVideo(
                                    sourceBitmap = bmpToUse,
                                    prompt = veoPrompt,
                                    aspectRatio = veoAspectRatio
                                )
                            },
                            enabled = !aiState.isGeneratingVideo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_veo_video_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CorruptionMagenta)
                        ) {
                            if (aiState.isGeneratingVideo) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating Video (veo-3.1-fast-generate-preview)...", color = Color.White)
                            } else {
                                Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Animate Photo with Veo ($veoAspectRatio)",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Text(aiState.videoProgressText, style = MaterialTheme.typography.bodyMedium, color = BadgeGold)

                        // Preview of uploaded photo or VideoView player
                        val ratioFloat = if (veoAspectRatio == "9:16") 9f / 16f else 16f / 9f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(ratioFloat.coerceIn(0.65f, 1.78f))
                                .clip(RoundedCornerShape(14.dp))
                                .background(NoirCard),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!aiState.latestVideoFilePath.isNullOrBlank()) {
                                AndroidView(
                                    factory = { ctx ->
                                        VideoView(ctx).apply {
                                            setVideoPath(aiState.latestVideoFilePath)
                                            setOnPreparedListener { mp ->
                                                mp.isLooping = true
                                                start()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                val previewBmp = uploadedVeoBitmap ?: aiState.generatedBitmap
                                if (previewBmp != null) {
                                    Image(
                                        bitmap = previewBmp.asImageBitmap(),
                                        contentDescription = "Uploaded Photo for Veo Animation",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_noir_city_banner_1791737366230),
                                        contentDescription = "Default Scene Photo for Veo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Surface(
                                    color = NoirObsidian.copy(alpha = 0.72f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Source Frame Ready ($veoAspectRatio) • Model: veo-3.1-fast-generate-preview",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PoliceCyan,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 2: NEWS CHANNEL (#54) & GOOGLE SEARCH GROUNDING (gemini-3.5-flash)
        // =========================================================================
        if (activeTab == 2) {
            // In-Game WNKP Channel 7 News Broadcast (#54: News channel)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirDeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SirenCrimson)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Newspaper, contentDescription = null, tint = SirenCrimson)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("WNKP CHANNEL 7 — NOIR CITY BREAKING NEWS", style = MaterialTheme.typography.headlineMedium, color = SirenCrimson)
                                Text("Live Headlines on Officer Sara's Cases & City Reputation", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                            }
                        }

                        val latestHeadline = if (inmates.isEmpty()) {
                            "BREAKING: Officer Sara, Partner Ryan & K9 Rex assigned to investigate the 2:00 AM Central Bank Vault heist!"
                        } else {
                            val last = inmates.last()
                            "BREAKING: Officer Sara resolves Case #${last.taskId} (${last.crimeTitle}) involving ${last.criminalName} via ${last.resolution}! (${inmates.size}/20 cases closed)"
                        }

                        Text(latestHeadline, style = MaterialTheme.typography.bodyLarge, color = Color.White)

                        Button(
                            onClick = {
                                viewModel.speak("WNKP News Anchor", "ANCHOR", latestHeadline)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NoirCard)
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = PoliceCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hear TV News Anchor Broadcast", color = PoliceCyan, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Google Search Grounding Wire (gemini-3.5-flash + googleSearch tool)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NoirCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PoliceCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "GOOGLE SEARCH GROUNDED POLICE INTEL WIRE",
                            style = MaterialTheme.typography.titleMedium,
                            color = PoliceCyan
                        )
                        Text(
                            "Powered by gemini-3.5-flash with googleSearch tool for accurate, up-to-date real-world crime & forensic data:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Ask Google Search grounded forensics / crime news") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_search_grounding_input")
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    searchQuery = "How do forensic detectives lift latent fingerprints and DNA from crime scenes?"
                                    viewModel.searchLiveCrimeNews(searchQuery)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Forensic DNA & Prints", style = MaterialTheme.typography.labelSmall, color = BadgeGold)
                            }
                            OutlinedButton(
                                onClick = {
                                    searchQuery = "How do police K9 German Shepherd dogs track scents in investigations?"
                                    viewModel.searchLiveCrimeNews(searchQuery)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("K9 Dog Scent Science", style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                            }
                        }

                        Button(
                            onClick = { viewModel.searchLiveCrimeNews(searchQuery) },
                            enabled = !aiState.isSearchingNews,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_google_search_grounding_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = PoliceCyan)
                        ) {
                            if (aiState.isSearchingNews) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NoirObsidian, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Searching with gemini-3.5-flash...", color = NoirObsidian)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, tint = NoirObsidian)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Search with Google Grounding (gemini-3.5-flash)",
                                    color = NoirObsidian,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        Surface(
                            color = NoirDeepNavy,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = aiState.groundedNewsReport,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )

                                if (aiState.groundedSources.isNotEmpty()) {
                                    Text(
                                        text = "Google Search Citations:",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = BadgeGold
                                    )
                                    aiState.groundedSources.forEach { src ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    try {
                                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(src.uri)))
                                                    } catch (_: Exception) {
                                                    }
                                                }
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = PoliceCyan, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(src.title, style = MaterialTheme.typography.labelSmall, color = PoliceCyan)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
