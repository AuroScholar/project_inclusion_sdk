package com.pi.projectinclusion.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.ResetPasswordListener
import com.pi.projectinclusion.auth.SetPasswordListener
import com.pi.projectinclusion.databinding.ActivityResetPasswordBinding
import com.pi.projectinclusion.model.ResetPasswordModel
import com.pi.projectinclusion.model.SetPasswordModel

//CODED BY SAHIL
private const val TAG = "ResetPasswordActivity"

class ResetPasswordActivity : AppCompatActivity(), ResetPasswordListener, SetPasswordListener {
    private lateinit var mContext: Context
    private lateinit var mBinding: ActivityResetPasswordBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var userId: String
    private var isForgotPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this


        initViewModel()
        userId = getData(this, getString(R.string.key_user_id))


        setLanguage()
        getIntents()


        mBinding.cvChangePass.setOnClickListener {
            val newPass = viewModel.currPasswordReset
            if (checkNullField()) {
                mBinding.pbReset.visibility = View.VISIBLE
                if (isForgotPassword) {
                    viewModel.setPassword(
                        userId,
                        newPass!!,
                        this
                    )


                } else {
                    viewModel.oldPasswordReset?.let { it1 ->
                        viewModel.currPasswordReset?.let { it2 ->
                            viewModel.resetCoroutinePassword(
                                userId,
                                it1,
                                it2, this
                            )
                        }
                    }
                }
            }
        }

        mBinding.cvBackResetPass.setOnClickListener {
            finish()
        }

    }

    private fun getIntents() {
        if (intent.getStringExtra(getString(R.string.key_user_id)) != null)
            userId = intent.getStringExtra(getString(R.string.key_user_id)).toString()
        if (intent.getStringExtra(getString(R.string.key_calling_fragment)) != null) {
            if (intent.getStringExtra(getString(R.string.key_calling_fragment)) == getString(R.string.activity_otp_verification)) {
                isForgotPassword = true
                mBinding.llCurrPass.visibility = View.GONE
                mBinding.tvCurrPass.visibility = View.GONE
                mBinding.tvTitlePass.text = hmLangValue["forget_pswd"]
            } else {

                mBinding.llCurrPass.visibility = View.VISIBLE
                mBinding.tvCurrPass.visibility = View.VISIBLE
                mBinding.tvTitlePass.text = hmLangValue["change_password"]
            }
        }
    }


    private fun setLanguage() {
        val langId = getData(mContext, mContext.getString(R.string.key_lang_id))
        if (langId == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")

        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))

        mBinding.tvCurrPass.text = hmLangValue["enter_current_password"]
        mBinding.etCurrPass.hint = hmLangValue["old_pass_txt"]
        mBinding.tvNewPass.text = hmLangValue["enter_a_new_password"]
        mBinding.etNewPass.hint = hmLangValue["new_password_txt"]
        mBinding.tvConNewPass.text = hmLangValue["confirm_new_password"]
        mBinding.etNewPassConfirm.hint = hmLangValue["confirm_pass_txt"]
        mBinding.crtAcntTxt.text = hmLangValue["change_password"]
    }

    private fun checkNullField(): Boolean {
        val validate = true
        if (!isForgotPassword) {
            if (TextUtils.isEmpty(mBinding.etCurrPass.text)) {
                // validate = true
                toast(hmLangValue["error_old_password"].toString())
                return false
            }
        }
        if (TextUtils.isEmpty(mBinding.etNewPass.text)) {
            // validate = true
            // } else {
            toast(hmLangValue["error_new_password"].toString())
            return false
        }
        if (mBinding.etNewPass.text.trim().isEmpty()) {
            // validate = true
            // } else {
            toast(hmLangValue["error_blank_pass"].toString())
            return false
        }
        if (mBinding.etNewPass.text.contains(" ")) {
            // validate = true
            // } else {
            toast("White space not allowed")
            return false
        }
        if (TextUtils.isEmpty(mBinding.etNewPassConfirm.text)) {
            toast(hmLangValue["error_reenter_new_password"].toString())
            return false
        }
        Log.d(TAG, "checkNullField: new: " + mBinding.etNewPass.text)
        Log.d(TAG, "checkNullField: con new: " + mBinding.etNewPassConfirm.text)
        if (mBinding.etNewPass.text.toString() != mBinding.etNewPassConfirm.text.toString()) {
            toast(hmLangValue["error_passwords_not_match"].toString())
            return false
        }
        if (mBinding.etNewPass.text.length < 6) {
            toast(hmLangValue["pwd_validation_min_char"].toString())
            return false
        }
        return validate
    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mResetListener = this
        viewModel.mSetPasswordListener = this
    }

    override fun onNetworkCallStarted() {
    }

    override fun onSetPasswordSuccess(setPasswordModel: SetPasswordModel) {

        if (setPasswordModel.result == true) {
            if (isForgotPassword) {
                toast(hmLangValue["pwd_chng_success"].toString())

                val i = Intent(
                    this,
                    LoginActivity::class.java
                ).putExtra(
                    getString(R.string.key_userTypeId),
                    getData(this, getString(R.string.key_userTypeId))
                )

                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finish()
            }
        } else {
            toast(hmLangValue["pwd_chng_fail"].toString())
        }
    }

    override fun onSetPasswordFail() {
        Log.d(TAG, "onSetPasswordFail: api call fail")
        if (isNetwork(this)) {
            viewModel.currPasswordReset?.let { it1 ->
                viewModel.setPassword(
                    userId,
                    it1,
                    this
                )
            }
        }
    }

    override fun onPasswordChangeSuccess(resetPassResponse: ResetPasswordModel) {
        val result = resetPassResponse.result
        Toast.makeText(mContext, result?.message, Toast.LENGTH_SHORT)
            .show()
        if (isForgotPassword) {
            val intent = Intent(mContext, DashBoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_left_first,
                R.anim.slide_out_left_first
            )
        } else {
            finish()
        }
        mBinding.pbReset.visibility = View.GONE

    }

    //Disposed Temporarily
//    override fun onPasswordChangeSuccess(resetPassResponse: MutableLiveData<ResetPasswordModel>) {
//        Toast.makeText(mContext, "Password Changed Successfully", Toast.LENGTH_SHORT).show()
//    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }

    override fun onPasswordChangeFail() {
        Log.d(TAG, "onLangKeyApiFailure: api call failed")
        if (isNetwork(this)) {
            viewModel.oldPasswordReset?.let { it1 ->
                viewModel.currPasswordReset?.let { it2 ->
                    viewModel.resetCoroutinePassword(
                        userId,
                        it1,
                        it2, this
                    )
                }
            }
        }
    }
}