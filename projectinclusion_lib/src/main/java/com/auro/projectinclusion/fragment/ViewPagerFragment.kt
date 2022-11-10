package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.DashBoardActivity
import com.pi.projectinclusion.auth.WebViewBackPressed
import com.pi.projectinclusion.repository.appBridge
import kotlinx.coroutines.*
import org.json.JSONObject


private const val TAG = "ViewPagerFragment"


class ViewPagerFragment : Fragment(), WebViewBackPressed {
    private lateinit var mWebview: WebView
    private lateinit var mContext: Context
    private lateinit var v: View
    var android: String = "android"
    /*added by rishabh*/
    var dwnType: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: " + "course")
        // Inflate the layout for this fragment
        v =
            LayoutInflater.from(context).inflate(R.layout.fragment_view_pager, container, false)

        mContext = requireActivity()

//        val jsonData = "[\n" +
//                "    {\n" +
//                "        \"courseID\": \"1\",\n" +
//                "        \"suspendDate\":\"20-2020-21\",\n" +
//                "        \"lessonState\":\"complete\",\n" +
//                "        \"sync\": 1\n" +
//                "    },\n" +
//                "    {\n" +
//                "        \"courseID\": \"2\",\n" +
//                "        \"suspendDate\":\"14-2020-21\",\n" +
//                "        \"lessonState\":\"incomplete\",\n" +
//                "        \"sync\": 0\n" +
//                "    }\n" +
//                "]"
//        savelmsData(jsonData)
//        Log.d(TAG, "onCreateView: course wise result: " + getLmsData("1"))
//        Log.d(TAG, "onCreateView: all results: " + getLmsData())
//        Log.d(
//            TAG,
//            "onCreateView: langId: " + getData(mContext, mContext.getString(R.string.key_lang_id))
//        )
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        mWebview = v.findViewById(R.id.webView)
        val text: TextView = v.findViewById(R.id.frag_text)
        val bundle = arguments
        text.text = bundle!!.getString("key")

        mWebview.clearFormData();
        mWebview.clearHistory();
        mWebview.clearCache(true);


