package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.NotificationUnreadStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {
    var periodicOfflineConnectivityIsRunning: Boolean = false
    var responseNotificationUnreadStatus = MutableLiveData<BaseResponse<NotificationUnreadStatus>>()
    var responseSiteMarker = MutableLiveData<BaseResponseList<MarkerTripleE>>()
    var mapLoading = MutableLiveData<Boolean>()

    fun getNotificationUnreadStatus() {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getNotificationUnreadStatus(
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseNotificationUnreadStatus.postValue(response)
        }
    }

    fun getListSite(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            dismissMapLoading()
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            showMapLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            responseSiteMarker.postValue(response)
            dismissMapLoading()
        }
    }

    fun dismissMapLoading() {
        mapLoading.postValue(false)
    }

    fun showMapLoading() {
        mapLoading.postValue(true)
    }


}