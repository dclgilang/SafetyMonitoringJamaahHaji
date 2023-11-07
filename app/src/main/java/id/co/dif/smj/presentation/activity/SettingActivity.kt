package id.co.dif.smj.presentation.activity

import android.content.Intent
import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.ActivitySettingBinding
import id.co.dif.smj.utils.stopLocationServiceScheduler

class SettingActivity : BaseActivity<BaseViewModel, ActivitySettingBinding>() {
    override val layoutResId= R.layout.activity_setting


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.txAccountSetting.setOnClickListener {
            startActivity(Intent(this, AccountSettingActivity::class.java))
        }
        binding.txActivity.setOnClickListener {
            startActivity(Intent(this, Activity::class.java))
        }
       binding.txChangePw.setOnClickListener {
           startActivity(Intent(this, ChangePasswordActivity::class.java))
       }
        binding.txChangeLog.setOnClickListener {
            startActivity(Intent(this, ChangeLogActivity::class.java))
        }
        binding.idLogout.setOnClickListener {
            preferences.wipe()
            stopLocationServiceScheduler()
            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        }
    }

}