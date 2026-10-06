package com.aly.travelio.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.setOnThrottledClickListener(clickListener: View.OnClickListener) {
    var lastClickTime: Long = 0
    setOnClickListener {
        val clickedTime = System.currentTimeMillis()
        if (clickedTime - lastClickTime < 800L) { return@setOnClickListener }
        clickListener.onClick(it)
        lastClickTime = clickedTime
    }
}

fun View.hideSoftKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}