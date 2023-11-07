package id.co.dif.smj.presentation.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.smj.DialogUploadFile
import id.co.dif.smj.MAX_IMAGE_SIZE_MEGABYTES
import id.co.dif.smj.R
import id.co.dif.smj.allowedMimeType
import id.co.dif.smj.base.BaseFragment
import id.co.dif.smj.base.BaseResponse
import id.co.dif.smj.data.*
import id.co.dif.smj.databinding.FragmentDetailBinding
import id.co.dif.smj.presentation.activity.CameraActivity
import id.co.dif.smj.presentation.activity.SelectEngineerActivity
import id.co.dif.smj.presentation.activity.SelectSiteActivity
import id.co.dif.smj.presentation.dialog.PickerDialog
import id.co.dif.smj.service.LocationClient
import id.co.dif.smj.utils.Resource
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.colorRes
import id.co.dif.smj.utils.compressImageFileToDefinedSize
import id.co.dif.smj.utils.constructLastLocation
import id.co.dif.smj.utils.copyImageToFile
import id.co.dif.smj.utils.findViewsByType
import id.co.dif.smj.utils.getFileFromUri
import id.co.dif.smj.utils.getFileNameFromUri
import id.co.dif.smj.utils.getMimeType
import id.co.dif.smj.utils.ifNot
import id.co.dif.smj.utils.ifTrue
import id.co.dif.smj.utils.isDeviceOnline
import id.co.dif.smj.utils.isImageMimeType
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.makeMultipartData
import id.co.dif.smj.utils.megaBytes
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.str
import id.co.dif.smj.utils.trimAllEditText
import id.co.dif.smj.utils.valueIsEmpty
import id.co.dif.smj.viewmodel.DetailViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.aprilapps.easyphotopicker.EasyImage
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : BaseFragment<DetailViewModel, FragmentDetailBinding>(), KoinComponent {
    override val layoutResId = R.layout.fragment_detail
    var easyImage: EasyImage? = null
    var onSubmit: (status: Boolean) -> Unit = { _ -> }
    var ticketDetails: TicketDetails? = null
    val locationClient: LocationClient by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        ticketDetails = preferences.ticketDetails.value?.copy()
        setupData()

        binding.etTicAssignTo.setOnClickListener {
            PickerDialog.newInstance(
                arrayListOf(
                    "Technical Support",
                    "Teritory Operation",
                    "Field Maintenance",
                    "Transport Provider",
                    "Tower Provider",
                    "Equipment Vendor"
                )
            ) { index, value ->
                binding.etTicAssignTo.setText(value)
                binding.etTicAssignTo.error = null
            }.show(childFragmentManager, getString(R.string.asign_to))
        }
        binding.etTicSeverity.setOnClickListener {
            PickerDialog.newInstance(
                TicketSeverity.getAll().map { it.label }
            ) { index, value ->
                binding.etTicSeverity.setText(value)
                binding.etTicSeverity.error = null
            }.show(childFragmentManager, getString(R.string.severity))
        }

        binding.systemFault.setOnClickListener {
            val systemFaultOptions = when (binding.etTicType.text.toString()) {
                "TTSR - Trouble Ticket Execution" -> {
                    arrayListOf("Power System", "Radio System", "Transmission System")
                }

                else -> {
                    arrayListOf()
                }
            }

            PickerDialog.newInstance(systemFaultOptions) { index, value ->
                binding.systemFault.setText(value)
                binding.systemFault.error = null
            }.show(childFragmentManager, getString(R.string.system_fault))
        }

        binding.subSystemFault.setOnClickListener {
            val subSystemFaultOptions = when (binding.systemFault.text.toString()) {
                "Power System" -> {
                    arrayListOf("PLN Outage", "Rectifier", "Battery", "Accessories")
                }

                "Radio System" -> {
                    arrayListOf("Radio Unit", "Base band", "Optic cable", "Connector", "Antena")
                }

                "Transmission System" -> {
                    arrayListOf("Radio Unit", "Indoor Unit", "Accessories")
                }

                else -> {
                    arrayListOf()
                }
            }

            PickerDialog.newInstance(subSystemFaultOptions) { index, value ->
                binding.subSystemFault.setText(value)
                binding.subSystemFault.error = null
            }.show(childFragmentManager, getString(R.string.status))
        }

        binding.etTicType.setOnClickListener {
            val optionsList = listOf(
                "--- site activity ---",
                "TTSR - Trouble Ticket Execution",
                "TSUR - Site Survey",
                "TBAL - Audit Balmon On Site",
                "TTSS - Troubleshoot On Site Quality Support",
                "TAUD - Audit Site",
                "--- non site activity ---",
                "TMON - Planing & Monitoring Team TS",
                "TMET - Meeting",
                "TISR - Validasi ISR"
            )

            PickerDialog.newInstance(optionsList) { index, value ->
                binding.etTicType.setText(value)
                binding.etTicType.error = null

                when (index) {
                    1 -> {
                        binding.formSystemFault.visibility = View.VISIBLE
                        binding.validationStatus = true
                        binding.systemFault.text.clear()
                        binding.subSystemFault.text.clear()
                    }

                    else -> {
                        binding.formSystemFault.visibility = View.GONE
                        binding.validationStatus = false
                        binding.systemFault.setText("-")
                        binding.subSystemFault.setText("-")
                        binding.systemFault.error = null
                        binding.subSystemFault.error = null
                    }
                }
            }.show(childFragmentManager, getString(R.string.ticket_type))
            return@setOnClickListener
        }


        binding.etStatus.setOnClickListener {
            PickerDialog.newInstance(
                TicketStatus.getAll(except = listOf(TicketStatus.Closed)).map { it.label }
            ) { index, value ->
                binding.etStatus.setText(value)
            }.show(childFragmentManager, getString(R.string.status))
        }

        viewModel.responseNearestTechnician.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.savedEngineers.value = it.data.list
                setupAutoCompleteEngineers(it.data.list)
            }
        }
        viewModel.responseSiteMarker.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.savedAllSite.value = it.data.list
                setAutoCompleteSite(it.data.list)
            }
        }

        viewModel.getListSite(null)
        viewModel.getNearestTechnician(viewModel.selectedSite.value?.site_id)
        setAutoCompleteSite(preferences.savedAllSite.value ?: listOf())
        setupAutoCompleteEngineers(preferences.savedEngineers.value ?: listOf())

        binding.btnRemoveEngineer.setOnClickListener {
            viewModel.selectedEngineer.value = null
        }

        binding.btnRemoveSite.setOnClickListener {
            viewModel.selectedSite.value = null
            binding.etTicSiteIdSiteName.setText("")
            binding.etTowerProvider.setText("")
        }
        binding.btnSelectEngineer.setOnClickListener {
            val intent = SelectEngineerActivity.newInstance(requireContext())
            intent.putExtra("selected_site", viewModel.selectedSite.value)
            startActivityForResult(intent, SELECT_ENGINEER_REQUEST_CODE)
        }
        binding.btnSelectSite.setOnClickListener {
            val intent = SelectSiteActivity.newInstance(requireContext())
            startActivityForResult(intent, SELECT_SITE_REQUEST_CODE)
        }
        viewModel.selectedSite.observe(lifecycleOwner) {
            viewModel.getSiteById(it?.site_id)
            viewModel.getNearestTechnician(it?.site_id)
        }

        viewModel.selectedEngineer.observe(lifecycleOwner) {
            binding.etTicFieldEngineer.setText(it?.name)
        }
        viewModel.responseGetSiteDetails.observe(lifecycleOwner) {
            binding.etTicSiteIdSiteName.setText("")
            binding.etTowerProvider.setText("")
            binding.etTicArea.setText("")
            if (it.status in StatusCode.SUCCESS) {
                val infoSite = it.data.info_site
                binding.etTicSiteIdSiteName.setText("${infoSite.siteIdCustomer} - ${infoSite.siteName} ")
                binding.etTowerProvider.setText(infoSite.siteProvider)
                binding.etTicArea.setText(infoSite.pgroupNsCluster)
                viewModel.selectedSite.value?.site_id_customer = it.data.info_site.siteIdCustomer
            }
        }
        binding.btnTicketReopen.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to reopen this ticket??")
                .setPositiveButton("Yes") { _, _ ->
                    locationClient.getCurrentLocation {
                        it?.let {
                            preferences.lastLocation.value = constructLastLocation(it)
                        }
                        when (currentActivity.lastLocationStatus) {
                            LocationStats.EXPIRED -> {
                                showSuccessMessage(
                                    requireContext(),
                                    getString(R.string.location_expired_check_if_gps_is_active_or_restart_app)
                                )
                            }

                            LocationStats.UNAVAILABLE -> {
                                showSuccessMessage(
                                    requireContext(),
                                    getString(R.string.location_unavailable_check_permission)
                                )
                            }

                            LocationStats.VALID -> {
                                reopenTicket()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            alertDialog.show()
        }


        binding.save.setOnClickListener {
            when (currentActivity.lastLocationStatus) {
                LocationStats.VALID -> {
                    submitData()
                }

                else -> {
                    viewModel.showLoading()
                    locationClient.getCurrentLocation {
                        viewModel.dissmissLoading()
                        if (it == null) {
                            currentActivity.showToast(getString(R.string.something_went_wrong_check_gps_is_active))
                        } else {
                            preferences.lastLocation.value = constructLastLocation(it)
                            submitData()
                        }
                    }
                }
            }
        }


        viewModel.responseUploadfile.observe(lifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showSuccessMessage(requireContext(), it.message.str)
                    onSubmit(false)
                    viewModel.dissmissLoading()
                }

                is Resource.Loading -> {
                    viewModel.showLoading()
                }

                is Resource.Success -> {
                    viewModel.dissmissLoading()
                    val status = it.data?.status in StatusCode.SUCCESS
                    onSubmit(status)
                    if (status) {
                        clearNotesData()
                        showSuccessMessage(
                            requireContext(),
                            it.data?.message ?: getString(R.string.notes_successfully_submitted)
                        )
                    } else {
//                        showSuccessMessage(requireContext(), it.data?.message.str)
                        currentActivity.showToast("Something went wrong!")
                    }
                }

                else -> {}
            }
        }

        viewModel.responseReOpenTicket.observe(lifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showSuccessMessage(requireContext(), it.message.str)
                    onSubmit(false)
                    viewModel.dissmissLoading()
                }

                is Resource.Loading -> {
                    viewModel.showLoading()
                }

                is Resource.Success -> {
                    viewModel.dissmissLoading()
                    val status = it.data?.status in StatusCode.SUCCESS
                    onSubmit(status)
                    if (status) {
                        showSuccessMessage(
                            requireContext(),
                            getString(R.string.reopen_ticket_successfully)
                        )
                        binding.txtAddNotes.setText("")
                        binding.closedTicket.isChecked = false

                    } else {
                        showSuccessMessage(requireContext(), it.data?.message.str)
                    }
                }

                else -> {}
            }
        }


        binding.chooceFile.setOnClickListener {
            locationClient.getCurrentLocation {
                it?.let {
                    preferences.lastLocation.value = constructLastLocation(it)
                }
                when (currentActivity.lastLocationStatus) {
                    LocationStats.EXPIRED -> {
                        showSuccessMessage(
                            requireContext(),
                            getString(R.string.location_expired_check_if_gps_is_active_or_restart_app)
                        )
                    }

                    LocationStats.UNAVAILABLE -> {
                        showSuccessMessage(
                            requireContext(),
                            getString(R.string.location_unavailable_check_permission)
                        )
                    }

                    LocationStats.VALID -> {
                        chooseFile()
                    }
                }
            }
        }

        viewModel.responseGetEngineerIsWithinRadius.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                viewModel.engineerIsWithinRadius = it.data.isWithinRadiusStatus
            } else {
                viewModel.engineerIsWithinRadius = true
            }
        }

        ticketDetails?.let {
            val myProfile = preferences.myDetailProfile.value
            val engineerId = myProfile?.id
            val siteId = it.site_info?.siteId
            viewModel.getEngineerIsWithinRadius(engineerId, siteId)
        }

    }

    private fun setupAutoCompleteEngineers(markers: List<MarkerTripleE>) {
        val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
            requireContext(),
            R.layout.item_spinner_dropdown,
            markers
        )

        binding.etTicFieldEngineer.setAdapter(adapter)
        binding.etTicFieldEngineer.setOnItemClickListener { _, _, position, _ ->
            binding.etTicFieldEngineer.clearFocus()
            viewModel.selectedEngineer.value = adapter.getItem(position)
        }
    }

    private fun setAutoCompleteSite(list: List<MarkerTripleE>) {
        val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
            requireContext(),
            R.layout.item_spinner_dropdown,
            list
        )

        binding.etTicSiteIdSiteName.setAdapter(adapter)
        binding.etTicSiteIdSiteName.setOnItemClickListener { _, _, position, _ ->
            binding.etTicSiteIdSiteName.clearFocus()
            viewModel.selectedSite.value = adapter.getItem(position)
        }
    }

    private fun reopenTicket() {
        val location = preferences.lastLocation.value
        val latitude = makeMultipartData("latitude", location?.latitude)
        val longitude = makeMultipartData("longitude", location?.longitude)
        val notes = makeMultipartData("tic_notes", "tickets have been reopened")
        val status = makeMultipartData("tic_status", "Completed")
        val acceptedticket =
            makeMultipartData("tic_accepted", binding.acceptedByTripleE.isChecked)
        val closedticket = makeMultipartData("tic_closed", false)
        val params = mutableListOf(
            notes,
            status,
            acceptedticket,
            closedticket,
            latitude,
            longitude,
        )
        (viewModel.file != null) ifTrue {
            val file = makeMultipartData("file", viewModel.file!!)
            params.add(file)
        }
        viewModel.reOpenTicket(
            id = ticketDetails?.tic_id, param = params.toMutableList()
        )
    }

    private fun clearNotesData() {
        binding.txtAddNotes.setText("")
        viewModel.file = null
    }

    private fun setupData() = fragmentContext?.let { context ->
        ticketDetails?.let { ticketDetails ->
            viewModel.selectedSite.value = ticketDetails.site_info?.toSiteLocation()
            binding.etTicAssignTo.setText(ticketDetails.tic_assign_to.orDefault(""))
            binding.etTicNumber.setText(ticketDetails.tic_number.orDefault(""))
            binding.etTicketNumberAssigned.setText(ticketDetails.tic_number_assigned.orDefault(""))
            binding.etTicSeverity.setText(ticketDetails.tic_severety.orDefault(""))
            binding.etTicType.setText(ticketDetails.tic_type.orDefault(""))
            binding.etTicFieldEngineer.setText(ticketDetails.tic_field_engineer.orDefault(""))
            binding.etTicSiteIdSiteName.setText("${ticketDetails.site_info?.siteId ?: ""} - ${ticketDetails.site_info?.siteName.orDefault("")}")
            binding.etTowerProvider.setText(ticketDetails.tower.orDefault(""))
            binding.etTicArea.setText(ticketDetails.tic_area.orDefault(""))
            binding.etTicPersonInCharge.setText(ticketDetails.tic_person_in_charge.orDefault(""))
            binding.subSystemFault.setText(ticketDetails.tic_system_sub.orDefault(""))
            binding.systemFault.setText(ticketDetails.tic_system.orDefault(""))
            binding.tvTicId.setText("#" + ticketDetails.tic_id.orDefault(""))
            binding.etStatus.setText(TicketStatus.fromLabel(ticketDetails.tic_status.orDefault("")).label)
            val ticStatus = ticketDetails.tic_status
            val role = session?.emp_security
            val ticIsAccepted = ticketDetails.tic_accepted
            val ticIsClosed = ticketDetails.tic_closed
            val ticIsMine = ticketDetails.status_ticket
            binding.acceptedByTripleE.isChecked = ticIsAccepted == true
            binding.closedTicket.isChecked = ticIsClosed == true


            if (ticketDetails.tic_type != "TTSR - Trouble Ticket Execution"){
                binding.formSystemFault.visibility = View.GONE
            } else {
                binding.formSystemFault.visibility = View.VISIBLE
            }


//            if (ticketDetails.tic_system.isNullOrEmpty() && ticketDetails.tic_system_sub.isNullOrEmpty()){
//                binding.formSystemFault.visibility = View.GONE
//            } else {
//                binding.formSystemFault.visibility = View.VISIBLE
//            }

            when (role) {
                1 -> {
                    binding.btnTicketReopen.isVisible = false
                }

                2, 3 -> {
                    binding.acceptedByTripleE.isEnabled =
                        ticStatus?.lowercase() == TicketStatus.Completed.label.lowercase()
                    binding.closedTicket.isEnabled = ticIsAccepted == true
                }
            }
            binding.closedTicket.setOnCheckedChangeListener { buttonView, isChecked ->
                binding.etStatus.setText(if (isChecked) TicketStatus.Closed.label else ticStatus)
            }

            binding.btnTicketReopen.isVisible =
                ticIsClosed == true && ticIsMine == true && role != 1

            Log.d("TAG", "setupData: check")

            if (ticIsMine != true) {
                val listOfEdt = findViewsByType(binding.root, EditText::class.java, exclude = arrayListOf(binding.txtAddNotes.id))
                listOfEdt.forEach {
                    it.isEnabled = false
                    it.backgroundTintList =
                        ColorStateList.valueOf(R.color.light_grey.colorRes(context))
                }
//            binding.btnTicketReopen.isVisible = false
                binding.chooceFile.isEnabled = false
//                binding.save.isEnabled = false
                binding.acceptedByTripleE.isEnabled = false
                binding.closedTicket.isEnabled = false
                binding.acceptedByTripleE.thumbTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.closedTicket.thumbTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.acceptedByTripleE.trackTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.closedTicket.trackTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
//                binding.save.backgroundTintList =
//                    ColorStateList.valueOf(R.color.dark_grey.colorRes(context))
            }

            if (ticIsClosed == true) {
                val listOfEdt = findViewsByType(binding.root, EditText::class.java)
                listOfEdt.forEach {
                    it.isEnabled = false
                    it.backgroundTintList =
                        ColorStateList.valueOf(R.color.light_grey.colorRes(context))
                }
//            binding.btnTicketReopen.isVisible = true
                binding.chooceFile.isEnabled = false
                binding.save.isEnabled = false
                binding.acceptedByTripleE.isEnabled = false
                binding.closedTicket.isEnabled = false
                binding.acceptedByTripleE.thumbTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.closedTicket.thumbTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.acceptedByTripleE.trackTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.closedTicket.trackTintList =
                    ColorStateList.valueOf(R.color.grey.colorRes(context))
                binding.save.backgroundTintList =
                    ColorStateList.valueOf(R.color.dark_grey.colorRes(context))
            }
        }
    }

    private fun submitData() {
        ticketDetails?.let { ticketDetails ->
            trimAllEditText()
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (validator.validate().not()) {
                showToast("There are still empty fields!")
                return
            }
            val location = preferences.lastLocation.value

            if (binding.txtAddNotes.valueIsEmpty() && !validator.validate()) {
                binding.txtAddNotes.requestFocus()
                Toast.makeText(context, "note must be filled in!", Toast.LENGTH_SHORT).show()
            }
            val params = mutableMapOf<String, Any?>(
                "latitude" to location?.latitude,
                "longitude" to location?.longitude,
                "tic_notes" to binding.txtAddNotes.text.toString(),
                "tic_status" to binding.etStatus.text.toString(),
                "tic_accepted" to binding.acceptedByTripleE.isChecked,
                "tic_closed" to binding.closedTicket.isChecked,
                "tic_area" to binding.etTicArea.text.toString(),
                "tic_field_engineer_emp_id" to (viewModel.selectedEngineer.value?.id ?: ticketDetails.tic_field_engineer_id),
                "tic_field_engineer" to (viewModel.selectedEngineer.value?.name ?: ticketDetails.tic_field_engineer),

                "tic_system" to binding.systemFault.text.toString(),
                "tic_system_sub" to binding.subSystemFault.text.toString(),
                "tic_number" to binding.etTicNumber.text.toString(),
                "tic_number_assigned" to binding.etTicketNumberAssigned.text.toString(),
                "tic_severety" to binding.etTicSeverity.text.toString(),
                "tic_assign_to" to binding.etTicAssignTo.text.toString(),
                "tic_field_engineer" to binding.etTicFieldEngineer.text.toString(),
                "file" to viewModel.file,
                "tic_type" to binding.etTicType.text.toString()
            )
            val site_name =   (viewModel.selectedSite.value?.site_name ?: ticketDetails.site_info?.siteName)
            val site_id =  (viewModel.selectedSite.value?.site_id ?: ticketDetails.site_info?.siteId)
            if (site_id != null){
                params.put("tic_site", site_name)
                params.put("tic_site_id", site_id)
            }
            log("mirza",params, "\n", params["tic_site"] )

            if (!requireContext().isDeviceOnline()) {
                val requestUploadOffline = preferences.requestUpload.value?.toMutableList() ?: mutableListOf()
                val uploadRequest = (ticketDetails.tic_id ?: "") to params
                requestUploadOffline.add(uploadRequest)
                ticketDetails.tic_status = binding.etStatus.text.toString()
                ticketDetails.tic_accepted = binding.acceptedByTripleE.isChecked
                ticketDetails.tic_closed = binding.closedTicket.isChecked
                val notes = ticketDetails.tic_notes
                ticketDetails.tic_notes = mutableListOf<Note>().apply {
                    var note = binding.txtAddNotes.text.toString()
                    val file = (viewModel.file?.toUri() ?: "").toString()
                    file.isNotEmpty().ifTrue {
                        note = "${note}\$${location?.longitude}\$${location?.latitude}\$$1"
                    }
                    add(
                        Note(
                            id = null,
                            date = SimpleDateFormat(
                                "dd MMMM yyyy",
                                Locale.getDefault()
                            ).format(Date()),
                            file = file,
                            note = note,
                            tic_status = "Pending",
                            time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                            username = preferences.myDetailProfile.value?.fullname,
                            isWithinRadius = "1",
                            longitude = location?.longitude.toString(),
                            latitude = location?.latitude.toString()
                        )
                    )
                    notes?.toList()?.let { addAll(it) }
                }
                val allTicketsDetails = preferences.allTicketsDetails.value ?: hashMapOf()
                allTicketsDetails[ticketDetails.tic_id] = ticketDetails
                preferences.allTicketsDetails.value = allTicketsDetails
                preferences.requestUpload.value = requestUploadOffline
                viewModel.responseUploadfile.postValue(
                    Resource.Success(
                        BaseResponse(200, "Notes will be uploaded when device goes online", "")
                    )
                )
            } else {
                viewModel.uploadfile(
                    id = ticketDetails.tic_id, param = params.map { makeMultipartData(it.key, it.value) }.toMutableList()
                )
            }
        }

    }

    private fun chooseFile() = fragmentContext?.let { context ->
        (viewModel.engineerIsWithinRadius).ifTrue {
            currentActivity.showAlert(
                context = context,
                message = getString(R.string.you_are_out_of_range),
                buttonPrimaryText = "Continue"
            ) {
                showUploadFileDialog()
            }
        }.ifNot {
            showUploadFileDialog()
        }
    }

    private fun showUploadFileDialog() {
        val dialog: DialogFragment = DialogUploadFile(onClickCamera = {
            val intent = Intent(context, CameraActivity::class.java)
            startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE)
        },
            onClickfile = {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = allowedMimeType.joinToString(separator = "|")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, allowedMimeType)
                startActivityForResult(intent, CHOOSE_FILE_REQUEST_CODE)
            }
        )
        dialog.show(childFragmentManager, "dialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                CHOOSE_FILE_REQUEST_CODE -> {
                    lifecycleScope.launch {
                        val fileUri = data.data
                        fileUri?.let { fileUri ->
                            binding.statusFile.text =
                                getString(
                                    R.string.selected_file,
                                    getFileNameFromUri(currentActivity, fileUri)
                                )
                            val mimeType = getMimeType(currentActivity, fileUri)
                            val isImage = isImageMimeType(mimeType)
                            var uri = fileUri
                            if (isImage) {
                                uri = currentActivity.copyImageToFile(uri).toUri()
                                uri = currentActivity.compressImageFileToDefinedSize(MAX_IMAGE_SIZE_MEGABYTES.megaBytes, uri).toUri()
                            }
                            viewModel.file = uri.let { requireContext().getFileFromUri(requireContext(), uri) }
                        }
                    }
                }

                OPEN_CAMERA_REQUEST_CODE -> {
                    lifecycleScope.launch {
                        data.data?.let { fileUri ->
                            var uri = fileUri
                            uri = currentActivity.copyImageToFile(uri).toUri()
                            uri = currentActivity.compressImageFileToDefinedSize(MAX_IMAGE_SIZE_MEGABYTES.megaBytes, uri).toUri()
                            binding.statusFile.text = getString(R.string.image_captured)
                            viewModel.file = uri.toFile()
                        }
                    }
                }

                SELECT_ENGINEER_REQUEST_CODE -> {
                    data?.let { intent ->
                        val selectedEngineer = intent.getSerializableExtra("selected_engineer") as MarkerTripleE?
                        viewModel.selectedEngineer.value = selectedEngineer
                        selectedEngineer?.name.log("selected_engineer")
                    }
                }

                SELECT_SITE_REQUEST_CODE -> {
                    data?.let { intent ->
                        val selectedSite = intent.getSerializableExtra("selected_site") as MarkerTripleE?
                        viewModel.selectedSite.value = selectedSite
                    }
                }
            }
        }
    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val SELECT_ENGINEER_REQUEST_CODE = 1
        const val SELECT_SITE_REQUEST_CODE = 2
        const val CHOOSE_FILE_REQUEST_CODE = 3
        const val OPEN_CAMERA_REQUEST_CODE = 4
    }
}