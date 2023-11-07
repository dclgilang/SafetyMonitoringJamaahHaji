package id.co.dif.smj.presentation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.databinding.FragmentMapsLocationJamaahBinding
import id.co.dif.smj.presentation.dialog.SelectEngineerDialog
import id.co.dif.smj.utils.TripleEMapClusterRenderer
import id.co.dif.smj.utils.customMaps
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.utils.zoom
import id.co.dif.smj.viewmodel.SelectSiteViewModel

class MapsLocationJamaahFragment : BaseFragment<SelectSiteViewModel, FragmentMapsLocationJamaahBinding>(),
    OnMapReadyCallback, SelectEngineerDialog.Listener,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE>,
    ClusterManager.OnClusterClickListener<MarkerTripleE>{
    override val layoutResId = R.layout.fragment_maps_location_jamaah
    private var clusterManager: ClusterManager<MarkerTripleE>? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var map: GoogleMap



    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 0, 0, 20.toDp)
        map.customMaps(requireContext())
        val selectedSite = arguments?.getSerializable("selected_site") as MarkerTripleE?
        selectedSite?.site_id.log("selected_site_for_engineer")
        val siteId = selectedSite?.site_id
        viewModel.selectedSite = selectedSite
        viewModel.getNearestTechnician(siteId)
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        clusterizeMap()
    }

    override fun onSelectedEngineer(marker: MarkerTripleE?) {
        marker.log("enginerelsdjflsdf")
        marker?.image = null
        clusterManager?.clearItems()
        viewModel.lastMarkerZoomed = marker
        val intent = Intent()
        intent.putExtra("selected_engineer", marker)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
        requireActivity().finish()
    }

    override fun pingLocation(engineer: MarkerTripleE) {
        viewModel.pingEnginer = engineer
        viewModel.pingEngineerToSendTheirLocation(engineerId = engineer.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            requireActivity().finish()
        }
    }

    private fun clusterizeMap() = context?.let { context ->
        clusterManager = ClusterManager(requireContext(), map)
        map.setOnCameraIdleListener(clusterManager)
        clusterManager?.let { clusterManager ->
            clusterManager.setOnClusterClickListener(this)
            clusterManager.setOnClusterItemClickListener(this)
            clusterManager.renderer = TripleEMapClusterRenderer(context, map, clusterManager)
        }
    }

    override fun onClusterItemClick(marker: MarkerTripleE): Boolean {
        when (LocationType.fromString(marker.type)) {
            LocationType.Site -> {

            }

            LocationType.Technician -> {
                SelectEngineerDialog.newInstance(listOf(marker), this) {
                    viewModel.pingEngineerToSendTheirLocation(engineerId = it.id)
                }.show(
                    childFragmentManager,
                    SelectEngineerDialog::class.java.name
                )
            }

            LocationType.TtMapAll -> {
                MarkerPopupDialog.newInstance(marker = marker).show(
                    childFragmentManager,
                    MarkerPopupDialog::class.java.name
                )
            }

            else -> Unit
        }
        return false
    }

    override fun onClusterClick(cluster: Cluster<MarkerTripleE>): Boolean {
        clusterManager?.let { clusterManager ->
            map.zoom(clusterManager, cluster.items.toList()) {
                binding.etSearch.clearFocus()
                if (map.cameraPosition.zoom < map.maxZoomLevel) {
                    return@zoom
                }
                val techniciansOnly =
                    cluster.items.filter { loc -> loc.type == LocationType.Technician.name }
                SelectEngineerDialog.newInstance(techniciansOnly.toList(), this) {
                }.show(
                    childFragmentManager,
                    SelectEngineerDialog::class.java.name
                )
            }
        }
        return true
    }

    fun resetViewport(): Boolean {
        val isReady = viewModel.responseNearestJamaah.value != null
        if (isReady) {
            viewModel.responseNearestJamaah.let { it.value = it.value }
        }
        return isReady
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}