package id.co.dif.smj.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import id.co.dif.smj.R
import id.co.dif.smj.base.BaseBottomSheetDialog
import id.co.dif.smj.base.BaseViewModel
import id.co.dif.smj.data.SiteDetails
import id.co.dif.smj.data.MarkerTripleE
import id.co.dif.smj.databinding.DialogMarkerPopUpBinding
import id.co.dif.smj.presentation.activity.EngineerProfileActivity
import id.co.dif.smj.presentation.activity.HomeDetailInfoActivity
import id.co.dif.smj.presentation.activity.MainActivity
import id.co.dif.smj.presentation.activity.MapSiteActivity
import id.co.dif.smj.presentation.activity.PowerGeneratorDetailActivity
import id.co.dif.smj.utils.base64ImageToBitmap
import id.co.dif.smj.utils.loadImage
import id.co.dif.smj.utils.log
import id.co.dif.smj.utils.orDefault
import id.co.dif.smj.utils.shimmerDrawable

class MarkerPopupDialog(val marker: MarkerTripleE?) :
    BaseBottomSheetDialog<BaseViewModel, DialogMarkerPopUpBinding, SiteDetails>() {

    override val layoutResId = R.layout.dialog_marker_pop_up

    var onSiteIsSelected: (marker: MarkerTripleE) -> Unit = { _ -> }
    var onViewProfileClicked: (engineer: MarkerTripleE) -> Unit = { _ -> }
    var isSiteSelectable = false

    companion object {
        fun newInstance(marker: MarkerTripleE?) = MarkerPopupDialog(marker)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSelectThisSite.setOnClickListener {
            marker?.let {
                onSiteIsSelected(marker)
                dismiss()
            }
        }

        Log.d("TAG", "onViewCreated: ${marker?.image}")
        binding.btnSelectThisSite.isVisible = isSiteSelectable
        binding.titleSelectThisSite.isVisible = isSiteSelectable
        binding.textAddress.text = marker?.site_addre_street
        binding.textPic.text = marker?.site_end_customer
        binding.contactPhone.text = marker?.site_contact_phone
        when (marker?.type) {
            "site",
            "TT Site All" -> {
                binding.textName.text = marker?.site_name
                val randomStrings = arrayOf(
                    "Tharik",
                    "Gilang",
                    "Ikky",
                    "Mirza",
                    "Adi"
                )

                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size) // Generates a random index within the array size
                val randomString = randomStrings[randomIndex]
                binding.textPic.text = randomString
//                binding.textPic.text = marker?.site_end_customer
                binding.textAddress.text = marker?.site_addre_street
                binding.contactPhone.text = marker?.site_contact_phone.orDefault()
                binding.tvAddress.text = marker.site_address_kelurahan

            }

            "TT Map All" -> {
                binding.textName.text = marker?.site_name.orDefault()
                binding.textAddress.text = marker?.pgroup_nscluster
                binding.contactPhone.text = marker?.site_contact_phone
                binding.tvAddress.text = marker.site_address_kelurahan
            }

            "technician" -> {
                binding.textName.text = marker?.name.orDefault()
                marker.image.let { image ->
                    binding.imagePerson.setImageBitmap(image?.let { base64ImageToBitmap(it) })
                }
//                binding.imgIcon.loadImage(marker?.image, shimmerDrawable(), R.drawable.ic_syehhh, circleCrop = true)
                marker.image.log("hduduwduewduwee")
                val randomStringsAddress = arrayOf(
                    "Purwakarta, Jawa Barat",
                    "Kuningan, Jakarta",
                    "Solo, Jawa tengah",
                    "Bandung, Jawa Barat",
                    "Cianjur, Jawa Barat"
                )
                val randomAddress = java.util.Random()
                val randomIndexAddress = randomAddress.nextInt(randomStringsAddress.size)
                val randomStringAddress = randomStringsAddress[randomIndexAddress]
                binding.textAddress.text = randomStringAddress
                val randomStrings = arrayOf(
                    "Uka Tour And Travel",
                    "DIF Travel Haji",
                    "Shakira Tour and Travel",
                    "Arness Shuttle",
                    "Nadiem Travel Haji"
                )
                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size)
                val randomString = randomStrings[randomIndex]
                binding.tvAddress.text = randomString
                val randomNumberPhone = arrayOf(
                    "081-827-282-289",
                    "087-778-311-222",
                    "083-822-444-000",
                    "083-111-555-222",
                    "087-777-123-000"
                )
                val randomPhone = java.util.Random()
                val randomPhoneIndex = randomPhone.nextInt(randomNumberPhone.size) // Generates a random index within the array size
                val randomPhoneString = randomNumberPhone[randomPhoneIndex]
                binding.contactPhone.text = randomPhoneString
                val randomStringsTL = arrayOf(
                    "Tharik (Muzawir)",
                    "Gilang (Muzawir)",
                    "Ikky (Muzawir)",
                    "Mirza (Muzawir)",
                    "Adi (Muzawir)"
                )
                val randomTl = java.util.Random()
                val randomIndexTl = randomTl.nextInt(randomStringsTL.size) // Generates a random index within the array size
                val randomStringTl = randomStringsTL[randomIndexTl]
                binding.textPic.text = randomStringTl
            }
            "Apartement" -> {
//                binding.textName.text = marker?.site_name
                binding.textName.text = "Jamaah Haji"
                val randomStrings = arrayOf(
                    "Tharik",
                    "Gilang",
                    "Ikky",
                    "Mirza",
                    "Adi"
                )
                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size) // Generates a random index within the array size
                val randomString = randomStrings[randomIndex]
                binding.textPic.text = randomString
//                binding.textPic.text = marker?.site_end_customer
                binding.textAddress.text = marker?.address
                val randomNumberPhone = arrayOf(
                    "081-827-282-289",
                    "087-778-311-222",
                    "083-822-444-000",
                    "083-111-555-222",
                    "087-777-123-000"
                )
                val randomPhone = java.util.Random()
                val randomPhoneIndex = randomPhone.nextInt(randomNumberPhone.size) // Generates a random index within the array size
                val randomPhoneString = randomNumberPhone[randomPhoneIndex]
                binding.contactPhone.text = randomPhoneString
//                binding.contactPhone.text = marker?.site_contact_phone.orDefault()
                binding.tvAddress.text = marker?.site_address_kelurahan
                binding.imgIcon.loadImage(marker?.images, shimmerDrawable(), R.drawable.ic_syehhh, circleCrop = true)
            }
            "Kapal" -> {
//                binding.textName.text = marker?.site_name
                binding.textName.text = "Jamaah Haji"
                val randomStrings = arrayOf(
                    "Tharik",
                    "Gilang",
                    "Ikky",
                    "Mirza",
                    "Adi"
                )
                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size) // Generates a random index within the array size
                val randomString = randomStrings[randomIndex]
                binding.textPic.text = randomString
