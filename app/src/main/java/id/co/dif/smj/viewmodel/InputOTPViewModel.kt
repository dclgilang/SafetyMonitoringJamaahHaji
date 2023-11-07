package id.co.dif.smj.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import id.co.dif.smj.data.Session
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class InputOTPViewModel : BaseViewModel() {

    var responseOtp = MutableLiveData<BaseResponse<Session>>()
    var responseDetailedProfile = MutableLiveData<BaseResponse<BasicInfo>>()

    fun postOtp(otp: String, latitude: Double? = null, longtitude: Double? = null, id: Int) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            showLoading()
            val token = task.result
            viewModelJob?.cancel()
            viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                viewModelJob?.cancel()
                dissmissLoading()
                throwable.printStackTrace()
                handleApiError(throwable)
            }) {
                showLoading()
                Log.d("TAG", "postOtp session: ${preferences.session.value}")
                val response = apiServices.postOtp(
                    id,
                    mutableMapOf(
                        "otp" to otp,
                        "token_firebase" to token,
                        "latitude" to latitude,
                        "longtitude" to longtitude,
                    )
                )
                Log.d("TAG", "postOtp session done otp: $response")
                dissmissLoading()
                responseOtp.postValue(response)
            }
        }
    }

    fun getDetailedProfile(id: Int?) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            handleApiError(e)
            e.printStackTrace()
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