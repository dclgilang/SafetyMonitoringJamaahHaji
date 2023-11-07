package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Work
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AddWorkPlaceViewModel : BaseViewModel() {

    var responseworkadd = MutableLiveData<BaseResponse<Work>>()

    fun putworkadd (param: MutableMap<String, Any?>) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.putworkadd(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            responseworkadd.postValue(response)
//            println(response?.status == 200)
//            if (response?.status == 200) {
//                var experienceList = apiServices.putworklist(bearerToken = "Bearer ${session?.token_access}")
//                responseWorkList.postValue(experienceList)
            }
        }
    }

