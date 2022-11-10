package com.pi.projectinclusion.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.LoginWithOtpListener
import com.pi.projectinclusion.auth.OtpSendListener
import com.pi.projectinclusion.auth.VerifyOtpListener
import com.pi.projectinclusion.databinding.ActivityOtpVerificationBinding
import com.pi.projectinclusion.model.LoginWithOTPModel
import com.pi.projectinclusion.model.SendOtpModel
import com.pi.projectinclusion.repository.AuroRepository


class OtpVerificationActivity : AppCompatActivity(), VerifyOtpListener, OtpSendListener,
    LoginWithOtpListener {
    private lateinit var mBinding: ActivityOtpVerificationBinding
    private lateinit var mPhoneNumber: String
    private lateinit var username: String
    private lateinit var mUserType: String
    private lateinit var userId: String
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    private var isLoginWithOtp = false
    private var isForgotPassword = false

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuroRepository().setStatusBarColor(this)

        initViewModel()
        getIntents()
        setLanguage()
        backNavigation()
        countdownStart()
        verifyOtp()
        resendOtp()
        addTextChangeListeners()
        /**
         * ALL METHODS
         * [onVerifyNetworkCall]
         * [onVerifyOtpSuccess]
         * [onVerifyOtpFail]
         * [onOtpNetworkStart]
         * [onOtpCallSuccess]
         * [setLanguage]
         * [onOtpCallFail]
         * [onLoginWithOtpSuccess]
         * [onLoginFailure]
         * [onLoginFailure]
         * [onResume]
         * [onBackPressed]
         */

    }

    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */
    private fun resendOtp() {
        mBinding.resendSmsLayout.setOnClickListener {
            viewModel.sendCoroutineOtpMsg(mPhoneNumber, this)
            mBinding.resendSmsLayout.isEnabled = false
            mBinding.resendSmsLayout.setBackgroundResource(R.drawable.otp_back)
            mBinding.resendTxt.setTextColor(Color.parseColor("#808080"))
            object : CountDownTimer(30000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    mBinding.otpTimerText.text = "" + millisUntilFinished / 1000

                }

                override fun onFinish() {
                    //   mBinding.otpTimerText.setText("done!")
                    mBinding.resendSmsLayout.isEnabled = true
                    mBinding.resendSmsLayout.setBackgroundResource(R.drawable.blue_stroke_background)
                    mBinding.resendTxt.setTextColor(Color.parseColor("#ff33b5e5"))
                }
            }.start()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getIntents() {

        if (intent.getStringExtra(getString(R.string.key_calling_fragment)) != null) {

            if (intent.getStringExtra(getString(R.string.key_calling_fragment)) == getString(R.string.fragment_email)) {
                isLoginWithOtp = true
                username = intent.getStringExtra(getString(R.string.key_username)).toString()
            }
            if (intent.getStringExtra(getString(R.string.key_calling_fragment)) == getString(R.string.fragment_password)) {
                isForgotPassword = true
            }
        }
        mUserType = intent.getStringExtra(getString(R.string.key_userTypeId)).toString()
        userId = intent.getStringExtra(getString(R.string.key_user_id)).toString()
        mPhoneNumber = intent.getStringExtra(getString(R.string.key_phone_number)).toString()
        // Log.d(TAG, "getIntents: phoneNumber: " + mPhoneNumber)
        if (mPhoneNumber.length > 6)
            mBinding.numberTxt.text = "+91 " + "XXX-XXX-" + mPhoneNumber.substring(6)
    }

    private fun verifyOtp() {
        mBinding.verifyOtpButton.setOnClickListener {
            if (viewModel.editOtp1.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else if (viewModel.editOtp2.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else if (viewModel.editOtp3.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else if (viewModel.editOtp4.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else if (viewModel.editOtp5.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else if (viewModel.editOtp6.isNullOrEmpty()) {
                toast(hmLangValue["error_enter_otp"].toString())
            } else {
                mBinding.verificationProgress.visibility = View.VISIBLE
                val otp =
                    viewModel.editOtp1 + viewModel.editOtp2 + viewModel.editOtp3 + viewModel.editOtp4 + viewModel.editOtp5 + viewModel.editOtp6

                if (isLoginWithOtp) {
                    // Log.d(TAG, "verifyOtp: username: " + username)
//                    Log.d(TAG, "verifyOtp: otp: " + otp)
//                    Log.d(TAG, "verifyOtp: mUserType: " + mUserType)
                    viewModel.loginWithOtp(
                        username,
                        otp.toInt(),
                        mUserType.toInt(), this
                    )
                } else {
                    viewModel.verifyTheCoroutineOtp(mPhoneNumber, otp, this)
                }
            }
        }
    }

    private fun countdownStart() {
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                mBinding.otpTimerText.text = "" + millisUntilFinished / 1000

            }


            override fun onFinish() {
                mBinding.resendSmsLayout.isEnabled = true
                mBinding.resendSmsLayout.setBackgroundResource(R.drawable.blue_stroke_background)
                mBinding.resendTxt.setTextColor(Color.parseColor("#ff33b5e5"))

            }
        }.start()
    }

    private fun backNavigation() {
        mBinding.cvBackOtp.setOnClickListener {
            finish()
        }
    }

    private fun setLanguage() {
        if (getData(this, getString(R.string.key_lang_id)) == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))

        mBinding.otpHeaderTxt.text = hmLangValue["otp_verification"]
        mBinding.otpHeader.text = hmLangValue["verification_msg"]
        mBinding.resendTxt.text = hmLangValue["resend_sms"]
        mBinding.callTxt.text = hmLangValue["call_txt"]
        mBinding.otpContinue.text = hmLangValue["cont_text"]
        mBinding.resendOtpTxt.text = hmLangValue["resend_otp"]
    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mVerifyOtpListener = this
        viewModel.mSendOtpListener = this
        viewModel.mLoginWithOtpListener = this
        mBinding.resendSmsLayout.isEnabled = false
    }

    private fun addTextChangeListeners() {
        mBinding.editFirstOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editFirstOtp.text.length == 1) {
                    mBinding.editSecondOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.editSecondOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editSecondOtp.text.length == 1) {
                    mBinding.editThirdOtp.requestFocus()
                } else if (mBinding.editSecondOtp.text.isEmpty()) {
                    mBinding.editFirstOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.editThirdOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editThirdOtp.text.length == 1) {
                    mBinding.editFourOtp.requestFocus()
                } else if (mBinding.editThirdOtp.text.isEmpty()) {
                    mBinding.editSecondOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.editFourOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editFourOtp.text.length == 1) {
                    mBinding.editFiveOtp.requestFocus()
                } else if (mBinding.editFourOtp.text.isEmpty()) {
                    mBinding.editThirdOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.editFiveOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editFiveOtp.text.length == 1) {
                    mBinding.editSixOtp.requestFocus()
                } else if (mBinding.editFiveOtp.text.isEmpty()) {
                    mBinding.editFourOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        mBinding.editSixOtp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (mBinding.editSixOtp.text.isEmpty()) {
                    mBinding.editFiveOtp.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

    }

    /**
     * --------------------------------------- On Item Clicks & api event listeners-------------------
     */
    ////////// VERIFY OTP
    override fun onVerifyNetworkCall() {
        //Log.d(TAG, "onVerifyNetworkCall: ")
    }

    override fun onVerifyOtpSuccess(verify_otp_response: SendOtpModel) {

        mBinding.verificationProgress.visibility = View.GONE
        val msg = verify_otp_response.message.toString()

        if (msg.contentEquals("Success")) {
            if (isForgotPassword) {
                startActivity(
                    Intent(
                        this,
                        ResetPasswordActivity::class.java
                    ).putExtra(getString(R.string.key_user_id), userId)
                        .putExtra(
                            getString(R.string.key_calling_fragment),
                            getString(R.string.activity_otp_verification)
                        )
                )

            } else {
                toast(hmLangValue["msg_otp_verified"].toString())
                finish()

                startActivity(
                    Intent(this, SignUpActivity::class.java)
                        .putExtra(getString(R.string.key_userTypeId), mUserType)
                        .putExtra(getString(R.string.key_phone_number), mPhoneNumber)

                )
            }


        } else {
            toast(hmLangValue["error_null_otp_response"].toString())

        }
    }

    override fun onVerifyOtpFail() {
        mBinding.verificationProgress.visibility = View.GONE
    }

    ///////SEND OTP
    override fun onOtpNetworkStart() {

    }

    override fun onOtpCallSuccess(otp_response: SendOtpModel) {

    }

    override fun onOtpCallFail() {

        mBinding.verificationProgress.visibility = View.GONE
    }

    ///////// LOGIN WITH OTP
    override fun onLoginWithOtpSuccess(loginOtpResponse: LoginWithOTPModel) {
        //(TAG, "onLoginWithOtpSuccess: loginOtpResponse: $loginOtpResponse")
        val status = loginOtpResponse.status.toString()
        val msg = loginOtpResponse.message.toString()

        if (status == "1" || msg.contentEquals("Success")) {
            val token = loginOtpResponse.token.toString()
            this.toast(hmLangValue["login_success_txt"].toString())

            saveData(this, getString(R.string.key_user_id), loginOtpResponse.userId.toString())
            saveData(this, getString(R.string.key_username), username)

            mBinding.verificationProgress.visibility = View.GONE

            val userResponse = loginOtpResponse.userResponse
            val hmProfile = AuroRepository().inflateProfileHashMap(this, userResponse!!)

            if (AuroRepository().validateProfileComplete(userResponse)) {
                saveData(
                    this,
                    getString(R.string.key_user_token),
                    token
                )
                val i = Intent(this, DashBoardActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
            } else {
                mBinding.flProfileContainer.visibility = View.VISIBLE
                startActivity(
                    Intent(
                        this,
                        ProfileActivity::class.java
                    ).putExtra(getString(R.string.key_lang_hm), hmLangValue)
                        .putExtra(getString(R.string.key_profile), hmProfile)
                        .putExtra(getString(R.string.key_phone_number), mPhoneNumber)
                        .putExtra(
                            getString(R.string.key_calling_fragment),
                            getString(R.string.fragment_password)
                        ).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        )
                )
            }


        } else {
            toast(hmLangValue["error_null_otp_response"].toString())

            mBinding.verificationProgress.visibility = View.GONE
        }
    }

    override fun onLoginFailure() {
        mBinding.verificationProgress.visibility = View.GONE
    }

    /**
     * ---------------------------------------LIFECYCLE CALLS---------------------------------------
     */
    override fun onResume() {
        super.onResume()
        setLanguage()
    }

    override fun onBackPressed() {
        finish()
    }


}