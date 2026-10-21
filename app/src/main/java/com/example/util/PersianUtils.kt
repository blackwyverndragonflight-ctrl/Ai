package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.Customer
import com.example.data.model.Store
import com.example.data.model.WalletTransaction
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PersianUtils {

    fun toPersianDigits(input: String): String {
        val persianChars = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val builder = StringBuilder()
        for (char in input) {
            if (char in '0'..'9') {
                builder.append(persianChars[char - '0'])
            } else {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    fun toEnglishDigits(input: String): String {
        val builder = StringBuilder()
        for (char in input) {
            when (char) {
                '۰' -> builder.append('0')
                '۱' -> builder.append('1')
                '۲' -> builder.append('2')
                '۳' -> builder.append('3')
                '۴' -> builder.append('4')
                '۵' -> builder.append('5')
                '۶' -> builder.append('6')
                '۷' -> builder.append('7')
                '۸' -> builder.append('8')
                '۹' -> builder.append('9')
                else -> builder.append(char)
            }
        }
        return builder.toString()
    }

    fun formatToman(amount: Long): String {
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(amount)
        return "${toPersianDigits(formatted)} تومان"
    }

    fun formatNumber(amount: Long): String {
        val formatter = DecimalFormat("#,###")
        return toPersianDigits(formatter.format(amount))
    }

    /**
     * Separates digits 3 by 3 with commas as user types
     */
    fun formatWithCommas(rawInput: String): String {
        val digitsOnly = toEnglishDigits(rawInput).filter { it.isDigit() }
        if (digitsOnly.isEmpty()) return ""
        val parsed = digitsOnly.toLongOrNull() ?: return toPersianDigits(rawInput)
        val formatter = DecimalFormat("#,###")
        return toPersianDigits(formatter.format(parsed))
    }

    fun parseAmount(input: String): Long {
        val digitsOnly = toEnglishDigits(input).filter { it.isDigit() }
        return digitsOnly.toLongOrNull() ?: 0L
    }

    fun isValidNationalId(rawCode: String): Boolean {
        val clean = toEnglishDigits(rawCode.trim())
        if (clean.length != 10) return false
        if (!clean.all { it.isDigit() }) return false
        if (clean.all { it == clean[0] }) return false

        var sum = 0
        for (i in 0 until 9) {
            sum += clean[i].digitToInt() * (10 - i)
        }
        val remainder = sum % 11
        val checkDigit = clean[9].digitToInt()

        return if (remainder < 2) {
            checkDigit == remainder
        } else {
            checkDigit == (11 - remainder)
        }
    }

    fun numberToPersianWords(amount: Long): String {
        if (amount == 0L) return "صفر تومان"
        val billions = amount / 1_000_000_000
        val remainderB = amount % 1_000_000_000
        val millions = remainderB / 1_000_000
        val remainderM = remainderB % 1_000_000
        val thousands = remainderM / 1_000

        val parts = mutableListOf<String>()
        if (billions > 0) {
            parts.add("${numberToWordChunk(billions)} میلیارد")
        }
        if (millions > 0) {
            parts.add("${numberToWordChunk(millions)} میلیون")
        }
        if (thousands > 0) {
            parts.add("${numberToWordChunk(thousands)} هزار")
        }
        val text = if (parts.isEmpty()) "${formatNumber(amount)} تومان" else parts.joinToString(" و ") + " تومان تمام"
        return "معادل $text"
    }

    private fun numberToWordChunk(num: Long): String {
        val ones = arrayOf("", "یک", "دو", "سه", "چهار", "پنج", "شش", "هفت", "هشت", "نه")
        val teens = arrayOf("ده", "یازده", "دوازده", "سیزده", "چهارده", "پانزده", "شانزده", "هفده", "هجده", "نوزده")
        val tens = arrayOf("", "ده", "بیست", "سی", "چهل", "پنجاه", "شصت", "هفتاد", "هشتاد", "نود")
        val hundreds = arrayOf("", "صد", "دویست", "سیصد", "چهارصد", "پانصد", "ششصد", "هفتصد", "هشتصد", "نهصد")

        if (num in 1..9) return ones[num.toInt()]
        if (num in 10..19) return teens[(num - 10).toInt()]
        if (num in 20..99) {
            val t = (num / 10).toInt()
            val o = (num % 10).toInt()
            return if (o == 0) tens[t] else "${tens[t]} و ${ones[o]}"
        }
        if (num in 100..999) {
            val h = (num / 100).toInt()
            val rem = num % 100
            return if (rem == 0L) hundreds[h] else "${hundreds[h]} و ${numberToWordChunk(rem)}"
        }
        return toPersianDigits(num.toString())
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i + 1]
        }
        if (gm2 > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0 until 11) {
            val days = if (i < 6) 31 else 30
            if (jDayNo < days) {
                jm = i + 1
                break
            }
            jDayNo -= days
        }
        if (jm == 0) {
            jm = 12
        }
        val jd = jDayNo + 1
        return Triple(jy, jm, jd)
    }

    fun getPersianDate(date: Date = Date()): String {
        val cal = java.util.Calendar.getInstance()
        cal.time = date
        val (jy, jm, jd) = gregorianToJalali(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
        val formatted = String.format(Locale.US, "%04d/%02d/%02d", jy, jm, jd)
        return toPersianDigits(formatted)
    }

    fun getCurrentPersianDate(): String {
        return getPersianDate(Date())
    }

    fun getCurrentPersianDateTime(): String {
        val dateStr = getCurrentPersianDate()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = toPersianDigits(timeFormat.format(Date()))
        return "$dateStr ساعت $time"
    }

    /**
     * Export to CSV with UTF-8 BOM so Microsoft Excel opens Persian characters seamlessly
     */
    fun exportToExcelCsv(
        context: Context,
        customers: List<Customer>,
        stores: List<Store>,
        transactions: List<WalletTransaction>
    ): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "Mellat_Wallet_Report_$timestamp.csv")

            FileOutputStream(file).use { fos ->
                // Write UTF-8 BOM for Microsoft Excel
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                val sb = StringBuilder()
                sb.append("گزارش جامع سامانه هوشمند پذیرندگان و کیف‌پول بانک ملت\n")
                sb.append("تاریخ استخراج گزارش: ").append(getCurrentPersianDateTime()).append("\n\n")

                sb.append("--- فهرست مشتریان و تسویه حساب‌ها ---\n")
                sb.append("ردیف,نام و نام خانوادگی,کد ملی,شماره تماس,شماره شبا,مبلغ کیف‌پول (تومان),نوع تسهیلات,تاریخ شارژ,وضعیت,فروشگاه عامل,سهم کارفرما (تومان),سهم فروشگاه (تومان),سهم خالص متقاضی (تومان),تاریخ تخلیه,تاریخ تسویه\n")

                customers.forEachIndexed { index, c ->
                    sb.append("${toPersianDigits((index + 1).toString())},")
                    sb.append("\"${c.fullName}\",")
                    sb.append("'${toPersianDigits(c.nationalId)},")
                    sb.append("'${toPersianDigits(c.phoneNumber)},")
                    sb.append("'${c.shebaNumber},")
                    sb.append("${toPersianDigits(c.walletBalance.toString())},")
                    sb.append("\"${c.walletType.titleFa}\",")
                    sb.append("\"${toPersianDigits(c.chargeDate)}\",")
                    sb.append("\"${c.status.titleFa}\",")
                    sb.append("\"${c.dischargingStoreName ?: "---"}\",")
                    sb.append("${toPersianDigits(c.employerAmount.toString())},")
                    sb.append("${toPersianDigits(c.storeAmount.toString())},")
                    sb.append("${toPersianDigits(c.netAmount.toString())},")
                    sb.append("\"${toPersianDigits(c.dischargeDate ?: "---")}\",")
                    sb.append("\"${toPersianDigits(c.settlementDate ?: "---")}\"\n")
                }

                sb.append("\n--- سوابق تراکنش‌ها و حواله‌های تسویه ---\n")
                sb.append("کد پیگیری,نام متقاضی,کد ملی,نوع تراکنش,مبلغ (تومان),تاریخ,فروشگاه عامل,شناسه مرجع,وضعیت\n")

                transactions.forEach { t ->
                    sb.append("${toPersianDigits(t.trackingCode)},")
                    sb.append("\"${t.customerName}\",")
                    sb.append("'${toPersianDigits(t.nationalId)},")
                    sb.append("\"${t.type.titleFa}\",")
                    sb.append("${toPersianDigits(t.amount.toString())},")
                    sb.append("\"${toPersianDigits(t.date)}\",")
                    sb.append("\"${t.storeName}\",")
                    sb.append("'${toPersianDigits(t.referenceId)},")
                    sb.append("\"${t.status}\"\n")
                }

                sb.append("\n--- فروشگاه‌ها و پذیرندگان ---\n")
                sb.append("ردیف,نام فروشگاه,آدرس,موقعیت,مجموع تسویه شده (تومان),در انتظار (تومان)\n")

                stores.forEachIndexed { index, s ->
                    sb.append("${toPersianDigits((index + 1).toString())},")
                    sb.append("\"${s.name}\",")
                    sb.append("\"${s.address}\",")
                    sb.append("\"${s.location}\",")
                    sb.append("${toPersianDigits(s.settledAmount.toString())},")
                    sb.append("${toPersianDigits(s.pendingAmount.toString())}\n")
                }

                fos.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Native Android PDF export using PdfDocument
     */
    fun exportToPdf(
        context: Context,
        customers: List<Customer>,
        stores: List<Store>
    ): File? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "Mellat_Report_$timestamp.pdf")

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val headerPaint = Paint().apply {
                color = AndroidColor.parseColor("#9E0B1B") // Mellat Red
                style = Paint.Style.FILL
            }
            // Draw top header banner
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, headerPaint)

            val textPaintWhite = Paint().apply {
                color = AndroidColor.WHITE
                textSize = 16f
                isFakeBoldText = true
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("بانک ملت - سامانه مدیریت هوشمند کیف‌پول و تسویه", (pageWidth - 30).toFloat(), 40f, textPaintWhite)

            val subTextPaintWhite = Paint().apply {
                color = AndroidColor.parseColor("#FFE082") // Amber
                textSize = 11f
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("تاریخ صدور گزارش: ${getCurrentPersianDateTime()}", (pageWidth - 30).toFloat(), 65f, subTextPaintWhite)

            val textPaintBlack = Paint().apply {
                color = AndroidColor.DKGRAY
                textSize = 10f
                textAlign = Paint.Align.RIGHT
            }

            val tableHeaderPaint = Paint().apply {
                color = AndroidColor.parseColor("#F5F5F5")
                style = Paint.Style.FILL
            }

            var currentY = 120f
            val boldPaint = Paint().apply {
                color = AndroidColor.BLACK
                textSize = 13f
                isFakeBoldText = true
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("فهرست متقاضیان و تراکنش‌های تسویه", (pageWidth - 30).toFloat(), currentY, boldPaint)
            currentY += 16f

            // Table Header Bar
            canvas.drawRect(20f, currentY, (pageWidth - 20).toFloat(), currentY + 24f, tableHeaderPaint)
            val colHeaderPaint = Paint().apply {
                color = AndroidColor.parseColor("#9E0B1B")
                textSize = 9.5f
                isFakeBoldText = true
                textAlign = Paint.Align.RIGHT
            }

            canvas.drawText("نام و نام خانوادگی", (pageWidth - 30).toFloat(), currentY + 16f, colHeaderPaint)
            canvas.drawText("کد ملی", (pageWidth - 140).toFloat(), currentY + 16f, colHeaderPaint)
            canvas.drawText("مبلغ کیف‌پول", (pageWidth - 230).toFloat(), currentY + 16f, colHeaderPaint)
            canvas.drawText("سهم خالص", (pageWidth - 320).toFloat(), currentY + 16f, colHeaderPaint)
            canvas.drawText("وضعیت تسویه", (pageWidth - 410).toFloat(), currentY + 16f, colHeaderPaint)
            canvas.drawText("فروشگاه عامل", (pageWidth - 490).toFloat(), currentY + 16f, colHeaderPaint)

            currentY += 32f

            val rowPaint = Paint().apply {
                color = AndroidColor.parseColor("#333333")
                textSize = 9f
                textAlign = Paint.Align.RIGHT
            }

            val linePaint = Paint().apply {
                color = AndroidColor.parseColor("#E0E0E0")
                strokeWidth = 0.8f
            }

            customers.take(22).forEach { c ->
                canvas.drawText(c.fullName, (pageWidth - 30).toFloat(), currentY, rowPaint)
                canvas.drawText(toPersianDigits(c.nationalId), (pageWidth - 140).toFloat(), currentY, rowPaint)
                canvas.drawText(formatToman(c.walletBalance), (pageWidth - 230).toFloat(), currentY, rowPaint)
                canvas.drawText(if (c.netAmount > 0) formatToman(c.netAmount) else "---", (pageWidth - 320).toFloat(), currentY, rowPaint)
                canvas.drawText(c.status.titleFa, (pageWidth - 410).toFloat(), currentY, rowPaint)
                canvas.drawText(c.dischargingStoreName ?: "تعیین‌نشده", (pageWidth - 490).toFloat(), currentY, rowPaint)

                currentY += 6f
                canvas.drawLine(20f, currentY, (pageWidth - 20).toFloat(), currentY, linePaint)
                currentY += 18f
            }

            currentY += 16f
            canvas.drawText("خلاصه پذیرندگان ثبت شده: ${stores.size.toPersian()} فروشگاه فعال", (pageWidth - 30).toFloat(), currentY, boldPaint)
            currentY += 16f
            stores.forEach { s ->
                canvas.drawText("• ${s.name} - ${s.address} (${s.location})", (pageWidth - 40).toFloat(), currentY, textPaintBlack)
                currentY += 14f
            }

            // Footer
            val footerPaint = Paint().apply {
                color = AndroidColor.GRAY
                textSize = 8.5f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("صفحه ۱ از ۱ - سند رسمی تولید شده توسط سامانه هوشمند پذیرندگان بانک ملت", (pageWidth / 2).toFloat(), (pageHeight - 20).toFloat(), footerPaint)

            document.finishPage(page)
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExportFile(context: Context, file: File, mimeType: String = "text/csv", title: String = "اشتراک‌گذاری گزارش") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun String.toPersian(): String = PersianUtils.toPersianDigits(this)
fun Long.toPersian(): String = PersianUtils.toPersianDigits(this.toString())
fun Int.toPersian(): String = PersianUtils.toPersianDigits(this.toString())
