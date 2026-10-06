package com.aly.travelio.util

import androidx.navigation.NavDirections

sealed interface NavigationCommands{
    data class To(val directions: NavDirections) : NavigationCommands
    data object Back : NavigationCommands
}