package id.co.dif.smj.presentation.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.data.SiteDetails
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.TroubleTicket
import id.co.dif.smj.databinding.DialogTicketListBinding
import id.co.dif.smj.presentation.adapter.TroubleTicketAdapter
import id.co.dif.smj.utils.PaginationScrollListener
import id.co.dif.smj.utils.Resource
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.toDp
import id.co.dif.smj.viewmodel.TicketListPopupDialogViewModel

class TicketListPopupDialog(val marker: MarkerTripleE?) :
    BaseBottomSheetDialog<TicketListPopupDialogViewModel, DialogTicketListBinding, SiteDetails>() {

    override val layoutResId = R.layout.dialog_ticket_list
    lateinit var troubleTicketAdapter: TroubleTicketAdapter

    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    private val pageStart = 1
    private var paginationListener: PaginationScrollListener? = null

    companion object {
        fun newInstance(marker: MarkerTripleE?) = TicketListPopupDialog(marker)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.tvSiteName.text = marker?.site_name
        binding.tvDescriptionSiteNameValue.text = marker?.site_name

        troubleTicketAdapter = TroubleTicketAdapter()
        binding.rvTickets.adapter = troubleTicketAdapter



        binding.rvTickets.addItemDecoration(object : RecyclerView.ItemDecoration() {

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
                if (position == troubleTicketAdapter.itemCount - 1) {
                    outRect.bottom = 25.toDp
                }
            }
        })

        viewModel.responseaGetSiteByid.observe(lifecycleOwner) {
            populateTroubleTicketList(it)
        }
        setupTroubleTicketList()
        Log.d("TAG", "onViewBindingCreated: ${marker!!.site_id}")

    }

    private fun populateTroubleTicketList(it: Resource<BaseResponseList<TroubleTicket>>) {
        when (it) {
            is Resource.Error -> {
                if (currentPage > pageStart) currentPage--
                troubleTicketAdapter.removeLoadingFooter()
                isLoading = false
                Log.d("TAG", "populateTroubleTicketList: Error $currentPage")
            }

            is Resource.Loading -> {
                isLoading = true
                Log.d("TAG", "populateTroubleTicketList: Loading $currentPage")
            }

            is Resource.Success -> {
                isLoading = false
                troubleTicketAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        val data = response.data.list
                        troubleTicketAdapter.addAll(response.data.list)
                        isLastPage = data.isEmpty()
                        Log.d("TAG", "populateTroubleTicketList: ${data.size}")
                    } else {
                        isLastPage = true
                    }
                }
                binding.txtNoticket.isVisible = troubleTicketAdapter.data.isEmpty()
                Log.d("TAG", "populateTroubleTicketList: Success $currentPage")

            }
        }
    }

    private fun setupTroubleTicketList() {
        Log.d("TAG", "populateTroubleTicketList setupTroubleTicketList: ")
        troubleTicketAdapter = TroubleTicketAdapter()
        val layoutManager = LinearLayoutManager(context)
        if (paginationListener == null) {
            paginationListener = object :
                PaginationScrollListener(layoutManager) {
                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun loadMoreItems() {
                    isLoading = true;
                    currentPage++;
                    troubleTicketAdapter.addLoadingFooter()
                    loadNextPage();
                }
            }
            binding.rvTickets.addOnScrollListener(paginationListener as PaginationScrollListener)
        }

        binding.rvTickets.layoutManager = layoutManager
        binding.rvTickets.adapter = troubleTicketAdapter
        loadFirstPage()
    }

    fun loadNextPage() {
        viewModel.getTicketsBySiteById(siteId = marker?.site_id!!, page = currentPage)
    }

    private fun loadFirstPage() {
        viewModel.cancelJob()
        viewModel.getTicketsBySiteById(siteId = marker?.site_id!!, page = pageStart)
    }
}