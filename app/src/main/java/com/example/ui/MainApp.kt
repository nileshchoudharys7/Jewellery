package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.NotificationsDialog
import com.example.ui.screens.ProfitLossScreen
import com.example.ui.screens.RatesScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.LuxuryDarkBg
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceBorder
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

enum class AppDestination(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    RATES("Rates", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
    CALCULATOR("Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    INVENTORY("Inventory", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    REPORTS("Reports", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    BILLING("Billing", Icons.Filled.Receipt, Icons.Filled.Receipt),
    PROFIT_LOSS("P&L Ledger", Icons.Filled.Assessment, Icons.Filled.Assessment),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Filled.Settings)
}

@Composable
fun MainApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val notifications by viewModel.notifications.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    // Android Predictive / Standard BackHandler: if not on Home, back goes to Home
    if (currentDestination != AppDestination.HOME) {
        BackHandler {
            currentDestination = AppDestination.HOME
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = LuxurySurface,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Brand Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(EmeraldDark, LuxurySurfaceElevated)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldContainer)
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = shopProfile?.shopName ?: "Jewellery Profit Master",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Proprietor: ${shopProfile?.ownerName ?: "Rajesh Soni"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldLight
                        )
                        Text(
                            text = "GSTIN: ${shopProfile?.gstin ?: "27AAAPL8901M1ZT"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                DrawerMenuItem(
                    icon = Icons.Default.Home,
                    label = "Home Dashboard",
                    selected = currentDestination == AppDestination.HOME,
                    onClick = {
                        currentDestination = AppDestination.HOME
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.TrendingUp,
                    label = "Live Metal Rates",
                    selected = currentDestination == AppDestination.RATES,
                    onClick = {
                        currentDestination = AppDestination.RATES
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Calculate,
                    label = "Jewellery Calculator",
                    selected = currentDestination == AppDestination.CALCULATOR,
                    onClick = {
                        currentDestination = AppDestination.CALCULATOR
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Inventory2,
                    label = "Stock Inventory",
                    selected = currentDestination == AppDestination.INVENTORY,
                    onClick = {
                        currentDestination = AppDestination.INVENTORY
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Receipt,
                    label = "Billing & GST Invoices",
                    selected = currentDestination == AppDestination.BILLING,
                    onClick = {
                        currentDestination = AppDestination.BILLING
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Assessment,
                    label = "Profit & Loss Ledger",
                    selected = currentDestination == AppDestination.PROFIT_LOSS,
                    onClick = {
                        currentDestination = AppDestination.PROFIT_LOSS
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Assessment,
                    label = "Business Reports",
                    selected = currentDestination == AppDestination.REPORTS,
                    onClick = {
                        currentDestination = AppDestination.REPORTS
                        scope.launch { drawerState.close() }
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    label = "Shop Settings & Profile",
                    selected = currentDestination == AppDestination.SETTINGS,
                    onClick = {
                        currentDestination = AppDestination.SETTINGS
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .background(LuxuryDarkBg),
            topBar = {
                // Top Bar with Drawer Hamburger, Title, and Alerts
                Surface(
                    color = LuxurySurface,
                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("app_drawer_open_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Navigation Menu",
                                    tint = GoldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Jewellery Profit Master",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showNotificationsDialog = true },
                            modifier = Modifier.testTag("top_bar_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = LossRed) {
                                            Text("$unreadCount", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = TextWhite
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Material 3 Navigation Bar respecting navigationBarsPadding
                NavigationBar(
                    containerColor = LuxurySurface,
                    contentColor = TextWhite,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .border(
                            width = 0.5.dp,
                            color = LuxurySurfaceBorder,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .testTag("bottom_navigation_bar")
                ) {
                    val bottomItems = listOf(
                        AppDestination.HOME,
                        AppDestination.RATES,
                        AppDestination.CALCULATOR,
                        AppDestination.INVENTORY,
                        AppDestination.REPORTS
                    )

                    bottomItems.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(
                                    text = destination.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LuxuryBlack,
                                selectedTextColor = GoldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = GoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(LuxuryDarkBg)
            ) {
                Crossfade(targetState = currentDestination, label = "ScreenTransition") { dest ->
                    when (dest) {
                        AppDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToRates = { currentDestination = AppDestination.RATES },
                            onNavigateToCalculator = { currentDestination = AppDestination.CALCULATOR },
                            onNavigateToInventory = { currentDestination = AppDestination.INVENTORY },
                            onNavigateToBilling = { currentDestination = AppDestination.BILLING },
                            onNavigateToProfitLoss = { currentDestination = AppDestination.PROFIT_LOSS },
                            onOpenNotifications = { showNotificationsDialog = true }
                        )
                        AppDestination.RATES -> RatesScreen(
                            viewModel = viewModel
                        )
                        AppDestination.CALCULATOR -> CalculatorScreen(
                            viewModel = viewModel,
                            onAddToInventory = { currentDestination = AppDestination.INVENTORY }
                        )
                        AppDestination.INVENTORY -> InventoryScreen(
                            viewModel = viewModel
                        )
                        AppDestination.BILLING -> BillingScreen(
                            viewModel = viewModel
                        )
                        AppDestination.PROFIT_LOSS -> ProfitLossScreen(
                            viewModel = viewModel
                        )
                        AppDestination.REPORTS -> ReportsScreen(
                            viewModel = viewModel
                        )
                        AppDestination.SETTINGS -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotificationsDialog = false },
            onMarkRead = { id -> viewModel.markNotificationRead(id) },
            onClearAll = { viewModel.clearNotifications() }
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(imageVector = icon, contentDescription = null, tint = if (selected) GoldPrimary else TextSilver) },
        label = { Text(label, color = if (selected) GoldLight else TextWhite, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = LuxurySurfaceElevated,
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}
