package id.co.dif.smj.presentation.adapter

import carbon.internal.ResourcesCompat
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseAdapter
import id.co.dif.smj.data.PieChartLegend
import id.co.dif.smj.databinding.ItemPiechartLegendBinding
import id.co.dif.smj.viewmodel.MyDashboardViewModel

class PieChartLegendAdapter(private val legends: List<PieChartLegend>) :
    BaseAdapter<MyDashboardViewModel, ItemPiechartLegendBinding, PieChartLegend>() {

    init {
        this.data.addAll(legends)
    }

    private var highlighted: PieChartLegend? = null
    override val layoutResId: Int
        get() = R.layout.item_piechart_legend

    override fun onLoadItem(
        binding: ItemPiechartLegendBinding,
        data: MutableList<PieChartLegend>,
        position: Int
    ) {
        val legendData = data[position]
        binding.legendData = legendData
        if (legendData == highlighted) {
            binding.tvLegendLabel.typeface = ResourcesCompat.getFont(context!!, R.font.roboto_bold)
            binding.tvLegendLabel.textSize = 15f
        } else {
            binding.tvLegendLabel.typeface =
                ResourcesCompat.getFont(context!!, R.font.roboto_regular)
            binding.tvLegendLabel.textSize = 12f
        }
    }

    fun highlight(highLightedLabel: String) {
        removeHighlight()
        this.highlighted = data.find { it.label.lowercase() == highLightedLabel.lowercase() }
        notifyItemChanged(data.indexOf(this.highlighted))
    }

    fun removeHighlight() {
        highlighted?.let {
            val index = data.indexOf(this.highlighted)
            highlighted = null
            notifyItemChanged(index)
        }
    }
}