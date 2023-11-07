package id.co.dif.smj.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentReportingDashboardBinding

class ReportingDashboardFragment : BaseFragment<BaseViewModel, FragmentReportingDashboardBinding>() {
    override val layoutResId = R.layout.fragment_reporting_dashboard

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val hoursConsumption = 250f
        val daysConsumption = 320f
        val weeksConsumption = 840f
        val monthsConsumption = 3.300f

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(hoursConsumption))
        entries.add(PieEntry(daysConsumption))
        entries.add(PieEntry(weeksConsumption))
        entries.add(PieEntry(monthsConsumption))

        val dataSet = PieDataSet(entries, "Fuel Consumption")
        dataSet.colors = mutableListOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
        dataSet.valueTextSize = 12f

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(CustomValueFormatter())

        binding.piechart.data = pieData

        // Set additional chart configurations
        binding.piechart.description.isEnabled = false
        binding.piechart.isRotationEnabled = true
        binding.piechart.isHighlightPerTapEnabled = true
        binding.piechart.setHoleColor(android.R.color.transparent)

        binding.piechart.invalidate()

    }


    class CustomValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "$value L"
        }

    }
}