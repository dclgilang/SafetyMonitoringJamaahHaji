package id.co.dif.smj.presentation.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.databinding.FragmentBasicReadingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BasicReadingFragment : BaseFragment<BaseViewModel, FragmentBasicReadingBinding>() {
    override val layoutResId = R.layout.fragment_basic_reading
    private val calendar = Calendar.getInstance()
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

//        setupCurrentData()
//        setupDataEnergy()
//        setupDataPower()
//        setupDataVoltage()

        setupChart(binding.chartcurrent, currentData)
        setupChart(binding.chartenergy, energyData)
        setupChart(binding.chartpower, powerData)
        setupChart(binding.chartvoltage, voltageData)

        binding.btnDate.setOnClickListener {
            showDateTimePickerDialog()
        }

    }

    private fun setupChart(chart: BarChart, data: List<BarEntry>) {
        chart.description.isEnabled = false
        chart.setBackgroundColor(Color.TRANSPARENT)

        val dataSet = BarDataSet(data, "Data")
        dataSet.colors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW)

        val barData = BarData(dataSet)
        chart.data = barData
        chart.invalidate()

        chart.xAxis.textSize = 8f
        chart.xAxis.textColor = Color.WHITE
        chart.axisLeft.textSize = 8f
        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.textSize = 8f
        chart.axisRight.textColor = Color.WHITE
    }

        private fun setupCurrentData(data: List<BarEntry>){

        binding.chartenergy.description.isEnabled = false
        binding.chartenergy.setBackgroundColor(Color.TRANSPARENT)

        val dataCurrent = mutableListOf<BarEntry>()
        dataCurrent.add(BarEntry(1f, 110f))
        dataCurrent.add(BarEntry(2f, 150f))
        dataCurrent.add(BarEntry(3f, 180f))
        dataCurrent.add(BarEntry(4f, 210f))

        val dataSetCurrent = BarDataSet(dataCurrent, "Current Data")
        dataSetCurrent.colors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW)
        val barCurrentData = BarData(dataSetCurrent)

        binding.chartcurrent.data = barCurrentData
        binding.chartcurrent.invalidate()

        binding.chartcurrent.xAxis.textSize = 8f
        binding.chartcurrent.xAxis.textColor = Color.WHITE
        binding.chartcurrent.axisLeft.textSize = 8f
        binding.chartcurrent.axisLeft.textColor = Color.WHITE

        binding.chartcurrent.axisRight.textSize = 8f
        binding.chartcurrent.axisRight.textColor = Color.WHITE
    }

    private fun setupDataEnergy(data: List<BarEntry>){
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(1f, 100f))
        entries.add(BarEntry(2f, 150f))
        entries.add(BarEntry(3f, 120f))

        val dataSet = BarDataSet(entries, "Energy Data")
        dataSet.colors = listOf(Color.BLUE, Color.RED, Color.GREEN)
        val barData = BarData(dataSet)


        binding.chartenergy.data = barData
        binding.chartenergy.invalidate()

        binding.chartenergy.xAxis.textSize = 8f
        binding.chartenergy.xAxis.textColor = Color.WHITE
        binding.chartenergy.axisLeft.textSize = 8f
        binding.chartenergy.axisLeft.textColor = Color.WHITE
        binding.chartenergy.axisRight.textSize = 8f
        binding.chartenergy.axisRight.textColor = Color.WHITE




    }

    private fun setupDataPower(data: List<BarEntry>){
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(1f, 100f))
        entries.add(BarEntry(2f, 150f))
        entries.add(BarEntry(3f, 120f))

        val dataSet = BarDataSet(entries, "Energy Data")
        dataSet.colors = listOf(Color.BLUE, Color.RED, Color.GREEN)
        val barData = BarData(dataSet)

        binding.chartpower.data = barData
        binding.chartpower.invalidate()

        binding.chartpower.xAxis.textSize = 8f
        binding.chartpower.xAxis.textColor = Color.WHITE
        binding.chartpower.axisLeft.textSize = 8f
        binding.chartpower.axisLeft.textColor = Color.WHITE
        binding.chartpower.axisRight.textSize = 8f
        binding.chartpower.axisRight.textColor = Color.WHITE

    }

    private fun setupDataVoltage(data: List<BarEntry>){
        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(1f, 100f))
        entries.add(BarEntry(2f, 150f))
        entries.add(BarEntry(3f, 120f))

        val dataSet = BarDataSet(entries, "Energy Data")
        dataSet.colors = listOf(Color.BLUE, Color.RED, Color.GREEN)
        val barData = BarData(dataSet)

        binding.chartvoltage.data = barData
        binding.chartvoltage.invalidate()

        binding.chartvoltage.xAxis.textSize = 8f
        binding.chartvoltage.xAxis.textColor = Color.WHITE
        binding.chartvoltage.axisLeft.textSize = 8f
        binding.chartvoltage.axisLeft.textColor = Color.WHITE
        binding.chartvoltage.axisRight.textSize = 8f
        binding.chartvoltage.axisRight.textColor = Color.WHITE


        binding.chartvoltageLn.data = barData
        binding.chartvoltageLn.invalidate()

        binding.chartvoltageLn.xAxis.textSize = 8f
        binding.chartvoltageLn.xAxis.textColor = Color.WHITE
        binding.chartvoltageLn.axisLeft.textSize = 8f
        binding.chartvoltageLn.axisLeft.textColor = Color.WHITE
        binding.chartvoltageLn.axisRight.textSize = 8f
        binding.chartvoltageLn.axisRight.textColor = Color.WHITE

    }

    private fun showDateTimePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(calendar.time)
                        val newCurrentData = fetchNewCurrentData(formattedDateTime)
                        val newEnergyData = fetchNewEnergyData(formattedDateTime)
                        val newPowerData = fetchNewPowerData(formattedDateTime)
                        val newVoltageData = fetchNewVoltageData(formattedDateTime)

                        binding.btnDate.setText(formattedDateTime)

                        setupChart(binding.chartcurrent, newCurrentData)
                        setupChart(binding.chartenergy, newEnergyData)
                        setupChart(binding.chartpower, newPowerData)
                        setupChart(binding.chartvoltage, newVoltageData)
                    },
                    hour,
                    minute,
                    true
                )

                timePickerDialog.show()
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private val currentData = listOf(
        BarEntry(1f, 110f),
        BarEntry(2f, 150f),
        BarEntry(3f, 180f),
        BarEntry(4f, 210f)
    )

    private val energyData = listOf(
        BarEntry(1f, 100f),
        BarEntry(2f, 150f),
        BarEntry(3f, 120f)
    )

    private val powerData = listOf(
        BarEntry(1f, 90f),
        BarEntry(2f, 130f),
        BarEntry(3f, 110f)
    )

    private val voltageData = listOf(
        BarEntry(1f, 80f),
        BarEntry(2f, 120f),
        BarEntry(3f, 100f)
    )

    private fun fetchNewCurrentData(selectedDateTime: String): List<BarEntry> {
        return listOf(
            BarEntry(1f, 410f),
            BarEntry(2f, 650f),
            BarEntry(3f, 780f),
            BarEntry(4f, 910f))
    }

    private fun fetchNewEnergyData(selectedDateTime: String): List<BarEntry> {
        return listOf(
            BarEntry(1f, 300f),
            BarEntry(2f, 350f),
            BarEntry(3f, 220f)
        )
    }

    private fun fetchNewPowerData(selectedDateTime: String): List<BarEntry> {
        return listOf(
            BarEntry(1f, 490f),
            BarEntry(2f, 430f),
            BarEntry(3f, 410f)
        )
    }

    private fun fetchNewVoltageData(selectedDateTime: String): List<BarEntry> {
        return listOf(
            BarEntry(1f, 180f),
            BarEntry(2f, 220f),
            BarEntry(3f, 400f)
        )
    }

}