package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.databinding.FragmentHistoryBinding
import id.co.dif.smj.presentation.adapter.HistoryAdapter
import id.co.dif.smj.viewmodel.SiteInfoViewModel


class HistoryFragment : BaseFragment<SiteInfoViewModel, FragmentHistoryBinding>() {
    override val layoutResId = R.layout.fragment_history

   lateinit var adapter: HistoryAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = HistoryAdapter()
        binding.rvHistory.adapter = adapter

        val data = preferences.siteData.value
//        viewModel.responseSiteInfo.observe(lifecycleOwner){
        adapter.data.clear()
        data?.site_history?.let { adapter.data.addAll(it) }
        adapter.also {
            it.notifyItemRangeChanged(0, it.itemCount)
        }
        binding.layoutEmptyState.isVisible = adapter.data.isEmpty()

//        }

    }

}