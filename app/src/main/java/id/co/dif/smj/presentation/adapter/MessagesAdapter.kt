package id.co.dif.smj.presentation.adapter

import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.TroubleTicketTimeline
import id.co.dif.smj.databinding.ItemMessageNotificationBinding


class MessagesAdapter() :
    BaseAdapter<BaseViewModel, ItemMessageNotificationBinding, TroubleTicketTimeline>() {


    override val layoutResId = R.layout.item_message_notification
    override fun onLoadItem(
        binding: ItemMessageNotificationBinding,
        data: MutableList<TroubleTicketTimeline>,
        position: Int
    ) {
//        val item = data[position]
//        binding.time.text = item.time
//        binding.date.text = item.date
//        binding.status.text = item.status
//        binding.name.text = item.name


    }

    override fun getItemCount(): Int {
        return 5
    }


}