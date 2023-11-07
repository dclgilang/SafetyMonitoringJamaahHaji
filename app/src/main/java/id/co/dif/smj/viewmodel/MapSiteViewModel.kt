package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import id.co.dif.smj.data.SiteData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class MapSiteViewModel : BaseViewModel()  {

    var responseDetailedProfile = MutableLiveData<BaseResponse<BasicInfo>>()
    var responseaGetSiteByid = MutableLiveData<BaseResponse<SiteData>>()

    fun getSiteById (id : Int? = null) {
        showLoading()
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getSiteById(
                id = id,
                bearerToken = "Bearer ${session?.token_access}",
            )
            dissmissLoading()
            responseaGetSiteByid.postValue(response)
        }
    }

    fun getDetailProfile(id: Int?) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDetailProfile(
                bearerToken = "Bearer ${session?.token_access}",
                id = id
            )
            dissmissLoading()
            responseDetailedProfile.postValue(response)
        }
    }

}
