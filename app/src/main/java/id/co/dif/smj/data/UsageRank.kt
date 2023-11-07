package id.co.dif.smj.data

data class UsageRank(
    val profilePicUrl: String? = null,
    val organization : String,
    val profileCompletion: String,
    val username: String,
    val position: Int,
): BaseData()
