package com.example

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CustomBottomNavigation
import com.example.ui.components.TopBrandingBar
import com.example.ui.screens.CustomerListScreen
import com.example.ui.screens.CustomerRegisterScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettlementScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WalletViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        enableEdgeToEdge()
        setContent {
            val viewModel: WalletViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                    MerchantWalletApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MerchantWalletApp(
    viewModel: WalletViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initStorage(context)
    }

    // User notifications
    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    // Predictive back navigation handling
    BackHandler(enabled = uiState.customerInDischargeStep != null) {
        viewModel.exitDischargeStep()
    }

    BackHandler(enabled = uiState.customerInDischargeStep == null && uiState.currentTab != "home") {
        viewModel.selectTab("home")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBrandingBar(
                isDarkTheme = uiState.isDarkTheme,
                onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                onSearchClick = { viewModel.selectTab("reports") },
                onNotificationClick = { viewModel.selectTab("home") },
                onSettingsClick = { viewModel.selectTab("settings") }
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = uiState.currentTab,
                isDarkTheme = uiState.isDarkTheme,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabContent"
            ) { tab ->
                when (tab) {
                    "home" -> HomeScreen(
                        uiState = uiState,
                        onTabSelected = { viewModel.selectTab(it) },
                        onHomeFilterChange = { viewModel.setHomeFilterTab(it) },
                        onDischargeClick = { customer ->
                            viewModel.startDischargeStep(customer)
                            viewModel.selectTab("customer_list")
                        }
                    )

                    "new_customer" -> CustomerRegisterScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    "customer_list" -> CustomerListScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    "settlement" -> SettlementScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    "stores" -> StoreScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    "reports" -> ReportsScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    "settings" -> SettingsScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )

                    else -> HomeScreen(
                        uiState = uiState,
                        onTabSelected = { viewModel.selectTab(it) },
                        onHomeFilterChange = { viewModel.setHomeFilterTab(it) },
                        onDischargeClick = { customer ->
                            viewModel.startDischargeStep(customer)
                            viewModel.selectTab("customer_list")
                        }
                    )
                }
            }
        }
    }
}
