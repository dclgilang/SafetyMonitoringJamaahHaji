package id.co.dif.smj.presentation.dialog

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.databinding.FragmentDialogUploadProfileCoverBinding
import id.co.dif.smj.viewmodel.EditProfileViewModel

class DialogUploadProfileCover(val onClickProfile:()->Unit, val onClickCover:()->Unit) : BaseBottomSheetDialog<EditProfileViewModel, FragmentDialogUploadProfileCoverBinding, Any?>() {
    override val layoutResId = R.layout.fragment_dialog_upload_profile_cover

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        binding.btnProfile.setOnClickListener {
            dialog?.dismiss()
            onClickProfile()
        }

        binding.btnCover.setOnClickListener {
            dialog?.dismiss()
            onClickCover.invoke()
        }


    }

}