//                binding.textPic.text = marker?.site_end_customer
                binding.textAddress.text = marker?.site_addre_street
                val randomNumberPhone = arrayOf(
                    "081-827-282-289",
                    "087-778-311-222",
                    "083-822-444-000",
                    "083-111-555-222",
                    "087-777-123-000"
                )
                binding.imgIcon
                val randomPhone = java.util.Random()
                val randomPhoneIndex = randomPhone.nextInt(randomNumberPhone.size) // Generates a random index within the array size
                val randomPhoneString = randomNumberPhone[randomPhoneIndex]
                binding.contactPhone.text = randomPhoneString
//                binding.contactPhone.text = marker?.site_contact_phone.orDefault()
                binding.tvAddress.text = marker?.site_address_kelurahan
                binding.imgIcon.loadImage(marker?.image, shimmerDrawable(), R.drawable.ic_syehhh, circleCrop = true)
            }
            "Excavator" -> {
//                binding.textName.text = marker?.site_name
                binding.textName.text = "Jamaah Haji"
                val randomStrings = arrayOf(
                    "Tharik",
                    "Gilang",
                    "Ikky",
                    "Mirza",
                    "Adi"
                )
                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size) // Generates a random index within the array size
                val randomString = randomStrings[randomIndex]
                binding.textPic.text = randomString
