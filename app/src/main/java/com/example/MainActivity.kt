package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiCrimeLabAndNewsScreen
import com.example.ui.screens.CasesAndMissionScreen
import com.example.ui.screens.OpenWorldScreen
import com.example.ui.screens.PrecinctAndEvidenceScreen
import com.example.ui.screens.ShopsAndCityLifeScreen
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NoirDeepNavy
import com.example.ui.theme.NoirObsidian
import com.example.ui.theme.PoliceCyan
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ShadowDetectiveViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ShadowDetectiveApp()
            }
        }
    }
}

@Composable
fun ShadowDetectiveApp(
    viewModel: ShadowDetectiveViewModel = viewModel()
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val evidenceList by viewModel.evidenceList.collectAsStateWithLifecycle()
    val jailInmates by viewModel.jailInmates.collectAsStateWithLifecycle()
    val toastBanner by viewModel.toastBanner.collectAsStateWithLifecycle()
    val autoWalkDest by viewModel.autoWalkDestination.collectAsStateWithLifecycle()
    val bankRobberyActive by viewModel.bankRobberyActive.collectAsStateWithLifecycle()
    val activeMission by viewModel.activeMission.collectAsStateWithLifecycle()
    val aiState by viewModel.aiLabState.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    // BackHandler for custom state navigation & active mission overlay
    BackHandler(enabled = activeMission != null || currentTab != 0) {
        if (activeMission != null) {
            viewModel.closeMissionOverlay()
        } else {
            currentTab = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = NoirObsidian,
        bottomBar = {
            NavigationBar(
                containerColor = NoirDeepNavy,
                contentColor = PoliceCyan
            ) {
                val navItems = listOf(
                    Triple(0, "City Patrol", Icons.Default.Public),
                    Triple(1, "20 Cases", Icons.Default.Assignment),
                    Triple(2, "Precinct HQ", Icons.Default.AccountBalance),
                    Triple(3, "Shops & Life", Icons.Default.Storefront),
                    Triple(4, "Lab & News", Icons.Default.AutoAwesome)
                )

                navItems.forEach { (index, label, icon) ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("nav_tab_$index"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NoirObsidian,
                            selectedTextColor = PoliceCyan,
                            indicatorColor = PoliceCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> OpenWorldScreen(
                    profile = profile,
                    toastBanner = toastBanner,
                    autoWalkDest = autoWalkDest,
                    bankRobberyActive = bankRobberyActive,
                    viewModel = viewModel,
                    onOpenTaskMission = { task ->
                        viewModel.launchMission(task)
                        currentTab = 1
                    },
                    onJumpToShopTab = {
                        currentTab = 3
                    }
                )
                1 -> CasesAndMissionScreen(
                    profile = profile,
                    activeMission = activeMission,
                    viewModel = viewModel
                )
                2 -> PrecinctAndEvidenceScreen(
                    profile = profile,
                    evidenceList = evidenceList,
                    inmates = jailInmates,
                    viewModel = viewModel,
                    onJumpToCasesTab = { currentTab = 1 }
                )
                3 -> ShopsAndCityLifeScreen(
                    profile = profile,
                    viewModel = viewModel
                )
                4 -> AiCrimeLabAndNewsScreen(
                    profile = profile,
                    inmates = jailInmates,
                    aiState = aiState,
                    viewModel = viewModel
                )
            }
        }
    }
}
