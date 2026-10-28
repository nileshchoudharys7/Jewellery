package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InventoryItemEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.ShopProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.common.Formatters
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldAmber
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
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import java.util.UUID

@Composable
fun BillingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val invoices by viewModel.invoices.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var selectedInvoiceForPreview by remember { mutableStateOf<InvoiceEntity?>(null) }

    val filteredInvoices = invoices.filter {
        it.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.customerPhone.contains(searchQuery, ignoreCase = true)
    }

    val totalBilled = filteredInvoices.sumOf { it.totalAmount }
    val totalBalanceDue = filteredInvoices.sumOf { it.balanceDue }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("billing_screen"),
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
                        text = "Jewellery Billing & GST",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Official Invoices • BIS Hallmark • HSN 7113",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = { showCreateInvoiceDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("billing_create_invoice_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Bill", tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Bill", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary Cards
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
                        Text("Total Billed Sales", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(Formatters.formatInr(totalBilled), style = MaterialTheme.typography.titleMedium, color = GoldLight, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Outstanding Receivables", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = Formatters.formatInr(totalBalanceDue),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (totalBalanceDue > 0) LossRed else ProfitGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search invoice by number, customer, phone...") },
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
                modifier = Modifier.fillMaxWidth().testTag("billing_search_input")
            )
        }

        // Invoice List
        item {
            Text(
                text = "INVOICES & RECEIPTS (${filteredInvoices.size})",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }

        items(filteredInvoices) { inv ->
            InvoiceCard(
                invoice = inv,
                onClick = { selectedInvoiceForPreview = inv }
            )
        }
    }

    // Create Invoice Modal Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            inStockItems = inventory.filter { it.status == "IN_STOCK" && it.quantity > 0 },
            onDismiss = { showCreateInvoiceDialog = false },
            onCreate = { newInvoice, items ->
                viewModel.createInvoice(newInvoice, items)
                showCreateInvoiceDialog = false
            }
        )
    }

    // Invoice Slip Preview Dialog
    selectedInvoiceForPreview?.let { inv ->
        InvoiceSlipDialog(
            invoice = inv,
            shopProfile = shopProfile,
            onDismiss = { selectedInvoiceForPreview = null }
        )
    }
}

