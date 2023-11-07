package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SkillViewModel: BaseViewModel() {

    var responseSkillList = MutableLiveData<BaseResponse<BasicInfo>>()

    fun skilllist(id: Int?) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseSkillList.postValue(response)
            dissmissLoading()
        }
    }

}
