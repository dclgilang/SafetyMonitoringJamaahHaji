package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.EngineerWithinRadiusStatus
import id.co.dif.smj.data.SparePartByCode
import id.co.dif.smj.data.SparePartByName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class RequestReturnSparePartViewModel : BaseViewModel() {
    var selecteSparepartName: String? = null
    var selecteSparepartCode: String? = null
    var responseGetEngineerIsWithinRadius =
        MutableLiveData<BaseResponse<EngineerWithinRadiusStatus>>()
    var responseGetSparePartByArticleName = MutableLiveData<BaseResponseList<SparePartByName>>()
    var responseGetSparePartByArticleCode = MutableLiveData<BaseResponseList<SparePartByCode>>()
    var responseaddSparePart = MutableLiveData<BaseResponse<Any?>>()

    fun addSparePart(tic_id: String, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            viewModelJob?.cancel()
            val response = BaseResponse<Any?>(
                data = Any(),
                message = "",
                status = 400
            )
            responseaddSparePart.postValue(response)
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.addSparePartID(
                bearerToken = "Bearer ${session?.token_access}",
                ticketId = tic_id,
                param = param
            )
            responseaddSparePart.postValue(response)
            dissmissLoading()
        }
    }

    fun getSparePartByArticleName(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            val response = apiServices.searchSparePartByName(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            responseGetSparePartByArticleName.postValue(response)
        }
    }

    fun getSparePartByArticleCode(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            val response = apiServices.searchSparePartByCode(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            responseGetSparePartByArticleCode.postValue(response)

        }
    }
}