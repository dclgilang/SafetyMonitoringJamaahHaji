package id.co.dif.smj.presentation.activity

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.data.Note
import id.co.dif.smj.databinding.ActivityMapsNotesBinding
import id.co.dif.smj.presentation.dialog.NotePopupDialog
import id.co.dif.smj.presentation.dialog.PopUpProfileDialog
import id.co.dif.smj.presentation.fragment.TicketListPopupDialog
import id.co.dif.smj.utils.TripleEMapClusterRenderer
import id.co.dif.smj.utils.addValidItem
import id.co.dif.smj.utils.animateToMeters
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.distanceTo
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.utils.urlTypeOf
import id.co.dif.smj.utils.zoom
import id.co.dif.smj.viewmodel.MapsViewModel
import kotlinx.coroutines.launch

class MapsNotesActivity : BaseActivity<MapsViewModel, ActivityMapsNotesBinding>(),
    OnMapReadyCallback, ClusterManager.OnClusterClickListener<MarkerTripleE>,
    ClusterManager.OnClusterItemClickListener<MarkerTripleE> {
    private var clusterManager: ClusterManager<MarkerTripleE>? = null
    override val layoutResId = R.layout.activity_maps_notes
    private lateinit var map: GoogleMap
    lateinit var notes: List<Note>
    var markers: List<MarkerTripleE> = mutableListOf()
    var zoomMap: () -> Unit = {}


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.imgRefresh.setOnClickListener {
            map.clear()
            setupClusterization()
            markers.forEach {
                clusterManager?.addValidItem(it)
            }
            zoomMap()
        }
    }

    private fun setupLocations() {
        val data = preferences.ticketDetails.value
        data?.let { opsTicket ->
            notes = opsTicket.tic_notes.orEmpty()
            val highlightedNote = preferences.highlightedNote
            val notesWithImage = notes.filter {
                urlTypeOf(it.file) == "image"
            }
            val siteInfo = opsTicket.site_info
            val siteLatitude = siteInfo?.technologyLatitude
            val siteLongitude = siteInfo?.technologyLongitude
            var siteLocation = preferences.selectedSite.value

            if (siteLocation == null) {
                siteLocation = MarkerTripleE(
                    latitude = siteLatitude.orDefault("0"),
                    longtitude = siteLongitude.orDefault("0"),
                    type = "site",
                    site_name = opsTicket.site_info?.siteName.orDefault("Site"),
                    site_addre_street = siteInfo?.siteAddressStreet.orDefault(),
                    site_end_customer = siteInfo?.siteEndCustomer,
                    site_contact_phone = siteInfo?.siteContactPhone
                )
            }

            Log.d(TAG, "setupLocations loc: $siteLatitude $siteLongitude")
            // TODO: modify this hardcode
            val notesMarkers = notesWithImage.mapNotNull {
                try {
                    val urlType = urlTypeOf(it.file)
                    val image = if (urlType == "image") {
                        it.file
                    } else {
                        null
                    }
                    it.log("maps marker")
                    MarkerTripleE(
                        id = it.id,
                        latitude = it.latitude.orDefault("0"),
                        longtitude = it.longitude.orDefault("0"),
                        image = image,
                        type = LocationType.Note.name
                    )
                } catch (e: Exception) {
                    null
                }
            }
            map.clear()
            markers = notesMarkers + siteLocation
            val highlightedNoteLocation = notesMarkers.find { it.id == highlightedNote.value?.id }
            highlightedNoteLocation?.let {
                zoomMap = {
                    val distance = it.position.distanceTo(siteLocation.position)
                    map.animateToMeters(clusterManager!!, distance, it.position)
                }
                zoomMap()
            }
            markers.forEach {
                clusterManager!!.addItem(it)
            }
        }
    }

    private fun setupClusterization() = activityContext?.let { context ->
        clusterManager = ClusterManager(this, map)
        clusterManager?.let {
            map.setOnCameraIdleListener(clusterManager)
            it.setOnClusterClickListener(this)
            it.setOnClusterItemClickListener(this)
            it.renderer = TripleEMapClusterRenderer(context, map, it)
            it.renderer
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupClusterization()
        map.setOnCameraIdleListener(clusterManager)
        map.setPadding(0, 80.toDp, 0, 20.toDp)

        try {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.google_map_style
                )
            )
        } catch (_: Resources.NotFoundException) {
        }


        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isMapToolbarEnabled = false
        val loc = MarkerTripleE(longtitude = "118.0148634", latitude = "-2.548926")
        map.zoom(clusterManager!!, loc) {
            setupLocations()
        }
    }

    override fun onClusterClick(cluster: Cluster<MarkerTripleE>): Boolean {
        map.zoom(clusterManager!!, cluster.items.toList()) {
            if (map.cameraPosition.zoom >= map.maxZoomLevel) {
                val onlyNotes = cluster.items.filter { it.type == LocationType.Note.name }
                val notesInCluster =
                    onlyNotes.mapNotNull { loc -> notes.first { note -> note.id == loc.id } }
                NotePopupDialog.newInstance(notesInCluster).show(
                    supportFragmentManager,
                    PopUpProfileDialog::class.java.name
                )
            }
        }
        return true
    }

    override fun onClusterItemClick(item: MarkerTripleE): Boolean {
        map.zoom(clusterManager!!, item) {
            lifecycleOwner.lifecycleScope.launch {
                if (item.type == LocationType.Note.name) {
                    val note = notes.first { note -> note.id == item.id }
                    NotePopupDialog.newInstance(listOf(note)).show(
                        supportFragmentManager,
                        TicketListPopupDialog::class.java.name
                    )
                }
            }
        }
        return false
    }


}