@Composable
fun InvoiceCard(
    invoice: InvoiceEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("invoice_card_${invoice.invoiceNumber}"),
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
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${invoice.customerName} • ${invoice.customerPhone}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (invoice.balanceDue <= 0) ProfitGreenBg else LossRedBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (invoice.balanceDue <= 0) ProfitGreen else LossRed)
                ) {
                    Text(
                        text = if (invoice.balanceDue <= 0) "PAID" else "DUE: ${Formatters.formatInr(invoice.balanceDue)}",
                        color = if (invoice.balanceDue <= 0) ProfitGreen else LossRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${Formatters.formatDateTime(invoice.date)} • Mode: ${invoice.paymentMode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = Formatters.formatInr(invoice.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CreateInvoiceDialog(
    inStockItems: List<InventoryItemEntity>,
    onDismiss: () -> Unit,
    onCreate: (InvoiceEntity, List<InvoiceItemEntity>) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("UPI") }
    var discountText by remember { mutableStateOf("0") }
    var paidAmountText by remember { mutableStateOf("") }

    val selectedItems = remember { mutableStateListOf<InventoryItemEntity>() }

    // If no inventory item is selected yet, allow user to pick
    var showItemPicker by remember { mutableStateOf(false) }

    // Calculate totals
    val subtotal = selectedItems.sumOf { it.sellingPrice }
    val discount = discountText.toDoubleOrNull() ?: 0.0
    val taxable = (subtotal - discount).coerceAtLeast(0.0)
    val gstRate = 3.0 // Indian standard jewellery GST
    val cgst = taxable * 0.015
    val sgst = taxable * 0.015
    val total = taxable + cgst + sgst
    val paid = paidAmountText.toDoubleOrNull() ?: total
    val due = (total - paid).coerceAtLeast(0.0)

    val invNumber = "INV-2026-${(100..999).random()}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Customer Bill", color = TextWhite, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("CUSTOMER DETAILS", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Full Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = customerAddress,
                            onValueChange = { customerAddress = it },
                            label = { Text("City / Address") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("JEWELLERY ITEMS (${selectedItems.size})", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { showItemPicker = true },
                            colors = ButtonDefaults.buttonColors(containerColor = LuxurySurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Add From Stock", color = GoldPrimary, fontSize = 11.sp)
                        }
                    }
                }

                items(selectedItems) { item ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LuxurySurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${item.purity} • ${item.netWeight}g • ${Formatters.formatInr(item.sellingPrice)}", color = TextSilver, fontSize = 11.sp)
                            }
                            IconButton(onClick = { selectedItems.remove(item) }, modifier = Modifier.size(28.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = LossRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            label = { Text("Discount ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = paidAmountText,
                            onValueChange = { paidAmountText = it },
                            placeholder = { Text(Formatters.formatInr(total)) },
                            label = { Text("Amount Paid ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LuxurySurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal", color = TextSilver, fontSize = 12.sp)
                                Text(Formatters.formatInr(subtotal), color = TextWhite, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GST 3% (1.5% CGST + 1.5% SGST)", color = TextSilver, fontSize = 12.sp)
                                Text(Formatters.formatInr(cgst + sgst), color = TextWhite, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Bill Amount", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(Formatters.formatInr(total), color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isNotBlank() && selectedItems.isNotEmpty()) {
                        val invoice = InvoiceEntity(
                            invoiceNumber = invNumber,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            customerAddress = customerAddress,
                            subtotal = subtotal,
                            discountAmount = discount,
                            taxableAmount = taxable,
                            gstRate = 3.0,
                            cgstAmount = cgst,
                            sgstAmount = sgst,
                            totalAmount = total,
                            paidAmount = paid,
                            balanceDue = due,
                            paymentMode = paymentMode,
                            status = if (due <= 0) "PAID" else "PARTIAL"
                        )

                        val invoiceItems = selectedItems.map { itm ->
                            InvoiceItemEntity(
                                invoiceNumber = invNumber,
                                sku = itm.sku,
                                itemName = itm.name,
                                metal = itm.metal,
                                purity = itm.purity,
                                grossWeight = itm.grossWeight,
                                netWeight = itm.netWeight,
                                rateApplied = itm.purchaseRate * 1.1,
                                makingCharges = itm.makingCharges,
                                stoneCharges = itm.stoneCost,
                                itemPrice = itm.sellingPrice,
                                itemCost = itm.purchaseCost
                            )
                        }

                        onCreate(invoice, invoiceItems)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Generate Bill", color = Color.Black, fontWeight = FontWeight.Bold)
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

    // Sub-dialog to pick from inventory
    if (showItemPicker) {
        AlertDialog(
            onDismissRequest = { showItemPicker = false },
            title = { Text("Select Jewellery from Stock", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (inStockItems.isEmpty()) {
                        item { Text("No items available in stock. Add stock first.", color = TextMuted) }
                    } else {
                        items(inStockItems) { stockItem ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = LuxurySurfaceElevated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedItems.add(stockItem)
                                        showItemPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(stockItem.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${stockItem.sku} • ${stockItem.purity} • ${stockItem.netWeight}g", color = TextSilver, fontSize = 11.sp)
                                    }
                                    Text(Formatters.formatInr(stockItem.sellingPrice), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showItemPicker = false }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) {
                    Text("Done", color = Color.Black)
                }
            },
            containerColor = LuxurySurface
        )
    }
}

@Composable
fun InvoiceSlipDialog(
    invoice: InvoiceEntity,
    shopProfile: ShopProfileEntity?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var copiedText by remember { mutableStateOf(false) }

    val formattedSlip = buildString {
        appendLine("==========================================")
        appendLine("       ${shopProfile?.shopName ?: "SHREE LAXMI JEWELLERS"}")
        appendLine("  ${shopProfile?.tagline ?: "Certified BIS Hallmark & Diamonds"}")
        appendLine("  ${shopProfile?.address ?: "Zaveri Market, Mumbai"}")
        appendLine("  GSTIN: ${shopProfile?.gstin ?: "27AAAPL8901M1ZT"} | BIS: ${shopProfile?.bisHallmarkId ?: "HM/C-7845210"}")
        appendLine("==========================================")
        appendLine("INVOICE NO : ${invoice.invoiceNumber}")
        appendLine("DATE       : ${Formatters.formatDateTime(invoice.date)}")
        appendLine("CUSTOMER   : ${invoice.customerName}")
        appendLine("PHONE      : ${invoice.customerPhone}")
        appendLine("HSN CODE   : 7113 (Jewellery of Precious Metal)")
        appendLine("------------------------------------------")
        appendLine("Subtotal               : ${Formatters.formatInr(invoice.subtotal)}")
        appendLine("Discount               : -${Formatters.formatInr(invoice.discountAmount)}")
        appendLine("Taxable Amount         : ${Formatters.formatInr(invoice.taxableAmount)}")
        appendLine("CGST (1.5%)            : ${Formatters.formatInr(invoice.cgstAmount, true)}")
        appendLine("SGST (1.5%)            : ${Formatters.formatInr(invoice.sgstAmount, true)}")
        appendLine("------------------------------------------")
        appendLine("TOTAL BILL AMOUNT      : ${Formatters.formatInr(invoice.totalAmount)}")
        appendLine("AMOUNT PAID (${invoice.paymentMode}) : ${Formatters.formatInr(invoice.paidAmount)}")
        appendLine("BALANCE OUTSTANDING    : ${Formatters.formatInr(invoice.balanceDue)}")
        appendLine("==========================================")
        appendLine("Terms & Conditions:")
        appendLine("1. Gold purity guaranteed as per Govt BIS Hallmark.")
        appendLine("2. Exchange valid against presentation of original bill.")
        appendLine("Thank you for your valued patronage!")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tax Invoice Receipt", color = TextWhite, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Jewellery Invoice", formattedSlip)
                    clipboard.setPrimaryClip(clip)
                    copiedText = true
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Copy / Share", tint = GoldPrimary)
                }
            }
        },
        text = {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LuxuryBlack,
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = formattedSlip,
                        color = TextWhite,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    if (copiedText) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Invoice copied to clipboard ready to send on WhatsApp!", color = ProfitGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
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
