package id.co.dif.smj.presentation.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.base.DataList
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.databinding.FragmentHomeBinding
import id.co.dif.smj.presentation.dialog.PopUpProfileDialog
import id.co.dif.smj.utils.TripleEMapClusterRenderer
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.addValidItem
import id.co.dif.smj.utils.hideSoftKeyboard
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.utils.zoom
import id.co.dif.smj.viewmodel.HomeViewModel
import org.koin.core.component.KoinComponent

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE>,
    ClusterManager.OnClusterClickListener<MarkerTripleE>, KoinComponent {
    override val layoutResId = R.layout.fragment_home
    var zoomMap: () -> Unit = {}
    var map: GoogleMap
        set(value) {
            viewModel.map = value
        }
        get() = viewModel.map
    private var clusterManager: ClusterManager<MarkerTripleE>
        set(value) {
            viewModel.clusterManager = value
        }
        get() = viewModel.clusterManager


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.getListSite(null)
        setAutoCompleteSearch(preferences.savedAllSite.value ?: listOf())
        binding.imgRefresh.isVisible = false
        binding.imgRefresh.setOnClickListener {
            map.clear()
            setupClusterization()
            viewModel.markerItems.forEach {
                clusterManager.addValidItem(it)
            }
            zoomMap()
        }


        viewModel.responseSiteMarker.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.savedAllSite.value = it.data.list
                setAutoCompleteSearch(it.data.list)
            }
        }

