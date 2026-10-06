package com.aly.travelio.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Trip(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val locationLowercase: String = "",
    val images: List<Image> = emptyList(),
    val price: Double = 0.0,
    val rate: Double = 0.0,
    val durationDays: Int = 0,
    val topExperiences: List<Experience> = emptyList(),
    val featured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
