package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.databinding.FragmentPopupUpMeItemBinding
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.base64ImageToBitmap
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.str
import id.co.dif.smj.viewmodel.BasicInfoViewModel


class PopUpProfileItemFragment(private val engineer: MarkerTripleE) :
    BaseFragment<BasicInfoViewModel, FragmentPopupUpMeItemBinding>() {
    override val layoutResId = R.layout.fragment_popup_up_me_item

    var directionIsAvailable = false
    var onViewProfileClicked: (engineer: MarkerTripleE) -> Unit = { _ -> }
    var onGetDirectionClicked: (engineer: MarkerTripleE) -> Unit = { _ -> }
    var shouldShowArrow = false

    companion object {
        fun newInstance(engineer: MarkerTripleE) = PopUpProfileItemFragment(engineer)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.getDetailProfile(engineer.id)
        binding.txtDistance.isVisible = directionIsAvailable
        binding.routeThisSite.isVisible = directionIsAvailable
        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.position.text = session?.emp_position.str
                binding.txtTechnicianName.text = session?.name.str

                it.data.photo_profile?.let{encoded ->
                    binding.imgProfile.setImageBitmap(base64ImageToBitmap(encoded))
                }

                binding.phone.text = it.data.phone.orDefault()
                binding.txtTechnicianName.text = it.data.fullname.orDefault()
                val info = preferences.myDetailProfile.value
                info?.let { myProfile ->
                    if (myProfile.id == engineer.id) {
                        binding.txtTechnicianName.text =
                            "${it.data.fullname.orDefault()} (You)"
                    }
                }
            }
        }
        binding.txtDistance.setOnClickListener {
            onGetDirectionClicked(engineer)
        }

        binding.viewArrow.isVisible = shouldShowArrow
        binding.viewArrow.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.anim_arrow_moving)
        )
        binding.btnViewProfile.setOnClickListener {
            onViewProfileClicked(engineer)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetailProfile(engineer.id)
    }

}