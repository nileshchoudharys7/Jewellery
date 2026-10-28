package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetalRateEntity
import com.example.ui.DashboardSummary
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldColor
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBg
import com.example.ui.theme.LuxuryBlack
import com.example.ui.theme.LuxuryDarkBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceBorder
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.PlatinumColor
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBg
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToRates: () -> Unit,
    onNavigateToCalculator: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToBilling: () -> Unit,
    onNavigateToProfitLoss: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.rates.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsState()
    val isOnlineSync by viewModel.isOnlineSync.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Luxury Banner
        item {
            LuxuryShopHeader(
                shopName = shopProfile?.shopName ?: "Shree Laxmi Jewellers",
                tagline = shopProfile?.tagline ?: "Certified BIS 916 Hallmark",
                city = shopProfile?.defaultCity ?: "Mumbai",
                unreadCount = unreadCount,
                onOpenNotifications = onOpenNotifications,
                isRefreshing = isRefreshing,
                onRefreshRates = { viewModel.refreshRates() }
            )
        }

        // Live Rate Status Bar
        item {
            ApiStatusBar(
                statusMessage = apiStatusMessage,
                isOnline = isOnlineSync,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshRates() },
                onManageRates = onNavigateToRates
            )
        }

        // Metal Rates Header
        item {
            SectionHeader(
                title = "Today's Bullion Rates",
                subtitle = "Per Gram • Auto Market Rates",
                actionLabel = "Rate History",
                onActionClick = onNavigateToRates
            )
        }

        // Metal Rates Cards Carousel / Row
        item {
            val keyRates = listOf(
                rates.find { it.id == "GOLD_24K" },
                rates.find { it.id == "GOLD_22K" },
                rates.find { it.id == "GOLD_18K" },
                rates.find { it.id == "SILVER_1G" },
                rates.find { it.id == "PLATINUM_1G" }
            ).filterNotNull()

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(keyRates) { rate ->
                    MetalRateCard(
                        rate = rate,
                        onClick = onNavigateToRates
                    )
                }
            }
        }

        // Silver Per Kg Quick Strip
        item {
            val silver = rates.find { it.id == "SILVER_1G" }
            if (silver != null) {
                val silverKgRate = silver.ratePerKg ?: (silver.ratePerGram * 1000.0)
                SilverKgBar(silverRateKg = silverKgRate, silverRateG = silver.ratePerGram)
            }
        }

        // Today's Sales & Profit Performance
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Today's Business Performance",
                subtitle = "Realized Revenue & Profit",
                actionLabel = "View P&L",
                onActionClick = onNavigateToProfitLoss
            )
        }

        item {
            TodayPerformanceCard(
                summary = summary,
                onViewDetails = onNavigateToProfitLoss
            )
        }

        // Inventory Stock Value & Investment
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Inventory Valuation",
                subtitle = "Live Market Worth vs Cost",
                actionLabel = "Manage Stock",
                onActionClick = onNavigateToInventory
            )
        }

        item {
            InventoryValuationCard(
                summary = summary,
                onViewInventory = onNavigateToInventory
            )
        }

        // Low Stock Alert Banner (if any)
        if (summary.lowStockCount > 0) {
            item {
                LowStockBanner(
                    lowStockCount = summary.lowStockCount,
                    onViewInventory = onNavigateToInventory
                )
            }
        }

        // Quick Business Actions
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "QUICK ACTIONS",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
            QuickActionsGrid(
                onNewBill = onNavigateToBilling,
                onCalculator = onNavigateToCalculator,
                onAddStock = onNavigateToInventory,
                onUpdateRates = onNavigateToRates
            )
        }
    }
}

@Composable
fun LuxuryShopHeader(
    shopName: String,
    tagline: String,
    city: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    isRefreshing: Boolean,
    onRefreshRates: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        EmeraldDark,
                        LuxurySurface
                    )
                )
            )
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldContainer)
                            .border(1.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Jewellery Logo",
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shopName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, GoldPrimary)
                            ) {
                                Text(
                                    text = "BIS 916",
                                    color = GoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "$tagline • $city",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSilver,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefreshRates,
                        enabled = !isRefreshing,
                        modifier = Modifier.testTag("home_refresh_rates_button")
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = GoldPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Market Rates",
                                tint = GoldPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("home_notifications_button")
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
                                contentDescription = "Alerts & Notifications",
                                tint = TextWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApiStatusBar(
    statusMessage: String,
    isOnline: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onManageRates: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        color = LuxurySurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) ProfitGreen else GoldAmber)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOnline) "Live Market Rates Synced" else "Offline / Board Rates",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOnline) ProfitGreen else GoldAmber,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " • $statusMessage",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "Configure",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onManageRates() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionLabel: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Text(
            text = "$actionLabel →",
            style = MaterialTheme.typography.labelMedium,
            color = GoldPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onActionClick() }
                .padding(4.dp)
        )
    }
}

