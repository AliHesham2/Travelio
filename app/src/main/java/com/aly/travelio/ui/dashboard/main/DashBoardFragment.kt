package com.aly.travelio.ui.dashboard.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels

import androidx.navigation.fragment.findNavController
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.data.remote.firebase.FirebaseSeeder
import com.aly.travelio.databinding.FragmentDashBoardBinding
import com.aly.travelio.model.Hotel
import com.aly.travelio.model.Transportation
import com.aly.travelio.model.Trip
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.ui.registration.RegistrationActivity
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateToEditAccount
import com.aly.travelio.util.navigateToHotels
import com.aly.travelio.util.navigateToOrders
import com.aly.travelio.util.navigateToTransport
import com.aly.travelio.util.navigateToTrips
import com.aly.travelio.util.setOnThrottledClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardFragment : BaseFragment<FragmentDashBoardBinding>(FragmentDashBoardBinding::inflate) {
    private lateinit var hotelAdapter: DashboardHotelsAdapter
    private lateinit var tripsAdapter: com.aly.travelio.ui.dashboard.trips.TripsAdapter
    private lateinit var transAdapter: com.aly.travelio.ui.dashboard.transport.TransportAdapter
    private var sectionsLoaded = 0

    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentDashBoardBinding.initialize() {
        postponeEnterTransition()
        allowEnterTransitionOverlap = false
        allowReturnTransitionOverlap = false

        initAdapters()
        initRecyclers()
        handleClicks()
        handleObservers()

        // Restore scroll position
        binding.dashboardScrollView.post {
            binding.dashboardScrollView.scrollTo(0, viewModel.dashboardScrollY)
        }

        // Wait for the parent view to be laid out, then start transition
        (view?.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
        androidx.core.view.ViewCompat.requestApplyInsets(binding.root)
    }

    override fun onDestroyView() {
        // Save scroll position before view is destroyed
        viewModel.dashboardScrollY = binding.dashboardScrollView.scrollY
        super.onDestroyView()
    }

    // ── Adapters ──────────────────────────────────────────────────────────────
    private fun initAdapters() {
        hotelAdapter = DashboardHotelsAdapter { hotel, imageView -> onHotelClick(hotel, imageView) }
        tripsAdapter = com.aly.travelio.ui.dashboard.trips.TripsAdapter(isDashboard = true) { trip, imageView -> onTripClick(trip, imageView) }
        transAdapter = com.aly.travelio.ui.dashboard.transport.TransportAdapter(isDashboard = true) { trans -> onTransportClick(trans) }
    }

    private fun initRecyclers() {
        binding.hotelList.adapter = hotelAdapter
        binding.tripsList.adapter = tripsAdapter
        binding.transList.adapter = transAdapter
    }

    // ── Clicks ────────────────────────────────────────────────────────────────
    private fun handleClicks() {
        binding.appCompatImageView5.setOnThrottledClickListener {
            binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
        }
        binding.closeDrawer.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }
        binding.viewAllHotels.setOnClickListener { navigateToHotels() }
        binding.viewAllTrips.setOnClickListener  { navigateToTrips() }
        binding.viewAllTrans.setOnClickListener  { navigateToTransport() }

        binding.hotelTab.setOnClickListener      { closeDrawerAndRun { navigateToHotels() } }
        binding.transTab.setOnClickListener      { closeDrawerAndRun { navigateToTransport() } }
        binding.tripTab.setOnClickListener       { closeDrawerAndRun { navigateToTrips() } }
        binding.editTab.setOnClickListener       { closeDrawerAndRun { navigateToEditAccount() } }
        binding.reservationTab.setOnClickListener { closeDrawerAndRun { navigateToOrders() } }
        binding.signOut.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            viewModel.signOut { ok ->
                if (ok) signOutIt()
                else viewModel.showErrorMessage.value = getString(R.string.Sign_out_again)
            }
        }
        binding.retryBtn.setOnClickListener {
            binding.networkErrorLayout.visibility = View.GONE
            showAllShimmers()
            viewModel.loadDashboard()
        }
    }

    private fun closeDrawerAndRun(action: () -> Unit) {
        binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        action()
    }

    // ── Observers ─────────────────────────────────────────────────────────────
    private fun handleObservers() {

        // Featured Trips
        viewModel.featuredTrips.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            binding.shimmerTripsLayout.root.visibility = View.GONE
            binding.tripsList.visibility          = View.VISIBLE
            when (result) {
                is ResultCallBack.Success -> {
                    tripsAdapter.submitList(result.data)
                    onSectionLoaded()
                }
                is ResultCallBack.Error   -> showErrorIfAllFailed()
            }
        }

        // Featured Transport
        viewModel.featuredTransportations.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            binding.shimmerTransLayout.root.visibility = View.GONE
            binding.transList.visibility          = View.VISIBLE
            when (result) {
                is ResultCallBack.Success -> {
                    transAdapter.submitList(result.data)
                    onSectionLoaded()
                }
                is ResultCallBack.Error   -> showErrorIfAllFailed()
            }
        }

        // Featured Hotels
        viewModel.featuredHotels.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            binding.shimmerHotelsLayout.root.visibility = View.GONE
            binding.hotelList.visibility           = View.VISIBLE
            when (result) {
                is ResultCallBack.Success -> {
                    hotelAdapter.submitList(result.data)
                    onSectionLoaded()
                }
                is ResultCallBack.Error -> showErrorIfAllFailed()
            }
        }

        // User data
        viewModel.currentUser.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            if (result is ResultCallBack.Success) {
                val user = result.data
                binding.name.text     = "Hello, ${user.name}"
                binding.userName.text = "Hello, ${user.name}"
                val opts = RequestOptions().circleCrop()
                Glide.with(this).load(user.profileImage).placeholder(R.drawable.drawer_avatar).into(binding.avatar1)
                Glide.with(this).load(user.profileImage).placeholder(R.drawable.drawer_avatar).into(binding.avatar)
            }
        }
    }

    // ── Show full-screen error when ALL sections fail ─────────────────────────
    private fun showErrorIfAllFailed() {
        val tripsError  = viewModel.featuredTrips.value is ResultCallBack.Error
        val transError  = viewModel.featuredTransportations.value is ResultCallBack.Error
        val hotelsError = viewModel.featuredHotels.value is ResultCallBack.Error
        if (tripsError && transError && hotelsError) {
            binding.tripsArea.visibility   = View.GONE
            binding.transArea.visibility   = View.GONE
            binding.HotelArea.visibility   = View.GONE
            binding.networkErrorLayout.visibility = View.VISIBLE
        }
    }

    private fun showAllShimmers() {
        binding.shimmerTripsLayout.root.visibility  = View.VISIBLE
        binding.shimmerTransLayout.root.visibility  = View.VISIBLE
        binding.shimmerHotelsLayout.root.visibility = View.VISIBLE
        binding.tripsList.visibility           = View.GONE
        binding.transList.visibility           = View.GONE
        binding.hotelList.visibility           = View.GONE
        binding.tripsArea.visibility           = View.VISIBLE
        binding.transArea.visibility           = View.VISIBLE
        binding.HotelArea.visibility           = View.VISIBLE
    }

    // ── Item click handlers ───────────────────────────────────────────────────
    private fun onHotelClick(hotel: Hotel, imageView: View) {
        val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
            imageView to "transition_image"
        )
        findNavController().navigate(
            DashBoardFragmentDirections.actionDashBoardFragmentToHotelDetailsFragment(hotel),
            extras
        )
    }

    private fun onTripClick(trip: Trip, imageView: View) {
        val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
            imageView to "transition_image"
        )
        findNavController().navigate(
            DashBoardFragmentDirections.actionDashBoardFragmentToTripsDetailsFragment(trip),
            extras
        )
    }

    private fun onTransportClick(transport: Transportation) {
        BookDialogFragment.newInstance(
            type           = "transport",
            refId          = transport.id,
            title          = "${transport.startAirportShort} → ${transport.destinationAirportShort}",
            location       = transport.startAirportFull,
            pricePerPerson = transport.pricePerPerson
        ).show(childFragmentManager, "book_transport")
    }

    // ── Section loaded counter ────────────────────────────────────────────────
    private fun onSectionLoaded() {
        sectionsLoaded++
        if (sectionsLoaded >= 3) animateContentIn()
    }

    // ── Section animation ─────────────────────────────────────────────────────
    private fun animateContentIn() {
        listOf(binding.tripsArea, binding.transArea, binding.HotelArea)
            .forEachIndexed { index, view ->
                view.visibility = View.VISIBLE
                // Subtle slide-up only — no alpha reset so content stays visible
                val transY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 40f, 0f)
                    .setDuration(350)
                transY.startDelay = index * 80L
                transY.interpolator = android.view.animation.DecelerateInterpolator()
                transY.start()
            }
    }

    // ── Sign out ──────────────────────────────────────────────────────────────
    private fun signOutIt() {
        startActivity(Intent(requireActivity(), RegistrationActivity::class.java))
        requireActivity().finish()
    }
}