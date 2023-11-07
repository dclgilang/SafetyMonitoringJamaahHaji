package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class ResponseForgetPassword(
    @SerializedName("email") val email: String,

) : BaseData()
