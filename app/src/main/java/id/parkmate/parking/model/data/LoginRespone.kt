package id.parkmate.parking.model.data

import com.google.gson.annotations.SerializedName

data class LoginRespone (

    @SerializedName("status_code")
    var statusCode: Int,

    @SerializedName("access_token")
    var authToken: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("keyword")
    val keyword: String,

    @SerializedName("Mhs")
    val mhs: String,

    @SerializedName("user")
    var user: List<profile>,
)