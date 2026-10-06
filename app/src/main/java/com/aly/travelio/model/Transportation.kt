package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Transportation(
    val id: String = "",
    val startAirportFull: String = "",
    val startAirportShort: String = "",
    val destinationAirportFull: String = "",
    val destinationAirportShort: String = "",
    val pricePerPerson: Double = 0.0,
    val departDate: Long = 0L,
    val duration: String = "",
    val transportType: String = "",
    val gate: String? = null,
    val flightNumber: String? = null,
    val featured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
