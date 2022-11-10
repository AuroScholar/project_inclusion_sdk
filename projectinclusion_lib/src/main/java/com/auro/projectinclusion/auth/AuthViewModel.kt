package com.pi.projectinclusion.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.projectinclusion.activity.internetError
import com.pi.projectinclusion.isNetwork
import com.pi.projectinclusion.model.LoginWithOTPModel
import com.pi.projectinclusion.model.SlotIdModel
import com.pi.projectinclusion.repository.AuroRepository
import com.pi.projectinclusion.toast
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    var mAuthListener: MyAuthListener? = null
    var mLangKeyListener: LangKeyListener? = null
    var mCertificateTypeListener: CertificateTypeListener? = null
    var mCertificateListListener: CertificateListListener? = null
    var mWebinarByLangListListener: WebinarByLangListListener? = null
    var mSlotIdListener: SlotIdListener? = null
    var mGenderListListener: GenderListListener? = null
    var mStateListListener: StateListListener? = null
    var mDistrictListListener: DistrictListListener? = null
    var mProAuthListener: ProfileAuthListener? = null
    var mSaveProListener: SaveProfileAuthListener? = null
    var mFragListener: FragAuthListener? = null
    var mResetListener: ResetPasswordListener? = null
    var mSetPasswordListener: SetPasswordListener? = null
    var mSignUpAuthListener: SignUpAuthListener? = null
    var mLoginWithOtpListener: LoginWithOtpListener? = null
    var mCheckUserListener: CheckForUser? = null
    var mSendOtpListener: OtpSendListener? = null
    var mVerifyOtpListener: VerifyOtpListener? = null
    var mBottomMenuListener: BottomMenuListener? = null
    var mWebinarBookListener : WebinarBookListener? = null

    var emailLogin: String? = ""
    var nameSignup: String? = ""
    var usernameSignup: String? = ""
    var passwordSignup: String? = ""
    var confirmPasswordSignup: String? = ""
    var oldPasswordReset: String? = ""
    var currPasswordReset: String? = ""
    var conCurrPasswordReset: String? = ""
    var editOtp1: String? = ""
    var editOtp2: String? = ""
    var editOtp3: String? = ""
    var editOtp4: String? = ""
    var editOtp5: String? = ""
    var editOtp6: String? = ""


    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getCoroutineLanguageData(context: Context) {
        viewModelScope.launch {
            try {
                val langResponse = AuroRepository().getCoroutineLanguageList()
                mAuthListener!!.onNetworkCallStarted()
                mAuthListener!!.onCoroutineApiSuccess(langResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mAuthListener!!.onAuthApiFailure()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Running Lang Key Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getLangKeyData(LanguageId: String, context: Context) {
        viewModelScope.launch {
            try {
                val langResponse = AuroRepository().getLangKeyList(LanguageId)
                //  Log.d(TAG, "getLangKeyData: lang_respones: " + langResponse)
                mLangKeyListener!!.onNetworkCallStarted()
                mLangKeyListener!!.onKeyListSuccess(langResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mLangKeyListener!!.onLangKeyApiFailure()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Running Profile Api Call on IO dispatcher thread
     */
    fun getCoroutineProfileData(context: Context) {
        viewModelScope.launch {
            try {
                val profileResponse = AuroRepository().getCoroutineProfileList()
                mProAuthListener!!.onProfileNetworkStarted()
                mProAuthListener!!.onCoroutineProfileApiSuccess(profileResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mProAuthListener!!.onProfileApiFailure()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Running Login Api Call on IO dispatcher thread
     * @param username : username given by user
     * @param password : password given by user
     * @param userType : userType integer selected by user
     */
    fun getCoroutineLogin(
        username: String,
        password: String,
        userType: String,
        context: Context,
    ) {
        viewModelScope.launch {
            try {
                val loginResponse =
                    AuroRepository().completeCoroutineLogin(username, password, userType)
                Log.d(TAG, "getLogin: loginResponse: $loginResponse")
                mFragListener!!.onNetworkFragStart()
                mFragListener!!.onCoroutineFragSuccess(loginResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mFragListener!!.onFragFail()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Running Check User Api Call on IO dispatcher thread
     * @param username : username given by user
     * @param userType : userType integer selected by user
     */
    fun coroutineCheckForExistingUser(
        username: String,
        userType: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val existingUserResponse = AuroRepository().coroutineCheckUser(username, userType)
                mCheckUserListener?.onNetworkCall()
                mCheckUserListener?.onCoroutineUserSuccess(existingUserResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mCheckUserListener?.onUserCheckFail()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Sending Otp to mobile number on IO dispatcher thread
     * @param mobile : mobile number given by user
     */
    fun sendCoroutineOtpMsg(mobile: String, context: Context) {
        viewModelScope.launch {
            try {
                val otpResponse = AuroRepository().sendCoroutineOtpFromMobile(mobile)
                mSendOtpListener?.onOtpNetworkStart()
                mSendOtpListener?.onOtpCallSuccess(otpResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mSendOtpListener?.onOtpCallFail()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Verify Otp to mobile number on IO dispatcher thread
     * @param phone : mobile number given by user
     * @param otp: otp given by user
     */
    fun verifyTheCoroutineOtp(phone: String, otp: String, context: Context) {
        viewModelScope.launch {
            try {
                val verifyOtpResponse = AuroRepository().onCoroutineVerifyOtp(phone, otp)
                mVerifyOtpListener?.onVerifyNetworkCall()
                mVerifyOtpListener?.onVerifyOtpSuccess(verifyOtpResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mVerifyOtpListener?.onVerifyOtpFail()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Create account  on IO dispatcher thread
     * @param username : username given by user
     * @param userType : userType integer selected by user
     * @param password : password given by user
     */
    fun coroutineSignUpUser(
        username: String,
        userType: String,
        password: String,
        mobileNo: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val signupResponse =
                    AuroRepository().coroutineSignup(username, userType, password, mobileNo)
                mSignUpAuthListener!!.onSignUpNetwork()
                mSignUpAuthListener!!.onSignUpSuccess(signupResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                mSignUpAuthListener?.onSignUpFailure()
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Create account  on IO dispatcher thread
     * @param username : username given by user
     * @param userTypeId : userType integer selected by user
     * @param otp : otp given by user
     */
    fun loginWithOtp(username: String, otp: Int, userTypeId: Int, context: Context) {
        viewModelScope.launch {
            try {
                val loginResponse = AuroRepository().loginWithOtp(username, otp, userTypeId)
                mLoginWithOtpListener!!.onLoginWithOtpSuccess(loginResponse)
            } catch (e: Exception) {
                mLoginWithOtpListener!!.onLoginFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Get Bottom Menu on IO dispatcher thread
     */
    fun getCoroutineMenu(context: Context) {
        viewModelScope.launch {
            try {
                val menuResponse = AuroRepository().getCoroutineBottomMenu()
                mBottomMenuListener?.onMenuCallStart()
                mBottomMenuListener?.onMenuCallSuccess(menuResponse)
            } catch (e: Exception) {
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Reset Password api
     * @param userId : userId of user
     * @param oldPassword : current password of user
     * @param newPassword : new password given by user
     */
    fun resetCoroutinePassword(
        userId: String,
        oldPassword: String,
        newPassword: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val resetResponse =
                    AuroRepository().resetCoroutinePassword(userId, oldPassword, newPassword)
                Log.d(TAG, "getLogin: loginResponse: $resetResponse")
                mResetListener!!.onNetworkCallStarted()
                mResetListener!!.onPasswordChangeSuccess(resetResponse)
            } catch (e: Exception) {
                mResetListener!!.onPasswordChangeFail()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Reset Password api
     * @param userId : userId of user
     * @param oldPassword : current password of user
     * @param newPassword : new password given by user
     */
    fun setPassword(userId: String, newPassword: String, context: Context) {
        viewModelScope.launch {
            try {
                val resetResponse =
                    AuroRepository().setPassword(userId, newPassword)
                Log.d(TAG, "getLogin: loginResponse: $resetResponse")
                mSetPasswordListener!!.onNetworkCallStarted()
                mSetPasswordListener!!.onSetPasswordSuccess(resetResponse)
            } catch (e: Exception) {
                mSetPasswordListener!!.onSetPasswordFail()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Added By Sahil this and below
     */
    fun getCertificateTypes(context: Context) {
        viewModelScope.launch {

            try {
                val certificateTypeResponse = AuroRepository().getCertificateTypeList()
                mCertificateTypeListener!!.onNetworkCallStarted()
                mCertificateTypeListener!!.onTypeCallSuccess(certificateTypeResponse)
            } catch (e: Exception) {
                mCertificateTypeListener!!.onTypeApiFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }
    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getCertificateList(
        userId: String,
        certificateId: String,
        authorization: String,
        context: Context
    ) {

        viewModelScope.launch {
            try {
                Log.d(TAG, "getCertificateList: certificateId: " + certificateId)
                val certificateResponse =
                    AuroRepository().getCertificatesList(userId, certificateId, authorization)
                mCertificateListListener!!.onNetworkCallStarted()
                mCertificateListListener!!.onListCallSuccess(certificateResponse)
            } catch (e: Exception) {
                mCertificateListListener!!.onListApiFail()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getWebinarListByLang(
        langId: Int,
        authorization: String,
        context: Context
    ) {

        viewModelScope.launch {
            try {
                val webinarResponse =
                    AuroRepository().getWebinarListByLang(langId, authorization)
                mWebinarByLangListListener!!.onWebinarLangStarted()
                mWebinarByLangListListener!!.onWebinarLangListSuccess(webinarResponse)
            } catch (e: Exception) {
                mWebinarByLangListListener!!.onWebinarLangListFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getSlotIdByWebinarId(
        webinarId: Int,
        authorization: String,
        context: Context
    ) {

        viewModelScope.launch {
            try {
                val slotIdResponse =
                    AuroRepository().getSlotIdByWebinarId(webinarId, authorization)
                mSlotIdListener!!.onSlotIdStarted()
                mSlotIdListener!!.onSlotIdSuccess(slotIdResponse)
            } catch (e: Exception) {
                mSlotIdListener!!.onSlotIdFailure()
                Log.d(TAG, "getSlotIdByWebinarId: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getGenderList(context: Context) {

        viewModelScope.launch {
            try {
                val genderResponse = AuroRepository().getGendersList()
                mGenderListListener!!.onNetworkCallStarted()
                mGenderListListener!!.onListCallSuccess(genderResponse)
            } catch (e: Exception) {
                mGenderListListener!!.onGenderApiFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getStateList(context: Context) {

        viewModelScope.launch {
            try {
                val stateResponse = AuroRepository().getStateList()
                mStateListListener!!.onNetworkCallStarted()
                mStateListListener!!.onStateListCallSuccess(stateResponse)
            } catch (e: Exception) {
                mStateListListener!!.onStateApiFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Lang Api Call on IO dispatcher thread
     * Added By Sahil this and below
     */
    fun getDistrictList(stateId: String, context: Context) {

        viewModelScope.launch {
            try {
                val stateResponse = AuroRepository().getDistrictList(stateId)
                mDistrictListListener!!.onNetworkCallStarted()
                mDistrictListListener!!.onDistrictListCallSuccess(stateResponse)
            } catch (e: Exception) {
                mDistrictListListener!!.onDistrictApiFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }

    /**
     * Running Long API call on IO dispatcher thread
     * Added by Rishabh
     * @param tokenID : The Auth Token
     * @param params : The Request params
     * @param context : contacts the context of the page
     * */
    fun getWebinarBookView(tokenID : String,
            params : HashMap<String,Any>,
            context: Context)
    {
        viewModelScope.launch {
            try {
                val bookingResponse = AuroRepository().getWebinarBooking(tokenID,params)
                mWebinarBookListener!!.onWebinarBookingStart()
                mWebinarBookListener!!.onWebinarSuccess(bookingResponse)
            }catch (e:Exception){
                mWebinarBookListener!!.onWebinarFailure()
                Log.d(TAG, "getCoroutineProfileData: error received: " + e.message)
                if (!isNetwork(context))
                    context.toast(internetError)
            }
        }

    }


//Disposed Temporarily
    /*fun getLanguageData() {
        val langResponse = AuroRepository().getLanguageList()
        mAuthListener!!.onNetworkCallStarted()
        mAuthListener!!.onApiSuccess(langResponse)
    }


    fun getProfileData() {
        val profileResponse = AuroRepository().getProfileList()
        mProAuthListener!!.onProfileNetworkStarted()
        mProAuthListener!!.onProfileApiSuccess(profileResponse)
        mProAuthListener!!.onProfileApiFailure()
    }

    fun getLogin(username: String, password: String, userType: String) {
        val loginResponse = AuroRepository().completeLogin(username, password, userType)
        Log.d(TAG, "getLogin: loginResponse: $loginResponse")
        mFragListener!!.onNetworkFragStart()
        mFragListener!!.onFragSuccess(loginResponse)
}
        fun checkForExistingUser(username: String, userType: String) {
        val existingUserResponse = AuroRepository().checkUser(username, userType)
        mCheckUserListener?.onNetworkCall()
        mCheckUserListener?.onUserSuccess(existingUserResponse)
    }  fun sendOtpMsg(mobile: String) {
        val otpResponse = AuroRepository().sendOtpFromMobile(mobile)
        mSendOtpListener?.onOtpNetworkStart()
        mSendOtpListener?.onOtpCallSuccess(otpResponse)
    }fun verifyTheOtp(phone: String, otp: String) {
        val verifyOtpResponse = AuroRepository().onVerifyOtp(phone, otp)
        mVerifyOtpListener?.onVerifyNetworkCall()
        mVerifyOtpListener?.onVerifyOtpSuccess(verifyOtpResponse)
    }
    fun signUpUser(username: String, userType: String, password: String) {
        val signupResponse = AuroRepository().signup(username, userType, password)
        mSignUpAuthListener!!.onSignUpNetwork()
        mSignUpAuthListener!!.onSignUpSuccess(signupResponse)

   }

    fun getMenu() {
            val menuResponse = AuroRepository().getBottomMenu()
            mBottomMenuListener?.onMenuCallStart()
            mBottomMenuListener?.onMenuCallSuccess(menuResponse)
    }

     fun resetPassword(userId: String, oldPassword: String, newPassword: String) {
        val resetResponse = AuroRepository().resetPassword(userId, oldPassword, newPassword)
        Log.d(TAG, "getLogin: loginResponse: $resetResponse")
        mResetListener!!.onNetworkCallStarted()
        mResetListener!!.onPasswordChangeSuccess(resetResponse)
    }


   */
}