//                binding.textPic.text = marker?.site_end_customer
                binding.textAddress.text = marker?.site_addre_street
                val randomNumberPhone = arrayOf(
                    "081-827-282-289",
                    "087-778-311-222",
                    "083-822-444-000",
                    "083-111-555-222",
                    "087-777-123-000"
                )
                binding.imgIcon
                val randomPhone = java.util.Random()
                val randomPhoneIndex = randomPhone.nextInt(randomNumberPhone.size) // Generates a random index within the array size
                val randomPhoneString = randomNumberPhone[randomPhoneIndex]
                binding.contactPhone.text = randomPhoneString
//                binding.contactPhone.text = marker?.site_contact_phone.orDefault()
                binding.tvAddress.text = marker?.site_address_kelurahan
                binding.imgIcon.loadImage(marker?.image, shimmerDrawable(), R.drawable.ic_syehhh, circleCrop = true)
            }
            "Generator" -> {
//                binding.textName.text = marker?.site_name
                binding.textName.text = "Jamaah Haji"
                val randomStrings = arrayOf(
                    "Tharik",
                    "Gilang",
                    "Ikky",
                    "Mirza",
                    "Adi"
                )
                val random = java.util.Random()
                val randomIndex = random.nextInt(randomStrings.size) // Generates a random index within the array size
                val randomString = randomStrings[randomIndex]
                binding.textPic.text = randomString
//                binding.textPic.text = marker?.site_end_customer
                binding.textAddress.text = marker?.site_addre_street
                val randomNumberPhone = arrayOf(
                    "081-827-282-289",
                    "087-778-311-222",
                    "083-822-444-000",
                    "083-111-555-222",
                    "087-777-123-000"
                )
                binding.imgIcon
                val randomPhone = java.util.Random()
                val randomPhoneIndex = randomPhone.nextInt(randomNumberPhone.size) // Generates a random index within the array size
                val randomPhoneString = randomNumberPhone[randomPhoneIndex]
                binding.contactPhone.text = randomPhoneString
//                binding.contactPhone.text = marker?.site_contact_phone.orDefault()
                binding.tvAddress.text = marker?.site_address_kelurahan
                binding.imgIcon.loadImage(marker?.image, shimmerDrawable(), R.drawable.ic_syehhh, circleCrop = true)
            }
        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btnDetail.setOnClickListener {
            if (marker?.type == "TT Site All") {
                preferences.selectedSite.value = marker
                startActivity(Intent(requireContext(), HomeDetailInfoActivity::class.java))
//                TicketListPopupDialog.newInstance(marker).show(
//                    childFragmentManager,
//                    TicketListPopupDialog::class.java.name
                //               )
            } else if (marker?.type == "Generator") {
                preferences.selectedSite.value = marker
                startActivity(Intent(requireContext(), HomeDetailInfoActivity::class.java))
//                startActivity(Intent(requireContext(), PowerGeneratorDetailActivity::class.java))
            } else {
                    preferences.selectedSite.value = marker
                    startActivity(Intent(requireContext(), HomeDetailInfoActivity::class.java))
//                    val info = preferences.myDetailProfile.value
//                    info?.let {
//                        val profileId = engineer.id ?: -1
//                        preferences.selectedProfileId.value = profileId
//                        val intent = Intent(currentActivity, HomeDetailInfoActivity::class.java)
//                        if (it.id == engineer.id) {
//                            currentActivity.startActivity(intent)
//                            startActivity(Intent(requireContext(), HomeDetailInfoActivity::class.java))
//                        }
//                    }


            }
        }


//            else {
//                preferences.selectedSite.value=marker
//                TicketListPopupDialog.newInstance(marker).show(
//                    childFragmentManager,
//                    TicketListPopupDialog::class.java.name
//                )
////                context?.startActivity(Intent(context, DetilTicketActivity::class.java))
////                startActivity(Intent(requireContext(), MapSiteActivity::class.java))
//            }
//        }
        binding.btnDetail.requestFocus()
    }


}

