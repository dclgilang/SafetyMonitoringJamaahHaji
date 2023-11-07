package id.co.dif.smj.presentation.activity

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.data.TabMenuItem
import id.co.dif.smj.databinding.ActivityMapSiteBinding
import id.co.dif.smj.presentation.adapter.ViewPagerAdapter
import id.co.dif.smj.presentation.fragment.HistoryFragment
import id.co.dif.smj.presentation.fragment.SiteInfoFragment
import id.co.dif.smj.viewmodel.MapSiteViewModel

class MapSiteActivity : BaseActivity<MapSiteViewModel, ActivityMapSiteBinding>() {
    override val layoutResId = R.layout.activity_map_site


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        viewModel.responseaGetSiteByid.observe(lifecycleOwner){
            if(it.status == 200) {
                preferences.siteData.value= it.data
                val tabMenuItems = mutableListOf<TabMenuItem>()
                tabMenuItems.add(TabMenuItem(getString(R.string.site_info), SiteInfoFragment()))
                tabMenuItems.add(TabMenuItem(getString(R.string.history),HistoryFragment()))

                binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }
        }

        viewModel.getSiteById(preferences.selectedSite.value?.site_id)

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}