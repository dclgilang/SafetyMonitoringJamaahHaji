package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.ActiveUser
import id.co.dif.smj.data.BasicInfo
import id.co.dif.smj.data.CompletedProfile
import id.co.dif.smj.data.PlainValueLabel
import id.co.dif.smj.data.TicketHandling
import id.co.dif.smj.data.TicketInfo
import id.co.dif.smj.data.TicketQuality
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MyDashboardViewModel : BaseViewModel() {
    var responseTicketInfo = MutableLiveData<BaseResponse<TicketInfo>>()
    var responseCompletedProfile = MutableLiveData<BaseResponse<CompletedProfile>>()
    var savedBasicInfo = MutableLiveData<BasicInfo>()
    var responseActiveUsers = MutableLiveData<BaseResponseList<ActiveUser>>()
    var responseTicketQuality = MutableLiveData<BaseResponse<TicketQuality>>()
    var responseTicketHandling = MutableLiveData<BaseResponse<TicketHandling>>()
    var responseUserActivityLog = MutableLiveData<BaseResponseList<PlainValueLabel>>()
    fun getFirst5ActiveUsers() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getActiveUsers(
                bearerToken = "Bearer ${session?.token_access}",
                limit = 5,
                page = 1
            )
            responseActiveUsers.postValue(
                response
            )
        }
    }

    fun getCompletedProfile(id: Int? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getCompletedProfile(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            responseCompletedProfile.postValue(
                response
            )
        }
    }


    fun getUserActivityLog(id: Int? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getUserActivityLog(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseUserActivityLog.postValue(
                response
            )
        }
    }

    fun getTicketInfo() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getTicketInfo(
                "Bearer ${session?.token_access}"
            )
            responseTicketInfo.postValue(response)
        }
    }

    fun getSavedBasicInfo() {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            savedBasicInfo.postValue(preferences.myDetailProfile.value)
        }
    }

    fun cancelJob() {
        viewModelJob?.cancel()
    }


    fun getTicketQuality(id: Int? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getTicketQuality(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseTicketQuality.postValue(response)
        }
    }

    fun getTicketHandling(id: Int? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
        }) {
            val response = apiServices.getTicketHandling(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseTicketHandling.postValue(response)
        }
    }

}