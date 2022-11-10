package com.pi.projectinclusion.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.pi.projectinclusion.*
import com.pi.projectinclusion.`interface`.ButtonAction
import com.pi.projectinclusion.adapter.MenuPagerAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.BottomMenuListener
import com.pi.projectinclusion.auth.MenuAdapterUpdate
import com.pi.projectinclusion.auth.WebViewBackPressed
import com.pi.projectinclusion.databinding.ActivityDashBoardBinding
import com.pi.projectinclusion.fragment.BookWebinarSlotFragment
import com.pi.projectinclusion.fragment.ViewPagerFragment
import com.pi.projectinclusion.model.BottomMenuModel
import com.pi.projectinclusion.model.MenuModel
import com.pi.projectinclusion.repository.AuroRepository
import com.pi.projectinclusion.repository.appBridge
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

private const val TAG = "DashBoardActivity"

class DashBoardActivity : AppCompatActivity(), BottomMenuListener, MenuAdapterUpdate,ButtonAction {
    private var WEBINAR_FRAGMENT_CODE: Int = 100
    private var BOOK_WEBINAR_FRAGMENT_CODE: Int = 101
    private lateinit var mViewPager: ViewPager2
    private lateinit var mTabLayout: TabLayout
    private lateinit var tabSelectedListener: TabLayout.OnTabSelectedListener
    private var mAdapter: MenuPagerAdapter? = null
    private var bottomMenuList = ArrayList<MenuModel>()
    private var hmLangValue = HashMap<String, String>()
    private lateinit var mBinding: ActivityDashBoardBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var mContext: Context
    private var backCount: Int = 0
    private var isCourseId: Boolean = false
    private lateinit var webViewBackPress: WebViewBackPressed

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [setLanguage]
     * [setAdapterToViewPager]
     * [onMenuCallStart]
     * [onMenuCallSuccess]
     * [onMenuCallFail]
     * [onBackPressed]
     * [onResume]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        AuroRepository().setStatusBarColor(this)
        initViewModel()
        mViewPager = findViewById(R.id.viewpager)
        mTabLayout = findViewById(R.id.tablayout)
        setLanguage()
        setAdapterToViewPager()
        WebView.setWebContentsDebuggingEnabled(true)

        /* var menuModel5 = MenuModel(R.drawable.order_icon,"Orders")
    var menuModel6 = MenuModel(R.drawable.icon_book,"Books")
     bottom_menuList.add(menuModel5)
    bottom_menuList.add(menuModel6)*/
    }

    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */
    private fun setLanguage() {
        bottomMenuList.clear()
        if (getData(this, getString(R.string.key_lang_id)) == "1") {
            setAppLocale(this, "en")

        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))
        val menuModel = MenuModel(R.drawable.ic_icon_home, hmLangValue["item_home"])
        val menuModel1 = MenuModel(R.drawable.ic_article_icon, hmLangValue["item_courses"])
//        val menuModel2 = MenuModel(R.drawable.ic_screening, hmLangValue["screening"])
        val menuModel3 = MenuModel(R.drawable.ic_webinar, hmLangValue["webinars_txt"])
        //val menuModel3 = MenuModel(R.drawable.ic_webinar, getString(R.string.webinars_txt))
        val menuModel4 = MenuModel(R.drawable.ic_certificate, hmLangValue["certificate"])
        val menuModel5 = MenuModel(R.drawable.profile_icon, hmLangValue["txt_refer"])
        val menuModel6 = MenuModel(R.drawable.ic_settings_icon, hmLangValue["item_settings"])
        val menuModel7 = MenuModel(R.drawable.logout_menu_icon, hmLangValue["logout"])
        bottomMenuList.add(menuModel)
        bottomMenuList.add(menuModel1)
