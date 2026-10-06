package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Experience(
    val title: String = "",
    val duration: String = "",
    val image: String = ""
) : Parcelable
