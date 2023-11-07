package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentReturnSparePartBinding
import id.co.dif.smj.presentation.adapter.ReturnSparePartAdapter


class ReturnSparePartFragment : BaseFragment<BaseViewModel, FragmentReturnSparePartBinding>() {
    override val layoutResId = R.layout.fragment_return_spare_part

    lateinit var adapter: ReturnSparePartAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = ReturnSparePartAdapter()
        binding.rvSparepart.adapter = adapter

    }

}