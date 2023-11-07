package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class BasicInfoViewModel : BaseViewModel() {

    var responseDetailedProfile = MutableLiveData<BaseResponse<BasicInfo>>()


    fun getDetailProfile(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseDetailedProfile.postValue(response)
            dissmissLoading()
        }
    }


}


