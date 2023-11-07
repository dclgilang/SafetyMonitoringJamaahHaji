package id.co.dif.smj.utils

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.ClusterManager
import id.co.dif.smj.R
import id.co.dif.smj.data.MarkerTripleE

fun GoogleMap.zoom(
    clusterManager: ClusterManager<MarkerTripleE>,
    list: List<MarkerTripleE>,
    onIdle: () -> Unit = {}
) {
    list.ifEmpty {
        return@zoom
    }
    Log.d("TAG", "zoom: ${list.joinToString { it.type }}")
    val latLngBounds = LatLngBounds.builder()
    list.forEach { location ->
        if (location.markerIsValid()) {
            latLngBounds.include(
                LatLng(
                    location.latitude.orDefault("0").toDouble(),
                    location.longtitude.orDefault("0").toDouble()
                )
            )
        }
    }
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
        latLngBounds.build(),
        80
    )

    animateCamera(cameraUpdate)
    setOnCameraIdleListener {
        onIdle()
        setOnCameraIdleListener {
            clusterManager.cluster()
        }
        clusterManager.cluster()
    }
}

fun GoogleMap.zoom(
    clusterManager: ClusterManager<MarkerTripleE>,
    loc: MarkerTripleE,
    onIdle: () -> Unit = {}
) {
    zoom(clusterManager, listOf(loc), onIdle)
}

fun GoogleMap.zoom(
    clusterManager: ClusterManager<MarkerTripleE>,
    latLng: android.location.Location,
    onIdle: () -> Unit = {}
) {
    val position = MarkerTripleE(
        latitude = latLng.latitude.toString(),
        longtitude = latLng.longitude.toString()
    )
    zoom(clusterManager, listOf(position), onIdle)
}

fun GoogleMap.animateToMeters(
    clusterManager: ClusterManager<MarkerTripleE>,
    meters: Float,
    ll: LatLng,
    onIdle: () -> Unit = {}
) {
    val latLngBounds = ll.calculateBounds(meters.toDouble());
    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
        latLngBounds,
        70
    )
    animateCamera(cameraUpdate);
    setOnCameraIdleListener {
        onIdle()
        setOnCameraIdleListener {
            clusterManager.cluster()
        }
        clusterManager.cluster()
    }
}

fun LatLng.calculateBounds(radius: Double): LatLngBounds {
    return LatLngBounds.Builder()
        .include(SphericalUtil.computeOffset(this, radius, 0.0))
        .include(SphericalUtil.computeOffset(this, radius, 90.0))
        .include(SphericalUtil.computeOffset(this, radius, 180.0))
        .include(SphericalUtil.computeOffset(this, radius, 270.0))
        .build();
}

fun GoogleMap.customMaps(context: Context) {
    try {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, R.raw.google_map_style
            )
        )
    } catch (_: Resources.NotFoundException) {
    }
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
        LatLng(-2.548926, 118.0148634), 3.6f
    )
    animateCamera(cameraUpdate)
}

fun Fragment.getMapAsync(callback: OnMapReadyCallback) {
    val mapFragment = this as SupportMapFragment
    mapFragment.getMapAsync(callback)
}