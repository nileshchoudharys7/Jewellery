package com.example.data.local

import com.example.data.model.CustomerEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.InventoryItemEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.MetalRateEntity
import com.example.data.model.NotificationItem
import com.example.data.model.RateHistoryEntity
import com.example.data.model.ShopProfileEntity
import com.example.data.model.SupplierEntity

object InitialData {
    val defaultShopProfile = ShopProfileEntity(
        id = 1,
        shopName = "Shree Laxmi Jewellers",
        tagline = "Certified BIS 916 Hallmark & Diamond Solitaires",
        ownerName = "Rajesh Soni",
        phone = "+91 98200 45678",
        address = "Shop 12, Sarafa Bazar, Zaveri Market, Mumbai - 400002",
        gstin = "27AAAPL8901M1ZT",
        bisHallmarkId = "HM/C-7845210",
        defaultCity = "Mumbai",
        defaultGstPercent = 3.0,
        defaultMakingChargePercent = 12.0
    )

    fun getDefaultRates(): List<MetalRateEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MetalRateEntity(
                id = "GOLD_24K",
                metal = "GOLD",
                purity = "24K",
                ratePerGram = 7880.0,
                ratePerKg = 7880000.0,
                previousRate = 7835.0,
                changeAmount = 45.0,
                changePercentage = 0.57,
                lastUpdated = now,
                marketSource = "IBJA / National Bullion",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            ),
            MetalRateEntity(
                id = "GOLD_22K",
                metal = "GOLD",
                purity = "22K",
                ratePerGram = 7225.0,
                ratePerKg = 7225000.0,
                previousRate = 7185.0,
                changeAmount = 40.0,
                changePercentage = 0.56,
                lastUpdated = now,
                marketSource = "IBJA / National Bullion",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            ),
            MetalRateEntity(
                id = "GOLD_18K",
                metal = "GOLD",
                purity = "18K",
                ratePerGram = 5910.0,
                ratePerKg = 5910000.0,
                previousRate = 5878.0,
                changeAmount = 32.0,
                changePercentage = 0.54,
                lastUpdated = now,
                marketSource = "IBJA / National Bullion",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            ),
            MetalRateEntity(
                id = "GOLD_14K",
                metal = "GOLD",
                purity = "14K",
                ratePerGram = 4600.0,
                ratePerKg = 4600000.0,
                previousRate = 4575.0,
                changeAmount = 25.0,
                changePercentage = 0.55,
                lastUpdated = now,
                marketSource = "IBJA / National Bullion",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            ),
            MetalRateEntity(
                id = "SILVER_1G",
                metal = "SILVER",
                purity = "999",
                ratePerGram = 94.50,
                ratePerKg = 94500.0,
                previousRate = 95.30,
                changeAmount = -0.80,
                changePercentage = -0.84,
                lastUpdated = now,
                marketSource = "MCX Mumbai Spot",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            ),
            MetalRateEntity(
                id = "PLATINUM_1G",
                metal = "PLATINUM",
                purity = "950",
                ratePerGram = 3240.0,
                ratePerKg = 3240000.0,
                previousRate = 3225.0,
                changeAmount = 15.0,
                changePercentage = 0.46,
                lastUpdated = now,
                marketSource = "LPPM Spot",
                city = "Mumbai",
                rateType = "Local Bullion Rate",
                localAdjustment = 0.0,
                gstPercent = 3.0
            )
        )
    }

    fun getDefaultRateHistory(): List<RateHistoryEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return listOf(
            RateHistoryEntity(metal = "GOLD_24K", rate = 7620.0, timestamp = now - 6 * dayMillis, label = "Day -6"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7680.0, timestamp = now - 5 * dayMillis, label = "Day -5"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7710.0, timestamp = now - 4 * dayMillis, label = "Day -4"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7760.0, timestamp = now - 3 * dayMillis, label = "Day -3"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7790.0, timestamp = now - 2 * dayMillis, label = "Day -2"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7835.0, timestamp = now - 1 * dayMillis, label = "Yesterday"),
            RateHistoryEntity(metal = "GOLD_24K", rate = 7880.0, timestamp = now, label = "Today"),

            RateHistoryEntity(metal = "SILVER_1G", rate = 91.20, timestamp = now - 6 * dayMillis, label = "Day -6"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 92.50, timestamp = now - 5 * dayMillis, label = "Day -5"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 93.00, timestamp = now - 4 * dayMillis, label = "Day -4"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 94.20, timestamp = now - 3 * dayMillis, label = "Day -3"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 95.00, timestamp = now - 2 * dayMillis, label = "Day -2"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 95.30, timestamp = now - 1 * dayMillis, label = "Yesterday"),
            RateHistoryEntity(metal = "SILVER_1G", rate = 94.50, timestamp = now, label = "Today"),

            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3180.0, timestamp = now - 6 * dayMillis, label = "Day -6"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3195.0, timestamp = now - 5 * dayMillis, label = "Day -5"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3210.0, timestamp = now - 4 * dayMillis, label = "Day -4"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3205.0, timestamp = now - 3 * dayMillis, label = "Day -3"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3220.0, timestamp = now - 2 * dayMillis, label = "Day -2"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3225.0, timestamp = now - 1 * dayMillis, label = "Yesterday"),
            RateHistoryEntity(metal = "PLATINUM_1G", rate = 3240.0, timestamp = now, label = "Today")
        )
    }

    fun getDefaultInventory(): List<InventoryItemEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return listOf(
            InventoryItemEntity(
                sku = "GLD-NCK-101",
                name = "22K Royal Temple Bridal Choker",
                category = "Chains & Necklaces",
                metal = "GOLD",
                purity = "22K",
                purityPercent = 0.916,
                grossWeight = 48.5,
                netWeight = 46.2,
                stoneWeight = 2.3,
                stoneCost = 8500.0,
                purchaseRate = 6950.0,
                purchaseCost = 321090.0 + 8500.0, // pure metal cost at purchase + stone
                makingCharges = 38000.0,
                wastagePercent = 3.5,
                sellingPrice = 382500.0,
                quantity = 1,
                supplierName = "Kalyan Bullion Crafts",
                purchaseDate = now - 15 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Antique matte polish, BIS Hallmarked 916 with Certificate"
            ),
            InventoryItemEntity(
                sku = "GLD-BNG-201",
                name = "22K Calcutta Filigree Kadas (Pair)",
                category = "Bangles & Kadas",
                metal = "GOLD",
                purity = "22K",
                purityPercent = 0.916,
                grossWeight = 34.2,
                netWeight = 34.2,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 7020.0,
                purchaseCost = 240084.0,
                makingCharges = 24500.0,
                wastagePercent = 2.0,
                sellingPrice = 278000.0,
                quantity = 2,
                supplierName = "Bengal Karigars Guild",
                purchaseDate = now - 10 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Size 2-6, screw openable mechanism"
            ),
            InventoryItemEntity(
                sku = "GLD-RNG-301",
                name = "18K Diamond Solitaire Engagement Ring",
                category = "Rings",
                metal = "GOLD",
                purity = "18K",
                purityPercent = 0.750,
                grossWeight = 4.8,
                netWeight = 4.6,
                stoneWeight = 0.2, // ~1 carat diamond
                stoneCost = 65000.0,
                purchaseRate = 5750.0,
                purchaseCost = 26450.0 + 65000.0,
                makingCharges = 12000.0,
                wastagePercent = 0.0,
                sellingPrice = 112000.0,
                quantity = 1,
                supplierName = "Surat Diamond Exporters",
                purchaseDate = now - 8 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "IGI Certified D-Color VVS1 Solitaire"
            ),
            InventoryItemEntity(
                sku = "GLD-EAR-401",
                name = "22K Traditional Peacock Jhumkas",
                category = "Earrings",
                metal = "GOLD",
                purity = "22K",
                purityPercent = 0.916,
                grossWeight = 18.2,
                netWeight = 17.4,
                stoneWeight = 0.8,
                stoneCost = 2800.0,
                purchaseRate = 7100.0,
                purchaseCost = 123540.0 + 2800.0,
                makingCharges = 14500.0,
                wastagePercent = 2.5,
                sellingPrice = 148000.0,
                quantity = 1, // LOW STOCK ALERT
                supplierName = "Jaipur Gems & Ornaments",
                purchaseDate = now - 5 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Natural ruby drop accents"
            ),
            InventoryItemEntity(
                sku = "GLD-CHN-501",
                name = "22K Solid Men's Rope Chain 22 Inch",
                category = "Chains & Necklaces",
                metal = "GOLD",
                purity = "22K",
                purityPercent = 0.916,
                grossWeight = 24.5,
                netWeight = 24.5,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 7120.0,
                purchaseCost = 174440.0,
                makingCharges = 13500.0,
                wastagePercent = 1.5,
                sellingPrice = 196000.0,
                quantity = 3,
                supplierName = "Rajkot Chain Works",
                purchaseDate = now - 4 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Sturdy lobster clasp lock"
            ),
            InventoryItemEntity(
                sku = "SLV-THL-601",
                name = "999 Pure Silver Puja Thali Set (7 Pcs)",
                category = "Silver Articles",
                metal = "SILVER",
                purity = "999",
                purityPercent = 0.999,
                grossWeight = 520.0,
                netWeight = 520.0,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 89.0,
                purchaseCost = 46280.0,
                makingCharges = 6500.0,
                wastagePercent = 1.0,
                sellingPrice = 57500.0,
                quantity = 2,
                supplierName = "Mathura Silver Art",
                purchaseDate = now - 12 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7114",
                notes = "Hand-embossed floral Gayatri mantra rim"
            ),
            InventoryItemEntity(
                sku = "SLV-PYL-602",
                name = "925 Sterling Silver Bridal Anklets / Payal",
                category = "Silver Articles",
                metal = "SILVER",
                purity = "925",
                purityPercent = 0.925,
                grossWeight = 98.0,
                netWeight = 98.0,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 82.0,
                purchaseCost = 8036.0,
                makingCharges = 1800.0,
                wastagePercent = 2.0,
                sellingPrice = 11200.0,
                quantity = 4,
                supplierName = "Agra Silver Crafters",
                purchaseDate = now - 14 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Anti-tarnish rhodium finish with melodic ghungroos"
            ),
            InventoryItemEntity(
                sku = "PLT-BND-701",
                name = "950 Platinum Couple Eternity Band",
                category = "Platinum Ornaments",
                metal = "PLATINUM",
                purity = "950",
                purityPercent = 0.950,
                grossWeight = 7.5,
                netWeight = 7.5,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 3150.0,
                purchaseCost = 23625.0,
                makingCharges = 5500.0,
                wastagePercent = 0.0,
                sellingPrice = 31500.0,
                quantity = 2,
                supplierName = "Platinum Guild India Partner",
                purchaseDate = now - 20 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7113",
                notes = "Hallmarked Pt950 laser engraved"
            ),
            InventoryItemEntity(
                sku = "GLD-CON-801",
                name = "24K Laxmi Gold Coin (10 Grams)",
                category = "Coins & Bars",
                metal = "GOLD",
                purity = "24K",
                purityPercent = 0.999,
                grossWeight = 10.0,
                netWeight = 10.0,
                stoneWeight = 0.0,
                stoneCost = 0.0,
                purchaseRate = 7750.0,
                purchaseCost = 77500.0,
                makingCharges = 1200.0,
                wastagePercent = 0.0,
                sellingPrice = 80500.0,
                quantity = 5,
                supplierName = "MMTC-PAMP Certified",
                purchaseDate = now - 3 * dayMillis,
                status = "IN_STOCK",
                hsnCode = "7118",
                notes = "Tamper-proof certicard blister packing"
            )
        )
    }

    fun getDefaultInvoices(): List<InvoiceEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return listOf(
            InvoiceEntity(
                invoiceNumber = "INV-2026-001",
                customerName = "Ananya Sharma",
                customerPhone = "+91 98111 22334",
                customerAddress = "Bandra West, Mumbai",
                customerGstin = "",
                date = now - 2 * 3600_000L, // 2 hours ago (Today)
                subtotal = 142000.0,
                discountAmount = 2000.0,
                makingChargesTotal = 14500.0,
                taxableAmount = 140000.0,
                gstRate = 3.0,
                cgstAmount = 2100.0,
                sgstAmount = 2100.0,
                totalAmount = 144200.0,
                paidAmount = 144200.0,
                balanceDue = 0.0,
                paymentMode = "UPI",
                status = "PAID",
                notes = "GPay Ref: 429188203912 - BIS Hallmarked"
            ),
            InvoiceEntity(
                invoiceNumber = "INV-2026-002",
                customerName = "Vikram Singhania",
                customerPhone = "+91 97222 33445",
                customerAddress = "Worli Sea Face, Mumbai",
                customerGstin = "",
                date = now - 5 * 3600_000L, // 5 hours ago (Today)
                subtotal = 78500.0,
                discountAmount = 1000.0,
                makingChargesTotal = 4500.0,
                taxableAmount = 77500.0,
                gstRate = 3.0,
                cgstAmount = 1162.5,
                sgstAmount = 1162.5,
                totalAmount = 79825.0,
                paidAmount = 50000.0,
                balanceDue = 29825.0,
                paymentMode = "CARD",
                status = "PARTIAL",
                notes = "Balance agreed on delivery next Monday"
            ),
            InvoiceEntity(
                invoiceNumber = "INV-2026-003",
                customerName = "Meera Patel",
                customerPhone = "+91 98333 44556",
                customerAddress = "Vile Parle, Mumbai",
                customerGstin = "",
                date = now - 2 * dayMillis, // 2 days ago
                subtotal = 265000.0,
                discountAmount = 5000.0,
                makingChargesTotal = 24000.0,
                taxableAmount = 260000.0,
                gstRate = 3.0,
                cgstAmount = 3900.0,
                sgstAmount = 3900.0,
                totalAmount = 267800.0,
                paidAmount = 267800.0,
                balanceDue = 0.0,
                paymentMode = "BANK_TRANSFER",
                status = "PAID",
                notes = "RTGS HDFC9928172 - Wedding collection"
            )
        )
    }

    fun getDefaultInvoiceItems(): List<InvoiceItemEntity> {
        return listOf(
            InvoiceItemEntity(
                invoiceNumber = "INV-2026-001",
                sku = "GLD-EAR-400",
                itemName = "22K Traditional Peacock Jhumkas",
                metal = "GOLD",
                purity = "22K",
                grossWeight = 17.6,
                netWeight = 17.0,
                rateApplied = 7200.0,
                makingCharges = 14500.0,
                wastageCost = 3000.0,
                stoneCharges = 2100.0,
                itemPrice = 142000.0,
                itemCost = 120500.0 // realized gross profit: ₹21,500
            ),
            InvoiceItemEntity(
                invoiceNumber = "INV-2026-002",
                sku = "SLV-THL-599",
                itemName = "999 Pure Silver Puja Thali Set",
                metal = "SILVER",
                purity = "999",
                grossWeight = 700.0,
                netWeight = 700.0,
                rateApplied = 94.0,
                makingCharges = 7700.0,
                wastageCost = 0.0,
                stoneCharges = 0.0,
                itemPrice = 78500.0,
                itemCost = 63500.0 // realized gross profit: ₹15,000
            ),
            InvoiceItemEntity(
                invoiceNumber = "INV-2026-003",
                sku = "GLD-BNG-199",
                itemName = "22K Designer Gold Bangles Pair",
                metal = "GOLD",
                purity = "22K",
                grossWeight = 33.0,
                netWeight = 33.0,
                rateApplied = 7180.0,
                makingCharges = 23000.0,
                wastageCost = 5060.0,
                stoneCharges = 0.0,
                itemPrice = 265000.0,
                itemCost = 228000.0 // realized gross profit: ₹37,000
            )
        )
    }

    fun getDefaultCustomers(): List<CustomerEntity> {
        return listOf(
            CustomerEntity(
                id = "CUST-001",
                name = "Ananya Sharma",
                phone = "+91 98111 22334",
                address = "Bandra West, Mumbai",
                totalPurchases = 144200.0,
                outstandingBalance = 0.0
            ),
            CustomerEntity(
                id = "CUST-002",
                name = "Vikram Singhania",
                phone = "+91 97222 33445",
                address = "Worli Sea Face, Mumbai",
                totalPurchases = 79825.0,
                outstandingBalance = 29825.0
            ),
            CustomerEntity(
                id = "CUST-003",
                name = "Meera Patel",
                phone = "+91 98333 44556",
                address = "Vile Parle, Mumbai",
                totalPurchases = 267800.0,
                outstandingBalance = 0.0
            ),
            CustomerEntity(
                id = "CUST-004",
                name = "Pooja Verma",
                phone = "+91 99444 55667",
                address = "Andheri East, Mumbai",
                totalPurchases = 89000.0,
                outstandingBalance = 12000.0
            )
        )
    }

    fun getDefaultSuppliers(): List<SupplierEntity> {
        return listOf(
            SupplierEntity(
                id = "SUPP-001",
                name = "Kalyan Bullion Crafts",
                phone = "+91 98210 11223",
                city = "Thrissur / Mumbai",
                totalPurchased = 1850000.0,
                outstandingPayable = 45000.0
            ),
            SupplierEntity(
                id = "SUPP-002",
                name = "Bengal Karigars Guild",
                phone = "+91 98300 44556",
                city = "Kolkata",
                totalPurchased = 1240000.0,
                outstandingPayable = 0.0
            ),
            SupplierEntity(
                id = "SUPP-003",
                name = "Surat Diamond Exporters",
                phone = "+91 98250 88990",
                city = "Surat",
                totalPurchased = 960000.0,
                outstandingPayable = 35000.0
            )
        )
    }

    fun getDefaultExpenses(): List<ExpenseEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L
        return listOf(
            ExpenseEntity(
                id = "EXP-001",
                title = "Showroom Rent & Electricity",
                category = "Rent & Electricity",
                amount = 45000.0,
                date = now - 3 * dayMillis,
                notes = "Zaveri Bazar shop monthly maintenance"
            ),
            ExpenseEntity(
                id = "EXP-002",
                title = "Security Guard & Safe Vault Service",
                category = "Security & Vault",
                amount = 18000.0,
                date = now - 5 * dayMillis,
                notes = "Godrej electronic safe surveillance subscription"
            ),
            ExpenseEntity(
                id = "EXP-003",
                title = "BIS Hallmarking & Laser Inscription Fees",
                category = "Hallmarking & BIS",
                amount = 6200.0,
                date = now - 1 * dayMillis,
                notes = "Govt recognized hallmarking center batch #4402"
            )
        )
    }

    fun getDefaultNotifications(): List<NotificationItem> {
        val now = System.currentTimeMillis()
        return listOf(
            NotificationItem(
                id = "NOTIF-1",
                title = "Gold Rate Surge: +₹45/g",
                message = "24K Gold rose to ₹7,880/g (+0.57%). Retail boards updated automatically.",
                type = "RATE_ALERT",
                timestamp = now - 30 * 60_000L,
                isRead = false
            ),
            NotificationItem(
                id = "NOTIF-2",
                title = "Low Stock Alert: 22K Peacock Jhumkas",
                message = "Only 1 unit remaining in stock. Consider reordering from Jaipur Gems.",
                type = "LOW_STOCK",
                timestamp = now - 3 * 3600_000L,
                isRead = false
            ),
            NotificationItem(
                id = "NOTIF-3",
                title = "Customer Balance Due: ₹29,825",
                message = "Vikram Singhania (Worli) has a pending payment for invoice INV-2026-002.",
                type = "PAYMENT_DUE",
                timestamp = now - 5 * 3600_000L,
                isRead = false
            ),
            NotificationItem(
                id = "NOTIF-4",
                title = "High Value Stock Item Stored",
                message = "Temple Bridal Choker valued at ₹3.82 Lakhs verified in main vault.",
                type = "HIGH_VALUE",
                timestamp = now - 24 * 3600_000L,
                isRead = true
            )
        )
    }
}
