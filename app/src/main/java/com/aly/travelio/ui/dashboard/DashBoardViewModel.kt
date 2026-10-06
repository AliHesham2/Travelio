package com.aly.travelio.ui.dashboard

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aly.travelio.base.BaseViewModel
import com.aly.travelio.datastore.AppDataStore
import com.aly.travelio.model.Hotel
import com.aly.travelio.model.Reservation
import com.aly.travelio.model.Transportation
import com.aly.travelio.model.Trip
import com.aly.travelio.model.User
import com.aly.travelio.repository.AuthRepository
import com.aly.travelio.repository.HotelRepository
import com.aly.travelio.repository.ReservationRepository
import com.aly.travelio.repository.TransportRepository
import com.aly.travelio.repository.TripRepository
import com.aly.travelio.repository.UserRepository
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    app: Application,
    private val hotelRepository: HotelRepository,
    private val tripRepository: TripRepository,
    private val transportRepository: TransportRepository,
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val appDataStore: AppDataStore
) : BaseViewModel(app) {

    // ── Dashboard scroll position (survives view recreation) ───────────────────
    var dashboardScrollY: Int = 0

    // ── Featured (Dashboard) ──────────────────────────────────────────────────
    private val _featuredHotels = MutableLiveData<ResultCallBack<List<Hotel>>?>()
    val featuredHotels: LiveData<ResultCallBack<List<Hotel>>?> = _featuredHotels

    private val _featuredTrips = MutableLiveData<ResultCallBack<List<Trip>>?>()
    val featuredTrips: LiveData<ResultCallBack<List<Trip>>?> = _featuredTrips

    private val _featuredTransportations = MutableLiveData<ResultCallBack<List<Transportation>>?>()
    val featuredTransportations: LiveData<ResultCallBack<List<Transportation>>?> = _featuredTransportations

    // ── Full Lists ────────────────────────────────────────────────────────────
    private val _allHotels = MutableLiveData<ResultCallBack<List<Hotel>>?>()
    val allHotels: LiveData<ResultCallBack<List<Hotel>>?> = _allHotels

    private val _allTrips = MutableLiveData<ResultCallBack<List<Trip>>?>()
    val allTrips: LiveData<ResultCallBack<List<Trip>>?> = _allTrips

    private val _allTransportations = MutableLiveData<ResultCallBack<List<Transportation>>?>()
    val allTransportations: LiveData<ResultCallBack<List<Transportation>>?> = _allTransportations

    // ── User ─────────────────────────────────────────────────────────────────
    private val _currentUser = MutableLiveData<ResultCallBack<User>?>()
    val currentUser: LiveData<ResultCallBack<User>?> = _currentUser

    // ── Update name ───────────────────────────────────────────────────────────
    private val _updateNameResult = MutableLiveData<ResultCallBack<Unit>?>()
    val updateNameResult: LiveData<ResultCallBack<Unit>?> = _updateNameResult

    // ── Refresh control ───────────────────────────────────────────────────────
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    // ── Reservation ──────────────────────────────────────────────────────────
    private val _reservationResult = MutableLiveData<ResultCallBack<Unit>?>()
    val reservationResult: LiveData<ResultCallBack<Unit>?> = _reservationResult

    // ── Helpers ───────────────────────────────────────────────────────────────
    private val currentFirebaseUser get() = authRepository.getCurrentUser()
    val isUserLoggedIn: Boolean get() = currentFirebaseUser != null
    fun getProvider(): String = currentFirebaseUser?.providerData
        ?.firstOrNull { it.providerId != "firebase" }?.providerId ?: "password"
    fun getCurrentUserId(): String = currentFirebaseUser?.uid ?: ""

    // ── Init ──────────────────────────────────────────────────────────────────
    init {
        loadDashboard()
        loadCurrentUser()
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────
    fun loadDashboard() {
        viewModelScope.launch {
            // Shimmer handles the loading skeleton — no loading spinner needed here
            val hotelsDeferred = async { hotelRepository.getFeaturedHotels() }
            val tripsDeferred  = async { tripRepository.getFeaturedTrips() }
            val transDeferred  = async { transportRepository.getFeaturedTransportations() }

            delay(3_500L) // minimum shimmer display

            _featuredHotels.value          = hotelsDeferred.await()
            _featuredTrips.value           = tripsDeferred.await()
            _featuredTransportations.value = transDeferred.await()
        }
    }

    // ── Full list loaders (one-shot) ──────────────────────────────────────────
    fun loadAllHotels() {
        viewModelScope.launch {
            loading.value       = true
            _isRefreshing.value = true
            _allHotels.value    = hotelRepository.getAllHotels()
            _isRefreshing.value = false
            loading.value       = false
        }
    }

    fun loadAllTrips() {
        viewModelScope.launch {
            loading.value       = true
            _isRefreshing.value = true
            _allTrips.value     = tripRepository.getAllTrips()
            _isRefreshing.value = false
            loading.value       = false
        }
    }

    fun loadAllTransportations() {
        viewModelScope.launch {
            loading.value             = true
            _isRefreshing.value       = true
            _allTransportations.value = transportRepository.getAllTransportations()
            _isRefreshing.value       = false
            loading.value             = false
        }
    }

    // ── User ─────────────────────────────────────────────────────────────────
    fun loadCurrentUser() {
        val uid = getCurrentUserId()
        if (uid.isEmpty()) return
        viewModelScope.launch {
            _currentUser.value = userRepository.getUserProfile(uid)
        }
    }

    fun updateUserName(newName: String) {
        val uid = getCurrentUserId()
        if (uid.isEmpty()) {
            _updateNameResult.value = ResultCallBack.Error(NetworkError.SERVER, "Not authenticated")
            return
        }
        viewModelScope.launch {
            loading.value        = true
            _updateNameResult.value = userRepository.updateUserName(uid, newName)
            if (_updateNameResult.value is ResultCallBack.Success) {
                // Refresh cached user so the drawer avatar name updates too
                _currentUser.value = userRepository.getUserProfile(uid)
            }
            loading.value        = false
        }
    }

    fun resetUpdateNameResult() { _updateNameResult.value = null }

    // ── Reservation ───────────────────────────────────────────────────────────
    fun addReservation(
        type: String,
        refId: String,
        title: String,
        imageUrl: String,
        location: String,
        pricePerPerson: Double,
        personsCount: Int,
        startDate: Long = 0L,
        endDate: Long = 0L
    ) {
        val uid = getCurrentUserId()
        if (uid.isEmpty()) {
            _reservationResult.value = ResultCallBack.Error(NetworkError.SERVER, "User not authenticated")
            return
        }
        viewModelScope.launch {
            loading.value = true
            val reservation = Reservation(
                id            = UUID.randomUUID().toString(),
                type          = type,
                refId         = refId,
                title         = title,
                image         = imageUrl,
                location      = location,
                pricePerPerson = pricePerPerson,
                totalPrice    = pricePerPerson * personsCount,
                personsCount  = personsCount,
                startDate     = startDate,
                endDate       = endDate,
                createdAt     = System.currentTimeMillis()
            )
            _reservationResult.value = reservationRepository.addReservation(uid, reservation)
            loading.value = false
        }
    }

    fun resetReservationResult() { _reservationResult.value = null }

    // ── Sign out ──────────────────────────────────────────────────────────────
    fun signOut(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try   { authRepository.logout(); onComplete(true) }
            catch (e: Exception) { onComplete(false) }
        }
    }

}