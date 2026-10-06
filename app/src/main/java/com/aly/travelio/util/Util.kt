package com.aly.travelio.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aly.travelio.R
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formats a Double as "$1,000" */
fun Double.toDisplayPrice(): String = String.format(Locale.US, "$%,.0f", this)

/** Formats a Long timestamp as "07AUG" */
fun Long.toDisplayDate(): String =
    SimpleDateFormat("ddMMM", Locale.US).format(Date(this)).uppercase(Locale.US)

class Util {
    companion object {
        private var toast: Toast? = null
        private var snackBar: Snackbar? = null

        fun alertMsg(view: View, msg: String? = null, id: Int? = null) {
            snackBar?.dismiss()
            snackBar = Snackbar.make(view, msg ?: view.context.getString(id!!), Snackbar.LENGTH_LONG).also { sb ->
                sb.setBackgroundTint(ContextCompat.getColor(view.context, R.color.black))
            }
            snackBar?.show()
        }

        fun toastMsg(context: Context, msg: String? = null, id: Int? = null) {
            toast?.cancel()
            toast = Toast.makeText(context, msg ?: context.getString(id!!), Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}