package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Image(
    val url: String = "",
    val isPrimary: Boolean = false
) : Parcelable
