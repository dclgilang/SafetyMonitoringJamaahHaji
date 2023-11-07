package id.co.dif.smj.presentation.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.databinding.DetailSiteDialogBinding
import id.co.dif.smj.presentation.adapter.TroubleTicketListSiteAdapter
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.viewmodel.TicketListSiteViewModel

class DetailSiteDialog : BaseBottomSheetDialog<TicketListSiteViewModel, DetailSiteDialogBinding, Any?>() {
    override val layoutResId = R.layout.detail_site_dialog


    lateinit var adapter: TroubleTicketListSiteAdapter


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = TroubleTicketListSiteAdapter()
        binding.recyclerView.adapter = adapter


        viewModel.responseListSite.observe(lifecycleOwner) {
            if (it.status == 200) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
            }
        }

        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                outRect.left = 19.toDp
                outRect.right = 19.toDp
                outRect.top = 16.toDp
                if (position == adapter.itemCount - 1) {
                    outRect.bottom = 25.toDp
                }
            }
        })

    }

}