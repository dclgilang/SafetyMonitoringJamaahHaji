package id.co.dif.smj.utils

sealed class TrendType {
    companion object{
        const val DAILY = "daily"
        const val MONTHLY = "month"
        const val YEARLY = "yearly"
    }
}