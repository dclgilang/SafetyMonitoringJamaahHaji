package id.co.dif.smj.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.view.isVisible
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.databinding.ActivitySelectSiteBinding
import id.co.dif.smj.presentation.dialog.SelectEngineerDialog
import id.co.dif.smj.presentation.fragment.MarkerPopupDialog
import id.co.dif.smj.utils.*
import id.co.dif.smj.viewmodel.SelectSiteViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 15:01
 *
 */
class SelectEngineerActivity : BaseActivity<SelectSiteViewModel, ActivitySelectSiteBinding>(),
    OnMapReadyCallback, SelectEngineerDialog.Listener,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE>,
    ClusterManager.OnClusterClickListener<MarkerTripleE> {
    override val layoutResId = R.layout.activity_select_site
    private var clusterManager: ClusterManager<MarkerTripleE>? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var map: GoogleMap

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, SelectEngineerActivity::class.java)
        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.title = "Select Engineer"
        supportFragmentManager.findFragmentById(R.id.map)?.getMapAsync(this)
        binding.rootLayout.onActionButtonClicked = {
            onSelectedEngineer(null)
        }
        viewModel.responseNearestTechnician.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                clusterManager?.let { clusterManager ->
                    map.clear()
                    clusterManager.clearItems()
                    val locations = it.data.list.filter { it.type == "technician" }
                    binding.etSearch.clearFocus()
                    locations.forEach { location ->
                        clusterManager.addValidItem(location)
                    }
                    val site = it.data.list.firstOrNull()
                    site?.let {
                        binding.rootLayout.title = site.site_name.orDefault("Select Engineer")
                        clusterManager.addValidItem(site)
                        if (viewModel.zoomMaps) {
                            if(site.type != LocationType.Technician.name ) {
                                val closestDistance = site.position.closestDistance(locations.map { it.position })
                                map.animateToMeters(clusterManager, closestDistance, site.position)
                            }else{
                                map.zoom(clusterManager, locations)
                            }
                        } else {
                            viewModel.zoomMaps = true
                            clusterManager.cluster()
                            locations.forEach { location ->
                                if (location.id == viewModel.pingEnginer?.id) {
                                    viewModel.pingEnginer = location
                                }
                            }
                            if (viewModel.pingEnginer?.locationIsUpdated != true) {
                                showAlert(
                                    context = this,
                                    message = "Engineer marker is not updated",
                                    buttonPrimaryText = "Dismiss",
                                    onButtonPrimaryClicked = {},
                                )
                            } else if (viewModel.pingEnginer?.isWithinSiteRadius == false) {
                                showAlert(
                                    context = this,
                                    message = "Engineer is not within site radius",
                                    buttonPrimaryText = "Dismiss",
                                    onButtonPrimaryClicked = {},
                                )
                            }
                        }
                    }
                }
                binding.layoutAction.isVisible = true
                binding.etSearch.isVisible = false
            } else {
                showToast("There is a problem while loading technician!")
            }
        }

        binding.imgRefresh.setOnClickListener {
            viewModel.selectedSite?.let {
                viewModel.getNearestTechnician(it.site_id)
            }
        }
        viewModel.responsePingEngineerToSendTheirLocation.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                startCountdown()
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 0, 0, 20.toDp)
        map.customMaps(this)
        val selectedSite = intent.getSerializableExtra("selected_site") as MarkerTripleE?
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
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun pingLocation(engineer: MarkerTripleE) {
        viewModel.pingEnginer = engineer
        viewModel.pingEngineerToSendTheirLocation(engineerId = engineer.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private fun clusterizeMap() = activityContext?.let { context ->
        clusterManager = ClusterManager(this, map)
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
                    supportFragmentManager,
                    SelectEngineerDialog::class.java.name
                )
            }

            LocationType.TtMapAll -> {
                MarkerPopupDialog.newInstance(marker = marker).show(
                    supportFragmentManager,
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
                    supportFragmentManager,
                    SelectEngineerDialog::class.java.name
                )
            }
        }
        return true
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.tvTitleTimer.text =
                    getString(R.string.requesting_engineer_latest_location, secondsLeft.toString())
            }

            override fun onFinish() {
                binding.tvTitleTimer.isVisible = false
                binding.tvTitleTimer.text = "Countdown Finished!"
                viewModel.selectedSite?.let {
                    viewModel.zoomMaps = false
                    viewModel.getNearestTechnician(it.site_id)
                }
            }
        }
        binding.tvTitleTimer.isVisible = true
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}