        mWebview.webViewClient = DashBoardActivity.MyBrowser()
        mWebview.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebview.settings.loadsImagesAutomatically = true
        mWebview.settings.javaScriptEnabled = true
        mWebview.settings.allowFileAccess = true
        mWebview.settings.domStorageEnabled = true
        mWebview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebview.settings.javaScriptCanOpenWindowsAutomatically = true
        mWebview.settings.pluginState = WebSettings.PluginState.ON
        mWebview.settings.mediaPlaybackRequiresUserGesture = false
        Log.d(TAG, "onCreateView: create url: " + createUrl())
        mWebview.loadUrl(createUrl())
        WebView.setWebContentsDebuggingEnabled(true)
        mWebview.addJavascriptInterface(appBridge(requireActivity(), mWebview), "appBridge")
        // mWebview.addJavascriptInterface(appBridge(requireActivity()), "Android")
//        val json =
//            "{\"cid\":\"9\",\"lesson_status\":\"incomplete\",\"suspend_data\":\"module01:1@en,@menu*n,n,n,n,y|module08*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module01*n|module06*n|module07*y,n,n,n,n|module03*n,n,n,n,n|module05*n,n,n,n,n|module04*n,n,n,n,n|module25*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module02*n|module09*n,n,n,n,n|module10*n,n,n,n,n|module11*n,n,n,n,n|module12*n,n,n,n,n|module13*n,n,n,n,n|module14*n,n,n,n,n|module15*n,n,n,n,n|module16*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module17*n|module18*n,n,n,n,n|module19*n,n,n,n,n|module20*n,n,n,n,n|module21*n,n,n,n,n|module95*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module23*n|module24*n,n,n,n,n|module26*n,n,n,n,n|module27*n,n,n,n,n|module28*n,n,n,n,n|module110*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module30*n|module31*n,n,n,n,n|module32*n,n,n,n,n|module33*n,n,n,n,n|module34*n,n,n,n,n|module35*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module36*n|module37*n,n,n,n,n|module38*n,n,n,n,n|module39*n,n,n,n,n|module40*n,n,n,n,n|module41*n,n,n,n,n|module42*n,n,n,n,n|module89*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module44*n|module45*n,n,n,n,n|module46*n,n,n,n,n|module47*n,n,n,n,n|module48*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module49*n|module50*n,n,n,n,n|module51*n,n,n,n,n|module52*n,n,n,n,n|module43*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module54*n|module55*n,n,n,n,n|module56*n,n,n,n,n|module57*n,n,n,n,n|module58*n,n,n,n,n|module53*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module60*n|module61*n,n,n,n,n|module62*n,n,n,n,n|module63*n,n,n,n,n|module64*n,n,n,n,n|module65*n,n,n,n,n|module59*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module67*n|module68*n,n,n,n,n|module69*n,n,n,n,n|module70*n,n,n,n,n|module71*n,n,n,n,n|module66*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module73*n|module74*n,n,n,n,n|module75*n,n,n,n,n|module76*n,n,n,n,n|module72*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module78*n|module79*n,n,n,n,n|module80*n,n,n,n,n|module81*n,n,n,n,n|module82*n,n,n,n,n|module77*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module84*n|module85*n,n,n,n,n|module86*n,n,n,n,n|module87*n,n,n,n,n|module83*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module88*n|module90*n,n,n,n,n|module91*n,n,n,n,n|module92*n,n,n,n,n|module93*n,n,n,n,n|module94*n,n,n,n,n|module109*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module96*n|module97*n,n,n,n,n|module98*n,n,n,n,n|module99*n,n,n,n,n|module101*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module102*n|module103*n,n,n,n,n|module104*n,n,n,n,n|module105*n,n,n,n,n|module106*n,n,n,n,n|module107*n,n,n,n,n|module108*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n|module100*n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n,n@module08:0:-1:0:0:0:0:0:0:0|averageScore:0@@@@0\",\"sync\":0}"
//        DashBoardActivity().savelmsData(json, requireActivity())
        Log.d(TAG, "onCreateView: url:" + mWebview.url)

        dManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        LocalBroadcastManager.getInstance(
            requireActivity()).registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        return v
    }


//    class WebAppInterface {
//        val mContext: Context;
//
//        //Instantiate the interface and set the context
//        constructor(c: Context) {
//            mContext = c;
//        }
//
//
//        //Show a toast from the web page
//        @JavascriptInterface
//        fun showToast(toast: String) {
//            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
//            // Here you can Start WhatEver Activty launch///////
//        }
//    }


    private fun createUrl(): String {
        val id = getData(mContext, mContext.getString(R.string.key_user_id))
        val token = getData(mContext, mContext.getString(R.string.key_user_token))
        val hmLangValue: HashMap<String, String> =
            getHashMap(mContext, mContext.getString(R.string.key_lang_list))
        val lang = hmLangValue[mContext.getString(R.string.key_lang_name)]

        Log.d(TAG, "createUrl: lang: $lang")

        return "esleave.com/lms_ziiei_prj/?id=$id&pi_token=$token&lang=$lang"
    }


    //    override fun onWebViewBackPressed() {
