package com.example.ui.common

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    fun formatInr(amount: Double, includeDecimals: Boolean = false): String {
        val isNegative = amount < 0
        val absAmount = kotlin.math.abs(amount)
        val intPart = absAmount.toLong()
        val decimalPart = ((absAmount - intPart) * 100).toInt()

        // Indian grouping logic
        val str = intPart.toString()
        val formatted = if (str.length <= 3) {
            str
        } else {
            val lastThree = str.substring(str.length - 3)
            val rest = str.substring(0, str.length - 3)
            val sb = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                sb.append(rest[i])
                count++
                if (count == 2 && i != 0) {
                    sb.append(",")
                    count = 0
                }
            }
            sb.reverse().append(",").append(lastThree).toString()
        }

        val prefix = if (isNegative) "-₹" else "₹"
        return if (includeDecimals) {
            "$prefix$formatted.${String.format(Locale.US, "%02d", decimalPart)}"
        } else {
            "$prefix$formatted"
        }
    }

    fun formatGrams(grams: Double): String {
        return "${String.format(Locale.US, "%.2f", grams)} g"
    }

    fun formatPercentage(pct: Double): String {
        val sign = if (pct > 0) "+" else ""
        return "$sign${String.format(Locale.US, "%.2f", pct)}%"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.US)
        return sdf.format(Date(timestamp))
    }
}
