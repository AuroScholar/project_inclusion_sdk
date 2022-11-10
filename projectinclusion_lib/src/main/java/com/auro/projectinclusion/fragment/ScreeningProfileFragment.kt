package com.pi.projectinclusion.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.ScreeningCertificateActivity
import com.pi.projectinclusion.activity.internetError
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.DistrictListListener
import com.pi.projectinclusion.auth.StateListListener
import com.pi.projectinclusion.databinding.FragmentScreeningProfileBinding
import com.pi.projectinclusion.model.DistrictModel
import com.pi.projectinclusion.model.GenderModel
import com.pi.projectinclusion.model.StateModel
import java.util.ArrayList

private const val TAG = "ScreeningProfileFragment"

class ScreeningProfileFragment : Fragment(),
    StateListListener, DistrictListListener {
    private lateinit var mBinding: FragmentScreeningProfileBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var v: View
    private lateinit var hmLangValue: HashMap<String, String>


    private var selectedState: Int? = 0
    private lateinit var alsStateList: ArrayList<StateModel>

    private var selectedDistrict: Int? = 0
    private lateinit var alsDistrictList: ArrayList<DistrictModel>

    private var selectedGender: Int? = 0
    private lateinit var alsGenderList: ArrayList<GenderModel>

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [setLanguage]
     */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")

        mContext = requireActivity()
        initViewModel(inflater, container)
        setLanguage()
        setGenderSpinner()
        continueToScreening()


        return v
    }

    /**
     * -------------------------------------- CUSTOM METHODS-----------------------------
     */
    private fun continueToScreening() {
        mBinding.clScreeningProfileContinue.setOnClickListener {
            startActivity(
                Intent(mContext, ScreeningCertificateActivity::class.java)
            )//
        }
    }

    private fun validateFields(): Boolean {
        val validate = true
        //TODO add checks for all compulsory fields
        if (selectedState == 0) {
            mContext.toast(hmLangValue["error_state"].toString())
            return false
        }
        if (selectedDistrict == 0) {
            mContext.toast(hmLangValue["error_district"].toString())
            return false
        }
        if (selectedGender == 0) {
            mContext.toast(hmLangValue["error_gender"].toString())
            return false
        }

        return validate
    }

    //SPINNERS
    private fun setGenderSpinner() {
        alsGenderList.add(GenderModel(name = hmLangValue["hint_gender"], id = 0))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_male"], id = 1))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_female"], id = 2))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_other"], id = 3))

        val alsGender = ArrayList<String>(5)
        for (i in alsGenderList) {
            alsGender.add(i.name.toString())
        }
        val genderAdapter =
            object :
                ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, alsGender) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        view.setTextColor(Color.BLACK)
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }
                    return view
                }
            }
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spGender.adapter = genderAdapter
//        Log.d(
//            TAG,
//            "setGenderSpinner: gender" + hmProfileValue!![mContext.getString(R.string.field_gender)]
//        )
        /*   if (hmProfileValue != null) {//TODO UNCOMMENT THIS CODE TO IMPLEMENT ALREADY SELECTED GENDER AND INFLATE DISTRICT LIST ACCORDINGLY


               for (i in alsGenderList) {
                   Log.d(com.pi.projectinclusion.activity.TAG, "setGenderSpinner: i.id: " + i.id)
                   if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_gender)]) {
                       selectedGender = i.id
                       mBinding.spGender.setSelection(alsGenderList.indexOf(i))
                       break
                   }

               }
           }*/
    }

    private fun setSpinnerOnClick() {
        //Log.d(TAG, "setSpinnerOnClick: spinner")
        mBinding.spState.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener { // STATE SPINNER
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "onItemSelected: position: $position"
                    )
                    selectedDistrict = 0
                    val value = parent!!.getItemAtPosition(position).toString()
                    if (value == alsStateList[0].name) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    mViewModel.getDistrictList(alsStateList[position].id.toString(), mContext)
                    selectedState =
                        if (alsStateList[position].id == null) 0 else alsStateList[position].id
                }
            }

        mBinding.spDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "onItemSelected: position: $position"
                    )
                    val value = parent!!.getItemAtPosition(position).toString()
                    if (value == alsDistrictList[0].name) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    selectedDistrict =
                        if (alsDistrictList[position].id == null) 0 else alsDistrictList[position].id
                }
            }

        mBinding.spGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "onItemSelected: position: $position"
                    )
                    val value = parent!!.getItemAtPosition(position).toString()
                    if (value == alsGenderList[0].name) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    selectedGender =
                        if (alsGenderList[position].id == null) 0 else alsGenderList[position].id
                }
            }
    }

    private fun initViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_screening_profile, container, false)
        v = mBinding.root
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel

        alsGenderList = ArrayList(5)
        alsStateList = ArrayList(29)
        alsDistrictList = ArrayList(29)
        mViewModel.mStateListListener = this
        mViewModel.mDistrictListListener = this

        mViewModel.getStateList(mContext)

    }

    private fun setLanguage() {
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))


        mBinding.tvStateId.text = hmLangValue["field_state"]
        mBinding.tvDistrictId.text = hmLangValue["field_district"]
        mBinding.tvGender.text = hmLangValue["field_gender"]
        mBinding.tvScreeningProfileHeading.text = hmLangValue["screening_profile"]
        mBinding.tvScreeningSub.text = hmLangValue["screening_sub_heading_txt"]
        mBinding.tvTName.text = hmLangValue["field_teacher_name"]
        mBinding.etTeacherName.hint = hmLangValue["hint_teacher_name"].toString()
        mBinding.tvSName.text = hmLangValue["field_student_name"]
        mBinding.etStudentName.hint = hmLangValue["hint_student_name"].toString()
        mBinding.tvSchoolName.text = hmLangValue["field_school"]
        mBinding.etSchool.hint = hmLangValue["hint_school"].toString()
        mBinding.tvClass.text = hmLangValue["field_class"]
        mBinding.etClass.hint = hmLangValue["hint_class"].toString()
        mBinding.tvAge.text = hmLangValue["field_age"]
        mBinding.etAge.hint = hmLangValue["hint_age"].toString()
        mBinding.tvPhNo.text = hmLangValue["field_phone_number"]
        mBinding.tvWpNo.text = hmLangValue["field_whatsapp_number"]
        mBinding.tvSame.text = hmLangValue["txt_same_as_your_mobile_number"]
        mBinding.tvEmail.text = hmLangValue["field_email"]
        mBinding.tvResidence.text = hmLangValue["field_residence"]
        mBinding.tvDurObv.text = hmLangValue["field_duration_observation"]
    }

    /**
     * -------------------------------------- OVERRIDE API LISTENER IMPLEMENTATIONS------------------------------
     */


    override fun onNetworkCallStarted() {

    }

    override fun onDistrictListCallSuccess(districtModel: List<DistrictModel>) {
        //  Log.d(TAG, "onDistrictListCallSuccess: $districtModel")
        alsDistrictList.clear()
        val alsDistrictNameList = ArrayList<String>(20)
        alsDistrictNameList.add(hmLangValue["error_district"].toString())
        alsDistrictList.add(DistrictModel(hmLangValue["error_district"].toString(), 0))
        if (districtModel.isNotEmpty()) {
            for (i in districtModel) {
                alsDistrictList.add(i)
                i.name?.let { alsDistrictNameList.add(it) }
            }
        }
//        Log.d(
//            TAG,
//            "onDistrictListCallSuccess: alsDistrictNameList: $alsDistrictNameList"
//        )
        val districtAdapter =
            object : ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_spinner_item,
                alsDistrictNameList
            ) {

                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    //set the color of first item in the drop down list to gray
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {

                        view.setTextColor(Color.BLACK)
                    }
                    return view
                }

            }
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spDistrict.adapter = districtAdapter

        /*    if (hmProfileValue != null) {//TODO UNCOMMENT THIS CODE TO IMPLEMENT ALREADY SELECTED DISTRICT AND INFLATE DISTRICT LIST ACCORDINGLY

                for (i in alsDistrictList) {
                    if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_district)]) {
                        selectedDistrict = alsDistrictList.indexOf(i)
                        mBinding.spDistrict.setSelection(alsDistrictList.indexOf(i))
                        break
                    }

                }
            }*/
    }

    override fun onDistrictApiFailure() {
        Log.d(TAG, "onDistrictApiFailure: api call failed")
        if (isNetwork(mContext)) {
            /*mViewModel.getDistrictList(
                alsStateList.get(selectedState!! - 1).id.toString(),
                mContext
            )*/
            mContext.toast(internetError)
        }
    }

    override fun onStateListCallSuccess(stateModel: List<StateModel>) {
        alsStateList.clear()
        val alsStateNameList = ArrayList<String>(10)
        alsStateNameList.add(hmLangValue["error_state"].toString())
        alsStateList.add(StateModel(hmLangValue["error_state"].toString(), 0))
        for (i in stateModel) {
            alsStateList.add(i)
            i.name?.let { alsStateNameList.add(it) }
        }
        val stateAdapter =
            object :
                ArrayAdapter<String>(
                    mContext,
                    android.R.layout.simple_spinner_item,
                    alsStateNameList
                ) {

                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    //set the color of first item in the drop down list to gray
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                        view.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spState.adapter = stateAdapter

        /* if (hmProfileValue != null) { //TODO UNCOMMENT THIS CODE TO IMPLEMENT ALREADY SELECTED STATE AND INFLATE DISTRICT LIST ACCORDINGLY
             for (i in alsStateList) {

                 if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_state)]) {
                     selectedState = alsStateList.indexOf(i)
                     mBinding.spState.setSelection(alsStateList.indexOf(i))

                     mViewModel.getDistrictList(
                         alsStateList[alsStateList.indexOf(i)].id.toString(),
                         this
                     )
                     break
                 }

             }
         }*/
        setSpinnerOnClick()

    }

    override fun onStateApiFailure() {
        Log.d(TAG, "onStateApiFailure: api call failed")
        if (isNetwork(mContext)) {
            mViewModel.getStateList(mContext)
        }
    }

    /**
     * -------------------------------------- LIFECYCLE AND OVERRIDE FUNCTIONS------------------------------
     */

    override fun onResume() {
        super.onResume()
        setLanguage()
    }
}