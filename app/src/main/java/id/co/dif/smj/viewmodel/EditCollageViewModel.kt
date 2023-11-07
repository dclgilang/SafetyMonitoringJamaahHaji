package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Education
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class EditCollageViewModel : BaseViewModel() {

    var responseeducationedit = MutableLiveData<BaseResponse<Education>>()


    fun editeducation (id : String, param: MutableMap<String, Any?>) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            val response = apiServices.editeducation(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
                param = param
            )
            responseeducationedit.postValue(response)
        }
    }


}