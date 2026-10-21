package com.example.data.model

enum class TransactionType(val titleFa: String) {
    CHARGE("شارژ کیف‌پول"),
    DISCHARGE("تخلیه و تسویه")
}

data class WalletTransaction(
    val id: String,
    val customerId: String,
    val customerName: String,
    val nationalId: String,
    val amount: Long,
    val type: TransactionType,
    val date: String,
    val storeName: String,
    val referenceId: String,
    val trackingCode: String,
    val status: String = "موفق"
)
