package id.co.dif.smj.presentation.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.databinding.FragmentChangeLogBinding
import id.co.dif.smj.presentation.adapter.ChangeLogAdapter
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.viewmodel.ChangeLogTicketViewModel

class ChangeLogFragment : BaseFragment<ChangeLogTicketViewModel, FragmentChangeLogBinding>() {
    override val layoutResId = R.layout.fragment_change_log
    lateinit var adapter: ChangeLogAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = ChangeLogAdapter()
        binding.rvChangelog.adapter = adapter

        val data = preferences.ticketDetails
        adapter.data.clear()
        adapter.data.addAll(data.value?.tic_changelog.orEmpty())
        adapter.also {
            it.notifyItemRangeChanged(0, it.itemCount)
        }
        binding.rvChangelog.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                outRect.left = 20.toDp
                outRect.right = 20.toDp
                outRect.top = 10.toDp
                if (position == adapter.itemCount - 1) {
                    outRect.bottom = 20.toDp
                }
            }
        })
    }


}