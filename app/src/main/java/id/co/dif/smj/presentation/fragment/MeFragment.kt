package id.co.dif.smj.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.databinding.FragmentMeBinding
import id.co.dif.smj.presentation.activity.EditProfileActivity
import id.co.dif.smj.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.smj.utils.base64ImageToBitmap
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.shimmerDrawable
import id.co.dif.smj.viewmodel.MeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>(), KoinComponent {
    override val layoutResId = R.layout.fragment_me
    private val overviewFragment: OverviewFragment by inject()
    private val workFragment: WorkFragment by inject()
    private val educationFragment: EducationFragment by inject()
    private val basicInfoFragment: BasicInfoFragment by inject()
    private val skillFragment: SkillFragment by inject()
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.refresh.setOnRefreshListener {
            setupData()
        }
        setupData()

        binding.rootLayout.onBackButtonClicked = {
            currentActivity.onBackPressedFragment()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(EditProfileActivity.newInstance(requireContext()))
        }

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status == 200) {
                binding.bgHeaderMe.setImageResource(R.drawable.img_default_bg)
                if (it.data.fullname != null) {
                    binding.userName.setText("${it.data.fullname}")
                } else {
                    ""
                }

                binding.userPosition.setText(session?.emp_position.orDefault())


                if (session?.pgroup_nscluster?.size == 1) {
                    binding.nsArea.setText(session?.pgroup_nscluster?.get(0).orDefault())
                }
                preferences.myDetailProfile.value = it.data
                it.data.photo_profile?.let { encoded ->
                    binding.imageMe.setImageBitmap(base64ImageToBitmap(encoded))
                }

                it.data.cover?.let { cover ->
                    Log.d("TAG", "onViewBindingCreatedcover: $cover")
                    binding.bgHeaderMe.loadImage(cover, shimmerDrawable(), skipMemoryCaching = true)
                }
            }
        }

        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
    }

    private fun setupData() {
        val viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)
        val fragments =
            arrayListOf(overviewFragment, workFragment, educationFragment, basicInfoFragment, skillFragment)
        val titles = arrayListOf(
            getString(R.string.title_overview),
            getString(R.string.title_work),
            getString(R.string.title_education),
            getString(R.string.title_basic_info),
            getString(R.string.title_skill)
        )
        viewPagerAdapter.replaceAll(
            fragments, titles
        )
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = fragments.count()
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.isSaveEnabled = false
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
        binding.refresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        setupData()
    }

}



