package com.example.data.model

enum class WalletType(val titleFa: String, val descriptionFa: String, val maxAmount: Long) {
    DIMA("طرح دیما", "تسهیلات طرح دیما تا سقف ۱۰۰ میلیون تومان", 100_000_000L),
    TARA("طرح تارا", "تسهیلات طرح تارا تا سقف ۳۰۰ میلیون تومان", 300_000_000L),
    INSTALLMENT_CREDIT("طرح دیما", "تسهیلات طرح دیما تا سقف ۱۰۰ میلیون تومان", 100_000_000L);
}

enum class DischargeStatus(val titleFa: String) {
    PENDING("در انتظار تخلیه"),
    AWAITING_SETTLEMENT("منتقل شده به تسویه"),
    SETTLED("تسویه شده و انتقال یافت")
}

data class Customer(
    val id: String,
    val nationalId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val birthDate: String = "",
    val shebaNumber: String = "",
    val walletBalance: Long,
    val isCharged: Boolean = true,
    val chargeDate: String,
    val isDischarged: Boolean = false,
    val dischargeDate: String? = null,
    val dischargingStoreId: String? = null,
    val dischargingStoreName: String? = null,
    val walletType: WalletType = WalletType.DIMA,
    val status: DischargeStatus = DischargeStatus.PENDING,
    val employerPercentage: Double = 0.0,
    val storePercentage: Double = 0.0,
    val employerAmount: Long = 0L,
    val storeAmount: Long = 0L,
    val netAmount: Long = 0L,
    val dimaDischargeMode: String? = null, // "STORE" or "COIN"
    val coinDeductedAmount: Long = 0L,
    val brokerPercentage: Double = 0.0,
    val brokerShareAmount: Long = 0L,
    val isBrokerTransferConfirmed: Boolean = false,
    val isInitialApproved: Boolean = false,
    val transferredToSettlement: Boolean = false,
    val settlementDate: String? = null,
    val isSettled: Boolean = false,
    val settlementCycle: String = "سیکل پایا متمرکز ملت",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String get() = "$firstName $lastName".trim()

    val initials: String
        get() {
            val f = firstName.firstOrNull()?.toString() ?: ""
            val l = lastName.firstOrNull()?.toString() ?: ""
            return "$f $l".trim().ifEmpty { "م" }
        }
}
