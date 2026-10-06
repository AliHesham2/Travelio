package com.aly.travelio.ui.orders

import android.view.View
import androidx.fragment.app.activityViewModels
import com.aly.travelio.base.BaseFragment
import com.aly.travelio.databinding.FragmentExpiredBinding
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpiredFragment : BaseFragment<FragmentExpiredBinding>(FragmentExpiredBinding::inflate) {

    private lateinit var adapter: ReservationAdapter
    override val viewModel: OrdersViewModel by activityViewModels()

    override fun FragmentExpiredBinding.initialize() {
        handleAdapter()
        handleObservers()
    }

    private fun handleAdapter() {
        adapter = ReservationAdapter(
            onClick = { /* expired — view only */ }
        )
        binding.orderRecycler.adapter = adapter
    }

    private fun handleObservers() {
        viewModel.expiredOrders.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            hideAll()
            when (it) {
                is ResultCallBack.Success -> {
                    if (it.data.isEmpty()) dataNotFoundHandle(true) else hideAll()
                    adapter.submitList(it.data)
                }
                is ResultCallBack.Error -> {
                    viewModel.showErrorMessage.value = it.message
                    when (it.type) {
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

    companion object { fun newInstance() = ExpiredFragment() }
}