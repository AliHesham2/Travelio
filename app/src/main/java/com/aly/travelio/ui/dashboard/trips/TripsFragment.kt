package com.aly.travelio.ui.dashboard.trips

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentTripsBinding
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateBack

import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripsFragment : BaseFragment<FragmentTripsBinding>(FragmentTripsBinding::inflate) {

    private lateinit var adapter: TripsAdapter
    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentTripsBinding.initialize() {
        postponeEnterTransition()
        initAdapter()
        handleClicks()
        handleObserver()
        // Load only on first entry; swipe-to-refresh forces reload
        if (viewModel.allTrips.value == null) viewModel.loadAllTrips()

        binding.tripsRecycler.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }
    }

    private fun initAdapter() {
        adapter = TripsAdapter { trip, imageView ->
            val extras = androidx.navigation.fragment.FragmentNavigatorExtras(
                imageView to "transition_image"
            )
            findNavController().navigate(
                TripsFragmentDirections.actionTripsFragmentToTripsDetailsFragment(trip),
                extras
            )
        }
        binding.tripsRecycler.apply {
            this.adapter = this@TripsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleClicks() {
        binding.swipeLoad.setOnRefreshListener { viewModel.loadAllTrips() }
        binding.appCompatImageButton2.setOnClickListener { navigateBack() }
    }

    private fun handleObserver() {
        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (!refreshing) binding.swipeLoad.isRefreshing = false
        }

        viewModel.allTrips.observe(viewLifecycleOwner) { result ->
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
                        binding.tripsRecycler.layoutAnimation =
                            android.view.animation.AnimationUtils.loadLayoutAnimation(
                                requireContext(), R.anim.layout_animation_slide_up
                            )
                        binding.tripsRecycler.scheduleLayoutAnimation()
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

    private fun serverErrorHandle(v: Boolean) { binding.serverError.root.visibility = if (v) View.VISIBLE else View.GONE }
    private fun networkErrorHandle(v: Boolean) { binding.networkError.root.visibility = if (v) View.VISIBLE else View.GONE }
    private fun dataNotFoundHandle(v: Boolean) { binding.noDataFound.visibility = if (v) View.VISIBLE else View.GONE }
    private fun hideAll() { serverErrorHandle(false); networkErrorHandle(false); dataNotFoundHandle(false) }
}