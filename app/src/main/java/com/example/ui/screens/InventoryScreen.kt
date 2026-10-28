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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InventoryItemEntity
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAmber
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

@Composable
fun InventoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val inventory by viewModel.inventory.collectAsState()
    val rates by viewModel.rates.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMetalFilter by remember { mutableStateOf("ALL") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var viewingItem by remember { mutableStateOf<InventoryItemEntity?>(null) }

    val gold24k = rates.find { it.id == "GOLD_24K" }?.ratePerGram ?: 7880.0
    val silver999 = rates.find { it.id == "SILVER_1G" }?.ratePerGram ?: 94.50
    val plat950 = rates.find { it.id == "PLATINUM_1G" }?.ratePerGram ?: 3240.0

    // Filter items
    val filteredItems = inventory.filter { item ->
        val matchesQuery = item.name.contains(searchQuery, ignoreCase = true) ||
                item.sku.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)

        val matchesMetal = selectedMetalFilter == "ALL" || item.metal.equals(selectedMetalFilter, ignoreCase = true)
        val matchesStatus = selectedStatusFilter == "ALL" || item.status.equals(selectedStatusFilter, ignoreCase = true)

        matchesQuery && matchesMetal && matchesStatus
    }

    // Totals for current filter
    val totalQty = filteredItems.sumOf { it.quantity }
    val totalNetWt = filteredItems.sumOf { it.netWeight * it.quantity }
    val totalRecordedCost = filteredItems.sumOf { it.purchaseCost * it.quantity }

    val totalCurrentMarketVal = filteredItems.sumOf { item ->
        val rate = when (item.metal) {
            "GOLD" -> gold24k * item.purityPercent
            "SILVER" -> silver999 * item.purityPercent
            "PLATINUM" -> plat950 * item.purityPercent
            else -> gold24k
        }
        ((item.netWeight * rate) + item.stoneCost + item.makingCharges) * item.quantity
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("inventory_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "Jewellery Inventory",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalQty items in stock • ${Formatters.formatGrams(totalNetWt)} pure metal",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { showAddItemDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("inventory_add_item_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Valuation Summary Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Current Market Value", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(Formatters.formatInr(totalCurrentMarketVal), style = MaterialTheme.typography.titleMedium, color = GoldLight, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Recorded Purchase Cost", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(Formatters.formatInr(totalRecordedCost), style = MaterialTheme.typography.titleMedium, color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Unrealized Gain", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        val gain = totalCurrentMarketVal - totalRecordedCost
                        Text(
                            text = if (gain >= 0) "+${Formatters.formatInr(gain)}" else Formatters.formatInr(gain),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (gain >= 0) ProfitGreen else LossRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by SKU, Name or Category...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = LuxurySurfaceBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = LuxurySurface,
                    unfocusedContainerColor = LuxurySurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("inventory_search_bar")
            )
        }

        // Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterPill(label = "All Metals", selected = selectedMetalFilter == "ALL", onClick = { selectedMetalFilter = "ALL" })
                }
                item {
                    FilterPill(label = "Gold (Au)", selected = selectedMetalFilter == "GOLD", onClick = { selectedMetalFilter = "GOLD" })
                }
                item {
                    FilterPill(label = "Silver (Ag)", selected = selectedMetalFilter == "SILVER", onClick = { selectedMetalFilter = "SILVER" })
                }
                item {
                    FilterPill(label = "Platinum (Pt)", selected = selectedMetalFilter == "PLATINUM", onClick = { selectedMetalFilter = "PLATINUM" })
                }
                item {
                    FilterPill(label = "In Stock", selected = selectedStatusFilter == "IN_STOCK", onClick = {
                        selectedStatusFilter = if (selectedStatusFilter == "IN_STOCK") "ALL" else "IN_STOCK"
                    })
                }
            }
        }

        // Items List
        items(filteredItems) { item ->
            val rate = when (item.metal) {
                "GOLD" -> gold24k * item.purityPercent
                "SILVER" -> silver999 * item.purityPercent
                "PLATINUM" -> plat950 * item.purityPercent
                else -> gold24k
            }
            val currentEstimatedVal = (item.netWeight * rate) + item.stoneCost + item.makingCharges

            InventoryItemRow(
                item = item,
                currentMarketVal = currentEstimatedVal,
                onClick = { viewingItem = item }
            )
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AddInventoryItemDialog(
            rates = rates,
            onDismiss = { showAddItemDialog = false },
            onSave = { newItem ->
                viewModel.saveInventoryItem(newItem)
                showAddItemDialog = false
            }
        )
    }

    // View / Edit Item Details Dialog
    viewingItem?.let { item ->
        val rate = when (item.metal) {
            "GOLD" -> gold24k * item.purityPercent
            "SILVER" -> silver999 * item.purityPercent
            "PLATINUM" -> plat950 * item.purityPercent
            else -> gold24k
        }
        val currentEstimatedVal = (item.netWeight * rate) + item.stoneCost + item.makingCharges

        InventoryItemDetailDialog(
            item = item,
            currentMarketVal = currentEstimatedVal,
            onDismiss = { viewingItem = null },
            onDelete = {
                viewModel.deleteInventoryItem(item)
                viewingItem = null
            }
        )
    }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) GoldPrimary else LuxurySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) GoldPrimary else LuxurySurfaceBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else TextSilver,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItemEntity,
    currentMarketVal: Double,
    onClick: () -> Unit
) {
    val metalColor = when (item.metal) {
        "GOLD" -> GoldPrimary
        "SILVER" -> SilverColor
        else -> PlatinumColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("inventory_item_${item.sku}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(metalColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Diamond, contentDescription = null, tint = metalColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SKU: ${item.sku} • ${item.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.status == "IN_STOCK") ProfitGreenBg else LossRedBg
                ) {
                    Text(
                        text = if (item.status == "IN_STOCK") "Qty: ${item.quantity}" else item.status,
                        color = if (item.status == "IN_STOCK") ProfitGreen else LossRed,
                        fontSize = 11.sp,
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
                        Text("Purity & Weight", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                        Text("${item.purity} • ${item.netWeight}g net", style = MaterialTheme.typography.bodyMedium, color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column {
                        Text("Recorded Cost", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                        Text(Formatters.formatInr(item.purchaseCost), style = MaterialTheme.typography.bodyMedium, color = TextSilver, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Live Market Val", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                        Text(Formatters.formatInr(currentMarketVal), style = MaterialTheme.typography.bodyMedium, color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddInventoryItemDialog(
    rates: List<com.example.data.model.MetalRateEntity>,
    onDismiss: () -> Unit,
    onSave: (InventoryItemEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rings") }
    var metal by remember { mutableStateOf("GOLD") }
    var purity by remember { mutableStateOf("22K") }
    var grossWeightInput by remember { mutableStateOf("") }
    var netWeightInput by remember { mutableStateOf("") }
    var stoneCostInput by remember { mutableStateOf("0") }
    var makingChargesInput by remember { mutableStateOf("0") }
    var purchaseCostInput by remember { mutableStateOf("") }
    var sellingPriceInput by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("1") }
    var supplierName by remember { mutableStateOf("Direct Bullion Artisan") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Jewellery to Inventory", color = TextWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name (e.g. 22K Peacock Jhumka)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = purity,
                            onValueChange = { purity = it },
                            label = { Text("Purity (e.g. 22K)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = grossWeightInput,
                            onValueChange = { grossWeightInput = it },
                            label = { Text("Gross Wt (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = netWeightInput,
                            onValueChange = { netWeightInput = it },
                            label = { Text("Net Wt (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = purchaseCostInput,
                            onValueChange = { purchaseCostInput = it },
                            label = { Text("Purchase Cost (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = sellingPriceInput,
                            onValueChange = { sellingPriceInput = it },
                            label = { Text("Selling Price (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantityInput,
                            onValueChange = { quantityInput = it },
                            label = { Text("Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = supplierName,
                            onValueChange = { supplierName = it },
                            label = { Text("Supplier") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val gross = grossWeightInput.toDoubleOrNull() ?: 10.0
                    val net = netWeightInput.toDoubleOrNull() ?: gross
                    val pCost = purchaseCostInput.toDoubleOrNull() ?: (net * 7000.0)
                    val sPrice = sellingPriceInput.toDoubleOrNull() ?: (pCost * 1.15)
                    val qty = quantityInput.toIntOrNull() ?: 1

                    val purityFraction = when (purity) {
                        "24K" -> 0.999
                        "22K" -> 0.916
                        "18K" -> 0.750
                        "999" -> 0.999
                        "925" -> 0.925
                        else -> 0.916
                    }

                    val prefix = when (metal) {
                        "SILVER" -> "SLV"
                        "PLATINUM" -> "PLT"
                        else -> "GLD"
                    }

                    onSave(
                        InventoryItemEntity(
                            sku = "$prefix-${UUID.randomUUID().toString().take(6).uppercase()}",
                            name = if (name.isBlank()) "Jewellery Item" else name,
                            category = category,
                            metal = metal,
                            purity = purity,
                            purityPercent = purityFraction,
                            grossWeight = gross,
                            netWeight = net,
                            stoneWeight = (gross - net).coerceAtLeast(0.0),
                            stoneCost = stoneCostInput.toDoubleOrNull() ?: 0.0,
                            purchaseRate = pCost / net,
                            purchaseCost = pCost,
                            makingCharges = makingChargesInput.toDoubleOrNull() ?: 0.0,
                            sellingPrice = sPrice,
                            quantity = qty,
                            supplierName = supplierName,
                            notes = notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Add to Stock", color = Color.Black, fontWeight = FontWeight.Bold)
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

@Composable
fun InventoryItemDetailDialog(
    item: InventoryItemEntity,
    currentMarketVal: Double,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, color = TextWhite, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = LossRed)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SKU: ${item.sku} • ${item.category}", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Metal: ${item.metal} (${item.purity})", color = TextWhite)
                Text("Gross Weight: ${item.grossWeight}g | Net Metal: ${item.netWeight}g", color = TextWhite)
                if (item.stoneCost > 0) Text("Stone / Diamond Value: ${Formatters.formatInr(item.stoneCost)}", color = TextSilver)
                Text("Historical Purchase Cost: ${Formatters.formatInr(item.purchaseCost)}", color = TextWhite)
                Text("Live Market Valuation: ${Formatters.formatInr(currentMarketVal)}", color = GoldLight, fontWeight = FontWeight.Bold)
                Text("Available Quantity: ${item.quantity}", color = TextWhite)
                Text("Supplier: ${item.supplierName}", color = TextSilver)
                if (item.notes.isNotBlank()) Text("Notes: ${item.notes}", color = TextMuted)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = LuxurySurface,
        shape = RoundedCornerShape(16.dp)
    )
}
