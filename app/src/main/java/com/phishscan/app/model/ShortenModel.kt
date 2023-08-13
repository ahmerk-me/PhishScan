package com.phishscan.app.model


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ShortenModel {
    @SerializedName("requested_url")
    @Expose
    var requestedUrl: String = ""

    @SerializedName("resolved_url")
    @Expose
    var resolvedUrl: String = ""

    @SerializedName("success")
    @Expose
    var success: Boolean = false

    @SerializedName("usage_count")
    @Expose
    var usageCount: Int = 0
}