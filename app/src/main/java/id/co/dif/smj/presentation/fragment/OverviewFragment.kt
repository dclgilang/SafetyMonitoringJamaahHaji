package id.co.dif.smj.presentation.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.view.isVisible
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.databinding.OverviewBinding
import id.co.dif.smj.presentation.activity.AddCollageActivity
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.viewmodel.OverviewViewModel

class OverviewFragment : BaseFragment<OverviewViewModel, OverviewBinding>() {
    override val layoutResId = R.layout.overview
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseCompletedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                if (it.data.score == 100) {
                    binding.layoutCompletedProfile.isVisible = false
                }
                binding.completedProfile = it.data
            }
        }


        viewModel.geteducationresponse.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val latestEducation = it.data.list.firstOrNull() ?: return@observe

                val text = SpannableString(
                    "Studied at ${latestEducation.school} ${latestEducation.attended_for}" +
                            " ${latestEducation.time_priode_from} ${latestEducation.time_priode_until}" +
                            " ${latestEducation.description}"
                )
                val blackColor = Color.BLACK
                val studiedAt = "Studied at"

                val startIndex = text.indexOf(studiedAt)
                val endIndex = startIndex + studiedAt.length

                text.setSpan(ForegroundColorSpan(blackColor), startIndex, endIndex, 0)

                if (latestEducation.school != null) {
                    binding.college.setText(
                        "${R.string.studied_at}" + " ${latestEducation.school}" + " ${latestEducation.attended_for}" +
                                " ${latestEducation.time_priode_from}" + " ${latestEducation.time_priode_until}" + " ${latestEducation.description}"
                    ).toString().orDefault()
                } else {
                    binding.college.setText("-")
                }
            }
            Log.d("TAG", "onViewBindingCfsdsdreated education: ${it}")
        }

        viewModel.responseBasicInfoList.observe(lifecycleOwner) {
            if (it.status == 200) {
                binding.name.text = it.data.fullname.orDefault()
                binding.position.text = session?.emp_position.orDefault()
                binding.jointeam.text =
                    getString(R.string.since, session?.join_team.orDefault())
                binding.marker.text = it.data.address.orDefault()
                binding.phone.text = it.data.phone.orDefault()
                binding.email.text = it.data.email.orDefault()
                binding.birthdate.text = it.data.birthday.orDefault()
                binding.`as`.text = session?.project_name.orDefault()
                binding.tvDescriptionAboutMyself.text = it.data.about.orDefault()

                it.data.skill?.let { skill ->
                    Log.d("TAG", "onViewBindingCreated: $skill")
                    val installationSkill = (skill.Installation ?: 0f).coerceAtMost(100f)
                    binding.skillInstalation.setValue(installationSkill)
                    val commissioningSkill = (skill.Commissioning ?: 0f).coerceAtMost(100f)
                    binding.skillCommisionning.setValue(commissioningSkill)
                    val integrationSkill = (skill.Integration ?: 0f).coerceAtMost(100f)
                    binding.skillIntegration.setValue(integrationSkill)
                    val projectSkill = (skill.Project ?: 0f).coerceAtMost(100f)
                    binding.skillProject.setValue(projectSkill)
                    val businessSkill = (skill.Business ?: 0f).coerceAtMost(100f)
                    binding.skillBusiness.setValue(businessSkill)
                    preferences.skill.value = skill
                }
            }
        }
        val selectedProfileId = preferences.selectedProfileId.value
        val myProfile = preferences.myDetailProfile.value
        if (selectedProfileId == myProfile?.id) {
            binding.layoutAboutMyself.isVisible = false
        }
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        val id = preferences.selectedProfileId.value
        viewModel.getEducationList(id ?: 0)
        viewModel.getDetailedProfile(id)
        viewModel.getCompletedProfile(id)
    }


}