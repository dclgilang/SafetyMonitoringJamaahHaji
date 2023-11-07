package id.co.dif.smj.utils

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MarkerAnimator(private val marker: Marker) {
    private val handler = Handler(Looper.getMainLooper())
    private val duration = 5000L // Animation duration in milliseconds
    private val interpolator = LinearInterpolator()

    fun animateAlongPath(path: List<LatLng>) {
        val startTime = SystemClock.uptimeMillis()
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - startTime
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lat = t * path[1].latitude + (1 - t) * path[0].latitude
                val lng = t * path[1].longitude + (1 - t) * path[0].longitude
                marker.position = LatLng(lat, lng)

                if (t < 1.0) {
                    handler.postDelayed(this, 16) // 60 FPS
                }
            }
        })
    }
}
