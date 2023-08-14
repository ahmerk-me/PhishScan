package com.phishscan.app.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.phishscan.app.R
import com.phishscan.app.adapters.ErrorListAdapter
import com.phishscan.app.classes.Arabic_Bold_Font
import com.phishscan.app.classes.Arabic_Regular_Font
import com.phishscan.app.classes.English_Bold_Font
import com.phishscan.app.classes.English_Regular_Font
import com.phishscan.app.classes.LanguageSessionManager
import com.phishscan.app.classes.LocaleHelper
import com.phishscan.app.classes.MaybePhishing
import com.phishscan.app.classes.Navigator
import com.phishscan.app.classes.NotPhishing
import com.phishscan.app.classes.Phishing
import com.phishscan.app.classes.SessionManager
import com.phishscan.app.classes.hideKeyboard
import com.phishscan.app.classes.isEnglish
import com.phishscan.app.classes.languageSessionManager
import com.phishscan.app.classes.sessionManager
import com.phishscan.app.databinding.ActivityMainBinding
import com.phishscan.app.view.fragment.ScanFragment
import com.phishscan.app.viewmodel.ScanViewModel

open class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var act: MainActivity

    companion object {

        lateinit var listener: BaseActivityListener
    }

    lateinit var context: Context

    lateinit var tf: Typeface

    lateinit var tfBold: Typeface

//    lateinit var mImageLoader: ImageLoader

    lateinit var viewModel: ScanViewModel

    var tabNumber = 0

