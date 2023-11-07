package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import android.view.View
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : BaseFragment<BaseViewModel, FragmentMaintenanceBinding>() {

    override val layoutResId = R.layout.fragment_maintenance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

    }
}