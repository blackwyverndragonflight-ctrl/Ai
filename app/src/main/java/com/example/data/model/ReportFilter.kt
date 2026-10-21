package com.example.data.model

enum class DateRangeFilter(val labelFa: String) {
    TODAY("امروز (۲۴ ساعت)"),
    LAST_7_DAYS("۷ روز گذشته"),
    CURRENT_MONTH("ماه جاری"),
    ALL("همه بازه‌ها")
}

data class ReportFilter(
    val dateRange: DateRangeFilter = DateRangeFilter.ALL,
    val selectedStoreId: String? = null,
    val dischargeStatus: DischargeStatus? = null,
    val searchQuery: String = ""
)
