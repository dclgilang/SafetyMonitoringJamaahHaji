package id.co.dif.smj.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.AccountSettings
import id.co.dif.smj.data.BasicInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AccountSettingViewModel : BaseViewModel() {
    var responseAccountSetting = MutableLiveData<BaseResponse<Any?>>()
    var responseBasicInfoList = MutableLiveData<BaseResponse<BasicInfo>>()

    fun getDetailedProfile(id: Int?) {
        viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDetailProfile(
                "Bearer ${session?.token_access}",
                id = id
            )
            responseBasicInfoList.postValue(response)
        }
    }

    fun updateAccountSettings(param: AccountSettings) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            handleApiError(e)
            viewModelJob?.cancel()
            dissmissLoading()
        }) {
            showLoading()
            val response = apiServices.updateAccountSettings(
                bearerToken = "Bearer ${session?.token_access}",
                param
            )
            responseAccountSetting.postValue(response)
            dissmissLoading()
        }
    }
}