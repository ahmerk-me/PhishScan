package com.phishscan.app.viewmodel

import android.util.Log
import androidx.compose.material3.Snackbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cells.library.networking.PhishScanAPICall
import com.phishscan.app.classes.DomainAge
import com.phishscan.app.classes.MaybePhishing
import com.phishscan.app.classes.NotPhishing
import com.phishscan.app.classes.Phishing
import com.phishscan.app.classes.ShortenUrl
import com.phishscan.app.model.ShortenModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.IDN
import java.net.InetAddress
import java.net.URI
import java.net.URL
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

class ScanViewModel : ViewModel() {

    var result: MutableLiveData<Int> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var isError: MutableLiveData<Boolean> = MutableLiveData()
    var errorText = "Some Error Occurred"

    var phishScore: Double = 0.0
    private var domain: String = ""
    private var subdomains: ArrayList<String> = ArrayList()
    private var path: String = ""
    private var queryParams: Map<String, String>? = null

    var errorList: ArrayList<String> = ArrayList()
    var expandedUrl = ""


    fun rateURL(url: String) {

        phishScore = 0.0

        expandedUrl = url

//        check all conditions here
        if (isURLShortened(url))
            expandedUrl = expandURL(url)

        if (expandedUrl.isEmpty()) {
            errorText = "Some error occurred! Please try another scan."
            isError.value = true

        } else {

            if (errorList.isNotEmpty())
                errorList.clear()

            //            todo: imp
            checkDomainAge(expandedUrl)

            segregateUrl(expandedUrl)

            domainSimilarity(expandedUrl)

            checkSubdomainAnomalies(expandedUrl)

            checkDomainSuspicion(expandedUrl)

            checkRedirects(expandedUrl)

            checkUrlValidity(expandedUrl)

            checkForIdnAndHomographAttacks(expandedUrl)

            checkForSuspiciousCharacters(expandedUrl)

            checkForDirectIPAddressUsage(expandedUrl)

            checkUrlStructureAndParameters(expandedUrl)

            checkUrlLength(expandedUrl)

            checkForAtSymbol(expandedUrl)

            checkProtocolInDomain(expandedUrl)

            checkPrimaryDomainLength()

            checkNumberOfDots(expandedUrl)

            calculateAverageWordLength(expandedUrl)

            checkLongestWordLength(expandedUrl)

            checkDomainCreationDate(expandedUrl)

            checkDNSRecord(expandedUrl)


            if (phishScore <= 12) {
                result.value = NotPhishing
            } else if (phishScore > 12 && phishScore <= 25) {
                result.value = MaybePhishing
            } else
                result.value = Phishing

            Log.e("11111", " PhishScore = $phishScore")

        }
    }

    //    todo: Segregating URL into its components
    private fun segregateUrl(url: String) {

        val uri = URI(url)

        try {
            domain = uri.host ?: "".lowercase()
            subdomains = domain.split(".").dropLast(1) as ArrayList<String>
            subdomains = subdomains.map { it.lowercase() } as ArrayList<String>
            path = uri.path.lowercase()
            queryParams = uri.query?.split("&")?.associate {
                val (key, value) = it.split("=")
                key to value
            }
        } catch (e: java.lang.Exception) {

            Log.e("11111", " Some error occurre in segregateUrl()")
        }

    }

    //    todo: Checking Domain Similarity
    private fun domainSimilarity(url: String) {

        val legitimateDomains =
            listOf("twitter.com", "google.com", "facebook.com", "paypal.com", "payment", "amazon")

        val normalizedDomain = domain

        for (domain in legitimateDomains) {
            if (isSimilarDomain(normalizedDomain, domain)) {
                addPhishScore(15.0)
                errorList.add("domainSimilarity => 15.0")

            }
        }
    }

    private fun isSimilarDomain(domain1: String, domain2: String): Boolean {
        val similarityThreshold = 0.8
        val similarity = calculateDomainSimilarity(domain1, domain2)
        return similarity >= similarityThreshold
    }

    private fun calculateDomainSimilarity(domain1: String, domain2: String): Double {
        val longerDomain = if (domain1.length >= domain2.length) domain1 else domain2
        val shorterDomain = if (domain1.length < domain2.length) domain1 else domain2

        val commonLength = longerDomain.length - longerDomain.diff(shorterDomain).length

        return commonLength.toDouble() / longerDomain.length.toDouble()
    }

