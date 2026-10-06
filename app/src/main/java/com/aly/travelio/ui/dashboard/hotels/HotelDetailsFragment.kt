package com.aly.travelio.ui.dashboard.hotels

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
class HotelDetailsFragment : BaseFragment<FragmentDetailsBinding>(FragmentDetailsBinding::inflate) {

    private lateinit var amenityAdapter: AmenityAdapter
    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSharedElementTransition()
    }

    override fun FragmentDetailsBinding.initialize() {
        postponeEnterTransition()
        initAdapters()
        hideTripsUi()
        bindData()
        handleClicks()

        binding.image.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun initAdapters() {
        amenityAdapter = AmenityAdapter()
        binding.AmenitiesList.adapter = amenityAdapter
    }

    private fun hideTripsUi() {
        binding.tripStaticSection.visibility   = View.GONE
        binding.expList.visibility             = View.GONE
        binding.durationTripSection.visibility = View.GONE
        binding.tripStaticSection1.visibility  = View.GONE
    }

    private fun bindData() {
        val hotel = HotelDetailsFragmentArgs.fromBundle(requireArguments()).hotels

        // Collapsing toolbar title
        binding.collapsingToolbar.title = hotel.title
        binding.collapsingToolbar.setExpandedTitleColor(Color.WHITE)
        binding.collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE)

        binding.location.text   = hotel.location
        binding.ratingText.text = hotel.rate.toString()
        binding.details.text    = hotel.description
        binding.price.text      = hotel.pricePerNight.toDisplayPrice()

        if (hotel.amenities.isNotEmpty()) {
            binding.hotelStaticSection.visibility = View.VISIBLE
            binding.AmenitiesList.visibility      = View.VISIBLE
            amenityAdapter.submitList(hotel.amenities)
        }

        // Load hero image
        Glide.with(binding.image)
            .load(hotel.images.firstOrNull()?.url)
            .placeholder(R.color.card_1)
            .centerCrop()
            .into(binding.image)
    }

    private fun handleClicks() {
        val hotel = HotelDetailsFragmentArgs.fromBundle(requireArguments()).hotels

        binding.back.setNavigationOnClickListener { navigateBack() }

        binding.reserve.setOnThrottledClickListener {
            BookDialogFragment.newInstance(
                type           = "HOTEL",
                refId          = hotel.id,
                title          = hotel.title,
                location       = hotel.location,
                pricePerPerson = hotel.pricePerNight,
                imageUrl       = hotel.images.firstOrNull()?.url ?: ""
            ).show(childFragmentManager, "book_hotel")
        }
    }
}