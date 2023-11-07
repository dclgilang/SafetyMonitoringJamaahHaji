package id.co.dif.smj.presentation.dialog

import android.os.Bundle
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Note
import id.co.dif.smj.databinding.DialogNotesPopupBinding
import id.co.dif.smj.presentation.adapter.TimeLineAdapter

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class NotePopupDialog(val notes: List<Note>) :
    BaseBottomSheetDialog<BaseViewModel, DialogNotesPopupBinding, Any?>() {

    override val layoutResId = R.layout.dialog_notes_popup
    companion object {
        fun newInstance(notes: List<Note>) = NotePopupDialog(notes)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rvTimeline.adapter = TimeLineAdapter().also {
            it.data.addAll(notes)
            it.notifyItemRangeChanged(0, notes.size)
            it.viewingOnly = true
        }
    }
}