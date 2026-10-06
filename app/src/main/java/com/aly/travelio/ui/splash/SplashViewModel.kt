package com.aly.travelio.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aly.travelio.base.BaseViewModel
import com.aly.travelio.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    app: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(app) {

    fun isUserAuthExist() = authRepository.getCurrentUser() != null
}