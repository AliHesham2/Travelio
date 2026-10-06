package com.aly.travelio.util

import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.aly.travelio.R
import com.aly.travelio.model.Hotel
import com.aly.travelio.model.Trip
import com.aly.travelio.ui.dashboard.hotels.HotelDetailsFragment
import com.aly.travelio.ui.dashboard.hotels.HotelsFragment
import com.aly.travelio.ui.dashboard.hotels.HotelsFragmentDirections
import com.aly.travelio.ui.dashboard.main.DashBoardFragment
import com.aly.travelio.ui.dashboard.main.DashBoardFragmentDirections
import com.aly.travelio.ui.dashboard.transport.TransportFragment
import com.aly.travelio.ui.dashboard.trips.TripsDetailsFragment
import com.aly.travelio.ui.dashboard.trips.TripsFragment
import com.aly.travelio.ui.dashboard.trips.TripsFragmentDirections
import com.aly.travelio.ui.orders.MainOrderFragment
import com.aly.travelio.ui.orders.MainOrderFragmentDirections
import com.aly.travelio.ui.registration.RegistrationFragment
import com.aly.travelio.ui.registration.RegistrationFragmentDirections
import com.aly.travelio.ui.registration.SignInFragment
import com.aly.travelio.ui.registration.SignInFragmentDirections
import com.aly.travelio.ui.registration.SignUpFragment
import com.aly.travelio.ui.registration.SignUpFragmentDirections
import com.aly.travelio.ui.user.edit.EditUserFragment

// ── Registration ─────────────────────────────────────────────────────────────
fun RegistrationFragment.navigateToSignUp() {
    viewModel.navigationCommand.value = NavigationCommands.To(RegistrationFragmentDirections.actionRegistrationFragmentToSignUpFragment())
}

fun RegistrationFragment.navigateToSignIn() {
    viewModel.navigationCommand.value = NavigationCommands.To(RegistrationFragmentDirections.actionRegistrationFragmentToSignInFragment())
}

fun SignInFragment.navigateToSignUp() {
    viewModel.navigationCommand.value = NavigationCommands.To(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
}

fun SignUpFragment.navigateToSignIn() {
    viewModel.navigationCommand.value = NavigationCommands.To(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
fun DashBoardFragment.navigateToHotels() {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToHotelsFragment())
}

fun DashBoardFragment.navigateToTransport() {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToTransportFragment())
}

fun DashBoardFragment.navigateToTrips() {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToTripsFragment())
}


fun DashBoardFragment.navigateToTripsDetails(trip: Trip) {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToTripsDetailsFragment(trip))
}

fun DashBoardFragment.navigateToHotelsDetails(hotel: Hotel) {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToHotelDetailsFragment(hotel))
}

fun DashBoardFragment.navigateToEditAccount() {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToEditUserFragment())
}

fun DashBoardFragment.navigateToOrders() {
    viewModel.navigationCommand.value = NavigationCommands.To(DashBoardFragmentDirections.actionDashBoardFragmentToMainOrderFragment())
}

// ── Hotels ────────────────────────────────────────────────────────────────────
fun HotelsFragment.navigateToHotelDetails(hotel: Hotel) {
    viewModel.navigationCommand.value = NavigationCommands.To(HotelsFragmentDirections.actionHotelsFragmentToHotelDetailsFragment(hotel))
}

fun HotelsFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}

// ── Hotel Details ─────────────────────────────────────────────────────────────
fun HotelDetailsFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}

// ── Transport ─────────────────────────────────────────────────────────────────
fun TransportFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}


// ── Trips ─────────────────────────────────────────────────────────────────────
fun TripsFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}

fun TripsFragment.navigateToTripsDetails(trip: Trip) {
    viewModel.navigationCommand.value = NavigationCommands.To(TripsFragmentDirections.actionTripsFragmentToTripsDetailsFragment(trip))
}

// ── Trips Details ──────────────────────────────────────────────────────────────
fun TripsDetailsFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}


// ── Edit Account ──────────────────────────────────────────────────────────────
fun EditUserFragment.navigateBack() {
    viewModel.navigationCommand.value = NavigationCommands.Back
}

// ── Orders ────────────────────────────────────────────────────────────────────
fun MainOrderFragment.navigateToHotel(hotel: Hotel) {
    viewModel.navigationCommand.value = NavigationCommands.To(MainOrderFragmentDirections.actionMainOrderFragmentToHotelDetailsFragment(hotel))
}

fun MainOrderFragment.navigateToTrips(trip: Trip) {
    viewModel.navigationCommand.value = NavigationCommands.To(MainOrderFragmentDirections.actionMainOrderFragmentToTripsDetailsFragment(trip))
}

// ── Shared Element Transition helper ──────────────────────────────────────────
fun Fragment.setupSharedElementTransition() {
    sharedElementEnterTransition = TransitionInflater.from(requireContext())
        .inflateTransition(android.R.transition.move)
        ?.setDuration(350)
}
