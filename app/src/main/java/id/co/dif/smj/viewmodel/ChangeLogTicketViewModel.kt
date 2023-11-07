package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TicketDetails
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ChangeLogTicketViewModel: BaseViewModel() {

    var responseDetilTicket = MutableLiveData<BaseResponse<TicketDetails>>()

    fun getListTroubleTicket (id : String? = null) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getTicketDetails(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            responseDetilTicket.postValue(response)
        }
    }




}
