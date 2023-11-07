package id.co.dif.smj.data

import androidx.annotation.ColorRes
import id.co.dif.smj.R

sealed class TicketStatus(
    val label: String,
    @ColorRes val foreground: Int,
    @ColorRes val background: Int
) {
    object Assigned : TicketStatus(
        label = "Assigned",
        background = R.color.alpha_20_dark_green,
        foreground = R.color.dark_green
    )

    object OnProgress : TicketStatus(
        label = "On-Progress",
        background = R.color.alpha_20_dark_pink,
        foreground = R.color.dark_pink,
    )

    object OnGoing : TicketStatus(
        label = "On-Going",
        background = R.color.alpha_20_dark_pink,
        foreground = R.color.dark_pink,
    )

    object Escalated : TicketStatus(
        label = "Escalated",
        background = R.color.alpha_20_green_moss,
        foreground = R.color.green_moss,
    )

    object Completed : TicketStatus(
        label = "Completed",
        background = R.color.alpha_20_dark_purple,
        foreground = R.color.dark_purple,
    )

    object Pending : TicketStatus(
        label = "Pending",
        background = R.color.alpha_20_red,
        foreground = R.color.red,
    )

    object Closed : TicketStatus(
        label = "Closed",
        background = R.color.alpha_20_black,
        foreground = R.color.black,
    )

    object Other : TicketStatus(
        label = "Other",
        background = R.color.transparent,
        foreground = R.color.transparent,
    )


    companion object {
        fun fromLabel(label: String): TicketStatus {
            return when (label.lowercase()) {
                "assigned" -> Assigned
                "on-progress" -> OnProgress
                "on-going" -> OnGoing
                "escalated" -> Escalated
                "completed" -> Completed
                "pending" -> Pending
                "closed" -> Closed
                else -> Other
            }
        }

        fun getAll(except: List<TicketStatus> = listOf()): List<TicketStatus> {
            val all = mutableListOf(Assigned, OnProgress, OnGoing, Escalated, Completed, Pending, Closed)
            except.forEach { all.remove(it) }
            return all
        }
    }
}