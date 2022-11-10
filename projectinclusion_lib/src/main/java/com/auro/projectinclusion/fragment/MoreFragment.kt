package com.pi.projectinclusion.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pi.projectinclusion.R

//private const val TAG = "MoreFragment"

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

       // Log.d(TAG, "onCreateView: ")



        return inflater.inflate(R.layout.fragement_more, container, false)
    }

/*    private fun setLanguage() {

        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        mBinding.selectText.text = hmLangValue["log_signup_txt"]
        mBinding.logBttnTxt.text = hmLangValue["cont_text"]
        mBinding.editPhone.hint = hmLangValue["field_phone_number"]
        mBinding.tvOr.text = hmLangValue["key_or"]
        mBinding.tvLoginWithOtp.text = hmLangValue["key_login_with_otp"]
    }*/
}