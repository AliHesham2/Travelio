package com.aly.travelio.ui.orders

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aly.travelio.base.BaseActivity
import com.aly.travelio.base.BaseViewModel
import com.aly.travelio.model.Reservation
import com.aly.travelio.repository.AuthRepository
import com.aly.travelio.repository.ReservationRepository
import com.aly.travelio.util.ResultCallBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChosenOrder(val type: String?, val id: String?)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    app: Application,
    private val reservationRepository: ReservationRepository,
    private val authRepository: AuthRepository
) : BaseViewModel(app) {

    private val _currentOrders = MutableLiveData<ResultCallBack<List<Reservation>>>()
    val currentOrders: LiveData<ResultCallBack<List<Reservation>>> = _currentOrders

    private val _expiredOrders = MutableLiveData<ResultCallBack<List<Reservation>>>()
    val expiredOrders: LiveData<ResultCallBack<List<Reservation>>> = _expiredOrders

    private val _navigate = MutableLiveData<ChosenOrder?>()
    val navigate: LiveData<ChosenOrder?> = _navigate

    private val _deleteDone = MutableLiveData<ResultCallBack<Unit>?>()
    val deleteDone: LiveData<ResultCallBack<Unit>?> = _deleteDone

    fun getOrders(activity: BaseActivity? = null) {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        if (_currentOrders.value == null || _currentOrders.value is ResultCallBack.Error) {
            loading.value = true
        }
        viewModelScope.launch {
            when (val result = reservationRepository.getReservations(uid)) {
                is ResultCallBack.Success -> splitOrders(result.data)
                is ResultCallBack.Error -> {
                    _currentOrders.value = result
                    _expiredOrders.value = result
                }
            }
            loading.value = false
        }
    }

    private fun splitOrders(reservations: List<Reservation>) {
        val current = mutableListOf<Reservation>()
        val expired = mutableListOf<Reservation>()
        val now = System.currentTimeMillis()

        for (res in reservations) {
            // Rough proxy: if the reservation start date is in the past, it's expired
            if (res.startDate in 1..<now) {
                expired.add(res)
            } else {
                current.add(res)
            }
        }
        _currentOrders.value = ResultCallBack.Success(current)
        _expiredOrders.value = ResultCallBack.Success(expired)
    }

    fun setNavigate(id: String?, type: String?) {
        _navigate.value = ChosenOrder(type, id)
    }

    fun removeOrder(id: String?, activity: BaseActivity? = null) {
        // Mock remove order since there's no remove API in ReservationRepository yet
        loading.value = true
        // Mocking success
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _deleteDone.value = ResultCallBack.Success(Unit)
            loading.value = false
        }
    }

    fun deleteFromLiveData(id: String?) {
        if (_currentOrders.value is ResultCallBack.Success) {
            val list = (_currentOrders.value as ResultCallBack.Success).data
            _currentOrders.value = ResultCallBack.Success(list.filter { it.id != id })
        }
        if (_expiredOrders.value is ResultCallBack.Success) {
            val list = (_expiredOrders.value as ResultCallBack.Success).data
            _expiredOrders.value = ResultCallBack.Success(list.filter { it.id != id })
        }
    }

    fun resetNavigate() { _navigate.value = null }
    fun resetDeletion() { _deleteDone.value = null }
}