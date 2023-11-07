package id.co.dif.smj.presentation.fragment

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.PolyUtil
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.SiteDetails
import id.co.dif.smj.data.TicketDetails
import id.co.dif.smj.databinding.FragmentUnitLocationBinding
import id.co.dif.smj.presentation.dialog.PopUpProfileDialog
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.TripleEMapClusterRenderer
import id.co.dif.smj.utils.addValidItem
import id.co.dif.smj.utils.colorRes
import id.co.dif.smj.utils.isDeviceOnline
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.utils.zoom
import id.co.dif.smj.viewmodel.MapsTicketViewModel

class UnitLocationFragment : BaseFragment<MapsTicketViewModel, FragmentUnitLocationBinding>(),
    OnMapReadyCallback, ClusterManager.OnClusterClickListener<MarkerTripleE>,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE>{
    override val layoutResId = R.layout.fragment_unit_location

    private lateinit var clusterManager: ClusterManager<MarkerTripleE>
    private lateinit var map: GoogleMap
    private var sourceLoc: LatLng? = null
    private var destinationLoc: LatLng? = null
    var markers = listOf<MarkerTripleE>()
    var zoomMap: () -> Unit = {}

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

            markers.forEach {
                clusterManager.addValidItem(it)
            }
            zoomMap()
        }

    private fun setupLocations(siteDetails: SiteDetails?) {
        siteDetails?.let { data ->
            if (!requireContext().isDeviceOnline()) {
                loadMarkerOffline()
            }
            else {
                viewModel.getNearestTechnician(data.siteId)
            }
        }
        viewModel.responseNearestTechnician.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                map.clear()
                setupClusterization()
                var info = preferences.myDetailProfile.value
                info?.let { data ->
                    val fieldEngineer =
                        it.data.list.firstOrNull { loc ->
                            loc.id == data.id
                        }
                    val site = it.data.list.firstOrNull()
                    preferences.selectedSite.value = site

                    site?.type = LocationType.Site.name
                    when (session?.emp_security) {
                        1 -> Unit
                        2 -> viewModel.getDetailProfile(data.id)
                        3 -> viewModel.getDetailProfile(data.id)
                        else -> Unit
                    }
                    markers = listOfNotNull(site, fieldEngineer)
                    markers.forEach { loc ->
                        clusterManager.addValidItem(loc)
                    }
                    map.setOnMapLoadedCallback {
                        zoomMap = {
                            map.zoom(clusterManager, markers)
                        }
                        zoomMap()
                    }
                }
            } else {
                currentActivity.showToast("Site not found!")
            }
        }

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val marker = MarkerTripleE(
                    id = it.data.id,
                    latitude = it.data.latitude.orDefault("0"),
                    longtitude = it.data.longtitude.orDefault("0"),
                    image = it.data.photo_profile,
                    name = it.data.fullname,
                    type = "technician"
                )
                markers = markers + marker
                var technicianOnly = markers.filter { it.type == LocationType.Technician.name }
                technicianOnly = technicianOnly.distinctBy { it.id }
                val site = markers.filter { it.type == LocationType.Site.name }
                markers = technicianOnly + site
                clusterManager.clearItems()
                markers.forEach {
                    clusterManager.addValidItem(it)
                }
                clusterManager.cluster()

                map.zoom(clusterManager, markers)

            }
        }

        viewModel.responseDirection.observe(lifecycleOwner) {
            if (sourceLoc != null && destinationLoc != null) {
                try {
                    val src =
                        MarkerTripleE(
                            latitude = sourceLoc!!.latitude.toString(),
                            longtitude = sourceLoc!!.longitude.toString()
                        )
                    val dst = MarkerTripleE(
                        latitude = destinationLoc!!.latitude.toString(),
                        longtitude = destinationLoc!!.longitude.toString()
                    )
                    binding.layoutAction.isVisible = true
                    redrawMaps()
                    val routes = it.routes
                    val legs = routes[0].legs
                    val steps = legs[0].steps
                    val path: MutableList<List<LatLng>> = ArrayList()
                    for (i in 0 until steps.size) {
                        val points = steps[i].polyline.points
                        path.add(PolyUtil.decode(points))
                    }
                    for (i in 0 until path.size) {
                        map.addPolyline(
                            PolylineOptions().addAll(path[i]).width(20f)
                                .color(R.color.light_blue.colorRes(requireContext())).endCap(
                                    RoundCap()
                                )
                        )
                    }
                    val markers = listOf(src, dst) + path.flatten()
                        .map { MarkerTripleE(latitude = it.latitude.toString(), longtitude = it.longitude.toString()) }
                    map.zoom(clusterManager, markers) {
                    }
                } catch (e: Exception) {
                    binding.layoutAction.isVisible = false
                    e.printStackTrace()
                    redrawMaps()
                    currentActivity.showToast("Something went wrong!")
                }
            }
        }
        binding.imgCancel.setOnClickListener {
            binding.layoutAction.isVisible = false
            redrawMaps()
        }

        binding.imgDirection.setOnClickListener {
            sourceLoc?.let { it1 -> destinationLoc?.let { it2 -> openMaps(it1, it2) } }
        }

    }

    fun loadMarkerOffline() {
        val data = preferences.ticketDetails.value
        val site = MarkerTripleE(
            type = "TT Site All",
            name = data?.site_info?.siteName,
            site_addre_street = data?.site_info?.siteAddressStreet,
            latitude = data?.site_info?.technologyLatitude.orDefault("0"),
            longtitude = data?.site_info?.technologyLongitude.orDefault("0")
        )
        val technician = MarkerTripleE(
            type = "technician",
            name = preferences.myDetailProfile.value?.fullname,
            image = preferences.myDetailProfile.value?.photo_profile,
            latitude = preferences.myDetailProfile.value?.latitude.orDefault("0"),
            longtitude = preferences.myDetailProfile.value?.longtitude.orDefault("0")
        )
        markers = listOfNotNull(site, technician)
        redrawMaps()
        map.zoom(clusterManager, markers)
    }

    private fun openMaps(src: LatLng, dst: LatLng) {
        val uri =
            Uri.parse("http://maps.google.com/maps?saddr=${src.latitude},${src.longitude}&daddr=${dst.latitude},${dst.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context?.startActivity(intent)
    }

    private fun redrawMaps() {
        map.clear()
        setupClusterization()
        clusterManager.clearItems()
        markers.forEach { loc ->
            clusterManager.addValidItem(loc)
        }
        clusterManager.cluster()
    }

    private fun setupClusterization() = fragmentContext?.let { context ->
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 80.toDp, 0, 20.toDp)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(-2.548926, 118.0148634), 3.6f
        )
        map.moveCamera(cameraUpdate)


        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        val siteDetails = preferences.siteData.value
        setupLocations(siteDetails!!.info_site)
    }

    override fun onClusterClick(cluster: Cluster<MarkerTripleE>): Boolean {
        map.zoom(clusterManager, cluster.items.toList()) {
            if (map.cameraPosition.zoom < map.maxZoomLevel) {
                return@zoom
            }
            val techniciansOnly =
                cluster.items.filter { loc -> loc.type == LocationType.Technician.name }
            PopUpProfileDialog.newInstance(techniciansOnly, true) { getDirection(it) }.show(
                childFragmentManager,
                PopUpProfileDialog::class.java.name
            )
        }
        return true
    }

    override fun onClusterItemClick(item: MarkerTripleE): Boolean {
        map.zoom(clusterManager, item) {
            when (LocationType.fromString(item.type)) {
                LocationType.Site -> {
//                    MarkerPopupDialog.newInstance(item).show(
//                        childFragmentManager,
//                        MarkerPopupDialog::class.java.name
//                    )
                }

                LocationType.Technician -> {
                    val popup = PopUpProfileDialog.newInstance(item, true) {
                        getDirection(it)
                    }
                    popup.also {
                        it.show(
                            childFragmentManager,
                            PopUpProfileDialog::class.java.name
                        )
                    }
                }

                else -> {}
            }
        }
        return false
    }

    private fun getDirection(direction: MarkerTripleE) {
        val site = preferences.siteData.value?.info_site
        val sitePosition = site?.toSiteLocation()?.position

        if (sitePosition != null && sitePosition.latitude != 0.0 && sitePosition.longitude != 0.0) {
            sourceLoc = sitePosition
            destinationLoc = direction.position
            viewModel.getDirection(sitePosition, direction.position)
        } else {
            currentActivity.showToast(" Cannot create direction! Ticket doesn't have site associated.")
        }
    }


}