    private fun String.diff(other: String): String {
        val diff = StringBuilder()
        for (i in 0 until this.length) {
            if (i >= other.length || this[i] != other[i]) {
                diff.append(this[i])
            }
        }
        return diff.toString()
    }

    //    todo: Checking for Subdomain anomalies
    private fun checkSubdomainAnomalies(url: String) {

        // Check if subdomains are unusual or unrelated to the main domain
        for (subdomain in subdomains) {
            if (!subdomain.matches("[a-zA-Z0-9-]+".toRegex())) {
                addPhishScore(10.0)
                errorList.add("checkSubdomainAnomalies => 10.0")

            }

        }
    }

    //    todo: Checking if url is from Suspicious Domain list
    private fun checkDomainSuspicion(url: String) {

        val suspiciousTlds = listOf(
            "xyz", "info", "biz", "top", "online", "club", "site", "ga", "cf",
            "tk", "org", "ml", "pw", "top", "ga", "icu"
        )

        val tld = domain.substringAfterLast(".", "")

        if (tld in suspiciousTlds) {
            addPhishScore(10.0)
            errorList.add("checkDomainSuspicion => 10.0")
        }
    }

    //    todo: Checking for Redirections and excessive use of shortened urls
    private fun checkRedirects(url: String) {

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode in 300..399) {
                val redirectUrl = connection.getHeaderField("Location")
                if (redirectUrl != null) {
                    // Increment suspicion score for each redirect
                    addPhishScore(3.0)
                    errorList.add("checkRedirects => 3.0")

                    // Check for excessive use of shorteners
                    if (redirectUrl.contains("bit.ly") || redirectUrl.contains("goo.gl")) {
                        addPhishScore(2.0)
                        errorList.add("checkRedirects => 2.0")
                    }
                }
            }

