package id.co.dif.smj.presentation.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseResponseList
import id.co.dif.smj.data.Notification
import id.co.dif.smj.data.TroubleTicket
import id.co.dif.smj.databinding.FragmentTroubleTicketBinding
import id.co.dif.smj.presentation.activity.AddTicketActivity
import id.co.dif.smj.presentation.activity.SelectSiteActivity
import id.co.dif.smj.presentation.adapter.TroubleTicketAdapter
import id.co.dif.smj.presentation.dialog.FilterDialog
import id.co.dif.smj.presentation.dialog.PickerDialog
import id.co.dif.smj.utils.LinearSpacingItemDecoration
import id.co.dif.smj.utils.PaginationScrollListener
import id.co.dif.smj.utils.Resource
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.isDeviceOnline
import id.co.dif.smj.viewmodel.TroubleTicketViewModel

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 00:34
 *
 */
class TroubleTicketFragment : BaseFragment<TroubleTicketViewModel, FragmentTroubleTicketBinding>() {
    override val layoutResId = R.layout.fragment_trouble_ticket
    var shouldExecuteOnResume = false
    lateinit var troubleTicketAdapter: TroubleTicketAdapter
    var isLoading = false
    var currentPage = 1
    var isLastPage = false
    var error = false
    var hasStarted = false
    private val pageStart = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldExecuteOnResume = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        preferences.requestUpload
        binding.edtSearch.doOnTextChanged() { text, start, before, count ->
            viewModel.search = text.toString()
            resetTroubleTicketList()
        }

        binding.filter.setOnClickListener {
            showDialog()
        }
        binding.btnEmptyListModifyFilter.setOnClickListener {
            showDialog()
        }
        binding.btnErrorList.setOnClickListener {
            resetTroubleTicketList()
        }

        troubleTicketAdapter = TroubleTicketAdapter()

        binding.recyclerView.adapter = troubleTicketAdapter

        session?.let { session ->
            val visibility = (session.emp_security ?: 0) > 1
            binding.btnAddTicket.isVisible = visibility
            binding.btnEmptyListAdd.isVisible = visibility
        }
//
//        binding.btnAddTicket.isVisible =
//            session?.emp_security == 3 || session?.emp_security == 2
//        binding.btnEmptyListAdd.isVisible =
//            session?.emp_security == 3 || session?.emp_security == 2

        binding.btnAddTicket.setOnClickListener {
            startActivity(AddTicketActivity.newInstance(requireContext()))
        }
        binding.btnEmptyListAdd.setOnClickListener {
            startActivity(AddTicketActivity.newInstance(requireContext()))
        }

        binding.recyclerView.addItemDecoration(
            LinearSpacingItemDecoration(
                topMost = 16,
                top = 16,
                right = 16,
                left = 16,
            )
        )


        viewModel.responseAllTroubleTickets.observe(lifecycleOwner) {
            binding.layoutOnRefresh.isRefreshing = false
            populateTroubleTicketList(it)
        }

        binding.layoutOnRefresh.setOnRefreshListener {
            binding.layoutOnRefresh.isRefreshing = true
            resetTroubleTicketList()
        }
        if (!hasStarted) {
            resetTroubleTicketList()
            hasStarted = true
        }

    }

    private fun resetTroubleTicketList() {
        isLoading = false
        currentPage = 1
        isLastPage = false
        setupTroubleTicketList()
    }

    private fun showDialog() {
        val dialog: DialogFragment = FilterDialog(
            fromDate = viewModel.fromDate,
            untilDate = viewModel.untilDate,
            status = viewModel.status,
            sortBy = viewModel.sortBy,
            severety = viewModel.severety,
            filterIsOn = viewModel.filterIsOn,
            onClickSave = { sortby, fromDate, untilDate, status, severety, filterIsOn ->
                binding.groupFilterDot.isVisible = filterIsOn
                viewModel.filterIsOn = filterIsOn
                viewModel.severety = severety
                viewModel.fromDate = fromDate
                viewModel.sortBy = sortby
                viewModel.untilDate = untilDate
                viewModel.status = status
                resetTroubleTicketList()
            })
        dialog.show(childFragmentManager, "dialog")
    }

    private fun populateTroubleTicketList(it: Resource<BaseResponseList<TroubleTicket>>) {
        when (it) {
            is Resource.Error -> {
                if (currentPage > pageStart) currentPage--
                troubleTicketAdapter.removeLoadingFooter()
                isLoading = false
                error = true
            }

            is Resource.Loading -> {
                troubleTicketAdapter.addLoadingFooter()
                isLoading = true
                error = false
            }

            is Resource.Success -> {
                isLoading = false
                error = false
                troubleTicketAdapter.removeLoadingFooter()
                it.data?.let { response ->
                    if (response.status in StatusCode.SUCCESS) {
                        val data = response.data.list
                        troubleTicketAdapter.addAll(data)
                        if (data.isEmpty()) isLastPage = true
                    }
                }

            }
        }
        val filterCriteria = listOf(
            viewModel.fromDate,
            viewModel.untilDate,
            viewModel.status,
            viewModel.search,
            viewModel.severety
        )
        val dataIsEmpty = troubleTicketAdapter.data.isEmpty()
        val filterIsEmpty = filterCriteria.all { it == "" }
        val shouldShowEmptyTicketView = filterIsEmpty && dataIsEmpty && !error
        val shouldShowEmptyTicketViewFilter = !shouldShowEmptyTicketView && dataIsEmpty
        val shouldShowErrorTicketView =
            !shouldShowEmptyTicketView && !shouldShowEmptyTicketViewFilter && error

        binding.layoutEmptyState.isVisible = shouldShowEmptyTicketView
        binding.layoutErrorState.isVisible = shouldShowErrorTicketView
        binding.layoutEmptyStateFilter.isVisible = shouldShowEmptyTicketViewFilter


    }

    private fun setupTroubleTicketList() {
        troubleTicketAdapter = TroubleTicketAdapter()
        troubleTicketAdapter.notifyDataSetChanged()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.clearOnScrollListeners()
        val paginationListener = object :
            PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                loadNextPage();
            }
        }
        binding.recyclerView.addOnScrollListener(paginationListener as PaginationScrollListener)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = troubleTicketAdapter
        loadFirstPage()
    }

    fun loadNextPage() {
        isLoading = true;
        currentPage++;
        viewModel.getListTroubleTicket(page = currentPage)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getListTroubleTicketThenSaveItToLocal()
        if (shouldExecuteOnResume) {
            resetTroubleTicketList()
        } else {
            shouldExecuteOnResume = true
        }
    }

    private fun loadFirstPage() {
        viewModel.getListTroubleTicket(page = pageStart)

        if (!requireContext().isDeviceOnline()) {
            currentActivity.showAlert(
                context = requireContext(),
                message = "You're currently offline!",
                value = "Tickets will be limited to last fetched data",
                buttonPrimaryText = "OK",
                iconId = R.drawable.baseline_info_24
            )
        }

    }
}