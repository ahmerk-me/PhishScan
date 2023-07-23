package com.phishscan.app.classes

import androidx.fragment.app.FragmentActivity
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.phishscan.app.R

object Navigator {
    fun loadFragment(
        activity: AppCompatActivity,
        baseFragment: Fragment?,
        containerId: Int,
        isStacked: Boolean,
        s: String?
    ) {
        if (!isStacked) {
            activity.supportFragmentManager.beginTransaction()
                .replace(containerId, baseFragment!!)
                .commit()
//                .commitAllowingStateLoss()
        } else {
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(containerId, baseFragment!!)
                .addToBackStack(s)
                .commit()
        }
    }

    fun loadFragment2(
        activity: FragmentActivity,
        baseFragment: Fragment?,
        containerId: Int,
        isStacked: Boolean,
        s: String?
    ) {
        if (!isStacked) {
            activity.supportFragmentManager.beginTransaction()
                .add(containerId, baseFragment!!)
                .commit()
        } else {
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(containerId, baseFragment!!)
                .addToBackStack(s).commit()
        }
    }

}