package id.co.dif.smj.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.DirectionsResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class DirectionViewModel : BaseViewModel() {

    var responseDirection = MutableLiveData<DirectionsResponse>()

    fun getDirection(site: LatLng, technician: LatLng) {
        viewModelJob?.cancel()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            viewModelJob?.cancel()
        }) {
            val response = apiServices.getDirection(
                mutableMapOf(
                    "origin" to "${technician.latitude}, ${technician.longitude}",
                    "destination" to "${site.latitude}, ${site.longitude}"
                ),
                        bearerToken = "Bearer ${session?.token_access}"
            )
            responseDirection.postValue(response)
        }
    }

}