//        viewModel.responseMapAlarm.observe(lifecycleOwner) {
//            if (it.status in StatusCode.SUCCESS) {
//                populateMapMe(it.data.list)
//            }else{
//                it.log("sdflkjsdlkfjlfjsd")
//            }
//        }

        viewModel.currentSelectedLocation.observe(lifecycleOwner){
            it?.let {
                zoomMap = {
                    map.zoom(clusterManager, it)
                }

                zoomMap()
            }
        }

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.d("TAG", "onViewBindingsdCreated: sdkads ${preferences.savedAllSite.value?.size}")
                binding.etSearch.setText("")
                populateMapSite(preferences.savedAllSite.value ?: listOf())
            }
        }
        viewModel.hasStarted = true

        binding.btnPrevious.setOnClickListener {
            viewModel.previousMarker()
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextMarker()
        }
    }

    private fun populateJamaah(markers: List<MarkerTripleE>){
        map.clear()
        viewModel.markerItems.clear()
        setupClusterization()
        clusterManager.clearItems()
        markers.forEach {location ->
            clusterManager.addValidItem(location)
            viewModel.markerItems.add(location)
        }
        viewModel.visitedLocations.clear()
        zoomMap = {
            map.zoom(clusterManager, markers)
        }
        zoomMap()


    }

    private fun populateMapSite(markers: List<MarkerTripleE>) {
        map.clear()
        viewModel.markerItems.clear()
        setupClusterization()
        clusterManager.clearItems()
        markers.forEach { location ->
            clusterManager.addValidItem(location)
            viewModel.markerItems.add(location)
        }
        viewModel.currentSelectedLocation.value = null
        viewModel.visitedLocations.clear()
        zoomMap = {
            map.zoom(clusterManager, markers)
        }
        zoomMap()
    }

    private fun populateMapMe(markerList: List<MarkerTripleE>) {
        map.clear()
        viewModel.markerItems.clear()
        setupClusterization()
        clusterManager.clearItems()
        markerList.forEachIndexed { index, location ->
            clusterManager.addValidItem(location)
            viewModel.markerItems.add(location)
        }
        clusterManager.cluster()
        viewModel.currentSelectedLocation.value = null
        viewModel.visitedLocations.clear()
        zoomMap = {
            map.zoom(clusterManager, markerList)
        }
        zoomMap()
    }

    private fun setAutoCompleteSearch(list: List<MarkerTripleE>) {
        val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
            currentActivity,
            R.layout.item_spinner_dropdown,
            list
        )

        binding.etSearch.setAdapter(adapter)
        binding.etSearch.setOnItemClickListener { _, _, position, _ ->
            hideSoftKeyboard(currentActivity)
            binding.etSearch.clearFocus()
            val site = adapter.getItem(position)
            site?.let {
                zoomMap = {
                    map.zoom(clusterManager, site) {
                        onClusterItemClick(site)
                    }
                }
                zoomMap()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 80.toDp, 0, 20.toDp)
        map.setOnCameraChangeListener{}
        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.google_map_style
                )
            )
        } catch (_: Resources.NotFoundException) {
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(-2.548926, 118.0148634), 3.6f
        )
        map.moveCamera(cameraUpdate)

        val bottomMarginInPixels = 200
        map.setPadding(0, 0, 0, bottomMarginInPixels)

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        populateMapSite(preferences.savedAllSite.value ?: listOf())
        val mapMe = preferences.lastMapAlarm.value ?: listOf()
        val response = BaseResponseList<MarkerTripleE>(
            data = DataList(list = mapMe, limit = mapMe.size, page = 1, total = mapMe.size),
            message = "",
            status = 200
        )
        viewModel.responseMapAlarm.value = response
        viewModel.getMapAlarm()
    }

    private fun setupClusterization() = fragmentContext?.let { context ->
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener { clusterManager.cluster() }
        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
    }

    private fun clearClusterMarkers() {
        clusterManager.clearItems()
    }

    override fun onClusterItemClick(marker: MarkerTripleE): Boolean {
        viewModel.visitedLocations.apply {
            clear()
            add(marker)
            viewModel.currentSelectedLocation.value = marker
        }
        viewModel.visitedLocations.log("sdfdsfdsfdsf"){it.size}
        binding.etSearch.clearFocus()
        map.zoom(clusterManager, marker) {
            when (LocationType.fromString(marker.type)) {
                LocationType.Technician -> {
//                    val technician = MarkerTripleE(id = session?.id?.toInt())
//                    PopUpProfileDialog.newInstance(listOf(technician)).show(
//                        childFragmentManager,
//                        PopUpProfileDialog::class.java.name
//                    )
                    MarkerPopupDialog.newInstance(marker).show(
                        childFragmentManager,
                        MarkerPopupDialog::class.java.name
                    )
                }

                LocationType.TtMapAll -> TicketListPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    TicketListPopupDialog::class.java.name
                )

                LocationType.TtSiteAll -> {
                    MarkerPopupDialog.newInstance(marker).show(
                        childFragmentManager,
                        MarkerPopupDialog::class.java.name
                    )
                }

                LocationType.Site -> MarkerPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )
                LocationType.Kapal -> MarkerPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )

                LocationType.Apartement -> MarkerPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )

                LocationType.Excavator -> MarkerPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )

                LocationType.Generator -> MarkerPopupDialog.newInstance(marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )

                LocationType.Note -> Unit
                else -> {}
            }
        }
        return false

    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.etSearch.setText("")
        viewModel.getMapAlarm()
    }
//
//    fun resetViewport(): Boolean  {
//        val isReady = viewModel.responseSiteMarker.value != null
//        if (isReady) {
//            viewModel.responseSiteMarker.let { it.value = it.value }
//        }
//        isReady.log("jndudunduwuewewdeu")
//        return isReady
//    }

    fun resetViewport(): Boolean  {
        val isReady = !viewModel.markerItems.isNullOrEmpty()
        if (isReady) {
            map.zoom(clusterManager, viewModel.markerItems)
        }
        isReady.log("jndudunduwuewewdeu")
        return isReady
    }

    override fun onClusterClick(cluster: Cluster<MarkerTripleE>): Boolean {
        hideSoftKeyboard(requireActivity())
        binding.etSearch.clearFocus()
        map.zoom(clusterManager, cluster.items.toList())
        return true
    }

}
