package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.databinding.FragmentSiteBinding
import id.co.dif.smj.presentation.activity.SiteViewModel

class SiteFragment : BaseFragment<SiteViewModel, FragmentSiteBinding>() {
    override val layoutResId = R.layout.fragment_site
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        setupData()
    }


    fun setupData(){
        val data = preferences.ticketDetails.value
        binding.siteInfo = data?.site_info
    }
    override fun refresh() {
        setupData()
    }
}