package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Reservation(
    val id: String = "",
    val type: String = "", // HOTEL / TRANSPORT / TRIP
    val refId: String = "",
    val title: String = "",
    val image: String = "",
    val location: String = "",
    val pricePerPerson: Double = 0.0,
    val totalPrice: Double = 0.0,
    val personsCount: Int = 0,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val extra: @kotlinx.parcelize.RawValue Map<String, Any> = emptyMap()
) : Parcelable