//        if (mLMS.equals("1")) {
//            //Log.d("LMS","back");
//            //check if course in progress
//            myWebView.evaluateJavascript(
//                "back_currentcourse()",
//                android.webkit.ValueCallback<String> { s ->
//                    //Log.d("LMS-titlebar",s);
//                    if (s == "1") {
//                        //Log.d("LMS","closeiframe");
//                        myWebView.evaluateJavascript("close_mobile_iframe()", null)
//                    } else {
//                        //Log.d("LMS","finish");
//                        finish()
//                    }
//                })
//        } else {
//            //Log.d("LMS","normalback");
//            MainActivity.checkratingalert()
//            finish()
//        }
//    }
    companion object {
        open class appBridge(activity: Activity?, webView: WebView?) {

            var activity: Activity? = null
            var webView: WebView? = null

            init {
                this.activity = activity
                this.webView = webView
            }


            @JavascriptInterface
            fun getLmsData(courseId: String): String {
                val handler = Handler(Looper.getMainLooper())
                var result: String = " "
                runBlocking {
                    launch(context = Dispatchers.Main) {
                        result = DashBoardActivity().getLmsData(courseId, activity!!)
                    }
                }

                Log.d(
                    TAG, "getLmsData: result: " + result
                )

                return result


            }

            @JavascriptInterface
            open fun getLmsData(): String {
                val handler = Handler(Looper.getMainLooper())
                var result = " "
                runBlocking {
                    launch(context = Dispatchers.Main) {
                        result = DashBoardActivity().getLmsData(activity!!)
                    }
                }
                Log.d(
                    TAG, "getLmsData: result final: " + result
                )
                return result
            }

            @JavascriptInterface
            open fun saveLmsData(json: String) {
                Log.d(TAG, "saveLmsData: json: " + json)
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    DashBoardActivity().savelmsData(json, activity!!)
                }
            }

           /* @JavascriptInterface
            open fun onBackPressed() {
                Log.d(TAG, "onBackPressed: worked")
                webView?.loadUrl("javascript:app_HandleBack()")
            }*/

            @JavascriptInterface
            open fun setGameMode() {
                //setfullscreen();
                //activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            @JavascriptInterface
            open fun getPrefs(key: String?): String? {
                val settings: SharedPreferences =
                    activity!!.getSharedPreferences("com.ip.ziiei", 0)
                return settings.getString(key, "")
            }

            @JavascriptInterface
            open fun pagechanged(pnum: Int) {
            }
//
//           @get:JavascriptInterface
//           val videoLink: String
//               get() = activity.fl_url

            @JavascriptInterface
            open fun downloadCertificate(str: String?, name: String?) {
                // activity.dwn_certificate(str, name)
                Log.e("test",".............................................................00................")
                if (name != null) {
                    dwn_certificate(str, name)
                }
            }

        }
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(arg0: Context, arg1: Intent) {
            // TODO Auto-generated method stub
            val q = DownloadManager.Query()
            q.setFilterById(downloadID)
            val cursor: Cursor = dManager!!.query(q)
            if (cursor.moveToFirst()) {
                val message = ""
                val status: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    //message="Download successful";
                    //Log.e("download",message);
                    //0
//                    Toast.makeText(this@appBridge, "Certificate Downloaded", Toast.LENGTH_LONG)
//                        .show()
                } else if (status == DownloadManager.STATUS_FAILED) {
                    downloadComplete()
                    //message="Download failed";
                    //download_failed();
                    Log.e("download", "download failed")
                }
            }
            cursor.close()
        }
    }

     private fun download_failed() {
         val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
         alertDialogBuilder.setMessage("File not found on server.")
         alertDialogBuilder.setPositiveButton("Continue", null)
         alertDialogBuilder.setNegativeButton("No", null)
         val alertDialog: AlertDialog = alertDialogBuilder.create()
         alertDialog.show()
     }

    private fun downloadComplete() {
        // myWebView.loadUrl("javascript:cust_downloadcomplete()");
    }

     override fun onDestroy() {
         super.onDestroy()
         try {
             LocalBroadcastManager.getInstance(requireActivity())
                 .unregisterReceiver(downloadReceiver)
         //unregisterReceiver(downloadReceiver)
         } catch (e: Exception) {
         }
     }

    override fun onWebViewBackPressed() {
        Log.d(TAG, "onWebViewBackPressed: ")
        mWebview.loadUrl("javascript:app_HandleBack()")
    }

}