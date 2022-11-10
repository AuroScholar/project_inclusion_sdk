package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.internetError
import com.pi.projectinclusion.adapter.LanguageNewAdapter
import com.pi.projectinclusion.adapter.LanguageWebinarAdapter
import com.pi.projectinclusion.adapter.WebinarListAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.FragmentToFragmentBackPress
import com.pi.projectinclusion.auth.MyAuthListener
import com.pi.projectinclusion.auth.WebinarByLangListListener
import com.pi.projectinclusion.databinding.FragmentWebinarsBinding
import com.pi.projectinclusion.model.LanguageModel
import com.pi.projectinclusion.model.WebinarModel
import com.pi.projectinclusion.model.WebinarResponse
import com.pi.projectinclusion.repository.AuroRepository
import com.savvi.rangedatepicker.CalendarPickerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


private const val TAG = "WebinarFragment"

class WebinarFragment : Fragment(), WebinarListAdapter.OnJoinClickListener,
    LanguageWebinarAdapter.OnNewLangClickListener, MyAuthListener, WebinarByLangListListener,
    FragmentToFragmentBackPress {
    private lateinit var mBinding: FragmentWebinarsBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var alsWebinarList: ArrayList<WebinarModel>
    private lateinit var alsLangList: ArrayList<LanguageModel>
    private lateinit var datePickerDialog: Dialog
    private lateinit var v: View
    private var mLangid: Int = 1

    /**
     * NAVIGATION TO ALL METHODS
     * [initRecyclerView]
     * [initDatePickerCalender]
     * [createDateDialog]
     * [setLanguage]
     * [initViewModel]
     * [onJoinClick]
     * [onItemClick]
     * [onResume]
     * [onFragmentBacked]
     */
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        alsWebinarList = ArrayList(10)
        mContext = requireActivity()

        initViewModel(inflater, container)
        setLanguage()
        initDatePickerCalender()
        datePickerDialog = createDateDialog()

        return v
    }

    /**
     * -------------------------------------------------------CUSTOM METHODS ---------------------------------------------------------------
     * [initLangRecyclerView]
     * [initRecyclerView]
     * [initDatePickerCalender]
     * [createDateDialog]
     * [setLanguage]
     * [initViewModel]
     */

    private fun initLangRecyclerView() {
        mBinding.pbLanguage.visibility = View.GONE
        mBinding.rvLangW.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvLangW.adapter = LanguageWebinarAdapter(mContext, alsLangList, mLangid, this)
    }

    private fun initWebinarRecyclerView() {
        /* alsLangList = ArrayList()
         alsLangList.add(LanguageModel("English", 1, "ok", "200", "English"))
         alsLangList.add(LanguageModel("Hindi", 1, "ok", "200", "हिन्दी"))*/
        /* alsWebinarList.add(WebinarModel("", "", "", "", 1, 1, "", "", 1, 1, "", "", ""))
         alsWebinarList.add(WebinarModel("", "", "", "", 1, 1, "", "", 1, 1, "", "", ""))
 */

        mBinding.pbLanguage.visibility = View.GONE
        mBinding.rvWebinars.layoutManager =
            LinearLayoutManager(mContext)
        mBinding.rvWebinars.adapter = WebinarListAdapter(mContext, alsWebinarList, this)
    }

    /**
     * init date picker dialog with current date and max date as current date
     */
    private fun initDatePickerCalender() {

        mBinding.acbDate.setOnClickListener {
            datePickerDialog.show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createDateDialog(): Dialog {
        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_custom_calendar)

        val c = Calendar.getInstance()
        val today = Calendar.getInstance().time
        //val today = c.time
        c.add(Calendar.YEAR, 1)
        val maxDate = c.time

        val alsDeactivated: ArrayList<Int> = ArrayList(2)
        alsDeactivated.add(2)

        val alsHighLight: ArrayList<Date?> = ArrayList(2)
        try {

            val dateformat = SimpleDateFormat("dd-MM-yyyy")
            val strdate = "10-10-2022"
            val strdate2 = "22-10-2022"
            val newdate = dateformat.parse(strdate)
            val newdate2 = dateformat.parse(strdate2)
            alsHighLight.add(newdate)
            alsHighLight.add(newdate2)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val cvCalendar =
            dialog.findViewById<CalendarPickerView>(R.id.cpvCalendar)
        cvCalendar.init(today, maxDate) //
            .inMode(CalendarPickerView.SelectionMode.SINGLE)
//            .withSelectedDate(today)
//            .withDeactivateDates(alsDeactivated)
//            .withHighlightedDates(alsHighLight)


        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    private fun setLanguage() {
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }

        hmLangValue = getHashMap(mContext, getString(R.string.key_lang_list))
    }

    private fun initViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_webinars, container, false)
        v = mBinding.root
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel
        mViewModel.mAuthListener = this
        mViewModel.mWebinarByLangListListener = this

    }

    /**
     * ------------------------------------------------------IMPLEMENTATIONS AND API INTERFACES--------------------------------------------------------------
     * [onJoinClick]
     * [onItemClick]
     * [onNetworkCallStarted]
     * [onCoroutineApiSuccess]
     * [onAuthApiFailure]
     * [onWebinarLangStarted]
     * [onWebinarLangListSuccess]
     * [onWebinarLangListFailure]
     *
     */
    override fun onJoinClick(position: Int) {
        val webinar = alsWebinarList[position]
        //startMeeting(webinar.zoomLink,"Sahil Jain")
        // mBinding.flBookSlot.visibility = View.VISIBLE
        val bookWebinarSlotFragment = BookWebinarSlotFragment()
        val bundle = Bundle()
        bundle.putSerializable(
            mContext.getString(R.string.key_user_token),
            alsWebinarList[position]
        )
        bookWebinarSlotFragment.arguments = bundle
        fragmentManager?.beginTransaction()!!
            .replace(
                ((view as ViewGroup).parent as View).id,
                bookWebinarSlotFragment,
                "bookWebinarSlotFragment"
            )
            .addToBackStack(null).commit()
    }

    override fun onItemClick(pos: Int, tvSelected: TextView, tvUnselected: TextView) {
        mLangid = pos + 1

        mBinding.rvLangW.adapter?.notifyItemRangeChanged(0, alsLangList.size, pos)
        mViewModel.getWebinarListByLang(
            mLangid,
            getData(mContext, mContext.getString(R.string.key_user_token)), mContext
        )
    }

    override fun onWebinarLangStarted() {
        Log.d(TAG, "onWebinarLangStarted: ")
        mBinding.pbWebinar.visibility = View.VISIBLE
    }

    override fun onWebinarLangListSuccess(webinarResponse: WebinarResponse) {
        Log.d(TAG, "onWebinarLangListSuccess: webinarResponse: " + webinarResponse)
        alsWebinarList = ArrayList()
        alsWebinarList.clear()
        mBinding.pbWebinar.visibility = View.GONE
        if (webinarResponse.message == "Success" && webinarResponse.status == 200) {
            if (webinarResponse.response?.size!! > 0) {
                mBinding.clNoWebinar.visibility = View.GONE
                alsWebinarList = webinarResponse.response!!
            } else {
                mBinding.clNoWebinar.visibility = View.VISIBLE
            }
        } else {
            mBinding.clNoWebinar.visibility = View.VISIBLE
        }
        initWebinarRecyclerView()
        Log.d(TAG, "onWebinarLangListSuccess: alsWebinarList: " + alsWebinarList)
    }

    override fun onWebinarLangListFailure() {
        if (isNetwork(mContext)) {
            mViewModel.getWebinarListByLang(
                mLangid,
                getData(mContext, mContext.getString(R.string.key_user_token)),
                mContext
            )
        } else {
            mContext.toast(internetError)
            mBinding.pbWebinar.visibility = View.GONE
        }
    }

    override fun onNetworkCallStarted() {
        Log.d(TAG, "onNetworkCallStarted: ")
        mBinding.pbLanguage.visibility = View.VISIBLE
    }

    override fun onCoroutineApiSuccess(lang_response: List<LanguageModel>) {
        alsLangList = ArrayList()
        alsLangList.clear()
        // get body from api response from IO Coroutine to main thread
        mBinding.pbLanguage.visibility = View.GONE
        for (i in lang_response.indices) { // fill ling with language from api
            val name = lang_response[i].name.toString()
            val translatedName = lang_response[i].translatedName
            val langId = lang_response[i].id
            alsLangList.add(LanguageModel(name, langId, "", "", translatedName))
        }
        //Init recycler view
        AuroRepository().runOnMainThread {
            initLangRecyclerView()
            mViewModel.getWebinarListByLang(
                mLangid,
                getData(mContext, mContext.getString(R.string.key_user_token)), mContext
            )

        }
    }

    override fun onAuthApiFailure() {
        if (isNetwork(mContext)) {
            mViewModel.getCoroutineLanguageData(mContext)
        } else {
            mContext.toast(internetError)
            mBinding.pbLanguage.visibility = View.GONE
        }
    }

    /**
     * -------------------------------------------------LIFECYCLE AND OVERRIDE FUNCTIONS---------------------------------------------------------------------
     * [onResume]
     * [onFragmentBacked]
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        setLanguage()
        mViewModel.getCoroutineLanguageData(mContext) // calling get languages api

    }

    override fun onFragmentBacked() {
        mBinding.flBookSlot.visibility = View.GONE
    }


    /**
     * __________________________________________________TO BE USED LATER CODE COMMENTED --------------------------------------------------------------------
     */


    /*  private fun initializeZoomSDK() {
         val sdk = ZoomSDK.getInstance()
         val params = ZoomSDKInitParams()
         params.appKey = "jf7CNmz1scHkYhPrmLoAGlkTEBBVfPXCV8Lu"
         params.appSecret = "NyCVnsUdM3rNuK3JQpmzHd7t4lExJbhdGktg"
         params.domain = "zoom.us"
         params.enableLog = false


         val zoomSDKInitializeListener = object : ZoomSDKInitializeListener {
             override fun onZoomSDKInitializeResult(p0: Int, p1: Int) {
                 Log.d(TAG, "onZoomSDKInitializeResult: ")
             }

             override fun onZoomAuthIdentityExpired() {
             }

         }
         sdk.initialize(mContext, zoomSDKInitializeListener, params)

     }

     private fun startMeeting(meetingLink: String, name: String) {
         val meetingService = ZoomSDK.getInstance().meetingService
         Uri.parse(meetingLink)
         *//* val joinMeetingOptions = JoinMeetingOptions()
         val params = JoinMeetingParams()

         params.displayName = name*//*
        meetingService.handZoomWebUrl(meetingLink)

        //  meetingService.joinMeetingWithParams(mContext, params, joinMeetingOptions)
    }*/
}