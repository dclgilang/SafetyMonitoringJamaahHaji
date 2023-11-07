package id.co.dif.smj.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TabMenuItem
import id.co.dif.smj.databinding.ActivityPowerGeneratorDetailBinding
import id.co.dif.smj.presentation.adapter.ViewPagerAdapter
import id.co.dif.smj.presentation.fragment.BasicReadingFragment
import id.co.dif.smj.presentation.fragment.CctvFragment
import id.co.dif.smj.presentation.fragment.EnergyMonitorFragment
import id.co.dif.smj.presentation.fragment.PowerQualityFragment
import id.co.dif.smj.presentation.fragment.RealTimeReportFragment
import id.co.dif.smj.presentation.fragment.ReportingDashboardFragment
import id.co.dif.smj.viewmodel.HomeViewModel
import id.co.dif.smj.viewmodel.MapSiteViewModel

class PowerGeneratorDetailActivity : BaseActivity<MapSiteViewModel, ActivityPowerGeneratorDetailBinding>() {
    override val layoutResId = R.layout.activity_power_generator_detail

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        viewModel.responseaGetSiteByid.observe(lifecycleOwner){
            if(it.status == 200) {
                preferences.siteData.value= it.data
                val tabMenuItems = mutableListOf<TabMenuItem>()
                tabMenuItems.add(TabMenuItem(getString(R.string.energy_monitor), EnergyMonitorFragment()))
                tabMenuItems.add(TabMenuItem(getString(R.string.basic_reading), BasicReadingFragment()))
                tabMenuItems.add(TabMenuItem(getString(R.string.basic_reading), PowerQualityFragment()))

                binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }
        }

        viewModel.getSiteById(preferences.selectedSite.value?.site_id)

    }

}