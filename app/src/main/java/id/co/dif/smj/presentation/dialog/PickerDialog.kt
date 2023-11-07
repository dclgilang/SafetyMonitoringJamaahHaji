package id.co.dif.smj.presentation.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.DialogPickerBinding

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class PickerDialog(val data: List<String>, val onSelectedItem: (Int, String)->Unit) :
    BaseBottomSheetDialog<BaseViewModel, DialogPickerBinding, Any?>() {

    override val layoutResId = R.layout.dialog_picker

    companion object {
        fun newInstance(data: List<String>, onSelectedItem: (Int, String)->Unit): PickerDialog {
            return PickerDialog(data, onSelectedItem)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.txtTitle.text = "$tag :"
//        binding.pickerView.items = generatePickerItems()
//        binding.btnChoose.setOnClickListener {
//            dismiss()
//            onSelectedItem(binding.pickerView.selectedItem?.id ?: 0)
//        }

        binding.listView.adapter = ArrayAdapter(requireContext(), R.layout.item_picker, data)
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            dismiss()
            onSelectedItem(position, data[position])
        }

//        if (selected.isNotEmpty()) {
//            binding.pickerView.items.forEach {
//                if (it.title == selected) {
//                    binding.pickerView.setSelectedItem(it)
//                }
//            }
//        }
    }

//    private fun generatePickerItems(): List<Item> {
//        return mutableListOf<Item>().apply {
//            data.forEachIndexed { index, item ->
//                add(PickerItem(id = index, title = item))
//            }
//        }
//    }

}