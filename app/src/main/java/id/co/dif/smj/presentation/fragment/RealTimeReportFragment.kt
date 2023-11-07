package id.co.dif.smj.presentation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentRealTimeReportBinding
import id.co.dif.smj.databinding.ItemChartSelectedPopupBinding
import id.co.dif.smj.utils.populateDottedCurvedLine
import java.util.Random
import kotlin.math.roundToInt


class RealTimeReportFragment : BaseFragment<BaseViewModel, FragmentRealTimeReportBinding>() {
    override val layoutResId = R.layout.fragment_real_time_report

    private var currentChartSelectedChipAnchor: View? = null
    private lateinit var chartSelectedChip: PopupWindow
    private lateinit var chartSelectedChipBinding: ItemChartSelectedPopupBinding
    private val handler = Handler(Looper.getMainLooper())
    private val random = java.util.Random()
    private val stableProgressRange = 50..60
    private val jumpingProgressRange = 80..100
    private var isStableRange = true

    private var currentProgress = 90 // Initial progress

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        val range = com.ekn.gruzer.gaugelibrary.Range()
        range.color = Color.parseColor("#F44336")
        range.from = 0.0
        range.to = 50.0

        val range2 = com.ekn.gruzer.gaugelibrary.Range()
        range2.color = Color.parseColor("#FFEB3B")
        range2.from = 50.0
        range2.to = 100.0

        val range3 = com.ekn.gruzer.gaugelibrary.Range()
        range3.color = Color.parseColor("#4CAF50")
        range3.from = 100.0
        range3.to = 150.0

        //add color ranges to gauge
        binding.gauge.addRange(range)
        binding.gauge.addRange(range2)
        binding.gauge.addRange(range3)

        //set min max and current value
        binding.gauge.minValue = 0.0
        binding.gauge.maxValue = 150.0
//        binding.gauge.value = 120.0

       // Dummy data Fuel Consumtion
        val dailyEntries = ArrayList<Entry>()
        for (day in 1..30) { // Assuming 30 days in a month
            val consumption = random.nextFloat() * 250 // Random consumption value
            dailyEntries.add(Entry(day.toFloat(), consumption))
        }
        val dailyDataSet = LineDataSet(dailyEntries, "Daily Consumption")
        dailyDataSet.color = Color.BLUE
        dailyDataSet.lineWidth = 2f
        dailyDataSet.setDrawValues(true)
        dailyDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val totalEntries = ArrayList<Entry>()
        for (month in 1..30) {
//            val consumption = Random.nextFloat() * 1000
            val consumption = random.nextFloat() * 1000
            totalEntries.add(Entry(month.toFloat(), consumption))
        }
        val totalDataSet = LineDataSet(totalEntries, "Total Consumption in a Month")
        totalDataSet.color = Color.RED
        totalDataSet.lineWidth = 2f
        totalDataSet.setDrawValues(true)
        totalDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val lineDataFuelConsumtion = LineData(dailyDataSet, totalDataSet)

        binding.piechart.data = lineDataFuelConsumtion
        binding.piechart.invalidate()


        // Set for Dummy data Temperature
        binding.chartNumberOfUpload.description.isEnabled = true
        binding.chartNumberOfUpload.setTouchEnabled(true)
        binding.chartNumberOfUpload.isDragEnabled = true
        binding.chartNumberOfUpload.setScaleEnabled(true)
        binding.chartNumberOfUpload.setPinchZoom(true)
        binding.chartNumberOfUpload.setBackgroundColor(Color.TRANSPARENT)

        val xAxis: XAxis = binding.chartNumberOfUpload.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE

        val yAxisLeft: YAxis = binding.chartNumberOfUpload.axisLeft
        yAxisLeft.textColor = Color.WHITE
        yAxisLeft.setDrawGridLines(true)

        val yAxisRight: YAxis = binding.chartNumberOfUpload.axisRight
        yAxisRight.isEnabled = false

