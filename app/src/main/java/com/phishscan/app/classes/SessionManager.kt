package com.phishscan.app.classes

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by webuser1 on 6/14/2015.
 */
class SessionManager @SuppressLint("CommitPrefEdits") constructor(var _context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var PRIVATE_MODE = 0

    fun LoginSession() {
        editor.putBoolean(IS_LOGGED, true)
        editor.commit()
    }

    val isLoggedin: Boolean
        get() = pref.getBoolean(IS_LOGGED, false)

    var isFirstTime: Boolean
        get() = pref.getBoolean(IsFirstTime, true)
        set(isFirstTime) {
            editor.putBoolean(IsFirstTime, isFirstTime)
            editor.commit()
        }

    fun LogoutSession() {
        editor.putBoolean(IS_LOGGED, false)
        editor.commit()
    }

    var userCode: String?
        get() = pref.getString(UserCode, "0")
        set(code) {
            editor.putString(UserCode, code)
            editor.commit()
        }

    var fragType: Int
        get() = pref.getInt(FragType, 1)
        set(code) {
            editor.putInt(FragType, code)
            editor.commit()
        }

    var userAddress: String?
        get() = pref.getString(UserAddress, "")
        set(address) {
            editor.putString(UserAddress, address)
            editor.commit()
        }

    var userName: String?
        get() = pref.getString(UserName, "")
        set(name) {
            editor.putString(UserName, name)
            editor.commit()
        }

    var userFullName: String?
        get() = pref.getString(UserFullName, "")
        set(name) {
            editor.putString(UserFullName, name)
            editor.commit()
        }

    var userPassword: String?
        get() = pref.getString(UserPassword, "")
        set(password) {
            editor.putString(UserPassword, password)
            editor.commit()
        }

    var userMobile: String?
        get() = pref.getString(UserMobile, "")
        set(mob) {
            editor.putString(UserMobile, mob)
            editor.commit()
        }

    var userEmail: String?
        get() = pref.getString(UserEmail, "")
        set(email) {
            editor.putString(UserEmail, email)
            editor.commit()
        }

    var userCountryId: String?
        get() = pref.getString(UserCountryId, "")
        set(id) {
            editor.putString(UserCountryId, id)
            editor.commit()
        }

    var userCurrencyCode: String?
        get() = pref.getString(UserCurrencyCode, "")
        set(userCurrencyId) {
            editor.putString(UserCurrencyCode, userCurrencyId)
            editor.commit()
        }

    var guestUserId: String?
        get() = pref.getString(GuestUserId, "0")
        set(code) {
            editor.putString(GuestUserId, code)
            editor.commit()
        }

    var token: String
        get() = pref.getString(
            Token,
            ""
        ).toString()
        set(code) {
            editor.putString(Token, code)
            editor.commit()
        }

    var agoraToken: String
        get() = pref.getString(
            AgoraToken,
            ""
        ).toString()
        set(code) {
            editor.putString(AgoraToken, code)
            editor.commit()
        }

    var facebook: String
        get() = pref.getString(
            Facebook,
            ""
        ).toString()
        set(code) {
            editor.putString(Facebook, code)
            editor.commit()
        }

    var twitter: String
        get() = pref.getString(
            Twitter,
            ""
        ).toString()
        set(code) {
            editor.putString(Twitter, code)
            editor.commit()
        }

    var instagram: String
        get() = pref.getString(
            Instagram,
            ""
        ).toString()
        set(code) {
            editor.putString(Instagram, code)
            editor.commit()
        }

    var youtube: String
        get() = pref.getString(
            Youtube,
            ""
        ).toString()
        set(code) {
            editor.putString(Youtube, code)
            editor.commit()
        }

    var agoraId: String
        get() = pref.getString(AgoraId, "").toString()
//    get() = "5a897a99f1584d79977ca07881ab7475"
        set(code) {
            editor.putString(AgoraId, code)
            editor.commit()
        }

    fun vendor() {
        editor.putBoolean(IsVendor, true)
        editor.commit()
    }

    fun customer() {
        editor.putBoolean(IsVendor, false)
        editor.commit()
    }

    val isVendor: Boolean
        get() = pref.getBoolean(IsVendor, false)

    var recentlyViewArrayList: String?
        get() = pref.getString(RecentlyViewArrayList, "")
        set(token) {
            editor.putString(RecentlyViewArrayList, token)
            editor.commit()
        }

    var versionNumber: Int?
        get() = pref.getInt(VersionName, 0)
        set(version) {
            version?.let { editor.putInt(VersionName, it) }
            editor.commit()
        }

    fun changeNotification(status: Boolean) {
        editor.putBoolean(IsNotificationOn, status)
        editor.commit()
    }

    val isNotificationOn: Boolean
        get() = pref.getBoolean(IsNotificationOn, true)

    companion object {
        private const val PREF_NAME = "com.camouflage.pref"
        private const val IS_LOGGED = "isLogged"
        private const val UserCode = "UserCode"
        private const val UserAddress = "UserAddress"
        private const val UserEmail = "UserEmail"
        private const val UserName = "UserName"
        private const val UserFullName = "UserFullName"
        private const val UserPassword = "UserPassword"
        private const val UserMobile = "UserMobile"
        private const val UserCountryId = "UserCountryId"
        private const val IsFirstTime = "IsFirstTime"
        private const val UserCurrencyCode = "UserCurrencyCode"
        private const val GuestUserId = "GuestUserId"
        private const val Token = "Token"
        private const val AgoraToken = "AgoraToken"
        private const val AgoraId = "AgoraId"
        private const val IsVendor = "IsVendor"
        private const val RecentlyViewArrayList = "RecentlyViewArrayList"
        private const val IsNotificationOn = "IsNotificationOn"
        private const val FragType = "FragType"
        private const val VersionName = "VersionName"
        private const val Facebook = "Facebook"
        private const val Twitter = "Twitter"
        private const val Instagram = "Instagram"
        private const val Youtube = "Youtube"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}