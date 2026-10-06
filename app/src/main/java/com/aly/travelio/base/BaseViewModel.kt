package com.aly.travelio.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aly.travelio.util.NavigationCommands
import com.aly.travelio.util.SingleLiveEvent

open class BaseViewModel(app:Application) : AndroidViewModel(app) {
    private val _getCity = MutableLiveData<HashMap<String,String>>()
    val getCity : LiveData<HashMap<String, String>> = _getCity

    private val _getMeals = MutableLiveData<HashMap<String,String>>()
    val getMeals : LiveData<HashMap<String,String>> = _getMeals

    private val _transportType= MutableLiveData<List<String>>()
    val transportType : LiveData<List<String>> = _transportType


    val showErrorMessage: SingleLiveEvent<String?> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String?> = SingleLiveEvent()
    val navigationCommand: SingleLiveEvent<NavigationCommands?> = SingleLiveEvent()
    val loading: SingleLiveEvent<Boolean> = SingleLiveEvent()

    init {
        getCities()
        getMeals()
        getTransportType()
    }

    private fun getCities(){
        _getCity.value =  hashMapOf(
            "Cairo" to "CAI", "6th of October City" to "OCT", "Alexandria" to "ALX", "Borg El Arab" to "BEA", "Giza" to "GIZ", "Sheikh Zayed" to "SZD", "Hurghada" to "HRG", "Marsa Alam" to "MSA", "Safaga" to "SFG", "El Qoseir" to "ELQ",
            "Aswan" to "ASW", "Abu Simbel" to "ABS", "Edfu" to "EDF", "Kom Ombo" to "KOM", "Luxor" to "LUX", "Esna" to "ESN", "Asyut" to "ASY", "Dayrout" to "DYR", "Manfalut" to "MNF", "Minya" to "MIN", "Mallawi" to "MLW", "Beni Mazar" to "BMZ",
            "Sohag" to "SOH", "Akhmim" to "AKH", "Girga" to "GIR", "Qena" to "QEN", "Dendera" to "DEN", "Beni Suef" to "BSF", "Al Fashn" to "ALF", "Faiyum" to "FYU", "Itsa" to "ITS", "Damietta" to "DAM", "Ras El Bar" to "REB", "Mansoura" to "MAN"
        )
    }

    private fun getMeals(){
        _getMeals.value = hashMapOf("Breakfast Only" to "Breakfast Only", "Half Board" to "Half Board", "Full Board" to "Full Board", "All Inclusive" to "All Inclusive")
    }
    private fun getTransportType(){
        _transportType.value = listOf("Bus","Boat","Plane")
    }

    fun resetErrorMsg(){ showErrorMessage.value = null }
    fun resetToast(){ showToast.value = null}
    fun resetNavCommand(){ navigationCommand.value = null }
    fun resetLoading(){ loading.value = false }

}