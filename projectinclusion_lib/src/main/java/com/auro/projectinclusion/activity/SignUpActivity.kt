package com.pi.projectinclusion.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.CheckForUser
import com.pi.projectinclusion.auth.SignUpAuthListener
import com.pi.projectinclusion.databinding.ActivitySignUpBinding
import com.pi.projectinclusion.model.DuplicateUserModel
import com.pi.projectinclusion.model.SignUpModel
import com.pi.projectinclusion.repository.AuroRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SignupPActivity"

class SignUpActivity : AppCompatActivity(), SignUpAuthListener, CheckForUser {
    private lateinit var mBinding: ActivitySignUpBinding
    private lateinit var mUsertype: String
    private lateinit var mPhoneNumber: String
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /**
         * NAVIGATION TO METHODS AND FUNCTIONS:
         * @return
         * [onContinue]
         * [validatePassword]
         * [setLanguage]
         * [getIntents]
         * [setBackNavigation]
         * [initViewModel]
         * [navigateToProfileActivity]
         * [onSignUpSuccess]
         * [onSignUpFailure]
         * [onSignUpNetwork]
         * [onCoroutineUserSuccess]
         * [onUserCheckFail]
         * [onBackPressed]
         * [onResume]
         */

        Log.d(TAG, "onCreate:")
        AuroRepository().setStatusBarColor(this)
        initViewModel()
        getIntents()
        setLanguage()
        setBackNavigation()
        onContinue()
    }


    /**
     * -------------------------------------- CUSTOM METHODS-----------------------------
     */

    private fun onContinue() {
        mBinding.signupContinueButton.setOnClickListener {
            viewModel.coroutineCheckForExistingUser(viewModel.usernameSignup!!, mUsertype, this)
        }
    }

    private fun validatePassword(): Boolean {
        if (viewModel.passwordSignup!!.length >= 6) {
            return true
        }
        return false
    }

    private fun setLanguage() {
        //  mBinding.tvGreeting1.text = hmLangValue["sign_up"]
        //  mBinding.tv2.text = hmLangValue["signup_header"]
        val langId = getData(this, getString(R.string.key_lang_id))
        if (langId == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")

        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))

        mBinding.tvUsername.text = hmLangValue["field_username"]
        mBinding.tvGreeting1.text = hmLangValue["key_sign_up"]
        mBinding.tv2.text = hmLangValue["signup_header"]
        mBinding.tvPass.text = hmLangValue["password_txt"]
        mBinding.tvConfirmPass.text = hmLangValue["confirm_pass_txt"]
        mBinding.editUsername.hint = hmLangValue["error_enter_username"]
        mBinding.etPass.hint = hmLangValue["error_enter_password"]
        mBinding.etPassConfirm.hint = hmLangValue["hint_reenter_pass"]
        mBinding.crtAcntTxt.text = hmLangValue["create_accnt_txt"]
        //mBinding.crtAcntTxt.text = hmLangValue["create_accnt"]

        //mBinding.editUsername.setText(mPhoneNumber)
        viewModel.usernameSignup = mPhoneNumber
    }

    private fun getIntents() {
        mUsertype = intent.getStringExtra(getString(R.string.key_userTypeId)).toString()
        mPhoneNumber = intent.getStringExtra(getString(R.string.key_phone_number)).toString()
    }

    private fun setBackNavigation() {
        mBinding.ivBackUser.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mSignUpAuthListener = this
        viewModel.mCheckUserListener = this
    }

    private fun navigateToProfileActivity() {
        startActivity(
            Intent(
                this,
                ProfileActivity::class.java
            )
                .putExtra(getString(R.string.password_txt), viewModel.passwordSignup)
                .putExtra(getString(R.string.key_phone_number), mPhoneNumber)
                .putExtra(
                    getString(R.string.key_calling_fragment),
                    getString(R.string.fragment_password)
                ).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                )

        )
    }

    override fun onSignUpNetwork() {
        Log.d(TAG, "onSignUpNetwork: ")
    }


    /**
     * -------------------------------------- OVERRIDE API LISTENER IMPLEMENTATIONS------------------------------
     */

    override fun onSignUpSuccess(signup_response: SignUpModel) {
        Log.d(TAG, "onSignUpSuccess: ")
        mBinding.signupProgress.visibility = View.GONE
        val status = signup_response.status.toString()
        val msg = signup_response.message.toString()
        // mBinding.pbSignUp.visibility = View.GONE
        if (status == "1" && msg.contentEquals("Success")) {
            val userId = signup_response.response?.id.toString()

            saveData(this, getString(R.string.key_user_id), userId)
            saveData(this, getString(R.string.key_phone_number), mPhoneNumber)
            saveData(this, getString(R.string.key_username), viewModel.usernameSignup!!)
            Log.d(
                TAG, "onSignUpSuccess: userid " + getData(
                    this,
                    getString(R.string.key_user_id)
                ).toInt()
            )
            val mContext = this
            CoroutineScope(Dispatchers.IO).launch {
                AuroRepository().saveBearerToken(
                    mPhoneNumber, viewModel.passwordSignup!!, getData(
                        mContext,
                        getString(R.string.key_userTypeId)
                    ),
                    mContext
                )
            }
            Log.d(TAG, "onSignUpSuccess: $userId")
            //toast(getString(R.string.error_internet_connection))
            // CHECK FOR IS PROFILE COMPLETED PARAMETER
            mBinding.flSignUpContainer.visibility = View.VISIBLE
            navigateToProfileActivity()


        } else {
            toast(getString(R.string.error_user_exists))

        }
    }

    override fun onSignUpFailure() {

        Log.d(TAG, "onSignUpFailure: api call failed")
        mBinding.signupProgress.visibility = View.GONE
    }

    override fun onNetworkCall() {
    }

    override fun onCoroutineUserSuccess(existingUserResponse: DuplicateUserModel) {

        val isDuplicateUser = existingUserResponse.isDuplicateUser.toString()
        val message = existingUserResponse.message.toString()
        if (isDuplicateUser == "0") {
            // mBinding.editUsername.setError("Username Available")
            //     mBinding.editUsername.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.check,0)
            mBinding.signupProgress.visibility = View.VISIBLE
            /*viewModel.coroutineSignUpUser(
                viewModel.usernameSignup!!,
                mUsertype,
                viewModel.passwordSignup!! // DISPOSED TEMPORARILY
            )*/
            if (viewModel.usernameSignup.isNullOrEmpty()) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_enter_username"].toString())
            } else if (viewModel.usernameSignup!!.startsWith(" ")) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_space_username"].toString())
            }else if (viewModel.usernameSignup!!.contains(" ")) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_space_username"].toString())
            } else if (viewModel.usernameSignup!!.length < 5) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_length_username"].toString())
            }
            /*To check if username is in valid conditions*/
            else if(!checkIfUsernameValid(viewModel.usernameSignup!!))
            {
                mBinding.signupProgress.visibility = View.GONE
                toast("Use Upper case character , Lower case character and special character @_")
            }
            else if (viewModel.passwordSignup.isNullOrEmpty()) {

                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_enter_password"].toString())
            }
            /*To not allow use of white space in password*/
            else if (viewModel.passwordSignup!!.contains(" ")) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_space_username"].toString())
            }
            /*Adding for Confirm Pass*/
            else if (viewModel.confirmPasswordSignup.isNullOrEmpty()) {

                mBinding.signupProgress.visibility = View.GONE
                toast("Please enter confirm password")
            //toast(hmLangValue["error_enter_password"].toString())
            }
            else if (viewModel.confirmPasswordSignup!!.trim().isEmpty()) {
                mBinding.signupProgress.visibility = View.GONE
                toast("Confirm password cannot be blank spaces")
                //toast(hmLangValue["error_blank_pass"].toString())
            }
            else if (!validatePassword()) {
                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["pwd_validation_min_char"].toString())
            } else if (viewModel.confirmPasswordSignup != viewModel.passwordSignup) {

                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_passwords_not_match"].toString())
            } else if (viewModel.passwordSignup!!.trim().isEmpty()) {

                mBinding.signupProgress.visibility = View.GONE
                toast(hmLangValue["error_blank_pass"].toString())
            } else {
                mBinding.signupProgress.visibility = View.VISIBLE
                viewModel.coroutineSignUpUser(
                    viewModel.usernameSignup!!,
                    mUsertype,
                    viewModel.passwordSignup!!,
                    mPhoneNumber, this
                )
            }

        } else {
            toast(message)
        }
    }

    override fun onUserCheckFail() {
    }


    /**
     * -------------------------------------- LIFECYCLE AND OVERRIDE FUNCTIONS------------------------------
     */
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(
            this,
            LoginActivity::class.java
        ).putExtra("userTypeId", getData(this, "userTypeId"))


        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()

    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }

}