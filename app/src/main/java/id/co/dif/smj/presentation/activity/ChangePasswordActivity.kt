package id.co.dif.smj.presentation.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.databinding.ActivityChangePasswordBinding
import id.co.dif.smj.viewmodel.ChangePasswordViewModel

class ChangePasswordActivity : BaseActivity<ChangePasswordViewModel, ActivityChangePasswordBinding>() {
    override val layoutResId = R.layout.activity_change_password

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.save.setOnClickListener {
            val oldpasswordValue = binding.oldPassword.text.toString()
            val newpasswordValue = binding.newPassword.text.toString()
            val retypepasswordValue = binding.confirmNewPassword.text.toString()
            var isValid = true
            if (TextUtils.isEmpty(oldpasswordValue)) {
                binding.oldPassword.error = "Old Password is required"
                isValid = false
            }
            if (TextUtils.isEmpty(newpasswordValue)) {
                binding.newPassword.error = "New Password is required"
                isValid = false
            }
            if (TextUtils.isEmpty(retypepasswordValue)) {
                binding.confirmNewPassword.error = "retype Password is required"
                isValid = false
            }
            if (newpasswordValue == oldpasswordValue) {
                showToast("Old password is the same to the new one")
                isValid = false
            }
            if (isValid) {
                viewModel.changepassword(
                    mutableMapOf(
                        "old_password" to binding.oldPassword.text.toString(),
                        "new_password" to binding.newPassword.text.toString(),
                        "confirm_new_password" to binding.confirmNewPassword.text.toString()
                    )
                )
            }
        }

        viewModel.responseChangePassword.observe(lifecycleOwner) {
            Log.d(TAG, "onViewBindingCredfdfated: $it")
            if (it.status == 200) {
                onBackPressed()
                showSuccessMessage(this, "Changes Password successfully!")

            } else {
                showToast(it.message)
            }
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}