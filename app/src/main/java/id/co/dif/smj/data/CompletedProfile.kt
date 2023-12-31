package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class CompletedProfile(
    @SerializedName("score") val score: Int,
    @SerializedName("validasi") val text: String
) : BaseData()