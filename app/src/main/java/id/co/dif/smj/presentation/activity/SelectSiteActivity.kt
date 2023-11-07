package id.co.dif.smj.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ArrayAdapter
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
import id.co.dif.smj.presentation.fragment.MarkerPopupDialog
import id.co.dif.smj.utils.*
import id.co.dif.smj.viewmodel.SelectSiteViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 15:01
 *
 */
class SelectSiteActivity : BaseActivity<SelectSiteViewModel, ActivitySelectSiteBinding>(),
    OnMapReadyCallback,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE>,
    ClusterManager.OnClusterClickListener<MarkerTripleE> {
    override val layoutResId = R.layout.activity_select_site
    private var clusterManager: ClusterManager<MarkerTripleE>? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var map: GoogleMap
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        supportFragmentManager.findFragmentById(R.id.map)?.getMapAsync(this)
        binding.rootLayout.onActionButtonClicked = {
            selectSite(null)
        }

        viewModel.responseListMarker.observe(lifecycleOwner) {
            if (it.status == 200 && it.data.list.isNotEmpty()) {
                clusterManager?.let { clusterManager ->
                    val hashSet = HashSet<String>()
                    map.clear()
                    clusterManager.clearItems()
                    binding.rootLayout.title = getString(R.string.select_site)
                    it.data.list.forEach { location ->
                        clusterManager.addValidItem(location)
                        location.image?.let { hashSet.add(it) }
                    }
                    map.zoom(clusterManager, it.data.list)
                    val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
                        this,
                        R.layout.item_spinner_dropdown,
                        it.data.list
                    )
                    if (binding.etSearch.text.isNotEmpty()) {
                        binding.etSearch.showDropDown()
                    }

                    binding.etSearch.setAdapter(adapter)
                    binding.etSearch.setOnItemClickListener { _, _, position, _ ->
                        hideSoftKeyboard(this)
                        binding.etSearch.clearFocus()
                        val site = adapter.getItem(position)
                        site?.let {
                            map.zoom(clusterManager, site)
                        }
                    }
                }
            }
        }

        binding.etSearch.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etSearch.setText("")
                viewModel.getListSite(null)
            }
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setPadding(0, 0, 0, 20.toDp)

        viewModel.getListSite()

        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        clusterizeMap()
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
            LocationType.TtMapAll -> {
                MarkerPopupDialog.newInstance(marker = marker).show(
                    supportFragmentManager,
                    MarkerPopupDialog::class.java.name
                )
            }

            LocationType.TtSiteAll -> {
                clusterManager?.let { clusterManager ->
                    map.zoom(clusterManager, marker) {
                        val popup = MarkerPopupDialog.newInstance(marker = marker)
                        popup.onSiteIsSelected = {
                            selectSite(it)
                        }
                        popup.isSiteSelectable = true
                        popup.show(
                            supportFragmentManager,
                            MarkerPopupDialog::class.java.name
                        )
                    }
                }
            }

            else -> Unit
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedSite?.let {
            viewModel.getListSite()
        }
    }

    override fun onClusterClick(cluster: Cluster<MarkerTripleE>): Boolean {
        clusterManager?.let { clusterManager ->
            map.zoom(clusterManager, cluster.items.toList()) {
                binding.etSearch.clearFocus()
            }
        }
        return true
    }

    private fun selectSite(selectedSite: MarkerTripleE?) {
        val intent = Intent()
        intent.putExtra("selected_site", selectedSite)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return Intent(context, SelectSiteActivity::class.java)
        }
    }


}