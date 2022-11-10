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
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.R
import com.pi.projectinclusion.adapter.LanguageWebinarAdapter
import com.pi.projectinclusion.adapter.WebinarListAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.FragmentToFragmentBackPress
import com.pi.projectinclusion.databinding.FragmentWebinars2Binding
import com.pi.projectinclusion.databinding.FragmentWebinarsBinding
import com.pi.projectinclusion.getData
import com.pi.projectinclusion.getHashMap
import com.pi.projectinclusion.model.LanguageModel
import com.pi.projectinclusion.model.WebinarModel
import com.pi.projectinclusion.setAppLocale
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.coroutines.delay
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "WebinarFragment2"

class WebinarFragment2 : Fragment() {
    private lateinit var mBinding: FragmentWebinars2Binding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var alsWebinarList: ArrayList<WebinarModel>
    private lateinit var alsLangList: ArrayList<LanguageModel>
    private lateinit var datePickerDialog: Dialog
    private lateinit var v: View
    private var mLangid: Int = 1
    private var isInTransition = false


    @RequiresApi(Build.VERSION_CODES.N)
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
        // initializeZoomSDK()
        //initRecyclerList()
        initDatePickerCalender()
        datePickerDialog = createDateDialog()
        return v
    }

    private fun initDatePickerCalender() {
        mBinding.tvBooked.setOnClickListener {
            changeTab(R.id.U_TO_B)
        }

        mBinding.tvUpcoming.setOnClickListener {
            changeTab(R.id.B_TO_U)
        }

    }

    private fun changeTab(transId: Int) {
        if (!isInTransition) {
            mBinding.mlTabWebinar.setTransition(transId)
            mBinding.mlTabWebinar.addTransitionListener(object :
                MotionLayout.TransitionListener {
                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {

                    isInTransition = true
                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                }

                override fun onTransitionCompleted(
                    motionLayout: MotionLayout?,
                    currentId: Int
                ) {
                    isInTransition = false
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {
                }

            })
            mBinding.mlTabWebinar.transitionToEnd()
        }
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        setLanguage()
    }

    private fun initViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_webinars2, container, false)
        v = mBinding.root
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel

    }

    private fun setLanguage() {
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }

        hmLangValue = getHashMap(mContext, getString(R.string.key_lang_list))
    }


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
    /**
     * init date picker dialog with current date and max date as current date
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDatePicker(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val cal = Calendar.getInstance()

        val dpd = DatePickerDialog(mContext, { _, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dobFormat = "yyyy-MM-dd" // mention the format you need
            val simpleDateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(dobFormat, Locale.ENGLISH)
            } else {
                TODO("VERSION.SDK_INT < N")
            }

            //TODO PUT THE DATE VALUE INTO A VALUE TO CALL API


        }, currYear, month, day)

        dpd.datePicker.maxDate = System.currentTimeMillis()
        return dpd
    }

    @SuppressLint("SimpleDateFormat")
    private fun createDateDialog(): Dialog {
        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_custom_calendar)

        val c = Calendar.getInstance()
        val today = c.time
        c.add(Calendar.YEAR, 1)
        val maxDate = c.time

        val alsDeactivated: ArrayList<Int> = ArrayList(2)
        alsDeactivated.add(2)

        val alsHighLight: ArrayList<Date?> = ArrayList(2)
        try {

            val dateformat = SimpleDateFormat("dd-MM-yyyy")
            val strdate = "07-10-2022"
            val strdate2 = "08-10-2022"
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
            .withSelectedDate(today)
// deactivates given dates, non selectable
            .withDeactivateDates(alsDeactivated)
// highlight dates in red color, mean they are aleady used.
            .withHighlightedDates(alsHighLight)
        /*
         val ivCamera =
             dialog.findViewById<ImageView>(R.id.ivCamera)

         ivGallery.setOnClickListener {
             *//*val intent =
                Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
          *//*

        }
        ivCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
            dialog.cancel()

        }
*/


        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

}