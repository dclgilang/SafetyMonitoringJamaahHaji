package id.co.dif.smj.presentation.adapter
//import id.co.dif.smj.utils.parseTimeToDayAndHour
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.android.gms.maps.model.BitmapDescriptor
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.base.BaseViewHolder
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.Notification
import id.co.dif.smj.data.TicketSeverity
import id.co.dif.smj.data.TicketStatus
import id.co.dif.smj.data.TroubleTicket
import id.co.dif.smj.databinding.ItemNotificationLoadingBinding
import id.co.dif.smj.databinding.ItemTroubleTicketBinding
import id.co.dif.smj.presentation.activity.DetilTicketActivity
import id.co.dif.smj.utils.asBitmap
import id.co.dif.smj.utils.colorRes
import id.co.dif.smj.utils.drawableRes
import id.co.dif.smj.utils.formatDate
import id.co.dif.smj.utils.ifFalse
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.shimmerDrawable
import id.co.dif.smj.utils.str
import id.co.dif.smj.utils.toBitmapDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 10:46
 *
 */
class TroubleTicketAdapter() :
    BaseAdapter<BaseViewModel, ViewBinding, TroubleTicket>(), KoinComponent {
    override val layoutResId = R.layout.item_trouble_ticket
    val startDate = Date() // Replace with your actual start date
    val endDate = Date() // Replace with your actual end date
    val remainingTime = endDate.time - startDate.time
    var timeInMillis = System.currentTimeMillis()
    val text: String = TimeAgo.using(timeInMillis)

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_CONTENT_GRAYED = 3
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    var isLoading = false

    init {
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                ItemTroubleTicketBinding.inflate(inflater, parent, false)
            }

            ITEM_VIEW_TYPE_LOADING -> {
                ItemNotificationLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) ITEM_VIEW_TYPE_LOADING else ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(TroubleTicket())
    }

    fun add(troubleTicket: TroubleTicket) {
        data.add(troubleTicket)
        notifyItemInserted(data.lastIndex)
    }

    fun addAll(newData: List<TroubleTicket>) {
        for (result in newData) {
            add(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): TroubleTicket {
        return data[position]
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
    fun parseTimeToDayAndHour(timeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance()
        val parsedTime = dateFormat.parse(timeString)
//        parsedTime.time = dateFormat.parse(timeString)
        val durationMillis = now.timeInMillis - parsedTime.time
        val days = durationMillis / (1000 * 60 * 60 * 24)
        val hours = (durationMillis / (1000 * 60 * 60)) % 24
        val result = StringBuilder()
        if (days > 0) {
            result.append("$days day")
            if (days > 1) result.append("s")
            result.append(" ")
        }
        result.append("$hours hour")
        if (hours > 1) result.append("s")

        return result.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<TroubleTicket>,
        position: Int,
    ) {
        context?.let { context ->
            when (getItemViewType(position)) {
                NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                    binding as ItemTroubleTicketBinding
                    val item = data[position]
                    binding.imgLogo.loadImage(item.images, R.drawable.ic_bakti)
                    binding.txtTowerName.text = item.ticSite.orDefault()
                    binding.txtTtNumber.text = item.ticId.toString()
                    if (item.ticSite.isNullOrEmpty() ){
                        binding.alertNoSite.isVisible = true
                    } else {
                        binding.alertNoSite.isVisible = false
                    }
                   if (item.ticFieldEngineer == null || item.ticFieldEngineer!!.isEmpty() ){
                       binding.alertNoEngineer.isVisible = true
                   } else {
                       binding.alertNoEngineer.isVisible = false
                   }

                    binding.txtTechnicianName.text = item.ticFieldEngineer
                    binding.txtPicName.text = item.ticPersonInCharge
                    binding.txtDate.text = item.ticUpdated.formatDate("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd h:mm a").orDefault()
                    binding.etStatus.text = item.ticStatus.orDefault()
                    binding.txtCode.text = item.ticSiteId.orDefault()
                    TicketSeverity.fromLabel(item.ticSeverety.str).also {
                        binding.bgHeader.text = it.label.orDefault()
                        binding.bgHeader.backgroundTintList =
                            ColorStateList.valueOf(it.background.colorRes(context))
                        binding.bgHeader.setTextColor(it.foreground.colorRes(context))
                    }


                    TicketStatus.fromLabel(item.ticStatus.str).also {
                        val fg = it.foreground.colorRes(context)
                        val bg = it.background.colorRes(context)
                        TextViewCompat.setCompoundDrawableTintList(
                            binding.etStatus,
                            ColorStateList.valueOf(fg)
                        )
                        binding.etStatus.setTextColor(fg)
                    }


                    item.status_ticket.ifFalse {
                        val fg = R.color.extra_dark_gray.colorRes(context)
                        val bg = R.color.dark_grey.colorRes(context)

                        binding.bgHeader.backgroundTintList =
                            ColorStateList.valueOf(bg)
                        binding.bgHeader.setTextColor(R.color.white.colorRes(context))

                        binding.etStatus.backgroundTintList = ColorStateList.valueOf(bg)
                        TextViewCompat.setCompoundDrawableTintList(
                            binding.etStatus,
                            ColorStateList.valueOf(fg)
                        )
                        binding.etStatus.setTextColor(fg)
                        binding.background.setBackgroundColor(
                            R.color.bg_ticket_grey.colorRes(
                                context
                            )
                        )
                        binding.txtTtNumber.setTextColor(bg)
                        binding.txtTowerName.setTextColor(bg)
                    }

                    binding.background.setOnClickListener {
                        preferences.selectedTicketId.value = item.ticId
                        context.startActivity(Intent(context, DetilTicketActivity::class.java))
                    }.log("gilanglog : ${item.ticId}")

                    binding.timer.text = parseTimeToDayAndHour(item.ticReceived.toString())
                }
            }
        }
    }

}