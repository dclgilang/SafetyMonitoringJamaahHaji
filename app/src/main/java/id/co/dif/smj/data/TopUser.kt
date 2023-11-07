package id.co.dif.smj.data

data class TopUser(
    val userPictureUrl: String? = null,
    val username: String,
    val position: String,
    val timeSpent: String
) : BaseData()
