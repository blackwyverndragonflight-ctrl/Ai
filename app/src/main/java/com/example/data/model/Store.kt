package com.example.data.model

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val location: String = "تهران",
    val branchCode: String = "۱۰۲",
    val terminalId: String = "TM-۱۰۲",
    val shebaNumber: String = "",
    val phoneNumber: String = "",
    val settledAmount: Long = 0L,
    val pendingAmount: Long = 0L,
    val activeWalletsCount: Int = 0,
    val isActive: Boolean = true
)
