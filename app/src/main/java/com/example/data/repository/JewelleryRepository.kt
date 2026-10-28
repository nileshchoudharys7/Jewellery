package com.example.data.repository

import com.example.data.api.MetalPriceService
import com.example.data.local.InitialData
import com.example.data.local.JewelleryDao
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class JewelleryRepository(
    private val dao: JewelleryDao,
    private val apiService: MetalPriceService = MetalPriceService()
) {
    val rates: Flow<List<MetalRateEntity>> = dao.getAllRates()
    val inventory: Flow<List<InventoryItemEntity>> = dao.getAllInventory()
    val invoices: Flow<List<InvoiceEntity>> = dao.getAllInvoices()
    val invoiceItems: Flow<List<InvoiceItemEntity>> = dao.getAllInvoiceItems()
    val customers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    val suppliers: Flow<List<SupplierEntity>> = dao.getAllSuppliers()
    val expenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    val shopProfile: Flow<ShopProfileEntity?> = dao.getShopProfile()
    val notifications: Flow<List<NotificationItem>> = dao.getAllNotifications()

    fun getRateHistory(metal: String): Flow<List<RateHistoryEntity>> = dao.getRateHistory(metal)

    suspend fun initializeIfEmpty() {
        val existingRates = dao.getAllRates().firstOrNull()
        if (existingRates.isNullOrEmpty()) {
            dao.insertRates(InitialData.getDefaultRates())
            dao.insertRateHistory(InitialData.getDefaultRateHistory())
            dao.insertInventoryList(InitialData.getDefaultInventory())
            dao.insertInvoices(InitialData.getDefaultInvoices())
            dao.insertInvoiceItems(InitialData.getDefaultInvoiceItems())
            dao.insertCustomers(InitialData.getDefaultCustomers())
            dao.insertSuppliers(InitialData.getDefaultSuppliers())
            dao.insertExpenses(InitialData.getDefaultExpenses())
            dao.insertShopProfile(InitialData.defaultShopProfile)
            dao.insertNotifications(InitialData.getDefaultNotifications())
        }
    }

    suspend fun resetToSampleData() {
        dao.insertRates(InitialData.getDefaultRates())
        dao.insertRateHistory(InitialData.getDefaultRateHistory())
        dao.insertInventoryList(InitialData.getDefaultInventory())
        dao.insertInvoices(InitialData.getDefaultInvoices())
        dao.insertInvoiceItems(InitialData.getDefaultInvoiceItems())
        dao.insertCustomers(InitialData.getDefaultCustomers())
        dao.insertSuppliers(InitialData.getDefaultSuppliers())
        dao.insertExpenses(InitialData.getDefaultExpenses())
        dao.insertShopProfile(InitialData.defaultShopProfile)
        dao.insertNotifications(InitialData.getDefaultNotifications())
    }

    suspend fun refreshLiveRates(): Pair<Boolean, String> {
        val current = dao.getAllRates().firstOrNull() ?: emptyList()
        val currentMap = current.associate { it.id to it.ratePerGram }
        val result = apiService.fetchLiveIndianBullionRates(currentMap)

        val updatedList = mutableListOf<MetalRateEntity>()
        val now = System.currentTimeMillis()

        fun buildOrUpdate(id: String, metal: String, purity: String, newRate: Double, ratePerKg: Double? = null): MetalRateEntity {
            val old = current.find { it.id == id }
            val oldRate = old?.ratePerGram ?: newRate
            val change = newRate - oldRate
            val changePct = if (oldRate > 0) (change / oldRate) * 100 else 0.0
            return MetalRateEntity(
                id = id,
                metal = metal,
                purity = purity,
                ratePerGram = newRate,
                ratePerKg = ratePerKg ?: (newRate * 1000.0),
                previousRate = oldRate,
                changeAmount = change,
                changePercentage = changePct,
                lastUpdated = now,
                marketSource = result.sourceName,
                city = old?.city ?: "Mumbai",
                rateType = old?.rateType ?: "Local Bullion Rate",
                localAdjustment = old?.localAdjustment ?: 0.0,
                gstPercent = old?.gstPercent ?: 3.0
            )
        }

        updatedList.add(buildOrUpdate("GOLD_24K", "GOLD", "24K", result.gold24kPerGram))
        updatedList.add(buildOrUpdate("GOLD_22K", "GOLD", "22K", result.gold22kPerGram))
        updatedList.add(buildOrUpdate("GOLD_18K", "GOLD", "18K", result.gold18kPerGram))
        updatedList.add(buildOrUpdate("GOLD_14K", "GOLD", "14K", kotlin.math.round(result.gold24kPerGram * 0.585 / 5.0) * 5.0))
        updatedList.add(buildOrUpdate("SILVER_1G", "SILVER", "999", result.silverPerGram, result.silverPerGram * 1000.0))
        updatedList.add(buildOrUpdate("PLATINUM_1G", "PLATINUM", "950", result.platinumPerGram))

        dao.insertRates(updatedList)

        // Add history point
        dao.insertRateHistory(
            listOf(
                RateHistoryEntity(metal = "GOLD_24K", rate = result.gold24kPerGram, timestamp = now, label = "Today"),
                RateHistoryEntity(metal = "SILVER_1G", rate = result.silverPerGram, timestamp = now, label = "Today"),
                RateHistoryEntity(metal = "PLATINUM_1G", rate = result.platinumPerGram, timestamp = now, label = "Today")
            )
        )

        return Pair(result.isLive, result.message)
    }

    suspend fun updateManualRate(
        rateId: String,
        newRate: Double,
        marketSource: String,
        city: String,
        rateType: String,
        localAdjustment: Double
    ) {
        val existing = dao.getRateById(rateId)
        val oldRate = existing?.ratePerGram ?: newRate
        val change = newRate - oldRate
        val changePct = if (oldRate > 0) (change / oldRate) * 100 else 0.0
        val now = System.currentTimeMillis()

        val updated = MetalRateEntity(
            id = rateId,
            metal = existing?.metal ?: "GOLD",
            purity = existing?.purity ?: "24K",
            ratePerGram = newRate,
            ratePerKg = if (existing?.metal == "SILVER") newRate * 1000.0 else newRate * 1000.0,
            previousRate = oldRate,
            changeAmount = change,
            changePercentage = changePct,
            lastUpdated = now,
            marketSource = marketSource,
            city = city,
            rateType = rateType,
            localAdjustment = localAdjustment,
            gstPercent = existing?.gstPercent ?: 3.0
        )
        dao.updateRate(updated)

        // Also if 24k was updated manually, proportionately adjust 22k and 18k if user wants
        if (rateId == "GOLD_24K") {
            val rate22k = kotlin.math.round((newRate * 0.916) / 5.0) * 5.0
            val rate18k = kotlin.math.round((newRate * 0.750) / 5.0) * 5.0
            val old22k = dao.getRateById("GOLD_22K")?.ratePerGram ?: rate22k
            val old18k = dao.getRateById("GOLD_18K")?.ratePerGram ?: rate18k
            dao.updateRate(
                MetalRateEntity(
                    id = "GOLD_22K",
                    metal = "GOLD",
                    purity = "22K",
                    ratePerGram = rate22k,
                    ratePerKg = rate22k * 1000.0,
                    previousRate = old22k,
                    changeAmount = rate22k - old22k,
                    changePercentage = if (old22k > 0) ((rate22k - old22k) / old22k) * 100 else 0.0,
                    lastUpdated = now,
                    marketSource = marketSource,
                    city = city,
                    rateType = rateType,
                    localAdjustment = localAdjustment,
                    gstPercent = 3.0
                )
            )
            dao.updateRate(
                MetalRateEntity(
                    id = "GOLD_18K",
                    metal = "GOLD",
                    purity = "18K",
                    ratePerGram = rate18k,
                    ratePerKg = rate18k * 1000.0,
                    previousRate = old18k,
                    changeAmount = rate18k - old18k,
                    changePercentage = if (old18k > 0) ((rate18k - old18k) / old18k) * 100 else 0.0,
                    lastUpdated = now,
                    marketSource = marketSource,
                    city = city,
                    rateType = rateType,
                    localAdjustment = localAdjustment,
                    gstPercent = 3.0
                )
            )
        }
    }

    // Inventory operations
    suspend fun saveInventoryItem(item: InventoryItemEntity) {
        dao.insertInventory(item)
    }

    suspend fun deleteInventoryItem(item: InventoryItemEntity) {
        dao.deleteInventory(item)
    }

    // Invoicing and automatic stock deduction
    suspend fun createInvoice(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>
    ) {
        dao.insertInvoice(invoice)
        dao.insertInvoiceItems(items)

        // Automatically reduce inventory stock
        for (item in items) {
            item.sku?.let { sku ->
                val inv = dao.getInventoryItem(sku)
                if (inv != null) {
                    val newQty = (inv.quantity - 1).coerceAtLeast(0)
                    val newStatus = if (newQty <= 0) "SOLD" else "IN_STOCK"
                    dao.updateStock(sku, newQty, newStatus)
                }
            }
        }

        // Update customer record
        val existingCustomer = dao.getAllCustomers().firstOrNull()?.find {
            it.phone == invoice.customerPhone || it.name.equals(invoice.customerName, ignoreCase = true)
        }
        if (existingCustomer != null) {
            dao.updateCustomer(
                existingCustomer.copy(
                    totalPurchases = existingCustomer.totalPurchases + invoice.totalAmount,
                    outstandingBalance = existingCustomer.outstandingBalance + invoice.balanceDue
                )
            )
        } else {
            dao.insertCustomer(
                CustomerEntity(
                    id = "CUST-${UUID.randomUUID().toString().take(6).uppercase()}",
                    name = invoice.customerName,
                    phone = invoice.customerPhone,
                    address = invoice.customerAddress,
                    totalPurchases = invoice.totalAmount,
                    outstandingBalance = invoice.balanceDue
                )
            )
        }
    }

    suspend fun addExpense(expense: ExpenseEntity) {
        dao.insertExpense(expense)
    }

    suspend fun saveShopProfile(profile: ShopProfileEntity) {
        dao.insertShopProfile(profile)
    }

    suspend fun markNotificationRead(id: String) {
        dao.markNotificationRead(id)
    }

    suspend fun clearNotifications() {
        dao.clearAllNotifications()
    }
}
