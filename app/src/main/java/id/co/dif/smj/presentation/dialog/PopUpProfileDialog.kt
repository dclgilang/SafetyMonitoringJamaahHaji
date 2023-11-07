package id.co.dif.smj.presentation.dialog

import android.content.Intent
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.BasicInfo
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.databinding.FragmentPopUpMeBinding
import id.co.dif.smj.presentation.activity.EngineerProfileActivity
import id.co.dif.smj.presentation.activity.MainActivity
import id.co.dif.smj.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.smj.presentation.fragment.PopUpProfileItemFragment


class PopUpProfileDialog(
    private val technicians: List<MarkerTripleE>,
    private val directionIsAvailable: Boolean = false,
    val onGetDirectionClicked: (engineer: MarkerTripleE) -> Unit
) :
    BaseBottomSheetDialog<BaseViewModel, FragmentPopUpMeBinding, BasicInfo>() {
    override val layoutResId = R.layout.fragment_pop_up_me

    companion object {
        fun newInstance(
            id: List<MarkerTripleE>,
            directionIsAvailable: Boolean = false,
            onGetDirectionClicked: (engineer: MarkerTripleE) -> Unit = {}
        ) = PopUpProfileDialog(id, directionIsAvailable, onGetDirectionClicked)

        fun newInstance(
            id: MarkerTripleE,
            directionIsAvailable: Boolean = false,
            onGetDirectionClicked: (engineer: MarkerTripleE) -> Unit = {}
        ) =
            PopUpProfileDialog(listOf(id), directionIsAvailable, onGetDirectionClicked)
    }


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)

        val fragments = technicians.map {
            PopUpProfileItemFragment(it)
        }


        fragments.forEachIndexed { index, fragment ->
            fragment.onGetDirectionClicked = { engineer ->
                onGetDirectionClicked(engineer)
                dismiss()
            }
            fragment.directionIsAvailable = directionIsAvailable
            fragment.onViewProfileClicked = { engineer ->
                dialog?.dismiss()
                val info = preferences.myDetailProfile.value
                info?.let {

                    val profileId = engineer.id ?: -1
                    preferences.selectedProfileId.value=profileId
                    val intent = Intent(currentActivity, EngineerProfileActivity::class.java)
                    if (it.id == engineer.id) {
                        if (currentActivity is MainActivity) {
                            (currentActivity as MainActivity).openProfileDetail(profileId)
                        } else {
                            currentActivity.startActivity(intent)
                        }
                    } else {
                        currentActivity.startActivity(intent)
                    }
                }
            }
            fragment.shouldShowArrow = index < fragments.lastIndex
        }

        val arrayListFragments =
            arrayListOf<BaseFragment<out BaseViewModel, out ViewDataBinding>>().also {
                it.addAll(fragments)
            }
        val titles = technicians.map { it.name.toString() }

        viewPagerAdapter.replaceAll(arrayListFragments, titles)
        binding.viewPager.adapter = viewPagerAdapter
//

    }
}