package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MetalType(val displayName: String) {
    GOLD("Gold"),
    SILVER("Silver"),
    PLATINUM("Platinum")
}

@Entity(tableName = "metal_rates")
data class MetalRateEntity(
    @PrimaryKey val id: String, // e.g. "GOLD_24K", "GOLD_22K", "GOLD_18K", "SILVER_1G", "PLATINUM_1G"
    val metal: String, // "GOLD", "SILVER", "PLATINUM"
    val purity: String, // "24K", "22K", "18K", "14K", "999", "925", "950"
    val ratePerGram: Double,
    val ratePerKg: Double? = null,
    val previousRate: Double,
    val changeAmount: Double,
    val changePercentage: Double,
    val lastUpdated: Long = System.currentTimeMillis(),
    val marketSource: String = "IBJA / National Bullion",
    val city: String = "Mumbai",
    val rateType: String = "Local Bullion Rate", // "Market Indicative", "Local Bullion Rate", "Shop Buying Rate"
    val localAdjustment: Double = 0.0, // ₹ per gram
    val gstPercent: Double = 3.0
)

@Entity(tableName = "rate_history")
data class RateHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metal: String, // "GOLD_24K", "GOLD_22K", "SILVER_1G", "PLATINUM_1G"
    val rate: Double,
    val timestamp: Long,
    val label: String
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val sku: String, // e.g. "RNG-22K-001"
    val name: String,
    val category: String, // "Rings", "Chains & Necklaces", "Bangles & Kadas", "Earrings", "Silver Articles", "Platinum Ornaments"
    val metal: String, // "GOLD", "SILVER", "PLATINUM"
    val purity: String, // "24K", "22K", "18K", "14K", "999", "925", "950"
    val purityPercent: Double, // e.g. 0.916
    val grossWeight: Double, // in grams
    val netWeight: Double, // net metal in grams
    val stoneWeight: Double = 0.0, // stone wt in grams/carats
    val stoneCost: Double = 0.0, // ₹
    val purchaseRate: Double, // ₹ per gram at purchase
    val purchaseCost: Double, // total recorded purchase/manufacturing cost
    val makingCharges: Double, // ₹
    val wastagePercent: Double = 0.0,
    val sellingPrice: Double, // current tagged price
    val quantity: Int = 1,
    val supplierName: String = "Direct Artisan",
    val purchaseDate: Long = System.currentTimeMillis(),
    val status: String = "IN_STOCK", // "IN_STOCK", "SOLD", "RETURNED", "REPAIRED"
    val hsnCode: String = "7113",
    val notes: String = ""
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val invoiceNumber: String, // e.g. "INV-2026-001"
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String = "",
    val customerGstin: String = "",
    val date: Long = System.currentTimeMillis(),
    val subtotal: Double,
    val discountAmount: Double = 0.0,
    val makingChargesTotal: Double = 0.0,
    val taxableAmount: Double,
    val gstRate: Double = 3.0,
    val cgstAmount: Double,
    val sgstAmount: Double,
    val totalAmount: Double,
    val paidAmount: Double,
    val balanceDue: Double,
    val paymentMode: String = "UPI", // "UPI", "CASH", "CARD", "BANK_TRANSFER", "SPLIT"
    val status: String = "PAID", // "PAID", "PARTIAL", "REFUNDED"
    val notes: String = ""
)

@Entity(tableName = "invoice_items")
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val sku: String? = null,
    val itemName: String,
    val metal: String,
    val purity: String,
    val grossWeight: Double,
    val netWeight: Double,
    val rateApplied: Double,
    val makingCharges: Double,
    val wastageCost: Double = 0.0,
    val stoneCharges: Double = 0.0,
    val itemPrice: Double,
    val itemCost: Double // Original purchase/mfg cost to compute realized profit
)

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val address: String = "",
    val totalPurchases: Double = 0.0,
    val outstandingBalance: Double = 0.0
)

@Entity(tableName = "suppliers")
data class SupplierEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val city: String,
    val totalPurchased: Double = 0.0,
    val outstandingPayable: Double = 0.0
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Rent & Electricity", "Staff Salary", "Security & Vault", "Hallmarking & BIS", "Store Maintenance", "Other"
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "shop_profile")
data class ShopProfileEntity(
    @PrimaryKey val id: Int = 1,
    val shopName: String = "Shree Laxmi Jewellers",
    val tagline: String = "Certified BIS 916 Hallmark & Diamond Solitaires",
    val ownerName: String = "Rajesh Soni",
    val phone: String = "+91 98200 45678",
    val address: String = "Shop 12, Sarafa Bazar, Zaveri Market, Mumbai - 400002",
    val gstin: String = "27AAAPL8901M1ZT",
    val bisHallmarkId: String = "HM/C-7845210",
    val defaultCity: String = "Mumbai",
    val defaultGstPercent: Double = 3.0,
    val defaultMakingChargePercent: Double = 12.0
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // "RATE_ALERT", "LOW_STOCK", "HIGH_VALUE", "PAYMENT_DUE", "SALES_SUMMARY"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
