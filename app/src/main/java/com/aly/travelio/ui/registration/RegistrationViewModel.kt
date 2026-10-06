package com.aly.travelio.ui.registration

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aly.travelio.base.BaseViewModel
import com.aly.travelio.repository.AuthRepository
import com.aly.travelio.util.ResultCallBack
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    app: Application,
    private val authRepository: AuthRepository
) : BaseViewModel(app) {
    private val _signIn = MutableLiveData<ResultCallBack<FirebaseUser>?>()
    val signIn : LiveData<ResultCallBack<FirebaseUser>?> = _signIn


    private val _forgetPassword = MutableLiveData<ResultCallBack<Any>?>()
    val forgetPassword : LiveData<ResultCallBack<Any>?> = _forgetPassword

    fun googleSign(context: Context){
        loading.value = true
        viewModelScope.launch {
            _signIn.value = authRepository.signInWithGoogle(context)
            loading.value = false
        }
    }

    fun signIn(email:String, password:String){
        loading.value = true
        viewModelScope.launch {
            _signIn.value = authRepository.login(email, password)
            loading.value = false
        }
    }

    fun signUp(email:String, password:String, name:String){
        loading.value = true
        viewModelScope.launch {
            _signIn.value = authRepository.register(email, password, name)
            loading.value = false
        }
    }

    fun forgotPassword(email:String){
        loading.value = true
        viewModelScope.launch {
            _forgetPassword.value = authRepository.resetPassword(email)
            loading.value = false
        }
    }

    fun resetSignIn(){ _signIn.value = null }
    fun resetForgetPassword(){ _forgetPassword.value = null }
}