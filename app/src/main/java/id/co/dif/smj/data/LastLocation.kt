package id.co.dif.smj.data

import android.location.Location

data class LastLocation(
    val latitude: Double,
    val longitude: Double,
    val lastUpdate: Long,
)