package id.co.dif.smj.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.databinding.ActivityMainBinding
import id.co.dif.smj.presentation.fragment.DashboardFragment
import id.co.dif.smj.presentation.fragment.HomeFragment
import id.co.dif.smj.presentation.fragment.InboxFragment
import id.co.dif.smj.presentation.fragment.MeFragment
import id.co.dif.smj.presentation.fragment.TroubleTicketFragment
import id.co.dif.smj.utils.drawableRes
import id.co.dif.smj.utils.eval
import id.co.dif.smj.utils.hasLocationPermission
import id.co.dif.smj.utils.hasPermission
import id.co.dif.smj.utils.isDeviceOnline
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.startLocationServiceScheduler
import id.co.dif.smj.viewmodel.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Timer
import java.util.TimerTask

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), KoinComponent {
    private val dashboardFragment: DashboardFragment = DashboardFragment()
    private val meFragment: MeFragment = MeFragment()
    private val inboxFragment: InboxFragment by inject()
    private val troubleTicketFragment: TroubleTicketFragment by inject()
    private val homeFragment: HomeFragment by inject()
    override val layoutResId = R.layout.activity_main
    private val PERMISSION_REQUEST_READ_CONTACTS = 1001

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ObsoleteSdkInt")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, you can proceed to access contacts
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_READ_CONTACTS)
        }


        binding.bottomNavigation.setOnItemSelectedListener {
            onNavigationItemSelected(it)
        }
        if (!viewModel.periodicOfflineConnectivityIsRunning) {
            startListenForConnectivity()
        }

        binding.onOff.setOnClickListener {
            if (binding.bottomNavigation.selectedItemId == R.id.menu_map) {
                homeFragment.resetViewport().log("ufuewhuewfew")
            } else {
                toggleMapButton(true)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.fragment_container_view, homeFragment, "HOME")
                    binding.bottomNavigation.selectedItemId = R.id.menu_map
                }
            }
        }


        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view, homeFragment)
            }
        }
        val profileId = intent.getIntExtra("open_profile", -1)
        if (profileId != -1) {
            openProfileDetail(profileId)
        }
        binding.locationUnavailableAlert.isVisible = !hasLocationPermission()

        requestLocationPermission(
            onGranted = {
                try {
                    startLocationServiceScheduler()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.locationUnavailableAlert.visibility = View.GONE
                requestNotificationPermission()
            },
            onDenied = {
                binding.locationUnavailableAlert.visibility = View.VISIBLE
                requestNotificationPermission()
            }
        )


    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val colorStateList = ContextCompat.getColorStateList(this, R.color.selector_bottom_nav_item)


        binding.bottomNavigation.itemTextColor = colorStateList
        binding.bottomNavigation.itemIconTintList = colorStateList
        (item.itemId == R.id.menu_map).not() && toggleMapButton(false)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (item.itemId) {
                R.id.menu_dashboard -> replace(R.id.fragment_container_view, dashboardFragment)
                R.id.menu_me -> replace(R.id.fragment_container_view, meFragment)

                R.id.previous -> {
                    showToast("Fitur is not ready")
                }

                R.id.next ->{
                    showToast("Fitur is not ready")
                }

                R.id.menu_map -> {
                    toggleMapButton(true)
                    replace(R.id.fragment_container_view, homeFragment, "HOME")

                }
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission(onDenied: () -> Unit, onGranted: () -> Unit) {
        getPermission(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            onDenied = {
                onDenied()
            },
            onGranted = {
                onGranted()
            }
        )
    }

    private fun requestNotificationPermission(onDenied: () -> Unit = {}, onGranted: () -> Unit = {}) {
        if (hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            onGranted()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPermission(
                android.Manifest.permission.POST_NOTIFICATIONS,
                onDenied = {
                    onDenied()
                },
                onGranted = {
                    onGranted()
                }
            )
        }
    }

    private fun toggleMapButton(active: Boolean): Boolean {
        binding.onOff.background =
            active.eval(R.drawable.bg_purple_gradient, R.drawable.bg_map_button_disabled)
                .drawableRes(this)
        return active
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    override fun onBackPressedFragment() {
        super.onBackPressedFragment()
        binding.bottomNavigation.selectedItemId = R.id.menu_dashboard
    }

    fun openProfileDetail(userId: Int) {
        val info = preferences.myDetailProfile.value
        info?.let {
            if (it.id == userId) {
                binding.bottomNavigation.selectedItemId = R.id.menu_me
            } else {
                needsImplementedToast("Go to other profile detail")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        session?.let {
            it.id?.let { id ->
                preferences.selectedProfileId.value = id.toInt()
            }
        }

    }

    private fun startListenForConnectivity() {
        Log.d(TAG, "startListenForConnectivity: ")
        viewModel.periodicOfflineConnectivityIsRunning = true
        viewModel.startPeriodicOfflineNotesUpload()
    }

    @SuppressLint("Range")
    private fun getContactNumbers() {
        val contactsList = mutableListOf<String>()
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(phoneNumber)
            }
        }

        // Now, contactsList contains the phone numbers
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can access contacts now
                    getContactNumbers()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                }
            }
        }
    }



}


