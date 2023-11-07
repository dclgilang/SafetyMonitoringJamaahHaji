package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ChangePasswordViewModel : BaseViewModel(){

//    var responseChangePassword = MutableLiveData<BaseResponse<changePassword>>()
    var responseChangePassword = MutableLiveData<BaseResponse<Int>>()

    fun changepassword (param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t->
            viewModelJob?.cancel()
            handleApiError(t)
            dissmissLoading()
        }) {
            val response = apiServices.changepassword(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            dissmissLoading()
            responseChangePassword.postValue(response)
        }
    }


}
