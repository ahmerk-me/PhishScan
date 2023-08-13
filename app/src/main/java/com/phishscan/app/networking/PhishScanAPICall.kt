package com.cells.library.networking

import com.phishscan.app.classes.BASE_URL_DOMAIN_AGE
import com.phishscan.app.classes.BASE_URL_SHORTEN
import com.phishscan.app.classes.SessionManager
import com.phishscan.app.classes.ShortenUrl
import com.phishscan.app.classes.sessionManager
import com.tapadootest.app.networking.PhishScanAPIInterface

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class PhishScanAPICall {

    companion object {

        lateinit var retrofit: Retrofit

        private fun getClient(sessionManager: SessionManager?, isFirst: Boolean, site: Int): Retrofit {

            var interceptor = HttpLoggingInterceptor()

            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        var newRequest = chain.request().newBuilder()
//                            .addHeader("Accept-Language", LanguageSessionManager.language)
                            .addHeader("Content-Type", "application/json")
//                            .addHeader("Authorization", if (!sessionManager!!.isLoggedin) "" else sessionManager?.token ?: "")
//                            .addHeader("OffsetHours", getCurrentTimezoneOffset() +"")
//                            .addHeader("OffsetHours", "3")
                            .build()
                        return chain.proceed(newRequest)
                    }
                })
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .addInterceptor(interceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(if (site == ShortenUrl) BASE_URL_SHORTEN else BASE_URL_DOMAIN_AGE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit
        }

        fun apiInterface(site: Int): PhishScanAPIInterface? {
            return getClient(sessionManager, false, site).create(PhishScanAPIInterface::class.java)
        }

    }
}