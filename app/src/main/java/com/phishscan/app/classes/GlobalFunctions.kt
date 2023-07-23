package com.phishscan.app.classes

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager


var sessionManager: SessionManager? = null

var languageSessionManager: LanguageSessionManager? = null

fun hideKeyboard(activity: Activity) {
    try {
        val inputManager: InputMethodManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView: View? = activity.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun DisableLayout(layout: ViewGroup) {
    layout.isEnabled = false
    for (i in 0 until layout.childCount) {
        val child = layout.getChildAt(i)
        if (child is ViewGroup) {
            DisableLayout(child)
        } else {
            child.isEnabled = false
        }
    }
}

fun EnableLayout(layout: ViewGroup) {
    layout.isEnabled = true
    for (i in 0 until layout.childCount) {
        val child = layout.getChildAt(i)
        if (child is ViewGroup) {
            EnableLayout(child)
        } else {
            child.isEnabled = true
        }
    }
}
