package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopProfileEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LossRed
import com.example.ui.theme.LuxuryDarkBg
import com.example.ui.theme.LuxurySurface
import com.example.ui.theme.LuxurySurfaceBorder
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val shopProfile by viewModel.shopProfile.collectAsState()

    var shopName by remember(shopProfile) { mutableStateOf(shopProfile?.shopName ?: "Shree Laxmi Jewellers") }
    var tagline by remember(shopProfile) { mutableStateOf(shopProfile?.tagline ?: "Certified BIS 916 Hallmark") }
    var ownerName by remember(shopProfile) { mutableStateOf(shopProfile?.ownerName ?: "Rajesh Soni") }
    var phone by remember(shopProfile) { mutableStateOf(shopProfile?.phone ?: "+91 98200 45678") }
    var address by remember(shopProfile) { mutableStateOf(shopProfile?.address ?: "Shop 12, Sarafa Bazar, Zaveri Market, Mumbai") }
    var gstin by remember(shopProfile) { mutableStateOf(shopProfile?.gstin ?: "27AAAPL8901M1ZT") }
    var bisId by remember(shopProfile) { mutableStateOf(shopProfile?.bisHallmarkId ?: "HM/C-7845210") }
    var city by remember(shopProfile) { mutableStateOf(shopProfile?.defaultCity ?: "Mumbai") }

    var savedStatus by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Shop Profile & Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Indian Jewellery Business Configuration & BIS Hallmark Registration",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        savedStatus?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ProfitGreen.copy(alpha = 0.15f),
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

        // Shop Credentials Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("STORE INFORMATION", style = MaterialTheme.typography.labelSmall, color = GoldPrimary, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = shopName,
                        onValueChange = { shopName = it },
                        label = { Text("Jewellery Store Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Store Tagline / Subtitle") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Proprietor Name") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Contact Phone") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Showroom Address") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gstin,
                            onValueChange = { gstin = it },
                            label = { Text("GSTIN Registration") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                        OutlinedTextField(
                            value = bisId,
                            onValueChange = { bisId = it },
                            label = { Text("BIS Hallmark Center ID") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveShopProfile(
                                ShopProfileEntity(
                                    id = 1,
                                    shopName = shopName,
                                    tagline = tagline,
                                    ownerName = ownerName,
                                    phone = phone,
                                    address = address,
                                    gstin = gstin,
                                    bisHallmarkId = bisId,
                                    defaultCity = city
                                )
                            )
                            savedStatus = "Store Profile updated successfully!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_save_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = "Save", tint = Color.Black)
                        Text(" Save Profile Details", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Demo Data Reset
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LuxurySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LuxurySurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("SAMPLE DEMO DATA", style = MaterialTheme.typography.labelSmall, color = GoldLight, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Populate or reset authentic Indian jewellery samples (Bridal Necklaces, Kadas, Solitaires, Silver thalis, sample invoices and bullion rate history).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSilver
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        modifier = Modifier.testTag("settings_reset_demo_button")
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, tint = GoldLight)
                        Text(" Reload Authentic Sample Data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Sample Data?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will restore the authentic initial Indian jewellery sample items, invoices, suppliers and bullion board rates.",
                    color = TextSilver
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDemoData()
                        showResetDialog = false
                        savedStatus = "Demo data successfully reloaded!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Confirm Reset", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextWhite)
                }
            },
            containerColor = LuxurySurface
        )
    }
}
