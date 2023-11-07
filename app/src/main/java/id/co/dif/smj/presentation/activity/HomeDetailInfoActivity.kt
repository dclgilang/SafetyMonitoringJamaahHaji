package id.co.dif.smj.presentation.activity

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TabMenuItem
import id.co.dif.smj.databinding.ActivityHomeDetailInfoBinding
import id.co.dif.smj.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.smj.presentation.adapter.ViewPagerAdapter
import id.co.dif.smj.presentation.fragment.CctvFragment
import id.co.dif.smj.presentation.fragment.HealthFragment
import id.co.dif.smj.presentation.fragment.HistoryFragment
import id.co.dif.smj.presentation.fragment.MapsTicketFragment
import id.co.dif.smj.presentation.fragment.MovementHistoryFragment
import id.co.dif.smj.presentation.fragment.RealTimeReportFragment
import id.co.dif.smj.presentation.fragment.ReportingDashboardFragment
import id.co.dif.smj.presentation.fragment.SiteInfoFragment
import id.co.dif.smj.presentation.fragment.UnitLocationFragment
import id.co.dif.smj.presentation.fragment.UserInfoFragment
import id.co.dif.smj.viewmodel.MapSiteViewModel
import org.koin.core.component.inject
import java.awt.font.NumericShaper.Range

class HomeDetailInfoActivity : BaseActivity<MapSiteViewModel, ActivityHomeDetailInfoBinding>() {
    override val layoutResId = R.layout.activity_home_detail_info

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

    binding.rootLayout.onBackButtonClicked = {
        onBackPressedDispatcher.onBackPressed()
        stopDataUpdateInFragment()
    }

        val tabMenuItems = mutableListOf<TabMenuItem>()
        tabMenuItems.add(TabMenuItem(getString(R.string.user_information), UserInfoFragment()))
        tabMenuItems.add(TabMenuItem(getString(R.string.healthy), HealthFragment()))
        tabMenuItems.add(TabMenuItem(getString(R.string.movement_history), MovementHistoryFragment()))

        binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

//    viewModel.responseaGetSiteByid.observe(lifecycleOwner){
//        if(it.status == 200) {
//            preferences.siteData.value= it.data
//            val tabMenuItems = mutableListOf<TabMenuItem>()
//            tabMenuItems.add(TabMenuItem(getString(R.string.user_information), UserInfoFragment()))
//            tabMenuItems.add(TabMenuItem(getString(R.string.healthy), HealthFragment()))
//            tabMenuItems.add(TabMenuItem(getString(R.string.movement_history), MovementHistoryFragment()))
//
//            binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
//            binding.tabLayout.setupWithViewPager(binding.viewPager)
//        }
//    }

//    viewModel.getSiteById(preferences.selectedSite.value?.site_id)

}

    private fun stopDataUpdateInFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("realtimeFragmentTag") as RealTimeReportFragment?
        fragment?.stopDataUpdate()
    }

}