//        bottomMenuList.add(menuModel2)
         bottomMenuList.add(menuModel3)
        bottomMenuList.add(menuModel4)
        bottomMenuList.add(menuModel5)
        bottomMenuList.add(menuModel6)
        //bottomMenuList.add(menuModel7)
        mAdapter?.notifyItemRangeChanged(0, bottomMenuList.size)
    }

    private fun setAdapterToViewPager() {
        mAdapter = MenuPagerAdapter(this@DashBoardActivity, bottomMenuList, mTabLayout,this@DashBoardActivity)
        mViewPager.adapter = mAdapter
        mViewPager.isUserInputEnabled = false
        TabLayoutMediator(
            mTabLayout, mViewPager
        ) { tab, position ->
            tab.text = bottomMenuList[position].title
            tab.setIcon(bottomMenuList[position].icon)
        }.attach()
        mViewPager.currentItem = 0
        tabSelectedListener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.icon!!.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Color.WHITE,
                        BlendModeCompat.SRC_IN
                    )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.icon!!.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        Color.GRAY,
                        BlendModeCompat.SRC_IN
                    )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        }
        mTabLayout.addOnTabSelectedListener(tabSelectedListener)
    }

    class MyBrowser : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view!!.loadUrl(url!!)
            return true
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            view!!.loadUrl(request?.url.toString())
        }
    }

    fun savelmsData(json: String, activity: Activity) {
        try {
            val db = appBridge(this@DashBoardActivity, null)

            //val db = appBridge(activity as DashBoardActivity, null)


            Log.d(TAG, "savelmsData: json: $json")
            // calling method to add
            // name to our database
            Log.d(TAG, "savelmsData: courseIdResult")
            db.saveLmsData(json, activity as DashBoardActivity)

            // Toast to message on the screen
            //this("Sucess database save")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "savelmsData: e: " + e.message)
        }

    }

    fun getLmsData(
        courseId: String, activity: Activity
    ): String {
        Log.d(TAG, "getLmsData: courseId: " + courseId)
        var result = " "
        try {
            val db = appBridge(activity as DashBoardActivity, null)
            // calling method to add
            // name to our database
            result = db.getLMSData(courseId)
//            val emptyAppBridge = com.pi.projectinclusion.model.appBridge(0, "", "", "")
//            val gson = Gson()
//            val jsonString = gson.toJson(emptyAppBridge)
//            var request = JSONObject()
//            try {
//                request = JSONObject(jsonString)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }

//
//            if (result == request.toString()) {
//                saveData(
//                    activity as DashBoardActivity,
//                    activity.getString(R.string.key_is_courseId_exists),
//                    "false"
//                )
//            } else {
//                saveData(
//                    activity as DashBoardActivity,
//                    activity.getString(R.string.key_is_courseId_exists),
//                    "true"
//                )
//            }
//            Log.d(
//                TAG, "getLmsData: getData" + getData(
//                    activity as DashBoardActivity,
//                    activity.getString(R.string.key_is_courseId_exists)
//                )
//            )

            Log.d(TAG, "getLmsData: result 1: " + result)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "savelmsData: e: " + e.message)
        }
        return result

    }

    fun getLmsData(activity: Activity): String {
        var result: ArrayList<com.pi.projectinclusion.model.appBridge>? = null

        val finalResult = StringBuilder()
        try {
            val db = appBridge(activity as DashBoardActivity, null)


            // calling method to add
            // name to our database
            result = db.getLMSData()

            for ((count, i) in result.withIndex()) {
                val gson = Gson()
                val jsonString = gson.toJson(i)
                var request = JSONObject()
                try {
                    request = JSONObject(jsonString)
                } catch (e: JSONException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                finalResult.append(request.toString())

                if ((count + 1) != result.size)
                    finalResult.append("%%")
            }
            Log.d(TAG, "getLmsData: finalResult: " + finalResult.toString())
            // Toast to message on the screen
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "savelmsData: e: " + e.message)
        }
        return finalResult.toString()
    }


    private fun initViewModel() {

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mBottomMenuListener = this
    }

    /**
     * --------------------------------------- On Item Clicks & api event listeners-------------------
     */
    override fun onMenuCallStart() {

    }

    override fun onMenuCallSuccess(menu_response: BottomMenuModel) {
    }

    override fun onMenuCallFail() {

    }

    /**
     * ---------------------------------------LIFECYCLE CALLS---------------------------------------
     */
    override fun onResume() {
        super.onResume()
        if (mAdapter != null)
            setLanguage()

    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment == ViewPagerFragment()) {
            webViewBackPress = fragment.context as WebViewBackPressed
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed: backCOunt: " + backCount)
        val fragmentList = supportFragmentManager.fragments

        if (bottomMenuList.get(mTabLayout.selectedTabPosition).title == hmLangValue["item_courses"]) {
            Log.d(TAG, "onBackPressed: ")
            //webViewBackPress.onWebViewBackPressed()
            //TODO: Perform your logic to pass back press here
            for (fragment in fragmentList) {
                if (fragment is WebViewBackPressed) {
                    (fragment as WebViewBackPressed).onWebViewBackPressed()
                }
            }
        } else if (bottomMenuList.get(mTabLayout.selectedTabPosition).title == getString(R.string.webinars_txt)) {
            val myFragment: BookWebinarSlotFragment? =
                supportFragmentManager.findFragmentByTag("bookWebinarSlotFragment") as BookWebinarSlotFragment?
            if (myFragment != null && myFragment.isVisible) {
                super.onBackPressed()

            } else {
                val homeTab = mTabLayout.getTabAt(0)
                homeTab?.select()
            }
        } else {
            if (mTabLayout.selectedTabPosition != 0) {
                val homeTab = mTabLayout.getTabAt(0)
                homeTab?.select()
            } else {
                super.onBackPressed()
            }
        }
    }


    override fun onDestroy() {
        mTabLayout.removeOnTabSelectedListener(tabSelectedListener)
        super.onDestroy()
    }

    override fun onMenuAdapterUpdate() {
        setLanguage()
    }

    /*to handle Log out menu*/
    override fun menuLogPressed(status: Boolean?) {
        Log.e("TestCk","Pressed****---------------")
        if(status == false){
            Log.e("menuLogPressed",status.toString())
            clearData(this, "user_token")
            clearData(this, mContext.getString(R.string.key_profile))
            clearData(this, mContext.getString(R.string.key_phone_number))
            clearData(this, "user_id")
            val i = Intent(
                this,
                LanguageActivity::class.java
            )
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            overridePendingTransition(
                R.anim.slide_in_left_first,
                R.anim.slide_out_left_first
            )
            finish()
        }

    }
}