package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Hotel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val locationLowercase: String = "",
    val images: List<Image> = emptyList(),
    val pricePerNight: Double = 0.0,
    val guestsNumber: Int = 0,
    val rate: Double = 0.0,
    val badge: String? = null,
    val amenities: List<Amenity> = emptyList(),
    val featured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