@Composable
fun MetalRateCard(
    rate: MetalRateEntity,
    onClick: () -> Unit
) {
    val isPositive = rate.changeAmount >= 0
    val cardBorder = when (rate.metal) {
        "GOLD" -> GoldPrimary.copy(alpha = 0.5f)
        "SILVER" -> SilverColor.copy(alpha = 0.4f)
        else -> PlatinumColor.copy(alpha = 0.4f)
    }
    val badgeColor = when (rate.metal) {
        "GOLD" -> GoldPrimary
        "SILVER" -> SilverColor
        else -> PlatinumColor
    }

    Card(
        modifier = Modifier
            .width(175.dp)
            .clickable { onClick() }
            .testTag("rate_card_${rate.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, badgeColor)
                ) {
                    Text(
                        text = "${rate.metal} ${rate.purity}",
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "/ 1g",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = Formatters.formatInr(rate.ratePerGram, includeDecimals = (rate.metal == "SILVER")),
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Comparison & Change
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = if (isPositive) "Increased" else "Decreased",
                    tint = if (isPositive) ProfitGreen else LossRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${Formatters.formatInr(rate.changeAmount, includeDecimals = true)} (${Formatters.formatPercentage(rate.changePercentage)})",
                    color = if (isPositive) ProfitGreen else LossRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Prev: ${Formatters.formatInr(rate.previousRate)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )

            Text(
                text = Formatters.formatDateTime(rate.lastUpdated),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SilverKgBar(silverRateKg: Double, silverRateG: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        color = LuxurySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, SilverColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SilverColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ag", color = SilverColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Silver Bullion Rate (999 Pure)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "1 Gram: ${Formatters.formatInr(silverRateG, true)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilver
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatInr(silverRateKg),
                    style = MaterialTheme.typography.titleMedium,
                    color = SilverColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Per 1 Kilogram (1000g)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun TodayPerformanceCard(
    summary: DashboardSummary,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onViewDetails() }
            .testTag("today_performance_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Performance",
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Today's Ledger",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${summary.todaySalesCount} transactions today",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (summary.todayProfit >= 0) ProfitGreenBg else LossRedBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (summary.todayProfit >= 0) ProfitGreen else LossRed
                    )
                ) {
                    Text(
                        text = if (summary.todayProfit >= 0) "REALIZED PROFIT" else "LOSS",
                        color = if (summary.todayProfit >= 0) ProfitGreen else LossRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Today Sales
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today's Gross Sales",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatInr(summary.todaySales),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Today Realized Profit
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Net Realized Profit",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatInr(summary.todayProfit),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (summary.todayProfit >= 0) ProfitGreen else LossRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryValuationCard(
    summary: DashboardSummary,
    onViewInventory: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onViewInventory() }
            .testTag("inventory_valuation_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Inventory",
                            tint = EmeraldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Showroom Stock Value",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Live Metal Rate Valuation",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                // Growth badge
                val gain = summary.unrealizedGain
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProfitGreenBg,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, ProfitGreen)
                ) {
                    Text(
                        text = "+${Formatters.formatInr(gain)} unrealized",
                        color = ProfitGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Total Stock Market Value",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatInr(summary.totalInventoryMarketValue),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Money Invested",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatInr(summary.totalInventoryCost),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LowStockBanner(
    lowStockCount: Int,
    onViewInventory: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onViewInventory() },
        shape = RoundedCornerShape(12.dp),
        color = LossRedBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LossRed.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = LossRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Low Stock Alert ($lowStockCount items)",
                    style = MaterialTheme.typography.titleSmall,
                    color = LossRed,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Some bridal and fast-moving jewellery items have <= 1 quantity remaining.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSilver
                )
            }
            Text(
                text = "Restock →",
                style = MaterialTheme.typography.labelMedium,
                color = LossRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickActionsGrid(
    onNewBill: () -> Unit,
    onCalculator: () -> Unit,
    onAddStock: () -> Unit,
    onUpdateRates: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                icon = Icons.Default.ReceiptLong,
                title = "New Bill",
                subtitle = "Generate GST Invoice",
                color = GoldPrimary,
                modifier = Modifier.weight(1f),
                onClick = onNewBill,
                testTag = "quick_action_billing"
            )
            ActionTile(
                icon = Icons.Default.Calculate,
                title = "Rate Calculator",
                subtitle = "Purity, Wastage & Margin",
                color = EmeraldAccent,
                modifier = Modifier.weight(1f),
                onClick = onCalculator,
                testTag = "quick_action_calculator"
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                icon = Icons.Default.Add,
                title = "Add Stock",
                subtitle = "New Jewellery Item",
                color = TextWhite,
                modifier = Modifier.weight(1f),
                onClick = onAddStock,
                testTag = "quick_action_add_stock"
            )
            ActionTile(
                icon = Icons.Default.Edit,
                title = "Board Rates",
                subtitle = "Update Daily Prices",
                color = GoldAmber,
                modifier = Modifier.weight(1f),
                onClick = onUpdateRates,
                testTag = "quick_action_board_rates"
            )
        }
    }
}

@Composable
fun ActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = LuxurySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
