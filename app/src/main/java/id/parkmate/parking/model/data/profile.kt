package id.parkmate.parking.model.data

import com.google.gson.annotations.SerializedName


data class profile (

    @SerializedName("Mhs")
    val dataprofile: dataprofiles
)

data class dataprofiles(
    val Npm: String? = null,
    val Nama: String? = null,
    val Angkatan: String? = null,
    val EmailAmikom: String? = null,
    val Prodi: String? = null,
    val NpmImg: String? = null
)
