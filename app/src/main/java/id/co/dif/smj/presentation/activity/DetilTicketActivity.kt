package id.co.dif.smj.presentation.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.databinding.ActivityDetilTicketBinding
import id.co.dif.smj.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.smj.presentation.fragment.ChangeLogFragment
import id.co.dif.smj.presentation.fragment.DetailFragment
import id.co.dif.smj.presentation.fragment.MapsTicketFragment
import id.co.dif.smj.presentation.fragment.SiteFragment
import id.co.dif.smj.presentation.fragment.SparePartListFragment
import id.co.dif.smj.presentation.fragment.TimelineFragment
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.log
import id.co.dif.smj.viewmodel.DetilTicketViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetilTicketActivity : BaseActivity<DetilTicketViewModel, ActivityDetilTicketBinding>(),
    KoinComponent {
    override val layoutResId = R.layout.activity_detil_ticket
    private val mapsTicketFragment: MapsTicketFragment by inject()
    private val detailFragment = DetailFragment()
    private val siteFragment: SiteFragment by inject()
    private val timelineFragment: TimelineFragment by inject()
    private val sparePartListFragment: SparePartListFragment by inject()
    private val changeLogFragment: ChangeLogFragment by inject()
    private lateinit var viewPagerAdapter: TitledViewPagerAdapter
    private val pageTitles = listOf(
        R.string.maps,
        R.string.ticket,
        R.string.site,
        R.string.timeline,
        R.string.spare_part,
        R.string.log,
        )
    private val fragments =
        arrayListOf(
            mapsTicketFragment,
            detailFragment,
            siteFragment,
            timelineFragment,
            sparePartListFragment,
            changeLogFragment,
            )
    var shouldGoToTimeline = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        shouldGoToTimeline = intent.getBooleanExtra("should_go_to_timeline", false)
        val notesPosition = intent.getIntExtra("notes_position", 0)
        detailFragment.onSubmit = {
            if (it) {
                shouldGoToTimeline = true
                viewModel.getTicketDetails(preferences.selectedTicketId.value)
            }
        }

        sparePartListFragment.onSubmit = {
            if (it) {
                shouldGoToTimeline = true
                viewModel.getTicketDetails(preferences.selectedTicketId.value)
            }
        }


        viewModel.responseTicketDetails.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val ticketDetails = it.data
                ticketDetails.log("laod dreta") { it.site_info?.siteId}
                val allTicketDetails = preferences.allTicketsDetails.value ?: hashMapOf()
                allTicketDetails[ticketDetails.tic_id] = ticketDetails
                preferences.allTicketsDetails.value = allTicketDetails
                preferences.ticketDetails.value = ticketDetails

                viewPagerAdapter = TitledViewPagerAdapter(supportFragmentManager)
                viewPagerAdapter.replaceAll(
                    fragments, pageTitles.map { id -> getString(id) }
                )
                binding.viewPager.adapter = viewPagerAdapter
                binding.viewPager.offscreenPageLimit = fragments.count()
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                binding.viewPager.isSaveEnabled = false
//                binding.rootLayout.onBackButtonClicked = {
//                    onBackPressedDispatcher.onBackPressed()
//                }
                if (shouldGoToTimeline) {
                    binding.viewPager.setCurrentItem(pageTitles.indexOf(R.string.timeline), true)
                    timelineFragment.scrollTo(notesPosition)
                    shouldGoToTimeline = false
                }

            } else {
                Toast.makeText(
                    this, it.message, Toast.LENGTH_SHORT
                ).show()
                finish()
            }

            binding.rootLayout.title = getString(R.string.detail_ticket, it.data.tic_id)
        }
        val ticId = preferences.selectedTicketId.value
        if (ticId == null) {
            showToast(getString(R.string.ticket_is_not_found))
            viewModel.dissmissLoading()
            finish()
        }
        Log.d("TAG", "onViewBinsdfsdfsdfsdfdingCreated: $ticId")
        viewModel.getTicketDetails(ticId.toString())
    }
}


