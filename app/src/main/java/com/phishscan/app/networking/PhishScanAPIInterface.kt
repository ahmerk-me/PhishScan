package com.tapadootest.app.networking


import com.phishscan.app.model.ShortenModel
import retrofit2.Call
import retrofit2.http.*

interface PhishScanAPIInterface {

    @GET("{url}")
    fun getExpandedUrl(@Path("url") url: String): Call<ShortenModel?>

    @GET("api.php")
    fun getDomainAge(@Query("domain") domain: String): Call<Int?>

//    @POST("Register")
//    fun register(@Body registerRequest: RegisterRequest): Call<ResponseMessage>

}