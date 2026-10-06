package com.aly.travelio.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun Date.convertToString(): String {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return date.format(this)
}

fun String.convertToDate():Timestamp?{
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(this)
        if (date != null) {
            Timestamp(date)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

//if same will return false
fun Date.isDateBefore(): Boolean {
    return try {
        val currentDate = Calendar.getInstance().time
        this.before(currentDate)
    } catch (e: Exception) {
        false
    }
}