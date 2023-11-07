package id.co.dif.smj.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import id.co.dif.smj.BuildConfig
import id.co.dif.smj.service.ApiServices
import id.co.dif.smj.utils.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

fun provideHttpLogingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

fun provideChuckerInterceptor(context: Context): ChuckerInterceptor {
    val chuckerCollector = ChuckerCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )
    return ChuckerInterceptor.Builder(context)
        .collector(chuckerCollector)
        .createShortcut(true)
        .build()
}

fun provideOkHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    chuckerInterceptor: ChuckerInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.MINUTES)
        .readTimeout(3, TimeUnit.MINUTES)
        .writeTimeout(3, TimeUnit.MINUTES)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()
}

fun provideGsonBuilder(): Gson? {
    return GsonBuilder().apply {
        setPrettyPrinting()
    }.create()
}

fun provideRetrofit(okHttpClient: OkHttpClient, gsonBuilder: Gson): Retrofit.Builder {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
}

fun provideTripleEApi(retrofitBuilder: Retrofit.Builder): ApiServices {
    return retrofitBuilder
        .baseUrl(BuildConfig.BASE_URL)
        .build()
        .create(ApiServices::class.java)
}

fun providePicassoDownloader(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    chuckerInterceptor: ChuckerInterceptor
): OkHttpClient {

    return OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.MINUTES)
        .readTimeout(3, TimeUnit.MINUTES)
        .writeTimeout(3, TimeUnit.MINUTES)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(ErrorInterceptor())
        .addInterceptor(chuckerInterceptor)
        .build()
}