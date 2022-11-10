package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.DashBoardActivity
import com.pi.projectinclusion.activity.OtpVerificationActivity
import com.pi.projectinclusion.activity.ProfileActivity
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.FragAuthListener
import com.pi.projectinclusion.auth.OtpSendListener
import com.pi.projectinclusion.databinding.FragmentPasswordBinding
import com.pi.projectinclusion.model.LoginModel
import com.pi.projectinclusion.model.SendOtpModel
import com.pi.projectinclusion.repository.AuroRepository

private const val TAG = "PasswordFragment"

class PasswordFragment : Fragment(), FragAuthListener, OtpSendListener {

    private lateinit var mContext: Context
    private lateinit var mBinding: FragmentPasswordBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var phone: String
    private lateinit var userType: String
    private lateinit var userName: String
    private lateinit var userId: String
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var v: View

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: ")
        // Inflate the layout for this fragment
        mContext = requireActivity()
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        v = mBinding.root

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mFragListener = this
        viewModel.mSendOtpListener = this
        val bundle = arguments
        phone = bundle!!.getString(mContext.getString(R.string.key_phone_number)).toString()
        userType = bundle.getString(mContext.getString(R.string.key_userTypeId)).toString()
        userName = bundle.getString(mContext.getString(R.string.key_username)).toString()
        userId = bundle.getString(mContext.getString(R.string.key_user_id)).toString()
        setLanguage()

        mBinding.clickHereText.setOnClickListener {
            viewModel.sendCoroutineOtpMsg(phone, mContext)
            val intent = Intent(activity, OtpVerificationActivity::class.java)
            intent.putExtra(mContext.getString(R.string.key_phone_number), phone)
            intent.putExtra(mContext.getString(R.string.key_user_id), userId)
            intent.putExtra(
                mContext.getString(R.string.key_userTypeId),
                getData(requireActivity(), mContext.getString(R.string.key_userTypeId))
            )
            intent.putExtra(
                mContext.getString(R.string.key_calling_fragment),
                mContext.getString(R.string.fragment_password)
            )

            startActivity(intent)
        }


        mBinding.hidePassImg.setOnClickListener {
            mBinding.editPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            mBinding.showPassImg.visibility = View.VISIBLE
            mBinding.hidePassImg.visibility = View.GONE
            mBinding.editPassword.setSelection(mBinding.editPassword.text.length)
        }
        mBinding.showPassImg.setOnClickListener {
            mBinding.editPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            mBinding.showPassImg.visibility = View.GONE
            mBinding.hidePassImg.visibility = View.VISIBLE
            mBinding.editPassword.setSelection(mBinding.editPassword.text.length)
        }
        mBinding.cvContinue.setOnClickListener {
            //Log.d(TAG, "onCreateView: button reached")
            val pswd = mBinding.editPassword.text.toString().trim()
          //  Log.d(TAG, "onCreateView: pswd: $pswd")
            if (pswd.isEmpty()) {
                mContext.toast(hmLangValue["enter_password_txt"].toString())
                // mBinding.editPassword.requestFocus()
            } else {
                mBinding.passwordProgress.visibility = View.VISIBLE
//                Log.d(TAG, "onCreateView: username: $userName")
//                Log.d(TAG, "onCreateView: pswd: $pswd")
//                Log.d(TAG, "onCreateView: userType: $userType")
                viewModel.getCoroutineLogin(
                    userName,
                    pswd,
                    userType,
                    mContext
                )
            }
        }

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

