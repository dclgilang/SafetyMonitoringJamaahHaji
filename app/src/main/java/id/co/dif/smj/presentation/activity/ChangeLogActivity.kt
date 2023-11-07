package id.co.dif.smj.presentation.activity

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.ActivityChangeLogBinding

class ChangeLogActivity : BaseActivity<BaseViewModel, ActivityChangeLogBinding>() {
    override val layoutResId = R.layout.activity_change_log

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked ={
            onBackPressedDispatcher.onBackPressed()
        }
    }

}