package id.co.dif.smj.presentation.adapter

import android.os.Build
import android.text.Html
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.changelog
import id.co.dif.smj.databinding.ItemChangeLogBinding

class ChangeLogAdapter() : BaseAdapter<BaseViewModel, ItemChangeLogBinding, changelog>() {
    override val layoutResId = R.layout.item_change_log
    override fun onLoadItem(
        binding: ItemChangeLogBinding,
        data: MutableList<changelog>,
        position: Int
    ) {
        val item = data[position]
        binding.date.setText(item.date)
        binding.time.setText(item.time)
        binding.name.setText(item.updated_by)
        binding.changedItem.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(item.changed_items, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(item.changed_items)
        }

    }
}