            connection.disconnect()
        } catch (e: Exception) {
            // Handle any exceptions and log errors
            println("An error occurred: ${e.message}")
        }
    }

    //    todo: checking URL validity by checking SSL certificates
    private fun checkUrlValidity(url: String) {
        var validityScore = 0.0

        try {
            val url = URL(url)
            val connection = url.openConnection()

            if (connection is HttpsURLConnection) {
                // Check if HTTPS is missing
                if (connection.url.protocol != "https") {
                    validityScore += 2.5
                }

                // Check if SSL certificates are invalid/expired
                try {
                    connection.connect()
                } catch (e: Exception) {
                    validityScore += 2.5
                }
            } else if (connection is HttpURLConnection) {
                // HTTP connection, increment suspicion score
                validityScore += 2.5
            }
        } catch (e: Exception) {
            // Error occurred, increment suspicion score
            validityScore += 2.5
        }

        addPhishScore(validityScore)

        if (validityScore > 0.0)
            errorList.add("checkUrlValidity => $validityScore")
    }

    //    todo: Checking Latin characters in the URL
    private fun checkForIdnAndHomographAttacks(url: String) {

        var isSuspicious = false

        // Convert the URL to its ASCII representation
        val asciiUrl = IDN.toASCII(url)

        // Check for non-Latin characters with similar Latin characters
        for (i in 0 until url.length) {
            val char = url[i]
            val asciiChar = asciiUrl[i]

            if (char != asciiChar) {
                isSuspicious = true
            }
        }

        if (isSuspicious) {
            addPhishScore(5.0)
            errorList.add("checkForIdnAndHomographAttacks => 5.0")
        }
    }

    //    todo: Checking for Suspicious characters
    private fun checkForSuspiciousCharacters(url: String) {
        val suspiciousCharacters = listOf(
            "@", "%", "!", "#", "$", "&", "*", "(", ")", "[", "]", "{", "}", "<", ">", "|", "\\",
            "/", "?", ":", ";", "'", "\"", "`", "~", "+", "=", "^"
        )

        var count = 0

        for (character in suspiciousCharacters) {
            if (url.contains(character)) {
                count++
            }
        }

        if (count > 4) {
            addPhishScore(5.0)
            errorList.add("checkForSuspiciousCharacters => 5.0")
        }
    }

    //    todo: Checking for direct IP address usage in the URL
    private fun checkForDirectIPAddressUsage(url: String) {

        try {
            val ipAddressRegex = """\b(?:\d{1,3}\.){3}\d{1,3}\b""".toRegex()
            val ipAddressMatchResult = ipAddressRegex.find(url)

            if (ipAddressMatchResult != null) {
                addPhishScore(5.0)
                errorList.add("checkForDirectIPAddressUsage => 5.0")

            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur during the IP address check
            println("An error occurred while checking for direct IP address usage: ${e.message}")
        }
    }

    //    todo: Checking for domain age and registration
    private fun checkDomainAge(url: String) {

        val withoutProtocol = url.replaceFirst("^(http://|https://)".toRegex(), "")

        // Extract the domain part by splitting the URL at the first slash ("/")
        val domainPart = withoutProtocol.split("/")[0]

        // Calculate domain age in years
        calculateDomainAge(domainPart)
    }

    private fun calculateDomainAge(url: String): Int {

        var domainAge = 0

        isLoading.value = true

        // Calculate domain age in days
        val apiCall: Call<Int?>? = PhishScanAPICall.apiInterface(DomainAge)?.getDomainAge(url)

        apiCall?.enqueue(object : Callback<Int?> {
            override fun onResponse(
                call: Call<Int?>,
                response: Response<Int?>
            ) {

                if (response.isSuccessful && response.body() != null) {

                    Log.e("11111", " domainAge ===>" + response.body())
                    domainAge = response.body()!!
                    if (domainAge <= 360) {
                        addPhishScore(5.0)
                        errorList.add("checkDomainAge => 5.0")
                    }
                }

                isLoading.value = false

            }

            override fun onFailure(call: Call<Int?>, t: Throwable) {

                isError.value = true
                errorText = t.toString()

                isLoading.value = false

            }

        })

        return domainAge
    }

    //    todo: Checking urL structure and parameters
    private fun checkUrlStructureAndParameters(url: String) {
        var suspicionScore = 0.0

        try {
            val url = URL(url)

            // Check for anomalies in URL structure
            if (url.protocol.isNullOrEmpty() || url.host.isNullOrEmpty()) {
                suspicionScore++
            }

            // Check for anomalies in URL parameters
            val queryParams = url.query?.split("&") ?: emptyList()
            for (param in queryParams) {
                val keyValue = param.split("=")
                if (keyValue.size != 2 || keyValue[0].isNullOrEmpty() || keyValue[1].isNullOrEmpty()) {
                    suspicionScore++
                }
            }

        } catch (e: Exception) {
            // Increment suspicion score if there is an error parsing the URL
            suspicionScore++
        }

        if (suspicionScore > 3) {
            errorList.add("checkUrlStructureAndParameters => 5.0")
            addPhishScore(5.0)
        } else if (suspicionScore > 0.0) {
            errorList.add("checkUrlStructureAndParameters => $suspicionScore")
            addPhishScore(suspicionScore)
        }
    }

    //    todo: Checking Url length
    private fun checkUrlLength(url: String) {

        val urlLength = url.length

        if (urlLength >= 70) {
            errorList.add("checkUrlLength => 5.0")
            addPhishScore(5.0)
        } else if (urlLength in 40..69) {
            errorList.add("checkUrlLength => 2.0")
            addPhishScore(2.0)
        } else
            addPhishScore(0.0)
    }

    //    todo: Checking the presence of '@' symbol in the URL
    private fun checkForAtSymbol(url: String) {

        var count = 0

        for (s in url) {
            if (s.equals("@"))
                count++
        }

        if (count == 1) {
            errorList.add("checkForAtSymbol => 2.0")
            addPhishScore(2.0)

        } else if (count > 1){
            errorList.add("checkForAtSymbol => 5.0")
            addPhishScore(5.0)
        }
    }

    //    todo: Checking domain protocol
    private fun checkProtocolInDomain(url: String) {
        val domain = getDomainFromUrl(url)

        if (!domain.startsWith("http://") || !domain.startsWith("https://")) {
            addPhishScore(2.0)
            errorList.add("checkProtocolInDomain => 2.0")
        }
    }

    private fun getDomainFromUrl(url: String): String {
        val domainStartIndex = url.indexOf("://") + 3
        val domainEndIndex = url.indexOf("/", domainStartIndex)
        return if (domainEndIndex == -1) {
            url.substring(domainStartIndex)
        } else {
            url.substring(domainStartIndex, domainEndIndex)
        }
    }

    //    todo: Checking length of the primary domain
    private fun checkPrimaryDomainLength() {
        try {
            if (domain.length > 14) {
                addPhishScore(3.0)
                errorList.add("checkPrimaryDomainLength => 3.0")
            } else if (domain.length in 7..13) {
                errorList.add("checkPrimaryDomainLength => 1.0")
                addPhishScore(1.0)
            } else
                addPhishScore(0.0)

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }

    //    todo: Checking number of dots in the url
    private fun checkNumberOfDots(url: String) {
        var dotCount = 0
        for (char in url) {
            if (char == '.') {
                dotCount++
            }
        }

        if (dotCount > 4) {
            errorList.add("checkNumberOfDots => 3.0")
            addPhishScore(3.0)
        }
    }

    //    todo: Calculate average word length
    private fun calculateAverageWordLength(url: String) {
        val words =
            url.split("\\W+".toRegex()) // Split the URL string into words using non-word characters as delimiters
        val totalWordLength = words.sumOf { it.length } // Calculate the total length of all words

        if (words.isNotEmpty())
            if (totalWordLength.toDouble() / words.size.toDouble() > 6) {
                errorList.add("calculateAverageWordLength => 2.0")
                addPhishScore(2.0)
            }
    }

    //    todo: Checking length of the longest word
    private fun checkLongestWordLength(url: String) {

        val words =
            url.split("\\W+".toRegex()) // Split the URL string into words using non-word characters as delimiters
        var longestWordLength = 0

        for (word in words) {
            if (word.length > longestWordLength) {
                longestWordLength = word.length
            }
        }

        if (longestWordLength > 15) {
            errorList.add("checkLongestWordLength => 3.0")
            addPhishScore(3.0)
        }
    }

    //    todo: Checking if the domain is created within 1 year from current date
    private fun checkDomainCreationDate(urlString: String) {

        var isNewDomain = false

        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            val headerFields = connection.headerFields

            val creationDate = headerFields["Creation-Date"]?.firstOrNull()
            if (creationDate != null) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val domainCreationDate = LocalDate.parse(creationDate, formatter)
                val currentDate = LocalDate.now()
                val oneYearAgo = currentDate.minusYears(1)

                isNewDomain = domainCreationDate.isBefore(oneYearAgo)
            }

        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }

        if (isNewDomain) {
            errorList.add("checkDomainCreationDate => 5.0")
            addPhishScore(5.0)
        }
    }

    //  todo: Checking DNS records if available or not
    private fun checkDNSRecord(url: String) {
        try {
            val ipAddress = InetAddress.getByName(url)
        } catch (e: UnknownHostException) {
            errorList.add("checkDNSRecord => 5.0")
            addPhishScore(5.0)
        }
    }

    //    todo: Expanding URL if shortened
    private fun expandURL(url: String): String {

        isLoading.value = true

        var resolvedUrl = ""

        val apiCall: Call<ShortenModel?>? =
            PhishScanAPICall.apiInterface(ShortenUrl)?.getExpandedUrl(url)

        apiCall?.enqueue(object : Callback<ShortenModel?> {
            override fun onResponse(
                call: Call<ShortenModel?>,
                response: Response<ShortenModel?>
            ) {

                if (response.isSuccessful && response.body() != null) {

                    resolvedUrl = response.body()!!.resolvedUrl

                    Log.e("11111", " resolvedURL ===>$resolvedUrl")
                }

                isLoading.value = false

            }

            override fun onFailure(call: Call<ShortenModel?>, t: Throwable) {

                isError.value = true
                errorText = t.toString()

                isLoading.value = false

            }

        })

        return resolvedUrl
    }

    private fun isURLShortened(url: String): Boolean {

        // Check if the URL starts with a known shortening service domain.
        val shorteningServiceDomains = listOf("bit.ly", "tinyurl.com", "goo.gl", "BL.INK", "Ow.ly")
        for (domain in shorteningServiceDomains) {
            if (url.contains(domain)) {
                addPhishScore(2.0)
                errorList.add("isURLShortened => 2.0")
                return true
            }
        }
        return false
    }

    //    todo: Adding Phish-Score
    private fun addPhishScore(points: Double) {

        phishScore += points
        Log.e("22222", " phishScore increased to ->$phishScore")
    }

    //    todo: Reducing Phish-Score
    private fun reducePhishScore(points: Double) {

        phishScore -= points
        Log.e("22222", " phishScore reduced to ->$phishScore")

    }
}