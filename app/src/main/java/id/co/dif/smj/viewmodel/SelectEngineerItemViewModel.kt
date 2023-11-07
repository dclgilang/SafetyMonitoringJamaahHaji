package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SelectEngineerItemViewModel : BaseViewModel() {
    var responsePingEngineerToSendTheirLocation = MutableLiveData<BaseResponse<Any>>()

    fun pingEngineerToSendTheirLocation(engineerId: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.pingEngineerToSendTheirLocation(
                "Bearer ${session?.token_access}",
                userId = engineerId
            )
            responsePingEngineerToSendTheirLocation.postValue(response)
        }
    }
}