package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Work
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class WorkViewModel : BaseViewModel() {

    var responseWorkList = MutableLiveData<BaseResponseList<Work>>()
    var responseDelete = MutableLiveData<BaseResponse<Work>>()

    fun getWorkList(id: Int?) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getWorkList(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseWorkList.postValue(response)
            dissmissLoading()
        }
    }

    fun deleteWork(it: Work) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.deleteWork(
                "Bearer ${session?.token_access}",
                it.id
            )
            responseDelete.postValue(response)
        }
    }

}