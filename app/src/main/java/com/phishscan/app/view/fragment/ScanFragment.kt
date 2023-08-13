package com.phishscan.app.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.snackbar.Snackbar
import com.phishscan.app.R
import com.phishscan.app.classes.DisableLayout
import com.phishscan.app.classes.EnableLayout
import com.phishscan.app.classes.LanguageSessionManager
import com.phishscan.app.classes.MaybePhishing
import com.phishscan.app.classes.NotPhishing
import com.phishscan.app.classes.Phishing
import com.phishscan.app.classes.SessionManager
import com.phishscan.app.databinding.FragmentScanBinding
import com.phishscan.app.view.activity.MainActivity
import com.phishscan.app.viewmodel.ScanViewModel

open class ScanFragment(private val act: MainActivity) : Fragment() {

    private val TAG = ScanFragment::class.java.simpleName

    private lateinit var mSessionManager: SessionManager

    private lateinit var languageSessionManager: LanguageSessionManager

    private lateinit var binding: FragmentScanBinding

    private lateinit var viewModel: ScanViewModel

    private lateinit var codeScanner: CodeScanner

    private var isPermissionGranted = false

    private val RC_PERMISSION = 10

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("Entered ScanFragment !!!====<>>>>>")

        try {

            mSessionManager = SessionManager(act)

            languageSessionManager = LanguageSessionManager(act)

            viewModel = ViewModelProvider(act)[ScanViewModel::class.java]

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

            binding = FragmentScanBinding.inflate(inflater, container, false)

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

        act.tabNumber = 1
        act.setupDefaultSettings()
        act.setTextFonts(binding.root)

        codeScanner = CodeScanner(act, binding.scannerView)

        act.binding.appBarHome.appBarNormal.title.text = act.getString(R.string.Scan)

        setData()
        observeViewModel()
        onClicks()

        Log.e("11111", " checkPermissions() ==>" + checkPermissions())
        if (checkPermissions()) {
            codeScanner.startPreview()
        }

        setupScanner()

    }


    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    private fun onClicks() {

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.tvNotPhishing.setOnClickListener { viewModel.result.value = NotPhishing }
        binding.tvMaybePhishing.setOnClickListener { viewModel.result.value = MaybePhishing }
        binding.tvPhishing.setOnClickListener { viewModel.result.value = Phishing }
    }


    private fun setData() {

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            act.runOnUiThread {
                Toast.makeText(act, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                Log.e("11111", " scanned URL ===> ${it.text}")

                if (it.text.isNotEmpty())
                    viewModel.rateURL(it.text)
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            act.runOnUiThread {
                Toast.makeText(
                    act, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }


    private fun observeViewModel() {

        viewModel.result.observe(viewLifecycleOwner) { result ->

            Log.d("11111", "result ->$result")

            act.showPhishDialog(result, viewModel.errorList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading != null) {
                act.binding.appBarHome.mloading.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    DisableLayout(act.binding.root as ViewGroup)
                    DisableLayout(binding.root as ViewGroup)
                } else {
                    EnableLayout(act.binding.root as ViewGroup)
                    EnableLayout(binding.root as ViewGroup)
                }
            }
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                Snackbar.make(binding.root, viewModel.errorText, Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun setupScanner() {
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
    }


    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (ContextCompat.checkSelfPermission(
                    act,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = false
                ActivityCompat.requestPermissions(
                    act,
                    arrayOf(Manifest.permission.CAMERA),
                    RC_PERMISSION
                )
                false
            } else {
                isPermissionGranted = true
                true
            }
        } else {
            isPermissionGranted = true
            return true
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
                codeScanner.startPreview()
            } else {
                isPermissionGranted = false
            }
        }
    }

}