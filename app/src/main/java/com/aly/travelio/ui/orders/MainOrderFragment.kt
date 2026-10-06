package com.aly.travelio.ui.orders

import androidx.fragment.app.activityViewModels
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentMainOrderBinding
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateToHotel
import com.aly.travelio.util.navigateToTrips
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainOrderFragment : BaseFragment<FragmentMainOrderBinding>(FragmentMainOrderBinding::inflate) {

    override val viewModel: OrdersViewModel by activityViewModels()
    private val mainViewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentMainOrderBinding.initialize() {
        viewModel.getOrders()
        setupTabAdapter()
        handleObservers()
    }

    private fun setupTabAdapter() {
        val adapter = OrderPager(requireActivity())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.RECENT) else getString(R.string.EXPIRED)
        }.attach()
    }

    private fun handleObservers() {
        viewModel.navigate.observe(viewLifecycleOwner) { chosen ->
            if (chosen == null) return@observe
            when (chosen.type?.uppercase()) {
                "HOTEL" -> {
                    val hotels = mainViewModel.allHotels.value
                    val data = if (hotels is ResultCallBack.Success)
                        hotels.data.find { it.id == chosen.id } else null
                    if (data != null) navigateToHotel(data)
                    else viewModel.showToast.value = getString(R.string.WRONG)
                }

                "TRIP" -> {
                    val trips = mainViewModel.allTrips.value
                    val data = if (trips is ResultCallBack.Success)
                        trips.data.find { it.id == chosen.id } else null
                    if (data != null) navigateToTrips(data)
                    else viewModel.showToast.value = getString(R.string.WRONG)
                }
            }
        }

    }

    override fun onDestroyView() {
        viewModel.resetNavigate()
        super.onDestroyView()
    }
}