package com.aly.travelio.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.aly.travelio.R

object LoadingDialog {
    private  var dialog: Dialog? = null

    fun showDialogue(context: Context){
        dialog = Dialog(context)
        dialog?.setContentView(R.layout.loading)
        dialog?.setCancelable(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    fun hideDialogue(){
        dialog?.dismiss()
    }
}