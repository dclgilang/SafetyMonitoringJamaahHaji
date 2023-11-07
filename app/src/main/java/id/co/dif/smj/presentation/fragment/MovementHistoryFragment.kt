package id.co.dif.smj.presentation.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.LocationType
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.SiteDetails
import id.co.dif.smj.databinding.FragmentMovementHistoryBinding
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


class MovementHistoryFragment : BaseFragment<MapsTicketViewModel, FragmentMovementHistoryBinding>(){
    override val layoutResId = R.layout.fragment_movement_history

    private lateinit var map: GoogleMap
    private val handler = Handler()
    private val polylineOptions = PolylineOptions()
    private val locationHistory: List<LatLng> = mutableListOf(
        LatLng(42.3591, -71.0932),
        LatLng(42.3601, -71.0912),
        LatLng(42.3611, -71.0892),
        LatLng(42.3621, -71.0872),
        // Add more waypoints here
        LatLng(42.3391, -71.0632)
    )
    private var marker: Marker? = null
    private var currentIndex = 0


    private var currentPoint = 0
    private var currentPolyline: Polyline? = null

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap
           drawHistoricalTrack()
        }

    }



    private fun drawHistoricalTrack() {
        val polylineOptions = PolylineOptions()
            .addAll(locationHistory)
            .color(R.color.red)
            .width(14f)
            .startCap(RoundCap())
            .endCap(RoundCap())
            .jointType(JointType.ROUND)

        // Add the polyline to the map
        currentPolyline = map.addPolyline(polylineOptions)

        // Zoom to fit the historical track
        val boundsBuilder = LatLngBounds.builder()
        for (point in locationHistory) {
            boundsBuilder.include(point)
        }
        val bounds = boundsBuilder.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

        // Add markers at the starting and ending points
        addMarker(locationHistory.last(), "End")
    }

    private fun addMarker(position: LatLng, title: String) {
        val markerOptions = MarkerOptions()
            .position(position)
            .title(title)

        map.addMarker(markerOptions)
    }


    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

}