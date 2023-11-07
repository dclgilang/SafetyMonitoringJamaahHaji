package id.co.dif.smj.utils

import android.content.Context
import android.graphics.Color
import android.webkit.URLUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import id.co.dif.smj.R
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.LocationType
import org.koin.core.component.KoinComponent
import java.util.Random

class TripleEMapClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    val clusterManager: ClusterManager<MarkerTripleE>,
) : DefaultClusterRenderer<MarkerTripleE>(context, map, clusterManager), KoinComponent {
    val items: MutableList<MarkerTripleE> = mutableListOf()
    private val icBakti: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.ic_bakti)
    private val icExcavator: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.icon_excavator)
    private val icBoat: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.ic_boat)
    private val icGenerator: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.ic_power_generator)
    private val icApartement: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.ic_apartement)
    private val brokenImage: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.baseline_broken_image_24)
    private val icJamaah: BitmapDescriptor? = context.toBitmapDescriptor(R.drawable.ic_syehhh)
    private val alarm = context.toBitmapDescriptor(R.drawable.ic_alarm_high_quality)
    private val person = context.toBitmapDescriptor(R.drawable.ic_person)

    val random = Random()
    val randomIndex = random.nextInt(4)
    val randomBitmapDescriptor: BitmapDescriptor? = when (randomIndex) {
        0 -> icBakti
        1 -> icExcavator
        2 -> icBoat
        3 -> icApartement
        else -> icExcavator // Default to a specific image (you can change it as needed)
    }
        private val images = mapOf<String, BitmapDescriptor?>(
        "_2.png" to context.toBitmapDescriptor(R.drawable._2),
        "_1.png" to context.toBitmapDescriptor(R.drawable._1),
        "_24.png" to context.toBitmapDescriptor(R.drawable._24),
        "_25.png" to context.toBitmapDescriptor(R.drawable._25),
        "_3.png" to context.toBitmapDescriptor(R.drawable._3),
        "_5.png" to context.toBitmapDescriptor(R.drawable._5),
        "_4.png" to context.toBitmapDescriptor(R.drawable._4),
    )

    override fun onBeforeClusterItemRendered(
        item: MarkerTripleE,
        markerOptions: MarkerOptions,
    ) {
        loadMarker(markerOptions, item)
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<MarkerTripleE>,
        markerOptions: MarkerOptions,
    ) {
        val bitmap = BitmapDescriptorFactory.fromBitmap(context.makeClusterMarker(cluster.size))
        markerOptions.icon(bitmap)
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor("#0756AB")
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MarkerTripleE>): Boolean {
        return cluster.size > 100
    }

    private fun loadMarker(markerOptions: MarkerOptions, item: MarkerTripleE) {
        items.add(item)
        items.distinct()
        val bitmapDescriptor = when (LocationType.fromString(item.type)) {
            LocationType.Note, LocationType.Technician -> {
                val loaded = if (URLUtil.isValidUrl(item.image)) {
                    loadImage(context, item.image)
                } else {
                    item.image?.let { encoded ->
                        base64ImageToBitmap(encoded)
                    }
                }
                if (loaded != null) BitmapDescriptorFactory.fromBitmap(context.makeClusterItemMarker(loaded.circularCrop(), item)) else person
            }


            LocationType.Site, LocationType.TtSiteAll -> {
                val resName = "_" + item.image?.substringAfterLast('/')
                val resource = images[resName]

                resource ?: icJamaah
            }

           LocationType.Kapal -> {
                val resName = "_" + item.image?.substringAfterLast('/')
                val resource = images[resName]

                resource ?: icJamaah
            }

            LocationType.Apartement -> {
                val resName = "_" + item.image?.substringAfterLast('/')
                val resource = images[resName]

                resource ?: icJamaah
            }

           LocationType.Excavator -> {
                val resName = "_" + item.image?.substringAfterLast('/')
                val resource = images[resName]

                resource ?: icJamaah
            }

            LocationType.Generator -> {
                val resName = "_" + item.image?.substringAfterLast('/')
                val resource = images[resName]

                resource ?: icJamaah
            }

            LocationType.TtMapAll -> alarm
        }
        markerOptions.icon(bitmapDescriptor)
    }

    companion object {
    }
}