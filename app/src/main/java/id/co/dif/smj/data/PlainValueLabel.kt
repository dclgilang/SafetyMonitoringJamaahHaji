package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class PlainValueLabel(
    @SerializedName("label", alternate = ["day"]) val label: String,
    @SerializedName("value", alternate = ["count", "ticket_count"]) val count: Int = 0
) : BaseData()
