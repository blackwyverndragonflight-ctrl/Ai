package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CreditScore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.DischargeStatus
import com.example.data.model.WalletType
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberPendingLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.GreenSuccessLight
import com.example.ui.theme.MellatGold
import com.example.util.PersianUtils
import com.example.util.toPersian
import com.example.viewmodel.WalletUiState
import com.example.viewmodel.WalletViewModel

@Composable
fun CustomerListScreen(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val activeDischargeCustomer = uiState.customerInDischargeStep

    if (activeDischargeCustomer != null) {
        DischargeStepTwoView(
            customer = activeDischargeCustomer,
            uiState = uiState,
            viewModel = viewModel,
            onBack = { viewModel.exitDischargeStep() },
            modifier = modifier
        )
    } else {
        CustomerListView(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
    }
}

@Composable
private fun CustomerListView(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<DischargeStatus?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    val filteredList = uiState.customers.filter { c ->
        val cleanQuery = PersianUtils.toEnglishDigits(searchQuery).filter { it.isDigit() }
        val matchesSearch = if (cleanQuery.isBlank()) true else {
            val cleanCustomerNationalId = PersianUtils.toEnglishDigits(c.nationalId).filter { it.isDigit() }
            cleanCustomerNationalId.contains(cleanQuery)
        }
        val matchesStatus = if (statusFilter == null) true else c.status == statusFilter
        matchesSearch && matchesStatus
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "فهرست جامع مشتریان و متقاضیان",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مجموع: ${uiState.customers.size.toPersian()} متقاضی ثبت‌شده در سامانه",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.selectTab("new_customer") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_customer_shortcut_btn")
                        ) {
                            Text(
                                text = "+ ثبت جدید",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Field - Search strictly by National ID
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجو بر اساس کد ملی متقاضی...") },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            textDirection = TextDirection.Rtl,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "جستجو بر اساس کد ملی",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChipItem(
                            label = "همه (${uiState.customers.size.toPersian()})",
                            isSelected = statusFilter == null,
                            onClick = { statusFilter = null },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChipItem(
                            label = "در انتظار (${uiState.customers.count { it.status == DischargeStatus.PENDING }.toPersian()})",
                            isSelected = statusFilter == DischargeStatus.PENDING,
                            onClick = { statusFilter = DischargeStatus.PENDING },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChipItem(
                            label = "تسویه (${uiState.customers.count { it.status == DischargeStatus.SETTLED }.toPersian()})",
                            isSelected = statusFilter == DischargeStatus.SETTLED,
                            onClick = { statusFilter = DischargeStatus.SETTLED },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "موردی یافت نشد",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { customer ->
                CustomerItemCard(
                    customer = customer,
                    onStartDischarge = { viewModel.startDischargeStep(customer) },
                    onDelete = { customerToDelete = customer },
                    onCopy = { text, label -> viewModel.copyToClipboard(context, text, label) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { cust ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = {
                Text(
                    text = "حذف مشتری از سامانه",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "آیا از حذف اطلاعات «${cust.fullName}» با کد ملی ${cust.nationalId} اطمینان دارید؟ این عمل غیرقابل بازگشت است.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomer(cust.id)
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("بله، حذف شود", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
private fun CustomerItemCard(
    customer: Customer,
    onStartDischarge: () -> Unit,
    onDelete: () -> Unit,
    onCopy: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_card_${customer.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Avatar + Name on Right, Actions & Status on Left
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.initials,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Full Name with Copy capability
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onCopy(customer.fullName, "نام و نام خانوادگی") }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = customer.fullName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "کپی نام و نام خانوادگی",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (customer.walletType == WalletType.DIMA || customer.walletType == WalletType.INSTALLMENT_CREDIT)
                                    Color(0xFFC61927).copy(alpha = 0.1f)
                                else
                                    MellatGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = customer.walletType.titleFa,
                                    color = if (customer.walletType == WalletType.DIMA || customer.walletType == WalletType.INSTALLMENT_CREDIT)
                                        Color(0xFFC61927)
                                    else
                                        Color(0xFF9E6200),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // National ID with Copy capability
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onCopy(customer.nationalId, "کد ملی") }
                                .padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "کد ملی: ${customer.nationalId.toPersian()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "کپی کد ملی",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Delete Icon
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp).testTag("delete_customer_${customer.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف مشتری",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Row: Phone with copy
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onCopy(customer.phoneNumber, "شماره تماس") }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "شماره تماس: ${customer.phoneNumber.toPersian()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "کپی شماره تماس",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Details Row: Sheba with copy
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onCopy(customer.shebaNumber, "شماره شبا") }
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "شماره شبا: ${customer.shebaNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(textDirection = TextDirection.Ltr),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "کپی شماره شبا",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Details Row: Birth Date - Display only (No copy button/click)
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = MellatGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "تاریخ تولد: ${if (customer.birthDate.isNotBlank()) customer.birthDate.toPersian() else "ثبت نشده"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row: Balance & Discharge action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "موجودی کیف‌پول:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = PersianUtils.formatToman(customer.walletBalance),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                when (customer.status) {
                    DischargeStatus.PENDING -> {
                        Button(
                            onClick = onStartDischarge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC61927),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("discharge_action_btn_${customer.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تخلیه کیف‌پول",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    DischargeStatus.AWAITING_SETTLEMENT -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberPendingLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "منتقل‌شده به تسویه",
                                color = AmberPending,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    DischargeStatus.SETTLED -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenSuccessLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "تسویه‌شده ✓",
                                color = GreenSuccess,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Display discharge date if available
            if (customer.dischargeDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = MellatGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تاریخ تخلیه: ${customer.dischargeDate.toPersian()} | عامل: ${customer.dischargingStoreName ?: "---"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Step 2: "مرحله دوم لیست: تخلیه کیف‌پول"
 */
@Composable
private fun DischargeStepTwoView(
    customer: Customer,
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDimaPlan = customer.walletType == WalletType.DIMA || customer.walletType == WalletType.INSTALLMENT_CREDIT

    // Mode for Dima: "STORE" or "COIN"
    var dimaDischargeMode by remember { mutableStateOf("STORE") }

    // Store discharge state
    var selectedStoreId by remember { mutableStateOf(uiState.stores.firstOrNull()?.id ?: "") }
    var isStoreDropdownExpanded by remember { mutableStateOf(false) }
    var isInitialApproved by remember { mutableStateOf(false) }
    var employerPercentageRaw by remember { mutableStateOf("5") }
    var storePercentageRaw by remember { mutableStateOf("3") }

    // Coin Plan state
    var coinDeductedRaw by remember { mutableStateOf("") }
    var selectedBrokerRate by remember { mutableDoubleStateOf(10.0) } // 10% or 15%
    var isBrokerTransferConfirmed by remember { mutableStateOf(false) }

    var dischargeDate by remember { mutableStateOf(PersianUtils.getCurrentPersianDate()) }

    val balance = customer.walletBalance

    // Calculations for Store Mode
    val employerPct = employerPercentageRaw.toDoubleOrNull() ?: 0.0
    val storePct = storePercentageRaw.toDoubleOrNull() ?: 0.0
    val employerAmt = (balance * (employerPct / 100.0)).toLong()
    val storeAmt = (balance * (storePct / 100.0)).toLong()
    val netAmt = (balance - employerAmt - storeAmt).coerceAtLeast(0L)

    // Calculations for Coin Mode
    val coinDeductedAmount = PersianUtils.toEnglishDigits(coinDeductedRaw).filter { it.isDigit() }.toLongOrNull() ?: 0L
    val totalFeeAmount = (balance * (selectedBrokerRate / 100.0)).toLong()
    val brokerShareAmount = (totalFeeAmount - coinDeductedAmount).coerceAtLeast(0L)
    val customerShareAmount = (balance - brokerShareAmount).coerceAtLeast(0L)

    val selectedStore = uiState.stores.find { it.id == selectedStoreId } ?: uiState.stores.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Navigation Back Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت به لیست افراد",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "مرحله دوم: تخلیه کیف‌پول و تعیین سهم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "انتقال وجه و هدایت متقاضی به چرخه تسویه حساب",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Customer Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مشخصات متقاضی:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = customer.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = if (isDimaPlan) Color(0xFFC61927).copy(alpha = 0.1f) else MellatGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = customer.walletType.titleFa,
                                color = if (isDimaPlan) Color(0xFFC61927) else Color(0xFF9E6200),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                viewModel.copyToClipboard(context, customer.shebaNumber, "شماره شبا")
                            }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "کد ملی: ${customer.nationalId.toPersian()} | شبا: ${customer.shebaNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "کپی شماره شبا",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (customer.birthDate.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "تاریخ تولد: ${customer.birthDate.toPersian()} | تماس: ${customer.phoneNumber.toPersian()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اصل مبلغ تسهیلات:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = PersianUtils.formatToman(balance),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Section 1: Facility Discharge Mode Selection (For Dima: Store or Coin Plan)
        if (isDimaPlan) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۱. مشخص کردن شیوه تخلیه تسهیلات طرح دیما:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "لطفاً مشخص نمایید تخلیه از طریق فروشگاه عامل انجام شود یا طرح سکه:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Option: فروشگاه عامل
                            val isStoreSelected = dimaDischargeMode == "STORE"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { dimaDischargeMode = "STORE" }
                                    .border(
                                        width = if (isStoreSelected) 2.dp else 1.dp,
                                        color = if (isStoreSelected) Color(0xFFC61927) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isStoreSelected) Color(0xFFC61927).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isStoreSelected,
                                        onClick = { dimaDischargeMode = "STORE" },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC61927))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = "فروشگاه عامل",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isStoreSelected) Color(0xFFC61927) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "تخلیه از طریق پوز",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Option: طرح سکه
                            val isCoinSelected = dimaDischargeMode == "COIN"
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { dimaDischargeMode = "COIN" }
                                    .border(
                                        width = if (isCoinSelected) 2.dp else 1.dp,
                                        color = if (isCoinSelected) Color(0xFFC61927) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCoinSelected) Color(0xFFC61927).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isCoinSelected,
                                        onClick = { dimaDischargeMode = "COIN" },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC61927))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = "طرح سکه",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCoinSelected) Color(0xFFC61927) else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "کسر سکه و کارمزد",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== FLOW A: STORE DISCHARGE (OR NON-DIMA) ====================
        if (!isDimaPlan || dimaDischargeMode == "STORE") {
            // Store Selection Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isDimaPlan) "انتخاب فروشگاه عامل طرف قرارداد:" else "۱. انتخاب فروشگاه عامل تخلیه:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                    .clickable { isStoreDropdownExpanded = true }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = selectedStore?.name ?: "انتخاب فروشگاه",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "آدرس: ${selectedStore?.address ?: "---"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Outlined.Storefront,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = isStoreDropdownExpanded,
                                onDismissRequest = { isStoreDropdownExpanded = false }
                            ) {
                                uiState.stores.forEach { store ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(store.name, fontWeight = FontWeight.Bold)
                                                Text(
                                                    "${store.address} (${store.location})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedStoreId = store.id
                                            isStoreDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Initial Approval Checkbox
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isInitialApproved) GreenSuccessLight.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .clickable { isInitialApproved = !isInitialApproved }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isInitialApproved,
                                onCheckedChange = { isInitialApproved = it },
                                colors = CheckboxDefaults.colors(checkedColor = GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "تأیید اولیه تخلیه توسط فروشگاه و پذیرنده",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInitialApproved) GreenSuccess else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تایید صحت هویت متقاضی و تحویل کالا/خدمات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Percentage Section: Employer & Store %
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۲. تعیین درصد سهم کارفرما و فروشگاه:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مبالغ مربوطه به طور خودکار از اصل موجودی کسر می‌گردد",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Employer %
                            OutlinedTextField(
                                value = employerPercentageRaw,
                                onValueChange = { employerPercentageRaw = PersianUtils.toEnglishDigits(it) },
                                label = { Text("درصد سهم کارفرما") },
                                trailingIcon = { Text("%", fontWeight = FontWeight.Bold) },
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    textDirection = TextDirection.Ltr,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("employer_percentage_input"),
                                singleLine = true
                            )

                            // Store %
                            OutlinedTextField(
                                value = storePercentageRaw,
                                onValueChange = { storePercentageRaw = PersianUtils.toEnglishDigits(it) },
                                label = { Text("درصد سهم فروشگاه") },
                                trailingIcon = { Text("%", fontWeight = FontWeight.Bold) },
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    textDirection = TextDirection.Ltr,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).testTag("store_percentage_input"),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live calculation breakdown
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("کسر سهم کارفرما (${employerPct.toString().toPersian()}٪):", style = MaterialTheme.typography.bodySmall)
                                Text(PersianUtils.formatToman(employerAmt), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("کسر سهم فروشگاه (${storePct.toString().toPersian()}٪):", style = MaterialTheme.typography.bodySmall)
                                Text(PersianUtils.formatToman(storeAmt), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.8.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("سهم خالص متقاضی جهت تسویه:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    PersianUtils.formatToman(netAmt),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Discharge Date Field
                        OutlinedTextField(
                            value = dischargeDate.toPersian(),
                            onValueChange = { dischargeDate = it },
                            label = { Text("تاریخ تخلیه کیف‌پول") },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                textDirection = TextDirection.Rtl,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("discharge_date_step2_input"),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = MellatGold
                                )
                            },
                            singleLine = true
                        )
                    }
                }
            }

            // Action: Discharge Wallet and Transfer to Settlement (Store Flow)
            item {
                Button(
                    onClick = {
                        viewModel.confirmDischargeAndTransfer(
                            customerId = customer.id,
                            storeId = selectedStoreId,
                            employerPercentage = employerPct,
                            storePercentage = storePct,
                            dischargeDate = dischargeDate
                        )
                    },
                    enabled = isInitialApproved && selectedStoreId.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_discharge_and_transfer_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC61927),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تخلیه کیف پول و انتقال به تسویه",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "تاریخ تخلیه ثبت‌شونده: ${dischargeDate.toPersian()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ==================== FLOW B: COIN PLAN (طرح سکه) ====================
        if (isDimaPlan && dimaDischargeMode == "COIN") {
            // Box 1: مبلغ کسر شده در طرح سکه
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۲. مبلغ کسر شده در طرح سکه:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "مبلغی که قبلاً در طرح سکه کسر گردیده را وارد نمایید:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = coinDeductedRaw,
                            onValueChange = { coinDeductedRaw = PersianUtils.toEnglishDigits(it).filter { c -> c.isDigit() } },
                            label = { Text("مبلغ کسر شده در طرح سکه (تومان)") },
                            placeholder = { Text("مثال: ۲,۰۰۰,۰۰۰") },
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                textDirection = TextDirection.Ltr,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            ),
                            trailingIcon = {
                                Text("تومان", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            },
                            supportingText = {
                                Text(
                                    text = if (coinDeductedAmount > 0L)
                                        "مبلغ وارد شده: ${PersianUtils.formatToman(coinDeductedAmount)}"
                                    else
                                        "در صورت عدم کسر، مقدار صفر در نظر گرفته می‌شود",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("coin_deducted_input"),
                            singleLine = true
                        )
                    }
                }
            }

            // Box 2: کادر انتخاب ۱۰ درصد یا ۱۵ درصد و محاسبه سهم کارگزار
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "۳. انتخاب درصد کارمزد و تعیین سهم کارگزار:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "با انتخاب ۱۰٪ یا ۱۵٪، درصد انتخابی از مبلغ کل تسهیلات کسر شده و منهای سهم کسر شده سکه می‌گردد تا سهم کارگزار مشخص شود:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Box containing 10% and 15% choices
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 10 Percent Card
                            val is10Selected = selectedBrokerRate == 10.0
                            val fee10 = (balance * 0.10).toLong()
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedBrokerRate = 10.0 }
                                    .border(
                                        width = if (is10Selected) 2.dp else 1.dp,
                                        color = if (is10Selected) Color(0xFFC61927) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (is10Selected) Color(0xFFC61927).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = is10Selected,
                                            onClick = { selectedBrokerRate = 10.0 },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC61927))
                                        )
                                        Text(
                                            text = "۱۰ درصد",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (is10Selected) Color(0xFFC61927) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = PersianUtils.formatToman(fee10),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // 15 Percent Card
                            val is15Selected = selectedBrokerRate == 15.0
                            val fee15 = (balance * 0.15).toLong()
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedBrokerRate = 15.0 }
                                    .border(
                                        width = if (is15Selected) 2.dp else 1.dp,
                                        color = if (is15Selected) Color(0xFFC61927) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (is15Selected) Color(0xFFC61927).copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = is15Selected,
                                            onClick = { selectedBrokerRate = 15.0 },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC61927))
                                        )
                                        Text(
                                            text = "۱۵ درصد",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (is15Selected) Color(0xFFC61927) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = PersianUtils.formatToman(fee15),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Broker Share Card (مبلغ سهم کارگزار مشخص که باید بصورت انتقالی انتقال داده شود)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MellatGold.copy(alpha = 0.12f))
                                .border(1.dp, MellatGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "مبلغ سهم کارگزار (انتقالی):",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B5300)
                                    )
                                    Text(
                                        text = "باید به‌صورت انتقالی برای کارگزار انتقال داده شود",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF6B4000),
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = PersianUtils.formatToman(brokerShareAmount),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5300)
                                )
                            }

                            HorizontalDivider(color = MellatGold.copy(alpha = 0.3f), thickness = 0.8.dp)

                            // Math breakdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "کل کارمزد (${selectedBrokerRate.toInt().toString().toPersian()}٪ از کل تسهیلات):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = PersianUtils.formatToman(totalFeeAmount),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "کسر سهم سکه (مرحله قبل):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "- ${PersianUtils.formatToman(coinDeductedAmount)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFC61927)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Broker Transfer Confirmation Toggle
                        // (با تایید مبلغ انتقال، سهم کارگزار مبلغ کسر شده در مرحله قبل به سهم مشتری اضافه می شود)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isBrokerTransferConfirmed) GreenSuccessLight.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { isBrokerTransferConfirmed = !isBrokerTransferConfirmed }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isBrokerTransferConfirmed,
                                onCheckedChange = { isBrokerTransferConfirmed = it },
                                colors = CheckboxDefaults.colors(checkedColor = GreenSuccess)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "تأیید انتقال مبلغ سهم کارگزار (${PersianUtils.formatToman(brokerShareAmount)})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBrokerTransferConfirmed) GreenSuccess else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBrokerTransferConfirmed)
                                        "مبلغ انتقال تأیید شد ✓ سهم سکه (${PersianUtils.formatToman(coinDeductedAmount)}) به سهم مشتری اضافه گردید."
                                    else
                                        "جهت افزودن مبلغ کسر شده مرحله قبل به سهم مشتری و آزادسازی تسویه، تأیید نمایید.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Box 3: سهم مشتری مشخص جهت انتقال به مرحله تسویه و مراحل بعد
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBrokerTransferConfirmed) GreenSuccessLight.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isBrokerTransferConfirmed) GreenSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "۴. سهم مشتری جهت انتقال به مرحله تسویه:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBrokerTransferConfirmed) GreenSuccess else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBrokerTransferConfirmed)
                                        "سهم سکه با تأیید انتقال اضافه شد و سهم مشتری آماده تسویه است"
                                    else
                                        "پس از تأیید انتقال سهم کارگزار، سهم مشتری نهایی می‌گردد",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                color = if (isBrokerTransferConfirmed) GreenSuccess else AmberPendingLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isBrokerTransferConfirmed) "آماده تسویه ✓" else "در انتظار تأیید انتقال",
                                    color = if (isBrokerTransferConfirmed) Color.White else AmberPending,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "مبلغ خالص سهم مشتری:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "اصل تسهیلات منهای سهم کارگزار",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = PersianUtils.formatToman(customerShareAmount),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC61927)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Discharge Date Field
                        OutlinedTextField(
                            value = dischargeDate.toPersian(),
                            onValueChange = { dischargeDate = it },
                            label = { Text("تاریخ تخلیه کیف‌پول طرح سکه") },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                textDirection = TextDirection.Rtl,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("coin_discharge_date_input"),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = MellatGold
                                )
                            },
                            singleLine = true
                        )
                    }
                }
            }

            // Action: Discharge Coin Plan and Transfer to Settlement
            item {
                Button(
                    onClick = {
                        viewModel.confirmDischargeAndTransfer(
                            customerId = customer.id,
                            storeId = "COIN_PLAN",
                            employerPercentage = selectedBrokerRate,
                            storePercentage = 0.0,
                            dischargeDate = dischargeDate,
                            isCoinPlan = true,
                            coinDeductedAmount = coinDeductedAmount,
                            brokerPercentage = selectedBrokerRate,
                            brokerShareAmount = brokerShareAmount,
                            customerShareAmount = customerShareAmount
                        )
                    },
                    enabled = isBrokerTransferConfirmed && customerShareAmount > 0L,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_coin_discharge_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC61927),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تخلیه کیف پول و انتقال به تسویه",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "سهم مشتری (${PersianUtils.formatToman(customerShareAmount)}) پس از تأیید به مرحله تسویه منتقل خواهد شد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
        )
    }
}
