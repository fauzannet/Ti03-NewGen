package id.parkmate.parking.model.service

import id.parkmate.parking.model.data.LoginRespone
import id.parkmate.parking.model.data.profile
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

//    @POST(constants.LOGIN_URL)
//    @Headers("User-Agent:@m!k0mXv=#neMob!le")
//    fun signin(@Body Data: Login): Call<LoginRespone>

    @POST(constants.LOGIN_URL)
    @FormUrlEncoded
    @Headers("User-Agent:@m!k0mXv=#neMob!le")
    fun signin(
        @Field("username") username: String?,
        @Field("keyword") keyword: String?
    ): Call<LoginRespone>

    @GET(constants.PROFILE_URL)
    @Headers("User-Agent:@m!k0mXv=#neMob!le")
    fun fetchdata(@Header("Authorization") token: String): Call<profile>

//        @FormUrlEncoded
//        @POST(constants.LOGIN_URL)
//        fun signin(@FieldMap params: Ha
////    @POST(constants.LOGIN_URL)shMap<String?, String?>): Response<LoginRespone>
//
//    @Headers("User-Agent:@m!k0mXv=#neMob!le", "Content-Type:application/x-www-form-urlencoded")
//    fun signin(
//        @Body data: Login
//    ): Call<LoginRespone>

//    @POST(constants.LOGIN_URL)
//    @Multipart
//    @Headers("User-Agent:@m!k0mXv=#neMob!le")
//    suspend fun loginUser (@Part ("username") username:RequestBody,
//                           @Part ("keyword") keyword:RequestBody
//    ): Response<LoginRespone>

}