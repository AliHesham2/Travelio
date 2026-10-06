package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Favourite(
    val id: String = "",
    val type: String = "", // HOTEL / TRANSPORT / TRIP
    val refId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
