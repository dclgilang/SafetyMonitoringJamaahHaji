package id.co.dif.smj.presentation.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.databinding.DialogSelectEngineerBinding
import id.co.dif.smj.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.smj.presentation.fragment.SelectEngineerItemFragment
import id.co.dif.smj.utils.log

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class SelectEngineerDialog(private val technicians: List<MarkerTripleE>, val listener: Listener, val pingLocation: (MarkerTripleE)->Unit) :
    BaseBottomSheetDialog<BaseViewModel, DialogSelectEngineerBinding, Any?>() {

    override val layoutResId = R.layout.dialog_select_engineer

    companion object {
        fun newInstance(technicians: List<MarkerTripleE>, listener: Listener, pingLocation: (MarkerTripleE)->Unit) =
            SelectEngineerDialog(technicians, listener, pingLocation)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)

        val fragments = technicians.map {
            SelectEngineerItemFragment(it)
        }

        fragments.forEachIndexed { index, fragment ->
            fragment.onSelectEngineerClicked = { location ->
                "selected".log()
                dialog?.dismiss()
                listener.onSelectedEngineer(location)
            }
            fragment.onClickPing = {
                dialog?.dismiss()
                listener.pingLocation(it)
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

    interface Listener {
        fun onSelectedEngineer(marker: MarkerTripleE?)
        fun pingLocation(engineer : MarkerTripleE)
    }
}