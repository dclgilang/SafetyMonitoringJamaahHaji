package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

data class EngineerWithinRadiusStatus(
    @SerializedName("is_within_site_radius") val isWithinRadiusStatus: Boolean
) : BaseData()
