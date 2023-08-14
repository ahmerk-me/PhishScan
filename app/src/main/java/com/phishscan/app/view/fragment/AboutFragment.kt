package com.phishscan.app.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.CodeScanner
import com.phishscan.app.R
import com.phishscan.app.classes.LanguageSessionManager
import com.phishscan.app.classes.SessionManager
import com.phishscan.app.databinding.FragmentAboutUsBinding
import com.phishscan.app.databinding.FragmentScanBinding
import com.phishscan.app.view.activity.MainActivity
import com.phishscan.app.viewmodel.ScanViewModel

class AboutFragment(private val act: MainActivity) : Fragment()  {

    private val TAG = ScanFragment::class.java.simpleName

    private lateinit var mSessionManager: SessionManager

    private lateinit var languageSessionManager: LanguageSessionManager

    private lateinit var binding: FragmentAboutUsBinding

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("Entered AboutFragment !!!====<>>>>>")

        try {

            mSessionManager = SessionManager(act)

            languageSessionManager = LanguageSessionManager(act)

        } catch (e: Exception) {

            Log.e(
                TAG + " onCreateLine>>LineNumber: " +
                        Thread.currentThread().stackTrace[2].lineNumber, e.message.toString()
            )

        }
    }


    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {

            binding = FragmentAboutUsBinding.inflate(inflater, container, false)

            initViews()

        } catch (e: Exception) {

            Log.e(
                TAG + " onCreateLine>>LineNumber: " +
                        Thread.currentThread().stackTrace[2].lineNumber, e.message.toString()
            )

        }

        return binding.root

    }


    open fun initViews() {

        act.tabNumber = 2
        act.setupDefaultSettings()
        act.setTextFonts(binding.root)

        act.binding.appBarHome.appBarNormal.title.text = act.getString(R.string.AboutUSLabel)

        binding.tvAbout.text = "PhishScan is an application developed by Ahmer Khalique Khan, student of MSc CyberSecurity at National College of Ireland (NCI). The objective of this app is to provide a Phish-Score to the user when he/she scans a barcode. PhishScan uses a novel algorithm based on heuristic techniques to determine the phish-score and alert the user if a scanned QR houses a phishing URL or not. PhishScan does visit the website (unlike the competitors) which adds additional security to the user's device."
    }
}