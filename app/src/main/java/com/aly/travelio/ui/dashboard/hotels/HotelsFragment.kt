package com.aly.travelio.ui.dashboard.hotels

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentHotelsBinding
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateBack

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotelsFragment : BaseFragment<FragmentHotelsBinding>(FragmentHotelsBinding::inflate) {

    private lateinit var adapter: HotelAdapter
    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentHotelsBinding.initialize() {
        postponeEnterTransition()
        initAdapter()
        handleClicks()
        handleObserver()
        // Load only on first entry; swipe-to-refresh will re-load
        if (viewModel.allHotels.value == null) viewModel.loadAllHotels()

        binding.hotelsRecycler.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun initAdapter() {
        adapter = HotelAdapter { hotel, imageView ->
            val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
                imageView to "transition_image"
            )
            findNavController().navigate(
                HotelsFragmentDirections.actionHotelsFragmentToHotelDetailsFragment(hotel),
                extras
            )
        }
        binding.hotelsRecycler.apply {
            this.adapter = this@HotelsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleClicks() {
        binding.swipeLoad.setOnRefreshListener { viewModel.loadAllHotels() }
        binding.appCompatImageButton2.setOnClickListener { navigateBack() }
    }

    private fun handleObserver() {
        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (!refreshing) binding.swipeLoad.isRefreshing = false
        }

        viewModel.allHotels.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            hideAll()
            when (result) {
                is ResultCallBack.Success -> {
                    binding.swipeLoad.isEnabled = true
                    val list = result.data
                    if (list.isEmpty()) {
                        dataNotFoundHandle(true)
                    } else {
                        adapter.submitList(list)
                        // Staggered slide-up animation
                        binding.hotelsRecycler.layoutAnimation =
                            android.view.animation.AnimationUtils.loadLayoutAnimation(
                                requireContext(), R.anim.layout_animation_slide_up
                            )
                        binding.hotelsRecycler.scheduleLayoutAnimation()
                    }
                }
                is ResultCallBack.Error -> {
                    binding.swipeLoad.isEnabled = false
                    viewModel.showErrorMessage.value = result.message
                    when (result.type) {
                        NetworkError.NETWORK -> networkErrorHandle(true)
                        NetworkError.SERVER  -> serverErrorHandle(true)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun serverErrorHandle(v: Boolean)  { binding.serverError.root.visibility  = if (v) View.VISIBLE else View.GONE }
    private fun networkErrorHandle(v: Boolean)  { binding.networkError.root.visibility  = if (v) View.VISIBLE else View.GONE }
    private fun dataNotFoundHandle(v: Boolean)  { binding.noDataFound.visibility         = if (v) View.VISIBLE else View.GONE }
    private fun hideAll() { serverErrorHandle(false); networkErrorHandle(false); dataNotFoundHandle(false) }
}