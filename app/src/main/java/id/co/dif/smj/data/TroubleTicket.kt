package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class TroubleTicket(
    var status_ticket : Boolean = true,
    @SerializedName("images"               ) var images : String? = null,
    @SerializedName("tic_accepted"         ) var ticAccepted       : Boolean?    = null,
    @SerializedName("tic_area"             ) var ticArea           : String? = null,
    @SerializedName("tic_assign_to"        ) var ticAssignTo       : String? = null,
    @SerializedName("tic_closed"           ) var ticClosed         : Boolean?    = null,
    @SerializedName("tic_field_engineer"   ) var ticFieldEngineer  : String? = null,
    @SerializedName("tic_id"               ) var ticId             : String?    = null,
    @SerializedName("tic_notes"            ) var ticNotes          : String? = null,
    @SerializedName("tic_number"           ) var ticNumber         : String? = null,
    @SerializedName("tic_number_assigned"  ) var ticNumberAssigned : String? = null,
    @SerializedName("tic_updated"  ) var ticUpdated : String? = null,
    @SerializedName("tic_person_in_charge" ) var ticPersonInCharge : String? = null,
    @SerializedName("tic_severety"         ) var ticSeverety       : String? = null,
    @SerializedName("tic_site"             ) var ticSite           : String? = null,
    @SerializedName("tic_site_id"          ) var ticSiteId         : String? = null,
    @SerializedName("tic_sparepart"        ) var ticSparepart      : Boolean?    = null,
    @SerializedName("tic_status"           ) var ticStatus         : String? = null,
    @SerializedName("tic_type"             ) var ticType           : String? = null,
    @SerializedName("tic_received"         ) var ticReceived           : String? = null
) : BaseData()
