package com.aly.travelio.ui.dashboard.transport

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.aly.travelio.R
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentTransportBinding
import com.aly.travelio.model.Transportation
import com.aly.travelio.ui.dashboard.DashBoardViewModel
import com.aly.travelio.ui.dashboard.main.BookDialogFragment
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.navigateBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportFragment : BaseFragment<FragmentTransportBinding>(FragmentTransportBinding::inflate) {

    private lateinit var adapter: TransportAdapter
    override val viewModel: DashBoardViewModel by activityViewModels()

    override fun FragmentTransportBinding.initialize() {
        initAdapter()
        handleClicks()
        handleObserver()
        // Load only on first entry; swipe-to-refresh forces reload
        if (viewModel.allTransportations.value == null) viewModel.loadAllTransportations()
    }

    private fun initAdapter() {
        adapter = TransportAdapter { transport -> onBookTransport(transport) }
        binding.transportsRecycler.apply {
            this.adapter = this@TransportFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onBookTransport(transport: Transportation) {
        BookDialogFragment.newInstance(
            type           = "TRANSPORT",
            refId          = transport.id,
            title          = "${transport.startAirportShort} → ${transport.destinationAirportShort}",
            location       = transport.startAirportFull,
            pricePerPerson = transport.pricePerPerson,
            startDate      = transport.departDate
        ).show(childFragmentManager, "book_transport")
    }

    private fun handleClicks() {
        binding.swipeLoad.setOnRefreshListener { viewModel.loadAllTransportations() }
        binding.appCompatImageButton2.setOnClickListener { navigateBack() }
    }

    private fun handleObserver() {
        viewModel.isRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (!refreshing) binding.swipeLoad.isRefreshing = false
        }

        viewModel.allTransportations.observe(viewLifecycleOwner) { result ->
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
                        binding.transportsRecycler.layoutAnimation =
                            android.view.animation.AnimationUtils.loadLayoutAnimation(
                                requireContext(), R.anim.layout_animation_slide_up
                            )
                        binding.transportsRecycler.scheduleLayoutAnimation()
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
    private fun networkErrorHandle(v: Boolean) { binding.networkError.root.visibility = if (v) View.VISIBLE else View.GONE }
    private fun dataNotFoundHandle(v: Boolean) { binding.noDataFound.visibility       = if (v) View.VISIBLE else View.GONE }
    private fun hideAll() { serverErrorHandle(false); networkErrorHandle(false); dataNotFoundHandle(false) }
}