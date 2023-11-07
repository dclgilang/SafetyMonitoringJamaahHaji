package id.co.dif.smj.presentation.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.smj.DialogUploadFile
import id.co.dif.smj.R
import id.co.dif.smj.allowedMimeType
import id.co.dif.smj.base.BaseActivity
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.data.LocationStats
import id.co.dif.smj.data.TicketSeverity
import id.co.dif.smj.data.TicketStatus
import id.co.dif.smj.databinding.ActivityAddTicketBinding
import id.co.dif.smj.presentation.dialog.PickerDialog
import id.co.dif.smj.service.LocationClient
import id.co.dif.smj.utils.StatusCode
import id.co.dif.smj.utils.constructLastLocation
import id.co.dif.smj.utils.findViewsByType
import id.co.dif.smj.utils.getFileFromUri
import id.co.dif.smj.utils.getFileNameFromUri
import id.co.dif.smj.utils.getImageFromUri
import id.co.dif.smj.utils.ifTrue
import id.co.dif.smj.utils.makeMultipartData
import id.co.dif.smj.utils.orDefault
import org.koin.core.component.inject

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 15:01
 *
 */
class AddTicketActivity : BaseActivity<AddTicketViewModel, ActivityAddTicketBinding>() {
    override val layoutResId = R.layout.activity_add_ticket
    private val locationClient: LocationClient by inject()
    private val methodOne = true

    companion object {
        const val SELECT_ENGINEER_REQUEST_CODE = 1
        const val SELECT_SITE_REQUEST_CODE = 2
        const val CHOOSE_FILE_REQUEST_CODE = 3
        const val OPEN_CAMERA_REQUEST_CODE = 4

        fun newInstance(context: Context): Intent {
            return Intent(context, AddTicketActivity::class.java)
        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseNearestTechnician.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.savedEngineers.value = it.data.list
                setupAutoCompleteEngineers(it.data.list)
            }
        }

