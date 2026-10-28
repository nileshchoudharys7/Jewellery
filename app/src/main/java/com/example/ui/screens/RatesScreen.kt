package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetalRateEntity
import com.example.data.model.RateHistoryEntity
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LuxuryDarkBg
import com.example.ui.theme.LuxuryGoldBorder
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceBorder
import com.example.ui.theme.LuxurySurfaceElevated
import com.example.ui.theme.PlatinumColor
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun RatesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.rates.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val apiStatusMessage by viewModel.apiStatusMessage.collectAsState()
    val isOnlineSync by viewModel.isOnlineSync.collectAsState()
    val rateHistory by viewModel.rateHistory.collectAsState()

    var selectedHistoryMetal by remember { mutableStateOf("GOLD_24K") }
    var selectedPeriod by remember { mutableStateOf("7 Days") }
    var editingRate by remember { mutableStateOf<MetalRateEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("rates_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Metal Rates",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Official Sarafa & Bullion Rates (India)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { viewModel.refreshRates() },
                    enabled = !isRefreshing,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("rates_refresh_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync API", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Live Market Status Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LuxurySurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(if (isOnlineSync) ProfitGreen else GoldAmber)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOnlineSync) "API Live Feed Active" else "Offline / Manual Mode",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isOnlineSync) ProfitGreen else GoldAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val lastUpdatedTime = rates.firstOrNull()?.lastUpdated ?: System.currentTimeMillis()
                        Text(
                            text = Formatters.formatDateTime(lastUpdatedTime),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = apiStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilver
                    )
                }
            }
        }

        // Rate Trend Chart Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rate Movement History",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )

                        // Metal Selector Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ChartMetalChip(
                                label = "Gold 24K",
                                selected = selectedHistoryMetal == "GOLD_24K",
                                onClick = { selectedHistoryMetal = "GOLD_24K" }
                            )
                            ChartMetalChip(
                                label = "Silver",
                                selected = selectedHistoryMetal == "SILVER_1G",
                                onClick = { selectedHistoryMetal = "SILVER_1G" }
                            )
                            ChartMetalChip(
                                label = "Platinum",
                                selected = selectedHistoryMetal == "PLATINUM_1G",
                                onClick = { selectedHistoryMetal = "PLATINUM_1G" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Period Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val historyPoints = when (selectedHistoryMetal) {
                            "SILVER_1G" -> listOf(91.2, 92.5, 93.0, 94.2, 95.0, 95.3, 94.5)
                            "PLATINUM_1G" -> listOf(3180.0, 3195.0, 3210.0, 3205.0, 3220.0, 3225.0, 3240.0)
                            else -> listOf(7620.0, 7680.0, 7710.0, 7760.0, 7790.0, 7835.0, 7880.0)
                        }

                        val curRate = historyPoints.last()
                        val prevRate = historyPoints.first()
                        val diff = curRate - prevRate

                        Column {
                            Text(
                                text = "Current: ${Formatters.formatInr(curRate, includeDecimals = (selectedHistoryMetal == "SILVER_1G"))}",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "7-Day Range: ${Formatters.formatInr(historyPoints.minOrNull() ?: 0.0)} - ${Formatters.formatInr(historyPoints.maxOrNull() ?: 0.0)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (diff >= 0) ProfitGreen.copy(alpha = 0.15f) else LossRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${if (diff >= 0) "+" else ""}${Formatters.formatInr(diff)}",
                                color = if (diff >= 0) ProfitGreen else LossRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas Line Chart
                    val points = when (selectedHistoryMetal) {
                        "SILVER_1G" -> listOf(91.2, 92.5, 93.0, 94.2, 95.0, 95.3, 94.5)
                        "PLATINUM_1G" -> listOf(3180.0, 3195.0, 3210.0, 3205.0, 3220.0, 3225.0, 3240.0)
                        else -> listOf(7620.0, 7680.0, 7710.0, 7760.0, 7790.0, 7835.0, 7880.0)
                    }
                    InteractiveRateChart(points = points)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("6 Days Ago", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("3 Days Ago", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("Today", style = MaterialTheme.typography.labelSmall, color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Metal Rate Items List
        item {
            Text(
                text = "BOARD RATES BY PURITY",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }

        items(rates) { rate ->
            DetailedRateCard(
                rate = rate,
                onEdit = { editingRate = rate }
            )
        }
    }

    // Manual Rate Edit Dialog
    editingRate?.let { rate ->
        ManualRateEditDialog(
            rate = rate,
            onDismiss = { editingRate = null },
            onSave = { newRate, source, city, rateType, localAdj ->
                viewModel.updateManualRate(rate.id, newRate, source, city, rateType, localAdj)
                editingRate = null
            }
        )
    }
}

@Composable
fun ChartMetalChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) GoldPrimary else LuxurySurfaceElevated,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) LuxuryDarkBg else TextSilver,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InteractiveRateChart(points: List<Double>) {
    if (points.isEmpty()) return

    val min = points.minOrNull() ?: 0.0
    val max = points.maxOrNull() ?: 1.0
    val range = if (max - min == 0.0) 1.0 else max - min

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(vertical = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1)

        val path = Path()
        val fillPath = Path()

        val coordinates = points.mapIndexed { index, value ->
            val x = index * stepX
            val normalized = ((value - min) / range).toFloat()
            val y = height - (normalized * (height - 30f)) - 15f
            Offset(x, y)
        }

        coordinates.forEachIndexed { index, offset ->
            if (index == 0) {
                path.moveTo(offset.x, offset.y)
                fillPath.moveTo(offset.x, height)
                fillPath.lineTo(offset.x, offset.y)
            } else {
                val prev = coordinates[index - 1]
                val midX = (prev.x + offset.x) / 2
                path.cubicTo(midX, prev.y, midX, offset.y, offset.x, offset.y)
                fillPath.cubicTo(midX, prev.y, midX, offset.y, offset.x, offset.y)
            }
        }

        fillPath.lineTo(coordinates.last().x, height)
        fillPath.close()

        // Gradient under line
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    GoldPrimary.copy(alpha = 0.35f),
                    GoldPrimary.copy(alpha = 0.0f)
                )
            )
        )

        // Line
        drawPath(
            path = path,
            color = GoldPrimary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points
        coordinates.forEachIndexed { index, offset ->
            drawCircle(
                color = LuxuryDarkBg,
                radius = 5.dp.toPx(),
                center = offset
            )
            drawCircle(
                color = if (index == coordinates.lastIndex) ProfitGreen else GoldPrimary,
                radius = 3.5.dp.toPx(),
                center = offset
            )
        }
    }
}

@Composable
fun DetailedRateCard(
    rate: MetalRateEntity,
    onEdit: () -> Unit
) {
    val isPositive = rate.changeAmount >= 0
    val accentColor = when (rate.metal) {
        "GOLD" -> GoldPrimary
        "SILVER" -> SilverColor
        else -> PlatinumColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rate_detail_${rate.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LuxurySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, accentColor)
                    ) {
                        Text(
                            text = "${rate.metal} ${rate.purity}",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = LuxurySurfaceElevated
                    ) {
                        Text(
                            text = rate.rateType,
                            color = TextSilver,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Board Rate",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Rate per Gram",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = Formatters.formatInr(rate.ratePerGram, includeDecimals = (rate.metal == "SILVER")),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isPositive) ProfitGreen else LossRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${Formatters.formatInr(rate.changeAmount, true)}",
                            color = if (isPositive) ProfitGreen else LossRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "(${Formatters.formatPercentage(rate.changePercentage)}) vs Prev Close",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Additional details: Silver per kg, local adjustment, source & city
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LuxurySurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${rate.city} • ${rate.marketSource}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilver,
                        fontSize = 11.sp
                    )
                    if (rate.metal == "SILVER") {
                        val kg = rate.ratePerKg ?: (rate.ratePerGram * 1000.0)
                        Text(
                            text = "1 Kg: ${Formatters.formatInr(kg)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SilverColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    } else if (rate.localAdjustment != 0.0) {
                        Text(
                            text = "Local Adj: ${if (rate.localAdjustment > 0) "+" else ""}${Formatters.formatInr(rate.localAdjustment)}/g",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldAmber,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRateEditDialog(
    rate: MetalRateEntity,
    onDismiss: () -> Unit,
    onSave: (newRate: Double, source: String, city: String, rateType: String, localAdj: Double) -> Unit
) {
    var rateInput by remember { mutableStateOf(rate.ratePerGram.toString()) }
    var sourceInput by remember { mutableStateOf(rate.marketSource) }
    var cityInput by remember { mutableStateOf(rate.city) }
    var rateTypeInput by remember { mutableStateOf(rate.rateType) }
    var localAdjInput by remember { mutableStateOf(rate.localAdjustment.toString()) }

    val rateTypes = listOf("Local Bullion Rate", "Market Indicative", "Shop Buying Rate")
    val cities = listOf("Mumbai", "Delhi", "Chennai", "Kolkata", "Ahmedabad", "Jaipur", "Bangalore", "Hyderabad")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit ${rate.metal} ${rate.purity} Board Rate",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rateInput,
                    onValueChange = { rateInput = it },
                    label = { Text("Rate per Gram (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LuxurySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = localAdjInput,
                    onValueChange = { localAdjInput = it },
                    label = { Text("Local Sarafa Adjustment (₹/g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LuxurySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("City / Region") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LuxurySurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sourceInput,
                    onValueChange = { sourceInput = it },
                    label = { Text("Market Source (e.g. IBJA / MCX)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LuxurySurfaceBorder,
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
                    val parsedRate = rateInput.toDoubleOrNull() ?: rate.ratePerGram
                    val parsedAdj = localAdjInput.toDoubleOrNull() ?: 0.0
                    onSave(parsedRate, sourceInput, cityInput, rateTypeInput, parsedAdj)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Save Rates", color = Color.Black, fontWeight = FontWeight.Bold)
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
