package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.LoginActivity
import com.pi.projectinclusion.activity.OtpVerificationActivity
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.CheckForUser
import com.pi.projectinclusion.auth.OtpSendListener
import com.pi.projectinclusion.databinding.FragmentEmailBinding
import com.pi.projectinclusion.model.DuplicateUserModel
import com.pi.projectinclusion.model.SendOtpModel

private const val TAG = "EmailFragment"

class EmailFragment : Fragment(), CheckForUser, OtpSendListener {
    private lateinit var mContext: Context

    // private var mUsername: String = ""
    private var isLoginWithOtp: Boolean = false
    private lateinit var loginActivity: LoginActivity
    private lateinit var mBinding: FragmentEmailBinding
    private lateinit var mViewModel: AuthViewModel
    private var isPhoneNumber: Boolean = false
    private lateinit var mUserType: String
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var v: View


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ")
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email, container, false)
        mContext = requireActivity()
        v = mBinding.root
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel
        mViewModel.mCheckUserListener = this
        mViewModel.mSendOtpListener = this
        loginActivity = (activity as LoginActivity)
        setLanguage()
        mUserType = getData(mContext, mContext.getString(R.string.key_userTypeId))

        makeLoginWithOtp()
        loginWithOtp()
        mBinding.loginPhoneButton.setOnClickListener {
            isLoginWithOtp = false
            loginCheck()
            //Log.e("test", checkIfUsernameValid(mViewModel.emailLogin!!.trim()).toString())
        }

        mBinding.etLoginUsername.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }
            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }
        }

       /* mBinding.etLoginUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val c = s.toString()
                if (c == " ") {
                    //backspace pressed
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })*/

        return v
    }

    private fun loginWithOtp() {
        mBinding.tvLoginWithOtp.setOnClickListener {
            isLoginWithOtp = true
            loginCheck()
        }
    }

    private fun makeLoginWithOtp() {
        mBinding.etLoginUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isNotEmpty()) {
                    mBinding.vLeft.visibility = View.VISIBLE
                    mBinding.vRight.visibility = View.VISIBLE
                    mBinding.tvOr.visibility = View.VISIBLE
                    mBinding.tvLoginWithOtp.visibility = View.VISIBLE
                } else {

                    mBinding.vLeft.visibility = View.GONE
                    mBinding.vRight.visibility = View.GONE
                    mBinding.tvOr.visibility = View.GONE
                    mBinding.tvLoginWithOtp.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun loginCheck() {
        Log.d(TAG, "onCreateView: email login: " + mViewModel.emailLogin)
        if (mViewModel.emailLogin!!.isNotEmpty())
        {
            if (mViewModel.emailLogin!!.contains(" "))
                {
                mContext.toast("No Space allowed in Username")
            }
            else{
                if (mViewModel.emailLogin!!.matches(Regex("[0-9]+")))
                {
                    if (mViewModel.emailLogin!!.length != 10) {
                        mViewModel.emailLogin!!.trim()
                        isPhoneNumber = false
                        mBinding.emailProgress.visibility = View.VISIBLE
                        mViewModel.coroutineCheckForExistingUser(
                            mViewModel.emailLogin!!.trim(),
                            mUserType,
                            mContext
                        )
                    } else {
                        mViewModel.emailLogin!!.trim()
                        isPhoneNumber = true
                        mBinding.emailProgress.visibility = View.VISIBLE
                        mViewModel.coroutineCheckForExistingUser(
                            mViewModel.emailLogin!!.trim(),
                            mUserType,
                            mContext
                        )
                    }
                }
                else {
                    mViewModel.emailLogin!!.trim()
                    isPhoneNumber = false
                    mBinding.emailProgress.visibility = View.VISIBLE
                    mViewModel.coroutineCheckForExistingUser(
                        mViewModel.emailLogin!!.trim(),
                        mUserType,
                        mContext
                    )
                }
            }

        } else {
            mContext.toast(hmLangValue["phone_or_username"].toString())
        }
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

        mBinding.selectText.text = hmLangValue["log_signup_txt"]
        mBinding.logBttnTxt.text = hmLangValue["cont_text"]
        //mBinding.etLoginUsername.hint = hmLangValue["phone_or_username"]
        mBinding.tvOr.text = hmLangValue["key_or"]
        mBinding.tvLoginWithOtp.text = hmLangValue["key_login_with_otp"]
    }


    override fun onNetworkCall() {
        Log.d(TAG, "onNetworkCall: ")
    }

    override fun onCoroutineUserSuccess(existingUserResponse: DuplicateUserModel) {
        Log.d(TAG, "onUserSuccess: ")
        val isDuplicateUser = existingUserResponse.isDuplicateUser.toString()
        val message = existingUserResponse.message.toString()
        Log.d(TAG, "onCoroutineUserSuccess: message: $message")
        Log.d(TAG, "onUserSuccess: isDuplicateUser: $isDuplicateUser")
        Log.d(
            TAG,
            "onUserSuccess: mobile no: ${existingUserResponse.response?.mobileNo.toString()}"
        )
        mBinding.emailProgress.visibility = View.GONE
        if (isDuplicateUser == "0") {


            // Log.d(TAG, "onCoroutineUserSuccess: isLoginWithotp: $isLoginWithOtp")
            //Log.d(TAG, "onCoroutineUserSuccess: mUsrname: " + mViewModel.emailLogin!!.trim())
            if (isLoginWithOtp) {
                mContext.toast(hmLangValue["error_registered_mobile"].toString())
            } else {
                if (isPhoneNumber) {
                    mViewModel.sendCoroutineOtpMsg(
                        mViewModel.emailLogin!!.toString(),
                        mContext
                    )

                    val intent = Intent(activity, OtpVerificationActivity::class.java)
                    intent.putExtra(
                        mContext.getString(R.string.key_phone_number),
                        mViewModel.emailLogin!!.toString().trim()
                    )
                    intent.putExtra(
                        mContext.getString(R.string.key_userTypeId),
                        mUserType
                    )
                    startActivity(intent)
                } else {
                    mContext.toast(hmLangValue["error_existing_username"].toString())
                }

            }
        } else {
            // Log.d(TAG, "onCoroutineUserSuccess: isDuplicateUser: $isDuplicateUser")
            if (isLoginWithOtp) {
                mViewModel.sendCoroutineOtpMsg(
                    existingUserResponse.response?.mobileNo.toString(),
                    mContext
                )
                val intent = Intent(activity, OtpVerificationActivity::class.java)
                intent.putExtra(
                    mContext.getString(R.string.key_phone_number),
                    existingUserResponse.response?.mobileNo.toString()
                )
                intent.putExtra(
                    mContext.getString(R.string.key_userTypeId),
                    mUserType
                )
                intent.putExtra(
                    mContext.getString(R.string.key_username),
                    mViewModel.emailLogin.toString().trim()
                )
                intent.putExtra(
                    mContext.getString(R.string.key_calling_fragment),
                    mContext.getString(R.string.fragment_email)
                )
                startActivity(intent)
            } else {

                val userId = existingUserResponse.response!!.id
                mBinding.emailProgress.visibility = View.GONE
                //      activity?.toast(message)
                val pswdFrag = PasswordFragment()
                val bundle = Bundle()
//                Log.d(
//                    TAG,
//                    "onCoroutineUserSuccess: exisitng user response; $existingUserResponse"
//                )
                bundle.putString(
                    mContext.getString(R.string.key_phone_number),
                    existingUserResponse.response?.mobileNo.toString()
                )
                bundle.putString(
                    mContext.getString(R.string.key_username),
                    mViewModel.emailLogin.toString().trim()
                )
                bundle.putString(mContext.getString(R.string.key_user_id), userId)
                bundle.putString(
                    mContext.getString(R.string.key_userTypeId),
                    mUserType
                )

                pswdFrag.arguments = bundle
                fragmentManager?.beginTransaction()!!
                    .replace(R.id.frame_frag_container, pswdFrag, "passwordFrag")
                    .addToBackStack(null).commit()
            }
        }
    }

    override fun onUserCheckFail() {
        Log.d(TAG, "onUserCheckFail: api call failed")
        /*if (isNetwork(mContext)) {
            mViewModel.coroutineCheckForExistingUser(
                mViewModel.emailLogin!!.trim(),
                mUserType,
                mContext
            )*/
        mBinding.emailProgress.visibility = View.GONE
        // }
    }


    override fun onOtpNetworkStart() {
        Log.d(TAG, "onOtpNetworkStart: ")
    }

    override fun onOtpCallSuccess(otp_response: SendOtpModel) {
        Log.d(TAG, "onOtpCallSuccess: ")


    }

    override fun onOtpCallFail() {

        Log.d(TAG, "onOtpCallFail: api call failed")
        mBinding.emailProgress.visibility = View.GONE
    }

    //Disposed Temporarily
    /*override fun onUserSuccess(existingUserResponse: MutableLiveData<DuplicateUserModel>)
    {
        Log.d(TAG, "onUserSuccess: ")
        existingUserResponse.observe(this) {
            val isDuplicateUser = it.isDuplicateUser.toString()
            var message = it.message.toString()
            Log.d(TAG, "onUserSuccess: isDuplicateUser: $isDuplicateUser")
            if (isDuplicateUser == "0") {
                mBinding.emailProgress.visibility = View.GONE
                mViewModel.sendOtpMsg(mUsername)
                //   activity?.toast(message)
                val intent = Intent(activity, OtpVerificationActivity::class.java)
                intent.putExtra("phone_no", mUsername)
                intent.putExtra("usertype", (activity as LoginActivity).mUserTypeId)
                startActivity(intent)

            } else {
                mBinding.emailProgress.visibility = View.GONE
                //      activity?.toast(message)
                val pswdFrag = PasswordFragment()
                val bundle = Bundle()
                bundle.putString("phone", mBinding.editPhone.text.toString())
                bundle.putString("usertype", (activity as LoginActivity).mUserTypeId)
                pswdFrag.arguments = bundle
                fragmentManager?.beginTransaction()!!
                    .replace(R.id.frame_frag_container, pswdFrag, "passwordFrag")
                    .addToBackStack(null).commit()
            }
        }
    }*/

}