//    lateinit var drawer: DrawerLayout

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    var isShowErrorList: Boolean = false

    fun setTextFonts(root: ViewGroup) {

        for (i in 0 until root.childCount) {

            val v = root.getChildAt(i)

            when (v) {

                is TextView -> {

                    v.typeface = tf

                }

                is Button -> {

                    v.typeface = tf

                }

                is EditText -> {

                    v.typeface = tf

                }

                is ViewGroup -> {

                    setTextFonts(v)

                }

            }

        }

    }


    fun setupDefaultSettings() {

        Log.d("setupDefaultSettings", "1")

        with(binding.appBarHome.appBarNormal) {

            setTabs()

            act.binding.appBarHome.bottomBar.visibility = VISIBLE

            act.binding.appBarHome.tvNoData.visibility = GONE

            listBtn.visibility = VISIBLE

            act.binding.appBarHome.appbar.visibility = VISIBLE

            act.binding.appBarHome.relativeHomeBtn.visibility = GONE

            title.setTypeface(tf, Typeface.BOLD)

//            imgEditTop.setImageResource(R.drawable.profile_edit)

            imgEditTop.visibility = GONE

            tvSkipTopBar.visibility = GONE

            relativeNotificationTop.visibility = GONE

//            imgProfileLogo.visibility = GONE

            tvUsername.visibility = GONE

            title.visibility = VISIBLE

            back.visibility = if (sessionManager?.isLoggedin == true) GONE else View.INVISIBLE

//            if (act.supportFragmentManager.backStackEntryCount > 0) {
//
//                back.visibility = VISIBLE
//
//                back.setImageResource(R.drawable.back)
//
//            }

        }

    }


    fun hideBottomBar() {

        act.binding.appBarHome.bottomBar.visibility = View.GONE

        act.binding.appBarHome.relativeHomeBtn.visibility = View.GONE

    }


    fun hideTopBar() {

        act.binding.appBarHome.appbar.visibility = View.GONE

    }


    private fun setTabs() {

        binding.appBarHome.relativeQr.setBackgroundColor(
            ContextCompat.getColor(
                act,
                R.color.colorPrimary
            )
        )
        binding.appBarHome.relativeAbout.setBackgroundColor(
            ContextCompat.getColor(
                act,
                R.color.colorPrimary
            )
        )

        when (tabNumber) {
            1 -> {
                binding.appBarHome.relativeQr.setBackgroundColor(
                    ContextCompat.getColor(
                        act,
                        R.color.bgColor
                    )
                )
                binding.appBarHome.relativeAbout.setBackgroundColor(
                    ContextCompat.getColor(
                        act,
                        R.color.colorPrimary
                    )
                )
            }

            2 -> {
                binding.appBarHome.relativeQr.setBackgroundColor(
                    ContextCompat.getColor(
                        act,
                        R.color.colorPrimary
                    )
                )
                binding.appBarHome.relativeAbout.setBackgroundColor(
                    ContextCompat.getColor(
                        act,
                        R.color.bgColor
                    )
                )
            }
        }

    }


    override fun attachBaseContext(newBase: Context?) {

        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Sets up permissions request launcher.
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            refreshUI()
                if (it) {
//                showDummyNotification()
                } else {
                    Snackbar.make(
                        findViewById<View>(android.R.id.content).rootView,
                        "Please grant Notification permission from App Settings",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        act = this@MainActivity

        context = this@MainActivity

        binding = ActivityMainBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[ScanViewModel::class.java]

        initImageLoader(this)

        setContentView(binding.root)

        languageSessionManager = LanguageSessionManager(this)

        sessionManager = SessionManager(this)

        val newbuilder = StrictMode.VmPolicy.Builder()

        StrictMode.setVmPolicy(newbuilder.build())

//        mImageLoader = ImageLoader.getInstance()

        if (Build.VERSION.SDK_INT > 9) {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()

            StrictMode.setThreadPolicy(policy)

        }

        if (LanguageSessionManager.language != null && LanguageSessionManager.language.isNotEmpty()) {

            updateViews(LanguageSessionManager.language.toString(), false)

        } else updateViews("en", false)

        with(binding.appBarHome) {

            setSupportActionBar(appBarNormal.toolbar)

            setTextFonts(binding.relativeSideMenu)

            setTextFonts(appBarNormal.linearTopBar)

            setTextFonts(bottomBar)

            setTextFonts(bottomBarDoctor)

            setTextFonts(relativeHomeBtn)

        }

//        tabNumber = 1
//        setTabs()

        with(binding) {

//            drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout

//            val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
//                act, drawer, appBarHome.appBarNormal.toolbar, R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close
//            ) {
//                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//                    super.onDrawerSlide(drawerView, slideOffset)
//
//                    val moveFactor = navView.width * slideOffset
//
//                    appBarHome.contentHome.translationX = if (isEnglish) -moveFactor else moveFactor
//
//                    appBarHome.appBarNormal.toolbar.translationX =
//                        if (isEnglish) -moveFactor else moveFactor
//
//                    Log.d("onDrawerSlide", "onDrawerSlide")
//
//                }
//
//                override fun onDrawerOpened(drawerView: View) {
//                    super.onDrawerOpened(drawerView)
//
//                    Log.d("onDrawerOpened", "onDrawerOpened")
//
////                    relativeSideMenu?.let { setTextFonts(it) }
//
//                }
//
//            }
//
//            drawer.addDrawerListener(toggle)

//            toggle.syncState()

            appBarHome.appBarNormal.toolbar.navigationIcon = null

        }

        onClicks()

        if (intent.hasExtra("fragType")) {

            val fragType = intent.getIntExtra("fragType", 0)

            Log.e("22222 ", "MainAct fragtype ===>>> " + fragType)
            if (sessionManager!!.isFirstTime) {

                sessionManager!!.isFirstTime = false

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
//                    showDummyNotification()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                Navigator.loadFragment(
                    this, ScanFragment(act),
                    R.id.content_home, false, "home"
                )

            } else {
                Navigator.loadFragment(
                    this, ScanFragment(act),
                    R.id.content_home, false, "home"
                )
            }

        } else {

            Navigator.loadFragment(
                this, ScanFragment(act),
                R.id.content_home, false, "home"
            )

        }

    }


    private fun listBtnClick() {

//        if (drawer.isDrawerOpen(
//                if (LanguageSessionManager.language.equals("en", ignoreCase = true))
//                    Gravity.RIGHT else Gravity.LEFT
//            )
//        ) {
//            drawer.closeDrawers()
//        } else {
//            setupSideMenu()
//            drawer.openDrawer(
//                if (LanguageSessionManager.language.equals("en", ignoreCase = true))
//                    Gravity.RIGHT else Gravity.LEFT
//            )
//        }

    }


    private fun backBtnClick() {
        onBackPressedDispatcher.onBackPressed()
        hideKeyboard(act)
    }


    private fun updateViews(languageCode: String, isReloadApp: Boolean) {

        if (languageCode == "en" && !isReloadApp)
            setEnglishView()
        else if (languageCode == "en" && isReloadApp)
            setArabicView()
        else if (languageCode == "ar" && !isReloadApp)
            setArabicView()
        else if (languageCode == "ar" && isReloadApp)
            setEnglishView()

        if (isReloadApp) {
            listener.onRestartApp()
        }

    }


    private fun setEnglishView() {
        LocaleHelper.setLocale(this, "en")
        isEnglish = true
        tfBold = Typeface.createFromAsset(context.assets, English_Bold_Font)
        tf = Typeface.createFromAsset(context.assets, English_Regular_Font)
        LanguageSessionManager.language = "en"
    }


    private fun setArabicView() {
        LocaleHelper.setLocale(this, "ar")
        isEnglish = false
        tfBold = Typeface.createFromAsset(this.assets, Arabic_Bold_Font)
        tf = Typeface.createFromAsset(this.assets, Arabic_Regular_Font)
        LanguageSessionManager.language = "ar"
    }


    private fun setupSideMenu() {

        with(binding) {

//            if (!isCustomerApp) {
//                tvWishlist.visibility = GONE
//                tvSearch.visibility = GONE
//            }

            tvEditProfile.text =
                if (sessionManager!!.isLoggedin) act.getString(R.string.MyProfileLabel) else
                    act.getString(R.string.LoginLabel)

            tvLogout.visibility =
                if (sessionManager!!.isLoggedin) VISIBLE else GONE

            tvWallet.visibility = if (sessionManager!!.isLoggedin) VISIBLE else GONE
            tvWishlist.visibility = if (sessionManager!!.isLoggedin) VISIBLE else GONE

            if (LanguageSessionManager.language.equals("en", ignoreCase = true)) {

                tvLanguage.text = act.getString(R.string.ArabicLabel)

                tvLanguage.typeface = Typeface.createFromAsset(
                    this@MainActivity.assets,
                    Arabic_Regular_Font
                )

            } else {

                tvLanguage.text = act.getString(R.string.EnglishLabel)

                tvLanguage.typeface = Typeface.createFromAsset(
                    this@MainActivity.assets,
                    English_Regular_Font
                )

            }

        }

    }


    private fun onClicks() {

        Log.d("onClicks", "1")

        //SideMenu Clicks
        with(binding) {

            tvEditProfile.setOnClickListener {

//                drawer.closeDrawers()

                if (sessionManager!!.isLoggedin) {

//                    Navigator.loadFragment(
//                        act,
//                        if (isCustomerApp) MyProfileFragment(act) else MyProfileDoctorFragment(act),
//                        R.id.content_home, true, "home"
//                    )

                } else {

//                    Navigator.loadFragment(act, LoginFragment(act), R.id.content_home, true, "home")

                }

            }

            tvWishlist.setOnClickListener {

//                drawer.closeDrawers()

//                Navigator.loadFragment(
//                    act,
//                    DoctorsFragment(act, ComingFrom.Favourites),
//                    R.id.content_home,
//                    true,
//                    "home"
//                )

            }

            tvWallet.setOnClickListener {

//                drawer.closeDrawers()

//                Navigator.loadFragment(
//                    act,
//                    MyWalletFragment(act),
//                    R.id.content_home,
//                    true,
//                    "home"
//                )

            }

            tvSearch.setOnClickListener {

//                drawer.closeDrawers()

            }

            tvLogout.setOnClickListener { }

            tvAboutUs.setOnClickListener {

//                drawer.closeDrawers()

            }

            tvContactUs.setOnClickListener {
//                drawer.closeDrawers()

            }

            tvPrivacyPolicy.setOnClickListener {

//                drawer.closeDrawers()

            }

            tvFaq.setOnClickListener {

//                drawer.closeDrawers()

            }

            tvTerms.setOnClickListener {

//                drawer.closeDrawers()
//
//                Navigator.loadFragment(
//                    act, AboutUsFragment(act, ComingFrom.TermsCondition), R.id.content_home,
//                    true, "home"
//                )

            }

            tvLanguage.setOnClickListener {
//                drawer.closeDrawers()
                updateViews(LanguageSessionManager.language.toString(), true)
            }

        }

        //TopBar Clicks
        with(binding.appBarHome.appBarNormal) {

            back.setOnClickListener {

                Log.d("onClicks", "2")

                backBtnClick()

            }

            listBtn.setOnClickListener { listBtnClick() }

//            imgProfileLogo.setOnClickListener {
//
//            }

            title.setOnClickListener { }

            tvSkipTopBar.setOnClickListener { }

            relativeNotificationTop.setOnClickListener {

            }

        }

        //customer bottom bar clicks
        with(binding.appBarHome) {

            relativeQr.setOnClickListener {

                tabNumber = 1
                setTabs()
            }

            relativeAbout.setOnClickListener {

                tabNumber = 2
                setTabs()
            }
        }

        binding.appBarHome.homeBtn.setOnClickListener {


        }

    }


    private fun initImageLoader(context: Context?) {

//        val config = ImageLoaderConfiguration.Builder(context)
//            .threadPriority(Thread.NORM_PRIORITY - 3)
//            .denyCacheImageMultipleSizesInMemory()
//            .tasksProcessingOrder(QueueProcessingType.FIFO)
//            .build()
//
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config)

    }


    fun onNotificationClicked(type: Int, id: Int) {

        when (type) {

        }

    }


    fun setBaseActivityListener(listener: BaseActivityListener) {
        MainActivity.listener = listener
    }


    interface BaseActivityListener {
        fun onRestartApp()
    }


    fun showPhishDialog(dialogType: Int, errorList: ArrayList<String>) {

        with(binding.layoutDialog) {

            linearDialogParent.visibility = VISIBLE

            tvPhishScore.text =
                act.getString(R.string.PhishScore).replace("aaa", viewModel.phishScore.toString())

            when (dialogType) {

                NotPhishing -> {
                    ivStatusImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            act,
                            R.drawable.not_phishing
                        )
                    )
                    tvResult.text = act.getString(R.string.NotPhishingText)
                }

                MaybePhishing -> {
                    ivStatusImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            act,
                            R.drawable.maybe_phishing
                        )
                    )
                    tvResult.text = act.getString(R.string.MaybePhishingText)
                }

                Phishing -> {
                    ivStatusImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            act,
                            R.drawable.phishing
                        )
                    )
                    tvResult.text = act.getString(R.string.PhishingText)
                }
            }

            tvVisit.setOnClickListener(View.OnClickListener {
                linearDialogParent.visibility = GONE
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.expandedUrl))
                startActivity(intent)
            })

            tvAbort.setOnClickListener(View.OnClickListener {
//                Snackbar.make(tvVisit, "Aborting...", Snackbar.LENGTH_SHORT).show()
                linearDialogParent.visibility = GONE
            })

            linearDialogParent.setOnClickListener { }

            var mAdapter = ErrorListAdapter(act, ArrayList())
            binding.layoutDialog.linearErrorList.visibility = GONE

            if (errorList.size > 0) {
                binding.layoutDialog.linearErrorList.visibility = VISIBLE
                setupRecycler(mAdapter)
                mAdapter.updateList(errorList)
            }

            binding.layoutDialog.tvScoreDetails.setOnClickListener {
                isShowErrorList = !isShowErrorList
                setErrorRecyclerVisibility()
            }
        }
    }

    private fun setErrorRecyclerVisibility() {

        binding.layoutDialog.rvRecycler.visibility = if (isShowErrorList) VISIBLE else GONE

    }

    private fun setupRecycler(madapter: ErrorListAdapter?) {

        with(binding.layoutDialog) {

            var mlayoutManagerLab = LinearLayoutManager(act)
            mlayoutManagerLab.orientation = RecyclerView.VERTICAL
            rvRecycler.layoutManager = mlayoutManagerLab
            rvRecycler.itemAnimator = DefaultItemAnimator()
            rvRecycler.adapter = madapter
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

//        if(ChatListFragment.fragment?.isVisible == true){
//            Log.d("ChatListFragment","isVisible")
//            ChatListFragment.fragment?.onEncCallClicked()
//            backBtnClick()
//        }
//        else{
        Log.d("backBtnClick", "isVisible")
        backBtnClick()
//        }

    }

}