        mBinding.selectTextPswd.text = hmLangValue["enter_password_txt"]
        mBinding.continuePswrdBttn.text = hmLangValue["cont_text"]
        mBinding.tvForgotPassword.text = hmLangValue["forget_pswd"]
        mBinding.clickHereText.text = hmLangValue["click_here"]
    }

    override fun onNetworkFragStart() {
        Log.d(TAG, "onNetworkFragStart: ")
    }

    override fun onCoroutineFragSuccess(loginResponse: LoginModel) {
       // Log.d(TAG, "onFragSuccess: ")


        val status = loginResponse.status.toString()
        val msg = loginResponse.message.toString()
        //Log.d(TAG, "onCoroutineFragSuccess: loginResponse: $loginResponse")

        if (status == "1" || msg.contentEquals("Success")) {
            val token = loginResponse.token.toString()
            saveData(
                requireActivity(),
                requireActivity().getString(R.string.key_user_token),
                token
            )
            saveData(
                requireActivity(),
                requireActivity().getString(R.string.key_username),
                userName
            )

            activity?.toast(
                hmLangValue["login_success_txt"].toString()
            )

            saveData(mContext, getString(R.string.key_user_id), loginResponse.userId.toString())

            mBinding.passwordProgress.visibility = View.GONE

            val userResponse = loginResponse.userResponse

            val hmProfile = AuroRepository().inflateProfileHashMap(mContext, userResponse!!)
            if (AuroRepository().validateProfileComplete(userResponse)) {
                startActivity(
                    Intent(
                        requireActivity(),
                        DashBoardActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra(getString(R.string.key_profile), hmProfile)
                )
                activity?.overridePendingTransition(
                    R.anim.slide_in_left_first,
                    R.anim.slide_out_left_first
                )
            } else {
                startActivity(
                    Intent(
                        requireActivity(),
                        ProfileActivity::class.java
                    )
                        .putExtra(getString(R.string.key_profile), hmProfile)
                        .putExtra(getString(R.string.key_phone_number), phone)
                        .putExtra(
                            getString(R.string.key_calling_fragment),
                            getString(R.string.fragment_password)
                        )
                        .setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        )
                )
                activity?.overridePendingTransition(
                    R.anim.slide_in_left_first,
                    R.anim.slide_out_left_first
                )

            }


        } else {
            activity?.toast(
                hmLangValue["incorrect_password_txt"].toString().replace("\\n", " ")
            )
            mBinding.passwordProgress.visibility = View.GONE
        }
    }

    override fun onFragFail() {
        Log.d(TAG, "onLangKeyApiFailure: api call failed")

        mBinding.passwordProgress.visibility = View.GONE
    }

    override fun onOtpNetworkStart() {
       // Log.d(TAG, "onOtpNetworkStart: ")
    }

    override fun onOtpCallSuccess(otp_response: SendOtpModel) {
      //  Log.d(TAG, "onOtpCallSuccess: ")
       // Log.d(TAG, "onOtpCallSuccess: otp_response: $otp_response")

    }

    override fun onOtpCallFail() {
       // Log.d(TAG, "onVerifyOtpFail: api call failed")
        mBinding.passwordProgress.visibility = View.GONE
    }


    //Disposed Temporarily
    /*override fun onFragSuccess(loginResponse: MutableLiveData<LoginModel>) {
        Log.d(TAG, "onFragSuccess: ")
        loginResponse.observe(this) {
            val status = it.status.toString()
            val msg = it.message.toString()

            Log.d(TAG, "onFragSuccess: status$status msg $msg")
            if (status == "1" || msg.contentEquals("Success")) {
                val token = it.token.toString()
                saveData(requireActivity(), "user_token", token)
                activity?.toast("Login Successfully")
                mBinding.passwordProgress.visibility = View.GONE
                val i = Intent(activity, DashBoardActivity::class.java)
                startActivity(i)
            } else {
                activity?.toast(msg)
            }
        }
    }*/

    /*override fun onClick(v: View?) {
        if (v!!.id==R.id.cvContinue)
        {
            Log.d(TAG, "onCreateView: button reached")
            val pswd = mBinding.editPassword.text.toString().trim()
            if (pswd.isNullOrEmpty()) {
                mBinding.editPassword.setError("Enter Password")
                // mBinding.editPassword.requestFocus()
            } else {
                mBinding.passwordProgress.visibility = View.VISIBLE
                viewmodel.getLogin(phone, pswd, user_type)
            }
        }

    }*/


}

