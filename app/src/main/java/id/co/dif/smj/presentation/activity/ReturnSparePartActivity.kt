package id.co.dif.smj.presentation.activity

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.ActivityReturnSparePartBinding

class ReturnSparePartActivity : BaseActivity<BaseViewModel, ActivityReturnSparePartBinding>() {
    override val layoutResId = R.layout.activity_return_spare_part
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btBack.setOnClickListener {
            onBackPressed()
        }
    }


}
