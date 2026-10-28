package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.data.model.InvoiceItemEntity
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBg
import com.example.ui.theme.LuxuryDarkBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceBorder
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.util.Calendar
import java.util.UUID

enum class PlTimePeriod(val label: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month"),
    YEAR("This Year"),
    ALL("All Time")
}

@Composable
fun ProfitLossScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val invoices by viewModel.invoices.collectAsState()
    val invoiceItems by viewModel.invoiceItems.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val dashboardSummary by viewModel.dashboardSummary.collectAsState()

    var selectedPeriod by remember { mutableStateOf(PlTimePeriod.ALL) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    // Filter by period
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance()

    val startTime = when (selectedPeriod) {
        PlTimePeriod.TODAY -> {
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        PlTimePeriod.WEEK -> {
            cal.add(Calendar.DAY_OF_YEAR, -7)
            cal.timeInMillis
        }
        PlTimePeriod.MONTH -> {
            cal.add(Calendar.DAY_OF_YEAR, -30)
            cal.timeInMillis
        }
        PlTimePeriod.YEAR -> {
            cal.add(Calendar.DAY_OF_YEAR, -365)
            cal.timeInMillis
        }
        PlTimePeriod.ALL -> 0L
    }

    val filteredInvoices = invoices.filter { it.date >= startTime }
    val filteredInvoiceNumbers = filteredInvoices.map { it.invoiceNumber }.toSet()
    val filteredItems = invoiceItems.filter { it.invoiceNumber in filteredInvoiceNumbers }

    val filteredExpenses = expenses.filter { it.date >= startTime }
    val totalExpenseAmount = filteredExpenses.sumOf { it.amount }

    val totalNetRevenue = filteredItems.sumOf { it.itemPrice }
    val totalRecordedCost = filteredItems.sumOf { it.itemCost }
    val realizedGrossProfit = totalNetRevenue - totalRecordedCost
    val netRealizedProfit = realizedGrossProfit - totalExpenseAmount
    val profitMargin = if (totalNetRevenue > 0) (realizedGrossProfit / totalNetRevenue) * 100.0 else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("profit_loss_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Profit & Loss Ledger",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Realized Sales vs Actual Recorded Costs",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { showAddExpenseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxurySurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Expense", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Period Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PlTimePeriod.values().forEach { period ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedPeriod == period) GoldPrimary else LuxurySurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedPeriod == period) GoldPrimary else LuxurySurfaceBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPeriod = period }
                    ) {
                        Text(
                            text = period.label,
                            color = if (selectedPeriod == period) Color.Black else TextSilver,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Realized Profit / Loss High-Level Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (realizedGrossProfit >= 0) ProfitGreen.copy(alpha = 0.5f) else LossRed.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REALIZED TRADING RESULT",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (realizedGrossProfit >= 0) ProfitGreenBg else LossRedBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (realizedGrossProfit >= 0) ProfitGreen else LossRed
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (realizedGrossProfit >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (realizedGrossProfit >= 0) ProfitGreen else LossRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${Formatters.formatPercentage(profitMargin)} Margin",
                                    color = if (realizedGrossProfit >= 0) ProfitGreen else LossRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = Formatters.formatInr(realizedGrossProfit),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (realizedGrossProfit >= 0) ProfitGreen else LossRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gross Profit (Sale Price − Recorded Item Cost)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = LuxurySurfaceBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Net Revenue", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(totalNetRevenue), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Item Purchase/Mfg Cost", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(Formatters.formatInr(totalRecordedCost), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Operating Expenses", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text("-${Formatters.formatInr(totalExpenseAmount)}", style = MaterialTheme.typography.titleMedium, color = LossRed, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Estimated Net Profit after expenses
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LuxurySurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated Net Business Profit (After Expenses)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSilver
                            )
                            Text(
                                text = Formatters.formatInr(netRealizedProfit),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (netRealizedProfit >= 0) ProfitGreen else LossRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Unrealized Current Stock Value Banner
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LuxurySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = EmeraldAccent, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unrealized Stock Growth: +${Formatters.formatInr(dashboardSummary.unrealizedGain)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = ProfitGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Current unsold inventory worth ${Formatters.formatInr(dashboardSummary.totalInventoryMarketValue)} vs book cost ${Formatters.formatInr(dashboardSummary.totalInventoryCost)}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSilver
                        )
                    }
                }
            }
        }

        // Item-by-Item P&L Transaction History Header
        item {
            Text(
                text = "SOLD JEWELLERY ITEMS P&L LEDGER (${filteredItems.size})",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }

        if (filteredItems.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LuxurySurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No sales recorded for this period", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            items(filteredItems) { item ->
                val itemProfit = item.itemPrice - item.itemCost
                val itemMargin = if (item.itemPrice > 0) (itemProfit / item.itemPrice) * 100.0 else 0.0

                ItemProfitCard(
                    item = item,
                    profit = itemProfit,
                    margin = itemMargin
                )
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onAddExpense = { title, category, amount, notes ->
                viewModel.addExpense(
                    ExpenseEntity(
                        id = "EXP-${UUID.randomUUID().toString().take(6).uppercase()}",
                        title = title,
                        category = category,
                        amount = amount,
                        notes = notes
                    )
                )
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
fun ItemProfitCard(
    item: InvoiceItemEntity,
    profit: Double,
    margin: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("item_profit_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SKU: ${item.sku ?: "CUSTOM"} • Invoice #${item.invoiceNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (profit >= 0) ProfitGreenBg else LossRedBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (profit >= 0) ProfitGreen else LossRed)
                ) {
                    Text(
                        text = if (profit >= 0) "+${Formatters.formatInr(profit)}" else Formatters.formatInr(profit),
                        color = if (profit >= 0) ProfitGreen else LossRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LuxurySurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Gross/Net Weight", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                        Text("${item.grossWeight}g / ${item.netWeight}g", style = MaterialTheme.typography.bodyMedium, color = TextSilver, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column {
                        Text("Recorded Cost", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                        Text(Formatters.formatInr(item.itemCost), style = MaterialTheme.typography.bodyMedium, color = TextSilver, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column {
                        Text("Selling Revenue", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                        Text(Formatters.formatInr(item.itemPrice), style = MaterialTheme.typography.bodyMedium, color = GoldLight, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Profit Margin", style = MaterialTheme.typography.bodySmall, color = TextMuted, fontSize = 10.sp)
                        Text(Formatters.formatPercentage(margin), style = MaterialTheme.typography.bodyMedium, color = if (profit >= 0) ProfitGreen else LossRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAddExpense: (title: String, category: String, amount: Double, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rent & Utilities") }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Rent & Utilities", "Staff Salary", "Security & Vault", "Hallmarking & BIS", "Store Maintenance", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Record Operating Expense", color = TextWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title (e.g. Safe Maintenance)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Voucher No.") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onAddExpense(title, category, amt, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Save Expense", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextWhite)
            }
        },
        containerColor = LuxurySurface,
        shape = RoundedCornerShape(16.dp)
    )
}
