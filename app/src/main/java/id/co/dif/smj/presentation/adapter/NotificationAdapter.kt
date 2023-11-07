package id.co.dif.smj.presentation.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.marlonlom.utilities.timeago.TimeAgoMessages
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewHolder
import id.co.dif.smj.data.MessageNotification
import id.co.dif.smj.data.Notification
import id.co.dif.smj.databinding.ItemNotificationBinding
import id.co.dif.smj.databinding.ItemNotificationLoadingBinding
import id.co.dif.smj.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class NotificationAdapter(data: List<MessageNotification> = listOf()) :
    BaseAdapter<NotificationViewModel, ViewBinding, MessageNotification>() {


    init {
        this.data.clear()
        this.data.addAll(data)
    }

    override val layoutResId = R.layout.item_notification
    var timeInMillis = System.currentTimeMillis()
//    val text: String = TimeAgo.using(timeInMillis)
    var onNotificationClicked: (index: Int, data: MessageNotification) -> Unit = { _, _ -> }
    var onNotificationLongClick: (item: MessageNotification) -> Unit = { _ -> }

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_LOADING = 2
    }


    init {
        this.data.addAll(data)
    }


    var isLoading = false


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val binding = when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                ItemNotificationBinding.inflate(inflater, parent, false)
            }

            ITEM_VIEW_TYPE_LOADING -> {
                ItemNotificationLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) ITEM_VIEW_TYPE_LOADING else ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(MessageNotification(0, "", "", "", "",  "", mesShow = false))
    }

    fun add(activeUser: MessageNotification) {
        data.add(activeUser)
        notifyItemInserted(data.lastIndex)
    }


    fun addAll(newData: List<MessageNotification>) {
        for (result in newData) {
            add(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): MessageNotification {
        return data[position]
    }

    override fun onLoadItem(binding: ViewBinding, data: MutableList<MessageNotification>, position: Int) {

        when (getItemViewType(position)) {
            ITEM_VIEW_TYPE_CONTENT -> {
                binding as ItemNotificationBinding
                val item = data[position]
                binding.notifDate.text = parseTimeToAgo(item.mesDate)
                binding.ticketNumber.text = "Ticket #${item.mesReffId}"
                binding.notifMessage.text = item.mesContent
                binding.isRead = !item.mesShow
                binding.background.setOnClickListener {
                    onNotificationClicked(position, item)
                }
                binding.background.setOnLongClickListener {
                    onNotificationLongClick(item)
                    true
                }
            } 

            else -> {

            }
        }
    }

    private fun parseTimeToAgo(timeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedTime = dateFormat.parse(timeString)

        val timeAgo = TimeAgo.using(parsedTime.time, TimeAgoMessages.Builder().withLocale(Locale.getDefault()).build())

        return timeAgo
    }


}