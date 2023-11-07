package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class ShouldSendLocationUpdate(
    @SerializedName("should_not_send_location") val shouldNotSendLocation: Boolean
) : BaseData()
