package com.aly.travelio.ui.dashboard.trips

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentDetailsBinding
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.ui.dashboard.main.BookDialogFragment
import com.aly.travelio.util.navigateBack
import com.aly.travelio.util.setOnThrottledClickListener
import com.aly.travelio.util.setupSharedElementTransition
import com.aly.travelio.util.toDisplayPrice
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsDetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private lateinit var experienceAdapter: ExperienceAdapter
    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSharedElementTransition()
    }

    override fun FragmentDetailsBinding.initialize() {
        postponeEnterTransition()
        initAdapters()
        hideHotelUi()
        bindData()
        handleClicks()

        binding.image.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun initAdapters() {
        experienceAdapter = ExperienceAdapter()
        binding.expList.adapter = experienceAdapter
    }

    private fun hideHotelUi() {
        binding.hotelStaticSection.visibility = View.GONE
        binding.AmenitiesList.visibility      = View.GONE
    }

    private fun bindData() {
        val trip = TripsDetailsFragmentArgs.fromBundle(requireArguments()).trips

        // Collapsing toolbar title
        binding.collapsingToolbar.title = trip.title
        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)
        binding.collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)

        binding.location.text   = trip.location
        binding.ratingText.text = trip.rate.toString()
        binding.details.text    = trip.description
        binding.price.text      = trip.price.toDisplayPrice()

        // Duration section
        if (trip.durationDays > 0) {
            binding.tripStaticSection1.visibility  = View.VISIBLE
            binding.durationTripSection.visibility = View.VISIBLE
            binding.days.text = "${trip.durationDays} Days"
        }

        // Top experiences section
        if (trip.topExperiences.isNotEmpty()) {
            binding.tripStaticSection.visibility = View.VISIBLE
            binding.expList.visibility           = View.VISIBLE
            experienceAdapter.submitList(trip.topExperiences)
        }

        // Load hero image
        Glide.with(binding.image)
            .load(trip.images.firstOrNull()?.url)
            .placeholder(R.color.card_1)
            .centerCrop()
            .into(binding.image)
    }

    private fun handleClicks() {
        val trip = TripsDetailsFragmentArgs.fromBundle(requireArguments()).trips

        binding.back.setNavigationOnClickListener { navigateBack() }

        binding.reserve.setOnThrottledClickListener {
            BookDialogFragment.newInstance(
                type           = "TRIP",
                refId          = trip.id,
                title          = trip.title,
                location       = trip.location,
                pricePerPerson = trip.price,
                imageUrl       = trip.images.firstOrNull()?.url ?: ""
            ).show(childFragmentManager, "book_trip")
        }
    }
}