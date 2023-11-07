package id.co.dif.smj.data

import androidx.annotation.ColorRes
import id.co.dif.smj.R

sealed class TicketSeverity(
    val label: String,
    @ColorRes val background: Int,
    @ColorRes val foreground: Int
) {
    object Emergency : TicketSeverity(
        label = "Emergency",
        background = R.color.alpha_20_red,
        foreground = R.color.red
    )

    object Major : TicketSeverity(
        label = "Major",
        background = R.color.alpha_20_light_orange,
        foreground = R.color.light_orange,
    )

    object Minor : TicketSeverity(
        label = "Minor",
        background = R.color.alpha_20_green,
        foreground = R.color.green,
    )

    object Low : TicketSeverity(
        label = "Low",
        background = R.color.alpha_20_blue,
        foreground = R.color.blue,
    )
    object Unknown : TicketSeverity(
        label = "Low",
        background = R.color.transparent,
        foreground = R.color.transparent,
    )


    companion object {
        fun fromLabel(label: String): TicketSeverity {
            return when (label) {
                "Emergency" -> Emergency
                "Major" -> Major
                "Minor" -> Minor
                "Low" -> Low
                else -> Unknown
            }
        }

        fun getAll(): List<TicketSeverity> {
            return listOf(Emergency, Major, Minor, Low)
        }
    }
}