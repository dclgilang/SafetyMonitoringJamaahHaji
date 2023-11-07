package id.co.dif.smj.presentation.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.NotificationUnreadStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EnergyMonitorViewModel: BaseViewModel() {

    var responseNotificationUnreadStatus = MutableLiveData<BaseResponse<NotificationUnreadStatus>>()

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


}
