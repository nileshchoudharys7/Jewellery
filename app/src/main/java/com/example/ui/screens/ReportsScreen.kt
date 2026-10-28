package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBg
import com.example.ui.theme.LuxuryDarkBg
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
fun ReportsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val invoices by viewModel.invoices.collectAsState()
    val invoiceItems by viewModel.invoiceItems.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val dashboardSummary by viewModel.dashboardSummary.collectAsState()

    var exportMessage by remember { mutableStateOf<String?>(null) }

    // Aggregate calculations
    val totalSalesRevenue = invoices.sumOf { it.totalAmount }
    val totalCostOfGoodsSold = invoiceItems.sumOf { it.itemCost }
    val totalGrossProfit = totalSalesRevenue - totalCostOfGoodsSold
    val totalOperatingExpenses = expenses.sumOf { it.amount }
    val estimatedNetProfit = totalGrossProfit - totalOperatingExpenses

    // Metal breakdown
    val goldSales = invoiceItems.filter { it.metal.equals("GOLD", ignoreCase = true) }.sumOf { it.itemPrice }
    val silverSales = invoiceItems.filter { it.metal.equals("SILVER", ignoreCase = true) }.sumOf { it.itemPrice }
    val platinumSales = invoiceItems.filter { it.metal.equals("PLATINUM", ignoreCase = true) }.sumOf { it.itemPrice }
    val totalMetalSales = (goldSales + silverSales + platinumSales).coerceAtLeast(1.0)

    val customersWithBalance = customers.filter { it.outstandingBalance > 0 }
    val suppliersWithPayables = suppliers.filter { it.outstandingPayable > 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Business Intelligence Reports",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Audited Sales, Profits, Metal Distribution & Ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = {
                        val reportText = buildString {
                            appendLine("==========================================")
                            appendLine("JEWELLERY PROFIT MASTER - BUSINESS REPORT")
                            appendLine("Generated: ${Formatters.formatDateTime(System.currentTimeMillis())}")
                            appendLine("==========================================")
                            appendLine("Total Sales Revenue       : ${Formatters.formatInr(totalSalesRevenue)}")
                            appendLine("Total Cost of Goods Sold  : ${Formatters.formatInr(totalCostOfGoodsSold)}")
                            appendLine("Realized Gross Profit     : ${Formatters.formatInr(totalGrossProfit)}")
                            appendLine("Operating Expenses        : -${Formatters.formatInr(totalOperatingExpenses)}")
                            appendLine("Net Business Profit       : ${Formatters.formatInr(estimatedNetProfit)}")
                            appendLine("------------------------------------------")
                            appendLine("METAL SALES SHARE:")
                            appendLine("Gold Sales     : ${Formatters.formatInr(goldSales)} (${String.format("%.1f", (goldSales/totalMetalSales)*100)}%)")
                            appendLine("Silver Sales   : ${Formatters.formatInr(silverSales)} (${String.format("%.1f", (silverSales/totalMetalSales)*100)}%)")
                            appendLine("Platinum Sales : ${Formatters.formatInr(platinumSales)} (${String.format("%.1f", (platinumSales/totalMetalSales)*100)}%)")
                            appendLine("------------------------------------------")
                            appendLine("INVENTORY VALUATION:")
                            appendLine("Total Stock Market Value  : ${Formatters.formatInr(dashboardSummary.totalInventoryMarketValue)}")
                            appendLine("Total Investment in Stock : ${Formatters.formatInr(dashboardSummary.totalInventoryCost)}")
                            appendLine("Unrealized Stock Growth   : +${Formatters.formatInr(dashboardSummary.unrealizedGain)}")
                            appendLine("Pending Receivables       : ${Formatters.formatInr(dashboardSummary.pendingReceivables)}")
                            appendLine("==========================================")
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Jewellery Report", reportText))
                        exportMessage = "Report exported to clipboard (CSV/Text)!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("reports_export_button")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        exportMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProfitGreenBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ProfitGreen)
                ) {
                    Text(
                        text = msg,
                        color = ProfitGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Executive Profit / Loss Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("EXECUTIVE FINANCIAL SUMMARY", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Realized Gross Profit", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(totalGrossProfit), style = MaterialTheme.typography.headlineSmall, color = ProfitGreen, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Estimated Net Profit", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(estimatedNetProfit), style = MaterialTheme.typography.headlineSmall, color = if (estimatedNetProfit >= 0) GoldLight else LossRed, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = LuxurySurfaceBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sales Revenue", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(totalSalesRevenue), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Cost of Goods Sold", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(totalCostOfGoodsSold), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Recorded Expenses", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text("-${Formatters.formatInr(totalOperatingExpenses)}", style = MaterialTheme.typography.titleMedium, color = LossRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Metal Sales Distribution Chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PRECIOUS METALS SALES SHARE", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Canvas
                        Box(
                            modifier = Modifier.size(110.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(100.dp)) {
                                val goldAngle = ((goldSales / totalMetalSales) * 360f).toFloat()
                                val silverAngle = ((silverSales / totalMetalSales) * 360f).toFloat()
                                val platinumAngle = 360f - goldAngle - silverAngle

                                var start = -90f
                                drawArc(color = GoldPrimary, startAngle = start, sweepAngle = goldAngle, useCenter = false, style = Stroke(width = 20f))
                                start += goldAngle
                                drawArc(color = SilverColor, startAngle = start, sweepAngle = silverAngle, useCenter = false, style = Stroke(width = 20f))
                                start += silverAngle
                                drawArc(color = PlatinumColor, startAngle = start, sweepAngle = platinumAngle, useCenter = false, style = Stroke(width = 20f))
                            }
                            Text("Sales", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                            MetalLegendItem(label = "Gold Jewellery", value = Formatters.formatInr(goldSales), color = GoldPrimary)
                            MetalLegendItem(label = "Silverware & Articles", value = Formatters.formatInr(silverSales), color = SilverColor)
                            MetalLegendItem(label = "Platinum Ornaments", value = Formatters.formatInr(platinumSales), color = PlatinumColor)
                        }
                    }
                }
            }
        }

        // Inventory Valuation Summary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SHOWROOM INVENTORY VALUATION", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Stock Market Worth", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(dashboardSummary.totalInventoryMarketValue), style = MaterialTheme.typography.titleMedium, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Capital Invested", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(dashboardSummary.totalInventoryCost), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Unrealized Gain", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text("+${Formatters.formatInr(dashboardSummary.unrealizedGain)}", style = MaterialTheme.typography.titleMedium, color = ProfitGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Customer Outstanding Balances
        if (customersWithBalance.isNotEmpty()) {
            item {
                Text(
                    text = "CUSTOMER OUTSTANDING RECEIVABLES (${customersWithBalance.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = LossRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            items(customersWithBalance) { cust ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LuxurySurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(cust.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${cust.phone} • ${cust.address}", color = TextMuted, fontSize = 11.sp)
                        }
                        Text(
                            text = "Due: ${Formatters.formatInr(cust.outstandingBalance)}",
                            color = LossRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetalLegendItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = TextSilver, fontSize = 11.sp)
        }
        Text(value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
