package id.co.dif.smj.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import id.co.dif.smj.service.DefaultLocationClient
import id.co.dif.smj.service.LocationClient
import id.co.dif.smj.utils.ColorGenerator

fun provideColorGenerator(): ColorGenerator? {
    return ColorGenerator.DEFAULT
}

fun provideLocationClient(context: Context): LocationClient {
    return DefaultLocationClient(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}