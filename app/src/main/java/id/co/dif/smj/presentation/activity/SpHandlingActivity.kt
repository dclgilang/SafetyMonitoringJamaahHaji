package id.co.dif.smj.presentation.activity

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TabMenuItem
import id.co.dif.smj.databinding.ActivityReturnFormBinding
import id.co.dif.smj.presentation.adapter.ViewPagerAdapter
import id.co.dif.smj.presentation.fragment.ReturnFromFragment
import id.co.dif.smj.presentation.fragment.ReturnSparePartFragment

class SpHandlingActivity : BaseActivity<BaseViewModel, ActivityReturnFormBinding>() {

    override val layoutResId = R.layout.activity_return_form

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val tabMenuItems = mutableListOf<TabMenuItem>()
        tabMenuItems.add(TabMenuItem(getString(R.string.return_form), ReturnFromFragment()))
        tabMenuItems.add(TabMenuItem(getString(R.string.return_spare_part), ReturnSparePartFragment()))
        binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

    }


}