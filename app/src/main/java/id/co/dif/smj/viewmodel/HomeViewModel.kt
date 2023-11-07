package id.co.dif.smj.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.utils.closestDistanceLoc
import id.co.dif.smj.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 04:00
 *
 */
class HomeViewModel : BaseViewModel() {
    var responseSiteMarker = MutableLiveData<BaseResponseList<MarkerTripleE>>()
    var responseListMapSiteMarker = MutableLiveData<BaseResponseList<MarkerTripleE>>()
    var responseMapAlarm = MutableLiveData<BaseResponseList<MarkerTripleE>>()
    var responseBasicInfoList = MutableLiveData<BaseResponse<BasicInfo>>()
    var mapLoading = MutableLiveData<Boolean>()
    var markerItems = mutableListOf<MarkerTripleE>()
    var currentSelectedLocation = MutableLiveData<MarkerTripleE>()
    val visitedLocations = mutableListOf<MarkerTripleE>()
    var responseNearestJamaah = MutableLiveData<BaseResponseList<MarkerTripleE>>()
    var hasStarted = false
    lateinit var clusterManager: ClusterManager<MarkerTripleE>
    lateinit var map: GoogleMap

    private val sharedPref: SharedPreferences = context.getSharedPreferences("YourPreferences", Context.MODE_PRIVATE)



    @RequiresApi(Build.VERSION_CODES.N)
    fun nextMarker() {
        val visitedLocId = visitedLocations.map { it.getId()}
        visitedLocId.joinToString { it.toString() }.log("adsasdsad")
        currentSelectedLocation.value?.let { loc ->
            val markerLoc = markerItems.toMutableList()
            val markerLocId = markerLoc.map { it.getId() }
            val visitedLocId = visitedLocations.map { it.getId() }
            markerLocId.joinToString { it.toString() }.log("adsasdfdfddsad")
            visitedLocId.forEach {vId ->
               markerLoc.removeIf { vId == it.getId() }
            }

            val closest = loc.closestDistanceLoc(markerLoc)
            visitedLocations.size.log("asdsads")
            visitedLocations.add(closest)
            currentSelectedLocation.value = closest
        } ?: run {
            currentSelectedLocation.value = markerItems.firstOrNull()
        }
    }

    fun previousMarker() {
        currentSelectedLocation.value = if (visitedLocations.isEmpty()) {
            markerItems.lastOrNull()
        } else {
            visitedLocations.removeLast()
        }
    }

    fun getNearestJamaah(idSite: Int? = 14277) {
        showLoading()
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            e.printStackTrace()
            dissmissLoading()
            viewModelJob?.cancel()
        }) {
            showLoading()
            val response = apiServices.getNearestTechnician(
                "Bearer ${session?.token_access}",
                idSite = idSite
            )
            responseNearestJamaah.postValue(response)
            dissmissLoading()
        }
    }


    fun getListSite(idSite: Int? = 14277) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            dismissMapLoading()
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            showMapLoading()
            val response = apiServices.getNearestTechnician(
                bearerToken = "Bearer ${session?.token_access}",
                idSite = idSite,
            )
            println(response)
            responseSiteMarker.postValue(response)
            dismissMapLoading()
        }
    }

    fun getListMapSite(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            dismissMapLoading()
            viewModelJob?.cancel()
            throwable.printStackTrace()
            handleApiError(throwable)
        }) {
            showMapLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            responseListMapSiteMarker.postValue(response)
            dismissMapLoading()
        }
    }


    fun showMapLoading() {
        mapLoading.postValue(true)
    }

    fun dismissMapLoading() {
        mapLoading.postValue(false)
    }

    fun getMapAlarm(search: String? = null) {
        viewModelJob = viewModelScope.launch(CoroutineExceptionHandler { _, t ->
            handleApiError(t)
            t.printStackTrace()
            dismissMapLoading()
            viewModelJob?.cancel()
        }) {
            showMapLoading()
            val response = apiServices.site(
                bearerToken = "Bearer ${session?.token_access}",
                search = search,
            )
            println(response)
            preferences.lastMapAlarm.value = response.data.list
            responseMapAlarm.postValue(response)
            dismissMapLoading()
        }
    }


}