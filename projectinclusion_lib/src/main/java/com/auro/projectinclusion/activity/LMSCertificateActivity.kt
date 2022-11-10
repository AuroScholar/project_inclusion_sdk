package com.pi.projectinclusion.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.adapter.CertificateNewTypeAdapter
import com.pi.projectinclusion.adapter.LMSCertificateListAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.CertificateListListener
import com.pi.projectinclusion.auth.CertificateTypeListener
import com.pi.projectinclusion.databinding.ActivityCertificatesListBinding
import com.pi.projectinclusion.model.CertificateDetailModel
import com.pi.projectinclusion.model.CertificateModel
import com.pi.projectinclusion.model.CertificateTypeModel
import com.pi.projectinclusion.repository.AuroRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * TO ADD A NEW CERTIFICATE TYPE TO THE RECYCLER VIEW
 * PLEASE FOLLOW THESE STEPS:
 * STEP 1: Add a new string for the name of the new type
 * STEP 2: Add logic in if statements in adapter if it has different layout
 * STEP 3:
 *
 * API TO BE IMPLEMENTED:
 * 1. Certificate Type Api
 * 2. List of certificates
 *

 */



class LMSCertificateActivity : AppCompatActivity(),
    CertificateNewTypeAdapter.OnNewCertTypeClickListener, CertificateTypeListener,
    CertificateListListener {
    private lateinit var mContext: Context
    private var certId: String? = "1" // Type of certificate selected currently
    private lateinit var mBinding: ActivityCertificatesListBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var alsCertificateList: ArrayList<CertificateDetailModel> // List of certificates of currently selected certificate type of the user
    private lateinit var alsCertTypeList: ArrayList<CertificateTypeModel> // List of certificate types

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [setLanguage]
     * [setBackNavigation]
     * [initCertTypeRV]
     * [initRecyclerView]
     * [onTypeCallSuccess]
     * [onListCallSuccess]
     * [onNetworkCallStarted]
     * [onItemViewClick]
     * [onBackPressed]
     * [onResume]
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this
        alsCertificateList = ArrayList(5)
        alsCertTypeList = ArrayList(3)

        //INIT
        initViewModel() // initializing view model details
        setLanguage()
        setBackNavigation() // Setting back navigation by finishing activity

        CoroutineScope(Dispatchers.IO).launch {
            try {
                //Calling all certificate types

                mBinding.pbCertificate.visibility = View.VISIBLE
                viewModel.getCertificateTypes(mContext)
                // Calling First certificate type i.e Module
                // here certId = 1
                val userId = getData(mContext, mContext.getString(R.string.key_user_id))
                val token =
                    "Bearer " + getData(mContext, mContext.getString(R.string.key_user_token))
                viewModel.getCertificateList(
                    userId, certId!!, token, mContext
                )
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    mContext.toast(mContext.getString(R.string.error_loading_txt))
                }
            }
        }

    }

    /**
     * -------------------------------------- CUSTOM METHODS-----------------------------
     */

    private fun setLanguage() {
        //Setting language keys from api based for hindi or english
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        mBinding.tvCertHeading.text = hmLangValue["lms_certificate"]
        mBinding.tvNoCert.text = hmLangValue["no_cert_txt"]
    }

    private fun initViewModel() { // View model binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_certificates_list)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mCertificateTypeListener = this
        viewModel.mCertificateListListener = this
    }

    private fun setBackNavigation() { // Back navigation on click
        mBinding.cvBackCert.setOnClickListener {
            finish()
        }
    }

    private fun initCertTypeRV() { // recycler view for certificate types

        mBinding.rvCertTypes.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Passing context = mContext, certificates type list = alsCertTypeList, certificate type = certId, click Listener = this
        mBinding.rvCertTypes.adapter =
            CertificateNewTypeAdapter(mContext, alsCertTypeList, certId, this)
    }

    private fun initRecyclerView(certType: String?) { // Recycler view for certificates of currently selected type
        mBinding.rvCertificates.layoutManager = LinearLayoutManager(this)
        // Passing context = mContext, certificates list = alsCertificateList, certificate type = certType, activity = this
        mBinding.rvCertificates.adapter =
            LMSCertificateListAdapter(mContext, alsCertificateList, hmLangValue, certType, this)
    }


    /**
     * -------------------------------------- OVERRIDE API LISTENER IMPLEMENTATIONS------------------------------
     */
    override fun onTypeCallSuccess(certificateTypeModel: List<CertificateTypeModel>) { // Certificate Types
        //clearing certificate type list to inflate with newly fetched certificate types
        alsCertTypeList.clear()
        //Adding certificate types into certificate type list
        for (i in certificateTypeModel) {
            i.name = hmLangValue["key_" + i.name.toString().lowercase()]
            alsCertTypeList.add(CertificateTypeModel(i.name, i.id, i.priority))
        }
        //inflating recycler view on main thread
        // as it is running IO thread in view model class
        AuroRepository().runOnMainThread {
            initCertTypeRV()
        }
    }

    override fun onTypeApiFailure() {
       // Log.d(TAG, "onTypeApiFailure: api call fail")
        if (isNetwork(this)) {
            viewModel.getCertificateTypes(mContext)
        }
    }

    override fun onListCallSuccess(certificateModel: CertificateModel) {
        //clearing certificate list to inflate with newly fetched certificates
        alsCertificateList.clear()
        // checking if user has any certificates {api response: "Status = 1" (has certificates) or Status = 0 (no certificates)}

        mBinding.pbCertificate.visibility = View.GONE
        if (certificateModel.status == "1") {

            //adding certificates to list
            for (i in certificateModel.response as List<CertificateDetailModel>) {

                alsCertificateList.add(i)
            }

            //inflating recycler view by passing certId of currently selected cert type on main thread
            // as it is running IO thread in view model class
            //Also hiding no certificates available view
            AuroRepository().runOnMainThread {
                initRecyclerView(certId)
                mBinding.clNoCert.visibility = View.GONE
            }
        } else {
            //status 0, show no certificate view
            mBinding.clNoCert.visibility = View.VISIBLE
        }
    }

    override fun onListApiFail() {
        //.d(TAG, "onListApiFail: api call fail")
        if (isNetwork(this)) {
            val userId = getData(mContext, mContext.getString(R.string.key_user_id))
            val token = "Bearer " + getData(mContext, mContext.getString(R.string.key_user_token))
            viewModel.getCertificateList(
                userId,
                certId!!,
                token,
                this
            )
        }else{
            mBinding.pbCertificate.visibility = View.GONE
        }
    }

    override fun onNetworkCallStarted() {// Certificate Types
        //.d(TAG, "onNetworkCallStarted:")
    }

    /**
     * -------------------------------------- LIFECYCLE AND OVERRIDE FUNCTIONS------------------------------
     */

    override fun onItemViewClick(pos: Int) { // Item click for certificate types
        //Setting certificate id to certId variable
        certId = alsCertTypeList[pos].id.toString()

        //updating cert type adapter by sending payload of selected certificate type
        mBinding.rvCertTypes.adapter?.notifyItemRangeChanged(
            0,
            alsCertTypeList.size,
            alsCertTypeList[pos]
        )
        //Clearing fetched certificates from previously selected certificate type in Certificate List Adapter
        val size = alsCertificateList.size
        alsCertificateList.clear()
        if (mBinding.rvCertificates.adapter != null) {
            mBinding.rvCertificates.adapter!!.notifyItemRangeRemoved(0, size)
        }

        //Getting new certificates from api based upon currently selected certificate type
        val userId = getData(mContext, mContext.getString(R.string.key_user_id))
        val token = "Bearer " + getData(mContext, mContext.getString(R.string.key_user_token))

        mBinding.pbCertificate.visibility = View.VISIBLE
        viewModel.getCertificateList(
            userId,
            certId!!,
            token,
            this
        )
    }

    override fun onBackPressed() {
        // When back button is pressed inside Certificate Fragment
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setLanguage()

    }
}