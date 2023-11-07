package id.co.dif.smj.presentation.dialog

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.MarkerItem
import id.co.dif.smj.databinding.DialogMarkerInfoBinding

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class MarkerInfoDialog(val location: MarkerItem?) : BaseBottomSheetDialog<BaseViewModel, DialogMarkerInfoBinding, Any?>() {

    override val layoutResId = R.layout.dialog_marker_info

    companion object {
        fun newInstance(location: MarkerItem?) = MarkerInfoDialog(location)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.textName.text = location?.tic_pos
        binding.textDescription.text = location?.tic_site

    }

}