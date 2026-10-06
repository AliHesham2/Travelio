package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Amenity(
    val title: String = "",
    val icon: String = ""
) : Parcelable
