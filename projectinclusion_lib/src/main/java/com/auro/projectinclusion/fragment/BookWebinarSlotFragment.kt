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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.auro.projectinclusion.model.WebinarBookingModel
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.internetError
import com.pi.projectinclusion.adapter.LanguageWebinarAdapter
import com.pi.projectinclusion.adapter.SlotListAdapter
import com.pi.projectinclusion.adapter.WebinarListAdapter
import com.pi.projectinclusion.auth.*
import com.pi.projectinclusion.databinding.FragmentWebinarBookSlotBinding
import com.pi.projectinclusion.databinding.FragmentWebinarsBinding
import com.pi.projectinclusion.model.LanguageModel
import com.pi.projectinclusion.model.SlotIdModel
import com.pi.projectinclusion.model.SlotIdResponse
import com.pi.projectinclusion.model.WebinarModel
import com.pi.projectinclusion.repository.AuroRepository
import com.savvi.rangedatepicker.CalendarPickerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


private const val TAG = "BookWebinarSlotFragment"

class BookWebinarSlotFragment : Fragment(), SlotIdListener, SlotListAdapter.OnSlotSelected, WebinarBookListener {
    private lateinit var mBinding: FragmentWebinarBookSlotBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var alsSlotList: ArrayList<SlotIdModel>
    private lateinit var successDialog: Dialog
    private lateinit var selectedWebinar: WebinarModel
    private lateinit var v: View
    private var mLangid: Int = 1
    private lateinit var params : HashMap<String,Any>
    private var slotIdSelected = -1

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        mContext = requireActivity()

        initViewModel(inflater, container)
        setLanguage()
        backNavigation()

        onSlotBook()
        successDialog = createSuccessDialog()
        mViewModel.mSlotIdListener = this
        mViewModel.mWebinarBookListener = this
        getExtras()