        val legend: Legend = binding.chartNumberOfUpload.legend
        legend.form = Legend.LegendForm.LINE
        legend.textColor = Color.WHITE


        val otherentries = ArrayList<Entry>()
        val random = Random()
        for (i in 0 until 10) {
            val xValue = i.toFloat()
            val yValue = random.nextFloat() * 100
            otherentries.add(Entry(xValue, yValue))
        }

        val dataSetTemperature = LineDataSet(otherentries, "Temperature")
        dataSetTemperature.color = Color.BLUE // Set line color
        dataSetTemperature.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSetTemperature.setDrawValues(true)

        val lineData = LineData(dataSetTemperature)

        binding.chartNumberOfUpload.data = lineData

        binding.chartNumberOfUpload.invalidate()

       startDataUpdate()

        startProgressUpdate()

    }

    private fun startDataUpdate() {
        handler.post(updateRunnable)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            val randomProgress = if (isStableRange) {
                stableProgressRange.random()
            } else {
                jumpingProgressRange.random()
            }
            binding.multigauge.progress = randomProgress
            binding.tvValueMultigauge.text = "$randomProgress°C"
            binding.tvTemperatureReal.text = "$randomProgress°C"

            val color = getColorForProgress(randomProgress)
            if (color != null) {
                binding.multigauge.setIndicatorColor(color)
            }
            val colorDrawable = ColorDrawable(randomProgress)
            binding.tvTitlePc.background = colorDrawable

            isStableRange = !isStableRange

            // for chart

            val xValue = System.currentTimeMillis()
            val yValue = random.nextFloat() * 100


            handler.postDelayed(this, 1000)
        }
    }

//    private fun updateDataPeriodically() {
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                val randomProgress =  if (isStableRange) {
//                    stableProgressRange.random()
//                } else {
//                    jumpingProgressRange.random()
//                }
//
//                binding.multigauge.progress = randomProgress
//                binding.tvValueMultigauge.text = "$randomProgress%"
//                binding.tvTemperatureReal.text =  "$randomProgress%"
//
//                val color = getColorForProgress(randomProgress)
//                binding.multigauge.setIndicatorColor(color)
//                val colorDrawable = ColorDrawable(randomProgress)
//                binding.tvTitlePc.background = colorDrawable
//
//                isStableRange = !isStableRange
//
//                handler.postDelayed(this, 1000)
//            }
//        }, 2000) // Initial delay of 1 second
//    }

    fun stopDataUpdate() {
        handler.removeCallbacks(updateRunnable)
    }
    private fun getColorForProgress(progress: Int): Int? {
        return when {
            progress in 0..31 -> context?.let { ContextCompat.getColor(it, R.color.green) }
            progress in 32..65 ->  context?.let { ContextCompat.getColor(it, carbon.R.color.carbon_yellow_a200)  }
            progress in 66..80 -> context?.let { ContextCompat.getColor(it, carbon.R.color.carbon_orange_100) }
            progress in 81..100  -> context?.let { ContextCompat.getColor(it, R.color.red) }
            else -> {
                context?.let { ContextCompat.getColor(it, R.color.red) }
            }
        }
    }


    class CustomValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "$value L"
        }

    }

    private fun setIndicatorColor(progress: Int) {
        when {
            progress >= 0 && progress < 30 -> binding.gauge.setGaugeBackGroundColor(Color.RED)
            progress >= 30 && progress < 60 -> binding.gauge.setGaugeBackGroundColor(Color.YELLOW)
            progress >= 60 && progress < 80 -> binding.gauge.setGaugeBackGroundColor(Color.GREEN)
            progress >= 80 -> binding.gauge.setGaugeBackGroundColor(Color.BLUE)
        }
    }

    private fun startProgressUpdate() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                currentProgress -= 1
                binding.gauge.setBackgroundColor(currentProgress)
                binding.gauge.value = currentProgress.toDouble()
//                setIndicatorColor(currentProgress)
                if (currentProgress > 0) {
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000) // Initial delay of 1 second
    }



}