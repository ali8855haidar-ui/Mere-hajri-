package com.example.domain

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToLong

object DateUtils {
    private val hindiLocale = Locale("hi", "IN")
    private val isoDateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }

    private val isoMonthFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM", Locale.US).apply { isLenient = false }

    fun todayIso(): String = isoDateFormat.format(Calendar.getInstance().time)

    fun currentMonthIso(): String = isoMonthFormat.format(Calendar.getInstance().time)

    fun monthOfDate(dateIso: String): String =
        if (dateIso.length >= 7) dateIso.substring(0, 7) else currentMonthIso()

    fun shiftMonth(monthIso: String, deltaMonths: Int): String {
        val cal = Calendar.getInstance()
        val parsed = runCatching { isoMonthFormat.parse(monthIso) }.getOrNull()
        if (parsed != null) {
            cal.time = parsed
        }
        cal.add(Calendar.MONTH, deltaMonths)
        return isoMonthFormat.format(cal.time)
    }

    fun formatHindiFullDate(dateIso: String): String {
        val parsed = runCatching { isoDateFormat.parse(dateIso) }.getOrNull() ?: return dateIso
        val fmt = SimpleDateFormat("d MMMM yyyy, EEEE", hindiLocale)
        return fmt.format(parsed)
    }

    fun formatHindiShortDate(dateIso: String): String {
        val parsed = runCatching { isoDateFormat.parse(dateIso) }.getOrNull() ?: return dateIso
        val fmt = SimpleDateFormat("d MMM yyyy (EEE)", hindiLocale)
        return fmt.format(parsed)
    }

    fun formatHindiMonth(monthIso: String): String {
        val parsed = runCatching { isoMonthFormat.parse(monthIso) }.getOrNull() ?: return monthIso
        val fmt = SimpleDateFormat("MMMM yyyy", hindiLocale)
        return fmt.format(parsed)
    }

    fun dateIsoToUtcMillis(dateIso: String): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val parsed = runCatching {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(dateIso)
        }.getOrNull()
        if (parsed != null) {
            cal.time = parsed
        }
        return cal.timeInMillis
    }

    fun utcMillisToDateIso(millis: Long): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return fmt.format(millis)
    }

    fun formatRupees(amount: Double): String {
        val rounded = (amount * 100.0).roundToLong() / 100.0
        val sign = if (rounded < 0) "-" else ""
        val absVal = abs(rounded)
        val nf = NumberFormat.getNumberInstance(Locale("en", "IN"))
        if (absVal % 1.0 == 0.0) {
            nf.maximumFractionDigits = 0
            nf.minimumFractionDigits = 0
        } else {
            nf.maximumFractionDigits = 2
            nf.minimumFractionDigits = 0
        }
        return "${sign}₹${nf.format(absVal)}"
    }

    fun formatHours(hours: Double): String {
        val rounded = (hours * 10.0).roundToLong() / 10.0
        return if (rounded % 1.0 == 0.0) {
            rounded.toLong().toString()
        } else {
            DecimalFormat("0.#").format(rounded)
        }
    }
}
