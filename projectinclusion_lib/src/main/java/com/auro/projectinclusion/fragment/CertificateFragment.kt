package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.LMSCertificateActivity
import com.pi.projectinclusion.activity.ScreeningCertificateActivity
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.databinding.FragmentCertificateBinding

private const val TAG = "CertificateFragment"

class CertificateFragment : Fragment() {
    private lateinit var mBinding: FragmentCertificateBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var mContext: Context
    private lateinit var v: View
    private lateinit var hmLangValue: HashMap<String, String>

    /**
     *
     */
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")

        mContext = requireActivity()
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_certificate, container, false)
        v = mBinding.root
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel
        setLanguage()
        mBinding.lmsLayout.setOnClickListener {
            startActivity(
                Intent(context!!, LMSCertificateActivity::class.java)
            )//
        }
//        mBinding.clScreening.setOnClickListener {
//            startActivity(
//                Intent(context!!, ScreeningCertificateActivity::class.java)
//            )//
//        }


        return v
    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }

    private fun setLanguage() {
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        mBinding.imageView2.text = hmLangValue["certificate"]
        mBinding.lmsText.text = hmLangValue["lms_certificate"]
        mBinding.screeningText.text = hmLangValue["screening_certificates"]
        //mBinding.screeningText.text = hmLangValue["screening_certificates"]
    }

}