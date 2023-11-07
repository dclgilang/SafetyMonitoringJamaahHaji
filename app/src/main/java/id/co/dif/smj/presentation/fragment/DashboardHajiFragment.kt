package id.co.dif.smj.presentation.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentDashboardBinding
import id.co.dif.smj.databinding.FragmentDashboardHajiBinding
import id.co.dif.smj.databinding.ItemChartSelectedPopupBinding


class DashboardHajiFragment : BaseFragment<BaseViewModel, FragmentDashboardHajiBinding>() {
    override val layoutResId = R.layout.fragment_dashboard_haji


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

//        val entries = ArrayList<BarEntry>()
//        entries.add(BarEntry(1f, 4f))
//        entries.add(BarEntry(2f, 8f))
//        entries.add(BarEntry(3f, 6f))
//        entries.add(BarEntry(4f, 2f))
//        entries.add(BarEntry(5f, 5f))
//
//        val dataSet = BarDataSet(entries, "Sample Data")
//        dataSet.color = Color.BLUE
//
//        val barData = BarData(dataSet)
//
//        binding.barChartKuotaHaji.data = barData
//
//        // Customize the chart
//        val description = Description()
//        description.text = "3D Bar Chart Example"
//        binding.barChartKuotaHaji.description = description
//
//        binding.barChartKuotaHaji.setDrawValueAboveBar(true)
//        binding.barChartKuotaHaji.isHighlightFullBarEnabled = false
//        binding.barChartKuotaHaji.description.isEnabled = false
//
//        binding.barChartKuotaHaji.animateY(1000)

        dataKuotaHaji()
        dataJumlahUmrah()


    }

    private fun dataKuotaHaji(){
        val entries = ArrayList<BarEntry>()
        val startYear = 2000
        val endYear = 2023
        for (year in startYear..endYear) {
            val randomQuota = (1000..10000).random().toFloat()
            entries.add(BarEntry(year.toFloat(), randomQuota))
        }
        val dataSet = BarDataSet(entries, "Hajj Quotas")
        dataSet.color = Color.BLUE
        val barData = BarData(dataSet)
        binding.barChartKuotaHaji.data = barData
        binding.barChartKuotaHaji.description.isEnabled = false

        val description = binding.barChartKuotaHaji.description
        description.text = "Hajj Quotas from 2000 to 2023"
        description.textColor = Color.BLUE
        binding.barChartKuotaHaji.invalidate()
    }

    private fun dataJumlahUmrah(){
        val years = (2000..2023).map { it.toString() }
        val pilgrimsData = listOf(
            20000f, 25000f, 28000f, 32000f, 35000f, 40000f, 42000f, 45000f, 48000f,
            52000f, 55000f, 58000f, 62000f, 65000f, 70000f, 72000f, 75000f, 78000f,
            82000f, 85000f, 88000f, 92000f, 95000f, 100000f, 102000f
        )

        val entries = ArrayList<BarEntry>()
        for ((i, pilgrims) in pilgrimsData.withIndex()) {
            entries.add(BarEntry(i.toFloat(), pilgrims))
        }
        val dataSet = BarDataSet(entries, "Umrah Pilgrims from Indonesia")
        dataSet.color = Color.RED
        val barData = BarData(dataSet)
        binding.barChartJumlahUmrah.data = barData
        val description = Description()
        description.text = "Umrah Pilgrims from Indonesia (2000-2023)"
        description.textColor = Color.RED
        binding.barChartJumlahUmrah.description = description
        binding.barChartJumlahUmrah.setDrawValueAboveBar(true)
        binding.barChartJumlahUmrah.animateY(1000)
        binding.barChartJumlahUmrah.invalidate()
    }

}