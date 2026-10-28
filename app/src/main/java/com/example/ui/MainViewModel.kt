package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
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
import com.example.data.repository.JewelleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardSummary(
    val todaySales: Double = 0.0,
    val todayProfit: Double = 0.0,
    val todaySalesCount: Int = 0,
    val totalInventoryMarketValue: Double = 0.0,
    val totalInventoryCost: Double = 0.0,
    val unrealizedGain: Double = 0.0,
    val lowStockCount: Int = 0,
    val pendingReceivables: Double = 0.0,
    val totalExpenses: Double = 0.0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JewelleryRepository

    val rates: StateFlow<List<MetalRateEntity>>
    val inventory: StateFlow<List<InventoryItemEntity>>
    val invoices: StateFlow<List<InvoiceEntity>>
    val invoiceItems: StateFlow<List<InvoiceItemEntity>>
    val customers: StateFlow<List<CustomerEntity>>
    val suppliers: StateFlow<List<SupplierEntity>>
    val expenses: StateFlow<List<ExpenseEntity>>
    val shopProfile: StateFlow<ShopProfileEntity?>
    val notifications: StateFlow<List<NotificationItem>>

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _apiStatusMessage = MutableStateFlow("Bullion Feed Synced")
    val apiStatusMessage: StateFlow<String> = _apiStatusMessage.asStateFlow()

    private val _isOnlineSync = MutableStateFlow(true)
    val isOnlineSync: StateFlow<Boolean> = _isOnlineSync.asStateFlow()

    private val _selectedMetalChart = MutableStateFlow("GOLD_24K")
    val selectedMetalChart: StateFlow<String> = _selectedMetalChart.asStateFlow()

    val rateHistory: StateFlow<List<RateHistoryEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = JewelleryRepository(db.jewelleryDao())

        rates = repository.rates.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        inventory = repository.inventory.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        invoices = repository.invoices.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        invoiceItems = repository.invoiceItems.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        customers = repository.customers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        suppliers = repository.suppliers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        expenses = repository.expenses.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        shopProfile = repository.shopProfile.stateIn(viewModelScope, SharingStarted.Eagerly, null)
        notifications = repository.notifications.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        rateHistory = repository.getRateHistory("GOLD_24K")
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        rates,
        inventory,
        invoices,
        invoiceItems,
        expenses
    ) { currentRates, invList, invcList, itemsList, expList ->
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = cal.timeInMillis

        // Today's invoices
        val todayInvoices = invcList.filter { it.date >= startOfToday }
        val todaySales = todayInvoices.sumOf { it.totalAmount }
        val todaySalesCount = todayInvoices.size

        // Today's profit: items matching today's invoices
        val todayInvNumbers = todayInvoices.map { it.invoiceNumber }.toSet()
        val todayItems = itemsList.filter { it.invoiceNumber in todayInvNumbers }
        val todayProfit = todayItems.sumOf { it.itemPrice - it.itemCost }

        // Inventory values
        val gold24k = currentRates.find { it.id == "GOLD_24K" }?.ratePerGram ?: 7880.0
        val silver999 = currentRates.find { it.id == "SILVER_1G" }?.ratePerGram ?: 94.50
        val plat950 = currentRates.find { it.id == "PLATINUM_1G" }?.ratePerGram ?: 3240.0

        var totalMarketVal = 0.0
        var totalInvCost = 0.0
        var lowStock = 0

        for (item in invList.filter { it.status == "IN_STOCK" }) {
            totalInvCost += item.purchaseCost * item.quantity

            val metalRate = when (item.metal) {
                "GOLD" -> gold24k * item.purityPercent
                "SILVER" -> silver999 * item.purityPercent
                "PLATINUM" -> plat950 * item.purityPercent
                else -> gold24k
            }
            val itemMarketVal = (item.netWeight * metalRate) + item.stoneCost + item.makingCharges
            totalMarketVal += itemMarketVal * item.quantity

            if (item.quantity <= 1) {
                lowStock++
            }
        }

        val totalExpenses = expList.sumOf { it.amount }

        DashboardSummary(
            todaySales = todaySales,
            todayProfit = todayProfit,
            todaySalesCount = todaySalesCount,
            totalInventoryMarketValue = totalMarketVal,
            totalInventoryCost = totalInvCost,
            unrealizedGain = totalMarketVal - totalInvCost,
            lowStockCount = lowStock,
            pendingReceivables = 0.0,
            totalExpenses = totalExpenses
        )
    }.combine(customers) { summary, custList ->
        summary.copy(pendingReceivables = custList.sumOf { it.outstandingBalance })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    fun setSelectedMetalChart(metalId: String) {
        _selectedMetalChart.value = metalId
    }

    fun refreshRates() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val (isLive, message) = repository.refreshLiveRates()
            _isOnlineSync.value = isLive
            _apiStatusMessage.value = message
            _isRefreshing.value = false
        }
    }

    fun updateManualRate(
        rateId: String,
        newRate: Double,
        source: String,
        city: String,
        rateType: String,
        localAdj: Double
    ) {
        viewModelScope.launch {
            repository.updateManualRate(rateId, newRate, source, city, rateType, localAdj)
        }
    }

    fun saveInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            repository.saveInventoryItem(item)
        }
    }

    fun deleteInventoryItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    fun createInvoice(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        viewModelScope.launch {
            repository.createInvoice(invoice, items)
        }
    }

    fun addExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    fun saveShopProfile(profile: ShopProfileEntity) {
        viewModelScope.launch {
            repository.saveShopProfile(profile)
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }
}
