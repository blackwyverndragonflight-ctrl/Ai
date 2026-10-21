package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Customer
import com.example.data.model.DateRangeFilter
import com.example.data.model.DischargeStatus
import com.example.data.model.ReportFilter
import com.example.data.model.Store
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
import com.example.data.model.WalletType
import com.example.util.PersianUtils
import com.example.util.toPersian
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class CustomerFormState(
    val nationalId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val phoneNumber: String = "",
    val shebaNumber: String = "",
    val isChargeEnabled: Boolean = true,
    val chargeAmount: Long = 50_000_000L,
    val chargeAmountFormatted: String = "۵۰,۰۰۰,۰۰۰",
    val walletType: WalletType = WalletType.DIMA,
    val errorMessage: String? = null,
    val isSubmittedSuccess: Boolean = false
)

data class WalletUiState(
    val customers: List<Customer> = emptyList(),
    val stores: List<Store> = emptyList(),
    val transactions: List<WalletTransaction> = emptyList(),
    val formState: CustomerFormState = CustomerFormState(),
    val reportFilter: ReportFilter = ReportFilter(),
    val currentTab: String = "home", // "new_customer", "customer_list", "home", "settlement", "stores", "reports", "settings"
    val homeFilterTab: String = "pending", // "all", "pending", "settled"
    val userNotification: String? = null,
    val customerToDischarge: Customer? = null,
    val customerInDischargeStep: Customer? = null, // For Stage 2 in Customer List
    val isDarkTheme: Boolean = false
)

class WalletViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    private var appContext: Context? = null

    init {
        // On fresh installation, app starts with clean state (no registered customers/stores)
    }

    fun initStorage(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            loadPersistedData(context)
        }
    }

    fun seedInitialData() {
        val initialStores = listOf(
            Store(
                id = "store-1",
                name = "شعبه مرکزی ونک",
                address = "تهران، میدان ونک، خ ملاصدرا، پلاک ۱۲",
                location = "منطقه ۳ - ونک",
                branchCode = "۱۴۰۲",
                terminalId = "TM-۹۸۲۱",
                shebaNumber = "IR۱۲۰۰۴۸۷۸۹۳۰۰۰۰۰۰۰۰۰۰۰۰۰۱",
                phoneNumber = "۰۲۱-۸۸۸۸۱۲۰۲",
                settledAmount = 167_500_000L,
                pendingAmount = 14_500_000L,
                activeWalletsCount = 28
            ),
            Store(
                id = "store-2",
                name = "شعبه بازار بزرگ تهران",
                address = "تهران، بازار بزرگ، سرای ملی، ورودی دوم",
                location = "منطقه ۱۲ - بازار",
                branchCode = "۴۴۱۲",
                terminalId = "TM-۴۴۱۲",
                shebaNumber = "IR۰۱۸۹۵۲۳۴۱۱۰۰۰۰۰۰۰۰۰۰۰۰۰۲",
                phoneNumber = "۰۲۱-۵۵۵۵۴۴۱۲",
                settledAmount = 137_000_000L,
                pendingAmount = 8_200_000L,
                activeWalletsCount = 19
            ),
            Store(
                id = "store-3",
                name = "فروشگاه افق کوروش ونک (شعبه ۳۳۹۰)",
                address = "تهران، ونک، خیابان شیراز شمالی، نبش ک دانشور",
                location = "منطقه ۳ - ملاصدرا",
                branchCode = "۳۳۹۰",
                terminalId = "TM-۳۳۹۰",
                shebaNumber = "IR۰۲۰۰۴۸۷۸۹۳۰۰۰۰۰۰۰۰۰۰۰۰۰۳",
                phoneNumber = "۰۲۱-۸۸۶۶۳۳۹۰",
                settledAmount = 80_700_000L,
                pendingAmount = 22_700_000L,
                activeWalletsCount = 12
            )
        )

        val initialCustomers = listOf(
            Customer(
                id = "cust-1",
                nationalId = "۰۰۱۲۳۴۵۶۷۸",
                firstName = "محمدرضا",
                lastName = "صادقی",
                phoneNumber = "۰۹۱۲۱۱۱۲۲۳۳",
                birthDate = "۱۳۶۸/۰۴/۱۵",
                shebaNumber = "IR650120000000001234567890",
                walletBalance = 14_500_000L,
                isCharged = true,
                chargeDate = "۱۴۰۳/۰۱/۲۰",
                isDischarged = false,
                dischargeDate = null,
                dischargingStoreId = null,
                dischargingStoreName = null,
                walletType = WalletType.INSTALLMENT_CREDIT,
                status = DischargeStatus.PENDING,
                employerPercentage = 0.0,
                storePercentage = 0.0,
                employerAmount = 0L,
                storeAmount = 0L,
                netAmount = 14_500_000L
            ),
            Customer(
                id = "cust-2",
                nationalId = "۰۴۵۹۸۷۶۵۴۳",
                firstName = "زهرا",
                lastName = "ابراهیمی",
                phoneNumber = "۰۹۳۵۸۸۸۹۹۰۰",
                birthDate = "۱۳۷۲/۱۱/۲۰",
                shebaNumber = "IR880120000000009876543210",
                walletBalance = 8_200_000L,
                isCharged = true,
                chargeDate = "۱۴۰۳/۰۱/۲۲",
                isDischarged = false,
                dischargeDate = null,
                dischargingStoreId = null,
                dischargingStoreName = null,
                walletType = WalletType.INSTALLMENT_CREDIT,
                status = DischargeStatus.PENDING,
                employerPercentage = 0.0,
                storePercentage = 0.0,
                employerAmount = 0L,
                storeAmount = 0L,
                netAmount = 8_200_000L
            ),
            Customer(
                id = "cust-3",
                nationalId = "۰۰۲۳۴۵۸۷۱۴",
                firstName = "علیرضا",
                lastName = "سهرابی‌نژاد",
                phoneNumber = "۰۹۱۲۳۴۵۶۷۸۹",
                birthDate = "۱۳۶۱/۰۹/۰۳",
                shebaNumber = "IR120120000000004561237890",
                walletBalance = 25_000_000L,
                isCharged = true,
                chargeDate = "۱۴۰۳/۰۱/۲۵",
                isDischarged = true,
                dischargeDate = "۱۴۰۳/۰۱/۲۸",
                dischargingStoreId = "store-1",
                dischargingStoreName = "شعبه مرکزی ونک",
                walletType = WalletType.INSTALLMENT_CREDIT,
                status = DischargeStatus.AWAITING_SETTLEMENT,
                employerPercentage = 5.0,
                storePercentage = 3.0,
                employerAmount = 1_250_000L,
                storeAmount = 750_000L,
                netAmount = 23_000_000L,
                isInitialApproved = true,
                transferredToSettlement = true
            ),
            Customer(
                id = "cust-4",
                nationalId = "۰۴۹۱۲۳۴۵۶۷",
                firstName = "امیرحسین",
                lastName = "باقری",
                phoneNumber = "۰۹۱۲۵۵۵۶۶۷۷",
                birthDate = "۱۳۶۵/۰۷/۲۵",
                shebaNumber = "IR340120000000007894561230",
                walletBalance = 20_000_000L,
                isCharged = true,
                chargeDate = "۱۴۰۳/۰۱/۲۳",
                isDischarged = true,
                dischargeDate = "۱۴۰۳/۰۱/۲۵",
                dischargingStoreId = "store-2",
                dischargingStoreName = "شعبه بازار بزرگ تهران",
                walletType = WalletType.INSTALLMENT_CREDIT,
                status = DischargeStatus.SETTLED,
                employerPercentage = 10.0,
                storePercentage = 5.0,
                employerAmount = 2_000_000L,
                storeAmount = 1_000_000L,
                netAmount = 17_000_000L,
                isInitialApproved = true,
                transferredToSettlement = true,
                settlementDate = "۱۴۰۳/۰۱/۲۷ ساعت ۱۰:۳۰",
                isSettled = true
            )
        )

        val initialTransactions = listOf(
            WalletTransaction(
                id = "tx-1",
                customerId = "cust-1",
                customerName = "محمدرضا صادقی",
                nationalId = "۰۰۱۲۳۴۵۶۷۸",
                amount = 14_500_000L,
                type = TransactionType.CHARGE,
                date = "۱۴۰۳/۰۱/۲۰",
                storeName = "شعبه مرکزی ونک",
                referenceId = "SHP-۹۹۲۱۴۰",
                trackingCode = "۹۹۲۱۴۰۱۲"
            ),
            WalletTransaction(
                id = "tx-2",
                customerId = "cust-2",
                customerName = "زهرا ابراهیمی",
                nationalId = "۰۴۵۹۸۷۶۵۴۳",
                amount = 8_200_000L,
                type = TransactionType.CHARGE,
                date = "۱۴۰۳/۰۱/۲۲",
                storeName = "شعبه مرکزی ونک",
                referenceId = "SHP-۹۹۲۱۴۱",
                trackingCode = "۹۹۲۱۴۰۱۳"
            ),
            WalletTransaction(
                id = "tx-3",
                customerId = "cust-3",
                customerName = "علیرضا سهرابی‌نژاد",
                nationalId = "۰۰۲۳۴۵۸۷۱۴",
                amount = 23_000_000L,
                type = TransactionType.DISCHARGE,
                date = "۱۴۰۳/۰۱/۲۸",
                storeName = "شعبه مرکزی ونک",
                referenceId = "PAYA-۸۸۴۱۲",
                trackingCode = "۸۸۴۱۲۳۰۹"
            ),
            WalletTransaction(
                id = "tx-4",
                customerId = "cust-4",
                customerName = "امیرحسین باقری",
                nationalId = "۰۴۹۱۲۳۴۵۶۷",
                amount = 17_000_000L,
                type = TransactionType.DISCHARGE,
                date = "۱۴۰۳/۰۱/۲۵",
                storeName = "شعبه بازار بزرگ تهران",
                referenceId = "PAYA-۸۸۴۱۳",
                trackingCode = "۸۸۴۱۲۳۱۰"
            )
        )

        _uiState.update {
            it.copy(
                stores = initialStores,
                customers = initialCustomers,
                transactions = initialTransactions
            )
        }
    }

    fun toggleDarkTheme() {
        _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
    }

    fun selectTab(tab: String) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setHomeFilterTab(filter: String) {
        _uiState.update { it.copy(homeFilterTab = filter) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }

    // --- Customer Form Handlers ---

    fun updateFormNationalId(id: String) {
        // Enforce strictly digits only and maximum 10 digits
        val cleanDigits = PersianUtils.toEnglishDigits(id).filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(formState = it.formState.copy(nationalId = cleanDigits.toPersian(), errorMessage = null)) }
    }

    fun updateFormWalletType(type: WalletType) {
        val currentAmount = _uiState.value.formState.chargeAmount
        val clamped = currentAmount.coerceAtMost(type.maxAmount)
        _uiState.update {
            it.copy(
                formState = it.formState.copy(
                    walletType = type,
                    chargeAmount = clamped,
                    chargeAmountFormatted = PersianUtils.formatNumber(clamped),
                    errorMessage = null
                )
            )
        }
    }

    fun updateFormFirstName(first: String) {
        _uiState.update { it.copy(formState = it.formState.copy(firstName = first, errorMessage = null)) }
    }

    fun updateFormLastName(last: String) {
        _uiState.update { it.copy(formState = it.formState.copy(lastName = last, errorMessage = null)) }
    }

    fun updateFormBirthDate(date: String) {
        _uiState.update { it.copy(formState = it.formState.copy(birthDate = date, errorMessage = null)) }
    }

    fun updateFormPhone(phone: String) {
        val cleanPhone = PersianUtils.toEnglishDigits(phone).filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(formState = it.formState.copy(phoneNumber = cleanPhone.toPersian(), errorMessage = null)) }
    }

    fun updateFormSheba(shebaInput: String) {
        // Enforce strictly digits only (maximum 24 numbers for Iranian Sheba without IR prefix)
        val cleanDigits = PersianUtils.toEnglishDigits(shebaInput).filter { it.isDigit() }.take(24)
        val prefixed = if (cleanDigits.isNotEmpty()) "IR$cleanDigits" else ""
        _uiState.update { it.copy(formState = it.formState.copy(shebaNumber = prefixed, errorMessage = null)) }
    }

    fun updateFormChargeEnabled(enabled: Boolean) {
        _uiState.update { it.copy(formState = it.formState.copy(isChargeEnabled = enabled)) }
    }

    fun updateFormChargeAmountRaw(rawInput: String) {
        val parsed = PersianUtils.parseAmount(rawInput)
        val maxAllowed = _uiState.value.formState.walletType.maxAmount
        val clamped = parsed.coerceAtMost(maxAllowed)
        val formatted = PersianUtils.formatWithCommas(clamped.toString())
        val errorMsg = if (parsed > maxAllowed) "حداکثر سقف مجاز ${_uiState.value.formState.walletType.titleFa} ${PersianUtils.formatToman(maxAllowed)} می‌باشد" else null
        _uiState.update {
            it.copy(
                formState = it.formState.copy(
                    chargeAmount = clamped,
                    chargeAmountFormatted = if (rawInput.isBlank()) "" else formatted,
                    errorMessage = errorMsg
                )
            )
        }
    }

    fun setQuickChargeAmount(amount: Long) {
        val maxAllowed = _uiState.value.formState.walletType.maxAmount
        val clamped = amount.coerceAtMost(maxAllowed)
        val formatted = PersianUtils.formatNumber(clamped)
        _uiState.update {
            it.copy(
                formState = it.formState.copy(
                    chargeAmount = clamped,
                    chargeAmountFormatted = formatted,
                    errorMessage = null
                )
            )
        }
    }

    fun submitCustomerForm() {
        val form = _uiState.value.formState
        val cleanNationalId = PersianUtils.toEnglishDigits(form.nationalId).filter { it.isDigit() }
        if (cleanNationalId.length != 10) {
            _uiState.update { it.copy(formState = form.copy(errorMessage = "کد ملی متقاضی باید دقیقاً ۱۰ رقم باشد")) }
            return
        }
        if (form.firstName.isBlank() || form.lastName.isBlank()) {
            _uiState.update { it.copy(formState = form.copy(errorMessage = "نام و نام خانوادگی متقاضی الزامی است")) }
            return
        }
        val cleanPhone = PersianUtils.toEnglishDigits(form.phoneNumber).filter { it.isDigit() }
        if (cleanPhone.length < 11) {
            _uiState.update { it.copy(formState = form.copy(errorMessage = "شماره تلفن همراه معتبر ۱۱ رقمی (۰۹...) الزامی است")) }
            return
        }

        if (form.isChargeEnabled && form.chargeAmount > form.walletType.maxAmount) {
            _uiState.update { it.copy(formState = form.copy(errorMessage = "سقف تسهیلات ${form.walletType.titleFa} حداکثر ${PersianUtils.formatToman(form.walletType.maxAmount)} می‌باشد")) }
            return
        }

        val customerId = "cust-${UUID.randomUUID().toString().take(6)}"

        val newCustomer = Customer(
            id = customerId,
            nationalId = form.nationalId.toPersian(),
            firstName = form.firstName.trim(),
            lastName = form.lastName.trim(),
            phoneNumber = form.phoneNumber.toPersian(),
            birthDate = form.birthDate.trim(),
            shebaNumber = form.shebaNumber.trim().ifEmpty { "IR000000000000000000000000" },
            walletBalance = if (form.isChargeEnabled) form.chargeAmount else 0L,
            isCharged = form.isChargeEnabled,
            chargeDate = PersianUtils.getCurrentPersianDate(),
            isDischarged = false,
            dischargeDate = null,
            dischargingStoreId = null,
            dischargingStoreName = null,
            walletType = form.walletType,
            status = DischargeStatus.PENDING,
            employerPercentage = 0.0,
            storePercentage = 0.0,
            employerAmount = 0L,
            storeAmount = 0L,
            netAmount = if (form.isChargeEnabled) form.chargeAmount else 0L
        )

        val newTx = if (form.isChargeEnabled && form.chargeAmount > 0) {
            WalletTransaction(
                id = "tx-${UUID.randomUUID().toString().take(6)}",
                customerId = customerId,
                customerName = newCustomer.fullName,
                nationalId = form.nationalId.toPersian(),
                amount = form.chargeAmount,
                type = TransactionType.CHARGE,
                date = PersianUtils.getCurrentPersianDate(),
                storeName = "ثبت‌نام و افتتاح کیف‌پول (${form.walletType.titleFa})",
                referenceId = "SHP-${(100000..999999).random()}".toPersian(),
                trackingCode = "${(10000000..99999999).random()}".toPersian()
            )
        } else null

        _uiState.update { state ->
            val updatedCustomers = listOf(newCustomer) + state.customers
            val updatedTx = if (newTx != null) listOf(newTx) + state.transactions else state.transactions

            state.copy(
                customers = updatedCustomers,
                transactions = updatedTx,
                formState = CustomerFormState(isSubmittedSuccess = true),
                currentTab = "customer_list",
                userNotification = "مشتری «${newCustomer.fullName}» با ${form.walletType.titleFa} افزوده شد."
            )
        }
        persistData()
    }

    // --- Customer List & Discharge Step 2 ---

    fun startDischargeStep(customer: Customer) {
        _uiState.update { it.copy(customerInDischargeStep = customer) }
    }

    fun exitDischargeStep() {
        _uiState.update { it.copy(customerInDischargeStep = null) }
    }

    fun confirmDischargeAndTransfer(
        customerId: String,
        storeId: String,
        employerPercentage: Double,
        storePercentage: Double,
        dischargeDate: String,
        isCoinPlan: Boolean = false,
        coinDeductedAmount: Long = 0L,
        brokerPercentage: Double = 0.0,
        brokerShareAmount: Long = 0L,
        customerShareAmount: Long = 0L
    ) {
        val customer = _uiState.value.customers.find { it.id == customerId } ?: return
        val balance = customer.walletBalance

        val dischargingId: String
        val dischargingName: String
        val employerAmt: Long
        val storeAmt: Long
        val netAmt: Long

        if (isCoinPlan) {
            dischargingId = "COIN_PLAN"
            dischargingName = "طرح سکه (سهم کارگزار: ${PersianUtils.formatToman(brokerShareAmount)})"
            employerAmt = brokerShareAmount
            storeAmt = coinDeductedAmount
            netAmt = customerShareAmount
        } else {
            val store = _uiState.value.stores.find { it.id == storeId } ?: _uiState.value.stores.firstOrNull()
            dischargingId = store?.id ?: "STORE"
            dischargingName = store?.name ?: "فروشگاه عامل"
            employerAmt = (balance * (employerPercentage / 100.0)).toLong()
            storeAmt = (balance * (storePercentage / 100.0)).toLong()
            netAmt = (balance - employerAmt - storeAmt).coerceAtLeast(0L)
        }

        val dischargeTx = WalletTransaction(
            id = "tx-${UUID.randomUUID().toString().take(6)}",
            customerId = customer.id,
            customerName = customer.fullName,
            nationalId = customer.nationalId.toPersian(),
            amount = balance,
            type = TransactionType.DISCHARGE,
            date = dischargeDate.toPersian(),
            storeName = dischargingName,
            referenceId = "PAYA-${(10000..99999).random()}".toPersian(),
            trackingCode = "${(10000000..99999999).random()}".toPersian()
        )

        _uiState.update { state ->
            val updatedCustomers = state.customers.map { c ->
                if (c.id == customerId) {
                    c.copy(
                        isDischarged = true,
                        status = DischargeStatus.AWAITING_SETTLEMENT,
                        dischargeDate = dischargeDate.toPersian(),
                        dischargingStoreId = dischargingId,
                        dischargingStoreName = dischargingName,
                        employerPercentage = if (isCoinPlan) brokerPercentage else employerPercentage,
                        storePercentage = if (isCoinPlan) 0.0 else storePercentage,
                        employerAmount = employerAmt,
                        storeAmount = storeAmt,
                        netAmount = netAmt,
                        dimaDischargeMode = if (isCoinPlan) "COIN" else "STORE",
                        coinDeductedAmount = coinDeductedAmount,
                        brokerPercentage = brokerPercentage,
                        brokerShareAmount = brokerShareAmount,
                        isBrokerTransferConfirmed = isCoinPlan,
                        isInitialApproved = true,
                        transferredToSettlement = true
                    )
                } else c
            }

            val updatedStores = if (!isCoinPlan) {
                state.stores.map { s ->
                    if (s.id == dischargingId) {
                        s.copy(
                            pendingAmount = s.pendingAmount + balance,
                            activeWalletsCount = s.activeWalletsCount + 1
                        )
                    } else s
                }
            } else state.stores

            val successMsg = if (isCoinPlan) {
                "طرح سکه ثبت شد؛ سهم کارگزار (${PersianUtils.formatToman(brokerShareAmount)}) تایید و سهم مشتری (${PersianUtils.formatToman(netAmt)}) به تسویه منتقل گردید."
            } else {
                "کیف‌پول «${customer.fullName}» تخلیه و به فهرست تسویه منتقل شد."
            }

            state.copy(
                customers = updatedCustomers,
                stores = updatedStores,
                transactions = listOf(dischargeTx) + state.transactions,
                customerInDischargeStep = null,
                userNotification = successMsg
            )
        }
        persistData()
    }

    // --- Settlement Tab: Confirm Transfer & Record Date ---

    fun confirmSettlementTransfer(customerId: String) {
        val customer = _uiState.value.customers.find { it.id == customerId } ?: return
        val store = _uiState.value.stores.find { it.id == customer.dischargingStoreId }

        val settlementDateTime = PersianUtils.getCurrentPersianDateTime()

        _uiState.update { state ->
            val updatedCustomers = state.customers.map { c ->
                if (c.id == customerId) {
                    c.copy(
                        status = DischargeStatus.SETTLED,
                        isSettled = true,
                        settlementDate = settlementDateTime
                    )
                } else c
            }

            val updatedStores = state.stores.map { s ->
                if (s.id == store?.id) {
                    s.copy(
                        settledAmount = s.settledAmount + customer.walletBalance,
                        pendingAmount = (s.pendingAmount - customer.walletBalance).coerceAtLeast(0L)
                    )
                } else s
            }

            state.copy(
                customers = updatedCustomers,
                stores = updatedStores,
                userNotification = "مبلغ ${PersianUtils.formatToman(customer.netAmount)} با موفقیت به شبا ${customer.shebaNumber} واریز و ثبت شد."
            )
        }
        persistData()
    }

    // --- Delete Customer & Store ---

    fun deleteCustomer(customerId: String) {
        _uiState.update { state ->
            val deleted = state.customers.find { it.id == customerId }
            state.copy(
                customers = state.customers.filter { it.id != customerId },
                userNotification = "مشتری «${deleted?.fullName}» با موفقیت حذف شد."
            )
        }
        persistData()
    }

    fun deleteStore(storeId: String) {
        _uiState.update { state ->
            val deleted = state.stores.find { it.id == storeId }
            state.copy(
                stores = state.stores.filter { it.id != storeId },
                userNotification = "فروشگاه «${deleted?.name}» با موفقیت حذف شد."
            )
        }
        persistData()
    }

    // --- Store Registration (ONLY 3 fields as requested) ---

    fun registerNewStore(
        name: String,
        address: String,
        location: String
    ): Boolean {
        if (name.isBlank() || address.isBlank()) return false

        val storeCode = (1000..9999).random().toString().toPersian()
        val terminalCode = "TM-${(1000..9999).random()}".toPersian()

        val newStore = Store(
            id = "store-${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            address = address.trim(),
            location = location.trim().ifEmpty { "مرکزی" },
            branchCode = storeCode,
            terminalId = terminalCode,
            shebaNumber = "IR000000000000000000000000",
            phoneNumber = "۰۲۱-۸۸۰۰۰۰۰۰",
            settledAmount = 0L,
            pendingAmount = 0L,
            activeWalletsCount = 0
        )

        _uiState.update {
            it.copy(
                stores = it.stores + newStore,
                userNotification = "فروشگاه «${newStore.name}» با موفقیت ثبت شد."
            )
        }
        persistData()
        return true
    }

    // --- Export Excel & PDF ---

    fun exportExcel(context: Context) {
        viewModelScope.launch {
            val file = PersianUtils.exportToExcelCsv(
                context = context,
                customers = _uiState.value.customers,
                stores = _uiState.value.stores,
                transactions = _uiState.value.transactions
            )
            if (file != null) {
                PersianUtils.shareExportFile(context, file, "text/csv", "گزارش اکسل سامانه پذیرندگان بانک ملت")
                _uiState.update { it.copy(userNotification = "فایل اکسل آماده و جهت اشتراک‌گذاری ارسال گردید.") }
            } else {
                _uiState.update { it.copy(userNotification = "خطا در تولید فایل اکسل") }
            }
        }
    }

    fun exportPdf(context: Context) {
        viewModelScope.launch {
            val file = PersianUtils.exportToPdf(
                context = context,
                customers = _uiState.value.customers,
                stores = _uiState.value.stores
            )
            if (file != null) {
                PersianUtils.shareExportFile(context, file, "application/pdf", "گزارش PDF رسمی بانک ملت")
                _uiState.update { it.copy(userNotification = "فایل PDF رسمی با موفقیت صادر و ارسال گردید.") }
            } else {
                _uiState.update { it.copy(userNotification = "خطا در تولید فایل PDF") }
            }
        }
    }

    // --- Persistence & Backup / Restore ---

    fun setUserNotification(message: String) {
        _uiState.update { it.copy(userNotification = message) }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "شماره شبا") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(userNotification = "$label با موفقیت کپی شد: $text") }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadPersistedData(context: Context) {
        try {
            val file = File(context.filesDir, "wallet_data.json")
            if (file.exists()) {
                val jsonStr = file.readText(Charsets.UTF_8)
                parseAndApplyJson(jsonStr, isSilent = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun persistData() {
        val ctx = appContext ?: return
        try {
            val file = File(ctx.filesDir, "wallet_data.json")
            val root = serializeStateToJson()
            file.writeText(root.toString(2), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun serializeStateToJson(): JSONObject {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", PersianUtils.getCurrentPersianDateTime())

        val customersArray = JSONArray()
        _uiState.value.customers.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("nationalId", c.nationalId)
            obj.put("firstName", c.firstName)
            obj.put("lastName", c.lastName)
            obj.put("phoneNumber", c.phoneNumber)
            obj.put("birthDate", c.birthDate)
            obj.put("shebaNumber", c.shebaNumber)
            obj.put("walletBalance", c.walletBalance)
            obj.put("isCharged", c.isCharged)
            obj.put("chargeDate", c.chargeDate)
            obj.put("isDischarged", c.isDischarged)
            obj.put("dischargeDate", c.dischargeDate ?: "")
            obj.put("dischargingStoreId", c.dischargingStoreId ?: "")
            obj.put("dischargingStoreName", c.dischargingStoreName ?: "")
            obj.put("walletType", c.walletType.name)
            obj.put("status", c.status.name)
            obj.put("employerPercentage", c.employerPercentage)
            obj.put("storePercentage", c.storePercentage)
            obj.put("employerAmount", c.employerAmount)
            obj.put("storeAmount", c.storeAmount)
            obj.put("netAmount", c.netAmount)
            obj.put("dimaDischargeMode", c.dimaDischargeMode ?: "")
            obj.put("coinDeductedAmount", c.coinDeductedAmount)
            obj.put("brokerPercentage", c.brokerPercentage)
            obj.put("brokerShareAmount", c.brokerShareAmount)
            obj.put("isBrokerTransferConfirmed", c.isBrokerTransferConfirmed)
            obj.put("isInitialApproved", c.isInitialApproved)
            obj.put("transferredToSettlement", c.transferredToSettlement)
            obj.put("settlementDate", c.settlementDate ?: "")
            obj.put("isSettled", c.isSettled)
            obj.put("settlementCycle", c.settlementCycle)
            obj.put("createdAt", c.createdAt)
            customersArray.put(obj)
        }
        root.put("customers", customersArray)

        val storesArray = JSONArray()
        _uiState.value.stores.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("name", s.name)
            obj.put("address", s.address)
            obj.put("location", s.location)
            obj.put("branchCode", s.branchCode)
            obj.put("terminalId", s.terminalId)
            obj.put("shebaNumber", s.shebaNumber)
            obj.put("phoneNumber", s.phoneNumber)
            obj.put("settledAmount", s.settledAmount)
            obj.put("pendingAmount", s.pendingAmount)
            obj.put("activeWalletsCount", s.activeWalletsCount)
            obj.put("isActive", s.isActive)
            storesArray.put(obj)
        }
        root.put("stores", storesArray)

        val txArray = JSONArray()
        _uiState.value.transactions.forEach { t ->
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("customerId", t.customerId)
            obj.put("customerName", t.customerName)
            obj.put("nationalId", t.nationalId)
            obj.put("amount", t.amount)
            obj.put("type", t.type.name)
            obj.put("date", t.date)
            obj.put("storeName", t.storeName)
            obj.put("referenceId", t.referenceId)
            obj.put("trackingCode", t.trackingCode)
            obj.put("status", t.status)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        return root
    }

    private fun parseAndApplyJson(jsonString: String, isSilent: Boolean): Boolean {
        return try {
            val root = JSONObject(jsonString)

            val customersList = mutableListOf<Customer>()
            val customersArray = root.optJSONArray("customers")
            if (customersArray != null) {
                for (i in 0 until customersArray.length()) {
                    val obj = customersArray.getJSONObject(i)
                    customersList.add(
                        Customer(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            nationalId = obj.optString("nationalId", ""),
                            firstName = obj.optString("firstName", ""),
                            lastName = obj.optString("lastName", ""),
                            phoneNumber = obj.optString("phoneNumber", ""),
                            birthDate = obj.optString("birthDate", ""),
                            shebaNumber = obj.optString("shebaNumber", "IR000000000000000000000000"),
                            walletBalance = obj.optLong("walletBalance", 0L),
                            isCharged = obj.optBoolean("isCharged", true),
                            chargeDate = obj.optString("chargeDate", PersianUtils.getCurrentPersianDate()),
                            isDischarged = obj.optBoolean("isDischarged", false),
                            dischargeDate = obj.optString("dischargeDate", "").ifEmpty { null },
                            dischargingStoreId = obj.optString("dischargingStoreId", "").ifEmpty { null },
                            dischargingStoreName = obj.optString("dischargingStoreName", "").ifEmpty { null },
                            walletType = try { WalletType.valueOf(obj.optString("walletType", "INSTALLMENT_CREDIT")) } catch (e: Exception) { WalletType.INSTALLMENT_CREDIT },
                            status = try { DischargeStatus.valueOf(obj.optString("status", "PENDING")) } catch (e: Exception) { DischargeStatus.PENDING },
                            employerPercentage = obj.optDouble("employerPercentage", 0.0),
                            storePercentage = obj.optDouble("storePercentage", 0.0),
                            employerAmount = obj.optLong("employerAmount", 0L),
                            storeAmount = obj.optLong("storeAmount", 0L),
                            netAmount = obj.optLong("netAmount", 0L),
                            dimaDischargeMode = obj.optString("dimaDischargeMode", "").ifEmpty { null },
                            coinDeductedAmount = obj.optLong("coinDeductedAmount", 0L),
                            brokerPercentage = obj.optDouble("brokerPercentage", 0.0),
                            brokerShareAmount = obj.optLong("brokerShareAmount", 0L),
                            isBrokerTransferConfirmed = obj.optBoolean("isBrokerTransferConfirmed", false),
                            isInitialApproved = obj.optBoolean("isInitialApproved", false),
                            transferredToSettlement = obj.optBoolean("transferredToSettlement", false),
                            settlementDate = obj.optString("settlementDate", "").ifEmpty { null },
                            isSettled = obj.optBoolean("isSettled", false),
                            settlementCycle = obj.optString("settlementCycle", "سیکل پایا متمرکز ملت"),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val storesList = mutableListOf<Store>()
            val storesArray = root.optJSONArray("stores")
            if (storesArray != null) {
                for (i in 0 until storesArray.length()) {
                    val obj = storesArray.getJSONObject(i)
                    storesList.add(
                        Store(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            name = obj.optString("name", ""),
                            address = obj.optString("address", ""),
                            location = obj.optString("location", "مرکزی"),
                            branchCode = obj.optString("branchCode", "۱۰۲"),
                            terminalId = obj.optString("terminalId", "TM-۱۰۲"),
                            shebaNumber = obj.optString("shebaNumber", ""),
                            phoneNumber = obj.optString("phoneNumber", ""),
                            settledAmount = obj.optLong("settledAmount", 0L),
                            pendingAmount = obj.optLong("pendingAmount", 0L),
                            activeWalletsCount = obj.optInt("activeWalletsCount", 0),
                            isActive = obj.optBoolean("isActive", true)
                        )
                    )
                }
            }

            val txList = mutableListOf<WalletTransaction>()
            val txArray = root.optJSONArray("transactions")
            if (txArray != null) {
                for (i in 0 until txArray.length()) {
                    val obj = txArray.getJSONObject(i)
                    txList.add(
                        WalletTransaction(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            customerId = obj.optString("customerId", ""),
                            customerName = obj.optString("customerName", ""),
                            nationalId = obj.optString("nationalId", ""),
                            amount = obj.optLong("amount", 0L),
                            type = try { TransactionType.valueOf(obj.optString("type", "CHARGE")) } catch (e: Exception) { TransactionType.CHARGE },
                            date = obj.optString("date", PersianUtils.getCurrentPersianDate()),
                            storeName = obj.optString("storeName", ""),
                            referenceId = obj.optString("referenceId", ""),
                            trackingCode = obj.optString("trackingCode", ""),
                            status = obj.optString("status", "موفق")
                        )
                    )
                }
            }

            _uiState.update {
                it.copy(
                    customers = customersList,
                    stores = storesList,
                    transactions = txList,
                    userNotification = if (!isSilent) "اطلاعات با موفقیت بازگردانی شد (${customersList.size.toPersian()} مشتری و ${storesList.size.toPersian()} فروشگاه)." else it.userNotification
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (!isSilent) {
                _uiState.update { it.copy(userNotification = "ساختار فایل پشتیبان نامعتبر است: ${e.message}") }
            }
            false
        }
    }

    fun exportBackup(context: Context) {
        viewModelScope.launch {
            try {
                val root = serializeStateToJson()
                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) exportDir.mkdirs()
                val backupFile = File(exportDir, "Mellat_Backup_${System.currentTimeMillis()}.json")
                FileOutputStream(backupFile).use { fos ->
                    fos.write(root.toString(2).toByteArray(Charsets.UTF_8))
                }
                PersianUtils.shareExportFile(context, backupFile, "application/json", "فایل پشتیبان سامانه پذیرندگان")
                _uiState.update { it.copy(userNotification = "فایل پشتیبان کامل آماده و به اشتراک گذاشته شد.") }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(userNotification = "خطا در ایجاد فایل پشتیبان: ${e.message}") }
            }
        }
    }

    fun restoreFromJsonContent(context: Context, jsonString: String): Boolean {
        val success = parseAndApplyJson(jsonString, isSilent = false)
        if (success) {
            persistData()
        }
        return success
    }

    fun restoreFromUri(context: Context, uri: android.net.Uri): Boolean {
        return try {
            val content = context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                it.readText()
            } ?: return false
            restoreFromJsonContent(context, content)
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(userNotification = "خطا در خواندن فایل پشتیبان: ${e.message}") }
            false
        }
    }

    fun clearAllData(context: Context) {
        _uiState.update {
            it.copy(
                customers = emptyList(),
                stores = emptyList(),
                transactions = emptyList(),
                userNotification = "تمامی اطلاعات سامانه با موفقیت پاکسازی شد."
            )
        }
        persistData()
    }

    fun restoreDefaultData() {
        seedInitialData()
        persistData()
        _uiState.update { it.copy(userNotification = "داده‌های نمونه اولیه با موفقیت بارگذاری شد.") }
    }
}
