package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class TicketHandling(
    @SerializedName("ticket_handling") val ticketHandling: Int
) : BaseData()
