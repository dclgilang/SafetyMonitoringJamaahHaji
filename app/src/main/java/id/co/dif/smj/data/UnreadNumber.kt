package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class UnreadNumber(
    @SerializedName("num_unread_message") val unreadNumberMessage: Int,
    @SerializedName("num_unread_notif") val unreadNumberNotification: Int
) : BaseData()
