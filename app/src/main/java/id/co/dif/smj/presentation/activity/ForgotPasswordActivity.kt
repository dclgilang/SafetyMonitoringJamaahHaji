package id.co.dif.smj.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.databinding.ActivityForgotPasswordBinding
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.viewmodel.ForgotPasswordViewModel

class ForgotPasswordActivity : BaseActivity<ForgotPasswordViewModel, ActivityForgotPasswordBinding>() {
    override val layoutResId = R.layout.activity_forgot_password

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnSend.setOnClickListener {
            viewModel.forgotPassord(
                email = binding.email.text.toString(),
            )
        }

        viewModel.responseForgotPassword.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.txtCheckYourmail.isVisible = true
                binding.yourMail.isVisible = true
                binding.btnNext.isVisible = true
                binding.btnSend.isVisible = false
                binding.email.isVisible = false
                binding.txtEmailAddress.isVisible = false
                binding.yourMail.text = it.data.email
                Toast.makeText(this, "Send email succes, check your email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

    }

}