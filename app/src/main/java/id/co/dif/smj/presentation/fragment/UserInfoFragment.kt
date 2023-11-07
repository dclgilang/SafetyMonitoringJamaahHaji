package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentUserInfoBinding
import id.co.dif.smj.utils.base64ImageToBitmap
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.shimmerDrawable
import id.co.dif.smj.viewmodel.MapSiteViewModel

class UserInfoFragment : BaseFragment<MapSiteViewModel, FragmentUserInfoBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_user_info

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status == 200) {
//                viewModel.responseBasicInfoList()
                if (it.data.fullname != null) {
                    binding.name.setText("${it.data.fullname}")
                } else {
                    ""
                }
                it.data.photo_profile?.let { encoded ->
                    binding.image.setImageBitmap(base64ImageToBitmap(encoded))
                }
                binding.gender.text = it.data.gender.orDefault("")
                binding.age.text = it.data.age.orDefault("")

            }
        }

        var info = preferences.selectedProfileId.value


    }

    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(preferences.selectedSite.value?.id)
    }

}