        return v
    }

    /**
     * -------------------------------------------------------CUSTOM METHODS ---------------------------------------------------------------
     * [onSlotBook]
     * [backNavigation]
     * [getExtras]
     * [createSuccessDialog]
     * [setLanguage]
     * [initViewModel]
     */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSlotBook() {
        mBinding.acbBookNow.setOnClickListener {
            if (slotIdSelected != -1)
                {
                    val token =   "Bearer " + getData(mContext, mContext.getString(R.string.key_user_token))
                    /*{
          "id": 0,
          "createdDate": "2022-11-07T03:51:29.657Z",
          "updatedDate": "2022-11-07T03:51:29.657Z",
          "createdBy": 1,
          "updatedBy": 0,
          "status": 1,
          "priority": 1,
          "userId": 1,
          "webinarId": 1,
          "slotId": 1
        }*/

                    params = HashMap()
                    params["id"] = 0
                    params["createdDate"] = selectedWebinar.webinarDate
                    params["updatedDate"] = convertDateInS()
                    params["createdBy"] = 1
                    params["updatedBy"] = 0
                    params["status"] = 1//will not change
                    params["priority"] = 1 //will not change
                    params["userId"] = getData(mContext,mContext.getString(R.string.key_user_id))
                    params["webinarId"] =  selectedWebinar.webinarId
                    params["slotId"] = slotIdSelected

                    try {
                        successDialog.show()
                        mViewModel.getWebinarBookView(token,params,requireContext())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
            }
            else{
                Toast.makeText(mContext, "Select a Time slot", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun backNavigation() {
        mBinding.ivBackBook.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()

        }
    }

    private fun initSlotRecyclerView() {
        mBinding.pbSlots.visibility = View.GONE
        mBinding.rvSlots.layoutManager =
            LinearLayoutManager(mContext)
        mBinding.rvSlots.adapter = SlotListAdapter(mContext, alsSlotList, this)
    }

    private fun getExtras() {
        val bundle = arguments
        if (bundle != null) {
            try {
                selectedWebinar = bundle.getSerializable(mContext.getString(R.string.key_user_token)) as WebinarModel
                //selectedWebinar = bundle.getSerializable(mContext.getString(R.string.key_selected_webinar)) as WebinarModel
            } catch (e: Exception) {
            }
        }
        setAllData()
        mViewModel.getSlotIdByWebinarId(
            selectedWebinar.webinarId,
            getData(mContext, mContext.getString(R.string.key_user_token)),
            requireActivity()
        )

        Log.d(TAG, "getExtras: selectedWebinar: " + selectedWebinar)
    }

    private fun setAllData() {
        mBinding.tvWebinarName.text = selectedWebinar.name
        mBinding.tvLanguageBox.text = selectedWebinar.language
        mBinding.rmtvDesp.text = selectedWebinar.description
        mBinding.tvSlotNumber.text = selectedWebinar.totalslot.toString()
        mBinding.tvSubjectName.text = selectedWebinar.subject.toString()
        if (selectedWebinar.iconURL != null) {
            AuroRepository().loadImageWithGlide(
                mContext,
                selectedWebinar.iconURL.toString(),
                mBinding.ivWebinarImage,
                null
            )
        }
    }

    private fun initViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_webinar_book_slot, container, false)
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


    @SuppressLint("SimpleDateFormat")
    private fun createSuccessDialog(): Dialog {
        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.dialog_booking_success)

        val acbDone =  dialog.findViewById<AppCompatButton>(R.id.acbDone)

        acbDone.setOnClickListener {
          dialog.cancel()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    /**
     * ------------------------------------------------------IMPLEMENTATIONS AND API INTERFACES--------------------------------------------------------------
     *
     */

    override fun onSlotIdStarted() {
        mBinding.pbSlots.visibility = View.VISIBLE
    }

    override fun onSlotIdSuccess(slotIdResponse: SlotIdResponse) {
        Log.d(TAG, "onSlotIdSuccess: slotIdResponse: " + slotIdResponse)
        alsSlotList = ArrayList()
        alsSlotList.clear()
        mBinding.pbSlots.visibility = View.GONE
        if (slotIdResponse.message == "Success" && slotIdResponse.status == 200) {
            if (slotIdResponse.response?.size!! > 0) {
                mBinding.clNoSlot.visibility = View.GONE
                alsSlotList.addAll(slotIdResponse.response!!)
                //alsSlotList = slotIdResponse.response!!
            } else {
                mBinding.clNoSlot.visibility = View.VISIBLE
            }
        } else {
            mBinding.clNoSlot.visibility = View.VISIBLE
        }
        initSlotRecyclerView()
        Log.d(TAG, "onWebinarLangListSuccess: alsWebinarList: " + alsSlotList)
    }

    override fun onSlotIdFailure() {
        if (isNetwork(mContext)) {
            mViewModel.getSlotIdByWebinarId(
                selectedWebinar.webinarId,
                getData(mContext, mContext.getString(R.string.key_user_token)),
                requireActivity()
            )
        } else {
            mContext.toast(internetError)
            mBinding.pbSlots.visibility = View.GONE
        }
    }


    /**
     * to handle Webinar Booking
     * */
    override fun onWebinarBookingStart() {
        mBinding.pbSlots.visibility = View.VISIBLE
    }

    override fun onWebinarSuccess(booking: WebinarBookingModel) {
        mBinding.pbSlots.visibility = View.GONE
        try {
            Log.e("onWebinarSuccess", booking.userId.toString())
            Toast.makeText(mContext, "Booking successful", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onWebinarFailure() {
        if (isNetwork(mContext)) {

        } else {
            mContext.toast(internetError)
            mBinding.pbSlots.visibility = View.GONE
        }
    }

    /**
     * -------------------------------------------------LIFECYCLE AND OVERRIDE FUNCTIONS---------------------------------------------------------------------
     * [onResume]
     */
    override fun onResume() {
        super.onResume()
        setLanguage()
        getExtras()
    }

    override fun onSelected(mStartTime: String, mEndTime: String,slotID: Int) {
        Log.e("onselected","Slot Id >>$slotID ")
        slotIdSelected = slotID
    }

}