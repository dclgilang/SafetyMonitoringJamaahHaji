package id.co.dif.smj.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import id.co.dif.smj.END_HOUR_LOCATION_SCHEDULE
import id.co.dif.smj.END_MINUTE_LOCATION_SCHEDULE
import id.co.dif.smj.LOCATION_UPDATE_INTERVAL
import id.co.dif.smj.R
import id.co.dif.smj.START_HOUR_LOCATION_SCHEDULE
import id.co.dif.smj.START_MINUTE_LOCATION_SCHEDULE
import id.co.dif.smj.data.LastLocation
import id.co.dif.smj.data.LocationScheduleCommand
import id.co.dif.smj.persistence.Preferences
import id.co.dif.smj.utils.calculateIntervalToClosestSchedules
import id.co.dif.smj.utils.constructLastLocation
import id.co.dif.smj.utils.log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Calendar

class LocationService : Service(), KoinComponent {
    val apiServices by inject<ApiServices>()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val locationClient: LocationClient by inject()
    private val preferences: Preferences by inject()
    private val database = Firebase.database
    private var job: Job? = null
    private var userRef: DatabaseReference? = null
    private val locationPingListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.d("PingLocation: " + preferences.myDetailProfile.value?.id + " " + snapshot)
            locationClient.getCurrentLocation { location ->
                location?.let {
                    saveLastLocation(it)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) = Unit
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    private fun updateLocation(lastLocation: LastLocation) {
        preferences.session.value?.let { session ->
            job = serviceScope.launch(CoroutineExceptionHandler { _, throwable ->
                job?.cancel()
                throwable.printStackTrace()
            }) {
                apiServices.putUpdateLocation(
                    bearerToken = "Bearer ${session.token_access}",
                    param = mutableMapOf(
                        "emp_latitude" to lastLocation.latitude,
                        "emp_longtitude" to lastLocation.longitude,
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action.log("service action")) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start() {
        "marker start".log("marker update")
        val myProfile = preferences.myDetailProfile.value
        val rootRef = database.getReference("/")
        Timber.tag(TAG).d("onCreate: service started ")
        Timber.tag(TAG).d("start: " + myProfile?.id)
        userRef?.removeEventListener(locationPingListener)
        userRef = rootRef.child(myProfile?.id.toString())
        userRef?.addValueEventListener(locationPingListener)
        val notification = buildForegroundNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
        startForeground(1, notification)
        locationClient
            .getLocationUpdates(
                preferences.locationUpdateInterval.value ?: LOCATION_UPDATE_INTERVAL
            )
            .catch { e ->
                e.printStackTrace()
            }
            .onEach { location ->
                location.log("marker update")
                val notification = buildForegroundNotification()
                notificationManager.notify(1, notification)
                saveLastLocation(location)
            }
            .launchIn(serviceScope)

        isServiceRunning = true
    }



    private fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, "marker")
            .setContentTitle(getString(R.string.triple_e_is_running))
            .setContentText(getString(R.string.triple_e_is_running_in_the_background_in_working_hours))
            .setSmallIcon(R.drawable.logo_triple_e)
            .setOngoing(true).build()
    }

    private fun stop() {
        Timber.d("stop: Service Stopped")
        stopForeground(true)
        stopSelf()
    }

    fun saveLastLocation(location: Location) {
        Timber.tag(TAG).d("saveLastLocation: %s", location)
        preferences.lastLocation.let { lastLocation ->
            val newLastLocation = constructLastLocation(location)
            lastLocation.postValue(newLastLocation)
            updateLocation(newLastLocation)
        }
    }

    override fun onDestroy() {
        isServiceRunning = false
        Timber.d("stop: Service Destroyed")
        stopForeground(true)
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        var isServiceRunning = false
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        val TAG: String = this::class.java.name
    }
}