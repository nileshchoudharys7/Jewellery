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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
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
import com.example.data.model.InventoryItemEntity
import com.example.data.model.MetalRateEntity
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LossRedBg
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
import java.util.UUID

enum class MakingChargeType(val label: String) {
    PER_GRAM("₹ / Gram"),
    PERCENTAGE("% of Metal"),
    FIXED("Fixed ₹")
}

@Composable
fun CalculatorScreen(
    viewModel: MainViewModel,
    onAddToInventory: (InventoryItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val rates by viewModel.rates.collectAsState()

    // Inputs
    var selectedMetal by remember { mutableStateOf("GOLD") }
    var selectedPurity by remember { mutableStateOf("22K") }
    var customPurityPercent by remember { mutableStateOf("91.6") }

    var grossWeightInput by remember { mutableStateOf("15.5") }
    var stoneWeightInput by remember { mutableStateOf("0.5") }
    var stoneCostInput by remember { mutableStateOf("2500") }

    var wastagePercentInput by remember { mutableStateOf("3.0") }
    var makingChargeType by remember { mutableStateOf(MakingChargeType.PER_GRAM) }
    var makingChargeValueInput by remember { mutableStateOf("650") }

    var otherChargesInput by remember { mutableStateOf("350") } // e.g. BIS hallmarking + certificate
    var discountInput by remember { mutableStateOf("1000") }
    var gstPercentInput by remember { mutableStateOf("3.0") }

    // Parse values safely
    val grossWeight = grossWeightInput.toDoubleOrNull() ?: 0.0
    val stoneWeight = stoneWeightInput.toDoubleOrNull() ?: 0.0
    val stoneCost = stoneCostInput.toDoubleOrNull() ?: 0.0
    val netWeight = (grossWeight - stoneWeight).coerceAtLeast(0.0)

    val wastagePercent = wastagePercentInput.toDoubleOrNull() ?: 0.0
    val makingChargeVal = makingChargeValueInput.toDoubleOrNull() ?: 0.0
    val otherCharges = otherChargesInput.toDoubleOrNull() ?: 0.0
    val discount = discountInput.toDoubleOrNull() ?: 0.0
    val gstPercent = gstPercentInput.toDoubleOrNull() ?: 3.0

    // Determine applied purity percentage and active metal rate
    val purityFraction = when (selectedMetal) {
        "GOLD" -> when (selectedPurity) {
            "24K" -> 0.999
            "22K" -> 0.916
            "18K" -> 0.750
            "14K" -> 0.585
            else -> (customPurityPercent.toDoubleOrNull() ?: 91.6) / 100.0
        }
        "SILVER" -> when (selectedPurity) {
            "999" -> 0.999
            "925" -> 0.925
            else -> 0.925
        }
        else -> 0.950 // Platinum
    }

    val gold24kRate = rates.find { it.id == "GOLD_24K" }?.ratePerGram ?: 7880.0
    val silver999Rate = rates.find { it.id == "SILVER_1G" }?.ratePerGram ?: 94.50
    val plat950Rate = rates.find { it.id == "PLATINUM_1G" }?.ratePerGram ?: 3240.0

    val baseRatePerGram = when (selectedMetal) {
        "GOLD" -> gold24kRate * purityFraction
        "SILVER" -> silver999Rate * purityFraction
        "PLATINUM" -> plat950Rate * (purityFraction / 0.950)
        else -> gold24kRate
    }

    // Calculations
    val metalCost = netWeight * baseRatePerGram
    val wastageGrams = netWeight * (wastagePercent / 100.0)
    val wastageCost = wastageGrams * baseRatePerGram

    val calculatedMakingCharges = when (makingChargeType) {
        MakingChargeType.PER_GRAM -> netWeight * makingChargeVal
        MakingChargeType.PERCENTAGE -> metalCost * (makingChargeVal / 100.0)
        MakingChargeType.FIXED -> makingChargeVal
    }

    // Manufacturing / Purchase Cost baseline (Raw metal + actual stone + bare minimum artisan cost)
    val baselineMfgCost = metalCost + wastageCost + stoneCost + (calculatedMakingCharges * 0.40) + otherCharges

    // Pre-Tax Price to Customer
    val taxableSellingPrice = (metalCost + wastageCost + calculatedMakingCharges + stoneCost + otherCharges - discount)
        .coerceAtLeast(0.0)

    val gstAmount = taxableSellingPrice * (gstPercent / 100.0)
    val finalCustomerBill = taxableSellingPrice + gstAmount

    val grossProfit = (taxableSellingPrice - baselineMfgCost)
    val profitMarginPct = if (taxableSellingPrice > 0) (grossProfit / taxableSellingPrice) * 100.0 else 0.0

    var savedMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("calculator_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Jewellery Rate Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Accurate Purity, Wastage, Making Charges, GST & Profit Margin",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Metal Selection
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. SELECT METAL & PURITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectableMetalTab(
                            title = "Gold (Au)",
                            color = GoldPrimary,
                            selected = selectedMetal == "GOLD",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedMetal = "GOLD"
                                selectedPurity = "22K"
                            }
                        )
                        SelectableMetalTab(
                            title = "Silver (Ag)",
                            color = SilverColor,
                            selected = selectedMetal == "SILVER",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedMetal = "SILVER"
                                selectedPurity = "925"
                            }
                        )
                        SelectableMetalTab(
                            title = "Platinum (Pt)",
                            color = PlatinumColor,
                            selected = selectedMetal == "PLATINUM",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedMetal = "PLATINUM"
                                selectedPurity = "950"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Purity Options
                    val purities = when (selectedMetal) {
                        "GOLD" -> listOf("24K", "22K", "18K", "14K", "Custom")
                        "SILVER" -> listOf("999", "925", "Custom")
                        else -> listOf("950", "Custom")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        purities.forEach { purity ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedPurity == purity) GoldPrimary else LuxurySurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedPurity == purity) GoldPrimary else LuxurySurfaceBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPurity = purity }
                            ) {
                                Text(
                                    text = purity,
                                    color = if (selectedPurity == purity) Color.Black else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    if (selectedPurity == "Custom") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customPurityPercent,
                            onValueChange = { customPurityPercent = it },
                            label = { Text("Custom Purity % (e.g. 91.6)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Applied Base Rate: ${Formatters.formatInr(baseRatePerGram, includeDecimals = true)} / gram",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Weight & Wastage Inputs
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2. WEIGHT & STONE DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = grossWeightInput,
                            onValueChange = { grossWeightInput = it },
                            label = { Text("Gross Wt (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = stoneWeightInput,
                            onValueChange = { stoneWeightInput = it },
                            label = { Text("Stone Wt (g/ct)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = stoneCostInput,
                            onValueChange = { stoneCostInput = it },
                            label = { Text("Stone/Diamond (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = wastagePercentInput,
                            onValueChange = { wastagePercentInput = it },
                            label = { Text("Wastage %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LuxurySurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Net Metal Wt: ${Formatters.formatGrams(netWeight)}",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Wastage: ${Formatters.formatGrams(wastageGrams)} (${Formatters.formatInr(wastageCost)})",
                                color = TextSilver,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Making Charges, GST & Discounts
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "3. MAKING CHARGES & TAXES",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    // Making Charge Mode Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MakingChargeType.values().forEach { mode ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (makingChargeType == mode) GoldPrimary else LuxurySurfaceElevated,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { makingChargeType = mode }
                            ) {
                                Text(
                                    text = mode.label,
                                    color = if (makingChargeType == mode) Color.Black else TextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = makingChargeValueInput,
                            onValueChange = { makingChargeValueInput = it },
                            label = { Text("Making Charge") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = otherChargesInput,
                            onValueChange = { otherChargesInput = it },
                            label = { Text("Hallmark / Other ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = gstPercentInput,
                            onValueChange = { gstPercentInput = it },
                            label = { Text("GST % (Standard 3%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                        OutlinedTextField(
                            value = discountInput,
                            onValueChange = { discountInput = it },
                            label = { Text("Discount ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )
                    }
                }
            }
        }

        // Calculation Breakdown Result Card (PROMINENT)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CALCULATION BREAKDOWN",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (grossProfit >= 0) ProfitGreenBg else LossRedBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (grossProfit >= 0) ProfitGreen else LossRed)
                        ) {
                            Text(
                                text = "${Formatters.formatPercentage(profitMarginPct)} Margin",
                                color = if (grossProfit >= 0) ProfitGreen else LossRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CalculationLine("Pure Metal Cost (${Formatters.formatGrams(netWeight)})", Formatters.formatInr(metalCost, true))
                    CalculationLine("Wastage Cost (${wastagePercent}%)", Formatters.formatInr(wastageCost, true))
                    CalculationLine("Making Charges", Formatters.formatInr(calculatedMakingCharges, true))
                    if (stoneCost > 0) CalculationLine("Stone & Diamond Charges", Formatters.formatInr(stoneCost))
                    if (otherCharges > 0) CalculationLine("Hallmark & Extra Expenses", Formatters.formatInr(otherCharges))
                    if (discount > 0) CalculationLine("Discount Offered", "-${Formatters.formatInr(discount)}", isDeduction = true)

                    Divider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = LuxurySurfaceBorder
                    )

                    CalculationLine("Taxable Amount", Formatters.formatInr(taxableSellingPrice), isHighlight = true)
                    CalculationLine("GST (${gstPercent}% - CGST+SGST)", Formatters.formatInr(gstAmount, true))

                    Divider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = GoldPrimary.copy(alpha = 0.5f)
                    )

                    // Final Customer Bill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Final Customer Bill",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Including all taxes & hallmark",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = Formatters.formatInr(finalCustomerBill),
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Profit / Loss Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LuxurySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (grossProfit >= 0) ProfitGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estimated Gross Profit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = Formatters.formatInr(grossProfit),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (grossProfit >= 0) ProfitGreen else LossRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Item Mfg Cost",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                                Text(
                                    text = Formatters.formatInr(baselineMfgCost),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Save to Inventory shortcut button
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newItem = InventoryItemEntity(
                                sku = "JWL-${UUID.randomUUID().toString().take(6).uppercase()}",
                                name = "$selectedPurity $selectedMetal Jewellery Piece",
                                category = "Custom Ornaments",
                                metal = selectedMetal,
                                purity = selectedPurity,
                                purityPercent = purityFraction,
                                grossWeight = grossWeight,
                                netWeight = netWeight,
                                stoneWeight = stoneWeight,
                                stoneCost = stoneCost,
                                purchaseRate = baseRatePerGram,
                                purchaseCost = baselineMfgCost,
                                makingCharges = calculatedMakingCharges,
                                wastagePercent = wastagePercent,
                                sellingPrice = taxableSellingPrice,
                                quantity = 1,
                                supplierName = "In-House Workshop",
                                notes = "Calculated via Jewellery Profit Master"
                            )
                            viewModel.saveInventoryItem(newItem)
                            savedMessage = "Item successfully saved to Inventory with SKU ${newItem.sku}!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calc_save_to_inventory_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save as Inventory Stock Item",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    savedMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            color = ProfitGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableMetalTab(
    title: String,
    color: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) color.copy(alpha = 0.2f) else LuxurySurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) color else LuxurySurfaceBorder
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (selected) TextWhite else TextSilver,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CalculationLine(
    label: String,
    value: String,
    isDeduction: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) TextWhite else TextSilver,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isDeduction -> ProfitGreen
                isHighlight -> GoldLight
                else -> TextWhite
            },
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
