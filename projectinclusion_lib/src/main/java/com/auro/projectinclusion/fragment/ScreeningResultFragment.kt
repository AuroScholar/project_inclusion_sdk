package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.R
import com.pi.projectinclusion.adapter.ScreeningResultAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.databinding.FragmentScreeningResultBinding
import com.pi.projectinclusion.getData
import com.pi.projectinclusion.getHashMap
import com.pi.projectinclusion.setAppLocale

private const val TAG = "ScreeningResultFragment"

class ScreeningResultFragment : Fragment(), ScreeningResultAdapter.OnAnsSelectedListener {
    private lateinit var mBinding: FragmentScreeningResultBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var hmMessage: HashMap<String, String>
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var hmAnsCount: LinkedHashMap<String, String>
    private lateinit var hmIntervention: HashMap<String, String>
    private lateinit var alsSectionList: ArrayList<String>
    private lateinit var v: View


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")

        mContext = requireActivity()
        initViewModel(inflater, container)
        setLanguage()
        getArgument()
        setBackNavigation()
        return v
    }

    private fun getArgument() {
        val bundle = arguments
        hmMessage =
            bundle!!.getSerializable(mContext.getString(R.string.key_screening_messages)) as HashMap<String, String>
        hmAnsCount =
            bundle.getSerializable(mContext.getString(R.string.key_ans_count)) as LinkedHashMap<String, String>
        hmIntervention =
            bundle.getSerializable(mContext.getString(R.string.key_interventions)) as HashMap<String, String>
        alsSectionList = ArrayList(hmAnsCount.keys.toList())
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mBinding.rvScreeningResult.layoutManager = LinearLayoutManager(mContext)
        // Passing context = mContext, certificates list = alsCertificateList, certificate type = certType, activity = this
        mBinding.rvScreeningResult.adapter = //TODO VALIDATE FOR NULL VALUE OF RESPONSE
            ScreeningResultAdapter(
                mContext,
                hmLangValue["conclusion_txt"].toString(),
                hmLangValue["intervention_txt"].toString(),
                hmLangValue["no_result_txt"].toString(),
                alsSectionList,
                hmMessage,
                hmAnsCount,
                hmIntervention,
                this
            )

    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }

    private fun initViewModel(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_screening_result, container, false)
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
        mBinding.tvSRHeading.text = hmLangValue["screening_result"]
        mBinding.tvSRHeading.text = hmLangValue["screening_result"]
        //mBinding.screeningText.text = hmLangValue["screening_certificates"]
    }

    private fun setBackNavigation() {
        mBinding.cvBackResult.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onItemClick(pos: Int, answer: String, ques: String) {
    }

}