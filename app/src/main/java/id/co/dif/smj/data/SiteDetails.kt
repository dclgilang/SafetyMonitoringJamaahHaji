package id.co.dif.smj.data

import com.google.gson.annotations.SerializedName

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
class SiteDetails(
    @SerializedName("pgroup_cluster") val pgroupCluster: String?,
    @SerializedName("pgroup_nscluster") val pgroupNsCluster: String?,
    @SerializedName("site_address_kecamatan") val siteAddressKecamatan: String?,
    @SerializedName("site_address_kelurahan") val siteAddressKelurahan: String?,
    @SerializedName("site_address_street") val siteAddressStreet: String?,
    @SerializedName("site_building_type") val siteBuildingType: String?,
    @SerializedName("site_contact_phone") val siteContactPhone: String?,
    @SerializedName("site_end_customer") val siteEndCustomer: String?,
    @SerializedName("site_id") var siteId: Int?,
    @SerializedName("site_id_customer") val siteIdCustomer: String?,
    @SerializedName("site_name", alternate = ["site_info_name"]) var siteName: String?,
    @SerializedName("site_provider") val siteProvider: String?,
    @SerializedName("site_technology") val siteTechnology: String?,
    @SerializedName("contact_email") val contactEmail: String? = null,
    @SerializedName("contact_location") val contactLocation: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    @SerializedName("site_info_address_kelurahan") val siteInfoAddressKelurahan: String? = null,
    @SerializedName("site_info_address_street") val siteInfoAddressStreet: String? = null,
    @SerializedName("site_info_email") val siteInfoEmail: String? = null,
    @SerializedName("technology_area") val technologyArea: String? = null,
    @SerializedName("technology_cluster") val technologyCluster: String? = null,
    @SerializedName("technology_latitude", alternate = ["site_latitude"]) val technologyLatitude: String? = null,
    @SerializedName("technology_longtitude", alternate = ["site_longtitude"]) val technologyLongitude: String? = null,
    @SerializedName("technology_site_id") val technologySiteId: String? = null,
    @SerializedName("technology_system") val technologySystem: String? = null
) : BaseData() {
    fun toSiteLocation(): MarkerTripleE {
        return MarkerTripleE(
            image = null,
            site_name = this.siteName ?: "",
            tic_site = "",
            countdown = null,
            latitude = this.technologyLatitude,
            longtitude = this.technologyLongitude,
            site_id = this.siteId,
            site_addre_street = this.siteAddressStreet,
            site_address_kelurahan = this.siteAddressKelurahan,
            site_end_customer = this.siteEndCustomer,
            type = "",
            pgroup_nscluster = this.pgroupNsCluster,
            tic_area = this.technologyArea,
            site_contact_phone = this.siteContactPhone,
        )
    }

    override fun toString(): String {
        return "SiteDetails(" +
                "pgroupCluster=$pgroupCluster, " +
                "pgroupNsCluster=$pgroupNsCluster, " +
                "siteAddressKecamatan=$siteAddressKecamatan, " +
                "siteAddressKelurahan=$siteAddressKelurahan, " +
                "siteAddressStreet=$siteAddressStreet, " +
                "siteBuildingType=$siteBuildingType, " +
                "siteContactPhone=$siteContactPhone, " +
                "siteEndCustomer=$siteEndCustomer, " +
                "siteId=$siteId, " +
                "siteIdCustomer=$siteIdCustomer, " +
                "siteLatitude=$technologyLatitude, " +
                "siteLongitude=$technologyLongitude, " +
                "siteName=$siteName, " +
                "siteProvider=$siteProvider, " +
                "siteTechnology=$siteTechnology, " +
                "contactEmail=$contactEmail, " +
                "contactLocation=$contactLocation, " +
                "contactPhone=$contactPhone, " +
                "siteInfoAddressKelurahan=$siteInfoAddressKelurahan, " +
                "siteInfoAddressStreet=$siteInfoAddressStreet, " +
                "siteInfoEmail=$siteInfoEmail, " +
                "technologyArea=$technologyArea, " +
                "technologyCluster=$technologyCluster, " +
                "technologyLatitude=$technologyLatitude, " +
                "technologyLongitude=$technologyLongitude, " +
                "technologySiteId=$technologySiteId, " +
                "technologySystem=$technologySystem" +
                ")"
    }
}