        viewModel.selectedEngineer.observe(lifecycleOwner) {
            binding.txtFieldEngineer.setText(it?.name)
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


        binding.btnSelectEngineer.setOnClickListener {
            val intent = SelectEngineerActivity.newInstance(this)
            intent.putExtra("selected_site", viewModel.selectedSite.value)
            startActivityForResult(intent, SELECT_ENGINEER_REQUEST_CODE)
        }

        binding.btnSelectSite.setOnClickListener {
            val intent = SelectSiteActivity.newInstance(this)
            startActivityForResult(intent, SELECT_SITE_REQUEST_CODE)
        }
        viewModel.selectedSite.observe(lifecycleOwner) {
            viewModel.getSiteById(it?.site_id)
            viewModel.getNearestTechnician(it?.site_id)
        }

        binding.btnRemoveEngineer.setOnClickListener {
            viewModel.selectedEngineer.value = null
        }
        binding.btnRemoveSite.setOnClickListener {
            viewModel.selectedSite.value = null
            binding.txtTowerProvider.setText("")
            binding.txtSiteIdSiteName.setText("")
            binding.txtAreaNsRegion.setText("")
        }
        viewModel.responseGetSiteDetails.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val infoSite = it.data.info_site
                binding.txtSiteIdSiteName.setText("${infoSite.siteIdCustomer} - ${infoSite.siteName} ")
                binding.txtTowerProvider.setText(infoSite?.siteProvider)
                binding.txtAreaNsRegion.setText(infoSite.pgroupNsCluster)
                if (methodOne) {
                    binding.btnSelectEngineer.callOnClick()
                }
            }
        }
        binding.txtAddNotes.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.txtAddNotes.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.txtAssignTo.setOnClickListener {
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
                binding.txtAssignTo.setText(value)
                binding.txtAssignTo.error = null
            }.show(supportFragmentManager, getString(R.string.asign_to))
        }
        binding.txtSeverity.setOnClickListener {
            PickerDialog.newInstance(
                TicketSeverity.getAll().map { it.label }
            ) { index, value ->
                binding.txtSeverity.setText(value)
                binding.txtSeverity.error = null
            }.show(supportFragmentManager, getString(R.string.severity))
        }

        binding.systemFault.setOnClickListener {
            val systemFaultOptions = when (binding.txtTicketType.text.toString()) {
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
            }.show(supportFragmentManager, getString(R.string.system_fault))
        }

        binding.subSystemFault.setOnClickListener {
            // Define your options based on systemFault selection
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
            }.show(supportFragmentManager, getString(R.string.status))
        }

        binding.txtTicketType.setOnClickListener {
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
                binding.txtTicketType.setText(value)
                binding.txtTicketType.error = null

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
            }.show(supportFragmentManager, getString(R.string.ticket_type))
            return@setOnClickListener
        }

        binding.etStatus.setText(TicketStatus.Assigned.label)
        binding.etStatus.setOnClickListener {
            PickerDialog.newInstance(
                arrayListOf("Assigned", "On-Progress", "Pending", "Escalated", "Completed")
            ) { index, value ->
                binding.etStatus.setText(value)
                binding.etStatus.error = null
            }.show(supportFragmentManager, getString(R.string.status))
        }

        binding.chooceFile.setOnClickListener {
            val dialog: DialogFragment = DialogUploadFile(
                onClickCamera = {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE)
                },
                onClickfile = {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = allowedMimeType.joinToString(separator = "|")
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, allowedMimeType)
                    startActivityForResult(intent, CHOOSE_FILE_REQUEST_CODE)
                }
            )


            dialog.show(supportFragmentManager, "dialog")
        }
        val info = preferences.myDetailProfile.value
        binding.txtTripleEInCharge.setText(info?.fullname)
        binding.idSubmit.setOnClickListener {
            locationClient.getCurrentLocation {
                it?.let {
                    preferences.lastLocation.value = constructLastLocation(it)
                }
                when (lastLocationStatus) {
                    LocationStats.EXPIRED -> {
                        showToast(
                            this,
                            "Data not submitted! marker expired, check if GPS is active"
                        )
                    }

                    LocationStats.UNAVAILABLE -> {
                        showToast(
                            this,
                            "Data not submitted! marker unavailable. Check permission!"
                        )
                    }

                    LocationStats.VALID -> {
                        submitData()
                    }
                }
            }
        }



        viewModel.responseaddticket.observe(lifecycleOwner) {
            if (it.status == 200) {
                val validator = Validator(binding)
                validator.enableFormValidationMode();
                if (validator.validate().not()) return@observe
                val location = preferences.lastLocation.value
                val notes = makeMultipartData("tic_notes", binding.txtAddNotes.text)
                val status = makeMultipartData("tic_status", binding.etStatus.text.toString())
                val latitude = makeMultipartData("latitude", location?.latitude)
                val longitude = makeMultipartData("longitude", location?.longitude)
                val acceptedticket = makeMultipartData(
                    "tic_accepted",
                    binding.acceptedByTripleE.isChecked,
                )
                val closedticket =
                    makeMultipartData("tic_accepted", binding.closedTicket.isChecked)
                val ticSystem = makeMultipartData("tic_system", binding.systemFault.text.toString())
                val ticSystemSub =
                    makeMultipartData("tic_system_Sub", binding.subSystemFault.text.toString())
                val ticSparePart = makeMultipartData("tic_sparepart", false)
                val params = mutableListOf(
                    notes,
                    status,
                    acceptedticket,
                    closedticket,
                    latitude,
                    longitude,
                    ticSparePart,
                    ticSystem,
                    ticSystemSub
                )

                (viewModel.file != null) ifTrue {
                    val file = makeMultipartData("file", viewModel.file!!)
                    params.add(file)
                }

                viewModel.uploadFile(
                    id = it.data.ticId, param = params.toMutableList()
                )

                showToast(this@AddTicketActivity, "Created Ticket successfully!")
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.responseeuploadfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                showToast(this, "Created Ticket successfully!")
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                showToast("Somethin went wrong!")
            }
        }

        if (methodOne) { // TODO
            binding.btnSelectSite.callOnClick()
        }
    }

    private fun setupAutoCompleteEngineers(markers: List<MarkerTripleE>) {
        val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
            this,
            R.layout.item_spinner_dropdown,
            markers
        )

        binding.txtFieldEngineer.setAdapter(adapter)
        binding.txtFieldEngineer.setOnItemClickListener { _, _, position, _ ->
            binding.txtFieldEngineer.clearFocus()
            viewModel.selectedEngineer.value = adapter.getItem(position)
        }
    }

    private fun setAutoCompleteSite(list: List<MarkerTripleE>) {
        val adapter: ArrayAdapter<MarkerTripleE> = ArrayAdapter<MarkerTripleE>(
            this,
            R.layout.item_spinner_dropdown,
            list
        )

        binding.txtSiteIdSiteName.setAdapter(adapter)
        binding.txtSiteIdSiteName.setOnItemClickListener { _, _, position, _ ->
            binding.txtSiteIdSiteName.clearFocus()
            viewModel.selectedSite.value = adapter.getItem(position)
        }
    }

    private fun submitData() {
        findViewsByType(
            binding.root,
            EditText::class.java
        ).forEach { it.setText(it.text.trim()) }
        val validator = Validator(binding)
        validator.enableFormValidationMode();
        val info = preferences.myDetailProfile.value
        if (!validator.validate()) {
            Toast.makeText(this, "There are fields that are still empty!", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.addTicket(
            mutableMapOf(
                "tic_number" to binding.txtTicketNumberCommand.text.toString().orDefault(""),
                "tic_number_assigned" to binding.txtTicketNumberAssigned.text.toString().orDefault(""),
                "tic_severety" to binding.txtSeverity.text.toString().orDefault(""),
                "tic_type" to binding.txtTicketType.text.toString().orDefault(""),
                "tic_assign_to" to binding.txtAssignTo.text.toString().orDefault(""),
                "tic_field_engineer" to binding.txtFieldEngineer.text.toString().orDefault(""),
                "tic_field_engineer_emp_id" to (viewModel.selectedEngineer.value?.id ?: ""),
                "tic_notes" to "",
                "tic_site_id" to (viewModel.responseGetSiteDetails.value?.data?.info_site?.siteId.toString() ?: ""),
                "tic_area" to binding.txtAreaNsRegion.text.toString().orDefault(""),
                "tic_person_in_charge" to binding.txtTripleEInCharge.text.toString().orDefault(""),
                "tic_person_in_charge_emp_id" to info?.id,
                "tic_status" to binding.etStatus.text.toString().orDefault(""),
                "tic_accepted" to binding.acceptedByTripleE.isChecked,
                "tic_closed" to binding.closedTicket.isChecked,
                "tic_system" to binding.systemFault.text.toString().orDefault(""),
                "tic_sparepart" to false,
                "tic_system_sub" to binding.subSystemFault.text.toString().orDefault(""),
                "tic_site" to viewModel.responseGetSiteDetails.value?.data?.info_site?.siteName.orDefault("")
            )
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_FILE_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        binding.statusFile.text =
                            getString(R.string.selected_file, getFileNameFromUri(this, uri))
                        viewModel.file = uri.let { getFileFromUri(this, it) }
                    }
                }

                OPEN_CAMERA_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        val file = uri.toFile()
                        binding.statusFile.text = getString(R.string.image_captured)
                        viewModel.file = getImageFromUri(file)
                    }
                }

                SELECT_ENGINEER_REQUEST_CODE -> {
                    data?.let { intent ->
                        val selectedEngineer = intent.getSerializableExtra("selected_engineer") as MarkerTripleE?
                        viewModel.selectedEngineer.value = selectedEngineer
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
}