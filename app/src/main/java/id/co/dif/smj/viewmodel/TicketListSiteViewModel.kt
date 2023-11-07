package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.SiteHistory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class TicketListSiteViewModel: BaseViewModel () {

    var responseListSite = MutableLiveData<BaseResponse<SiteHistory>>()

    fun getListSite(id: Int? = null) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getListSite(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseListSite.postValue(response)
        }
    }
}


