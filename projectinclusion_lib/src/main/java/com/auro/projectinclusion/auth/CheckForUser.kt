package com.pi.projectinclusion.auth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.auro.projectinclusion.model.WebinarBookingModel
import com.pi.projectinclusion.model.*

interface CheckForUser {
    fun onNetworkCall()

    //fun onUserSuccess(existingUserResponse: MutableLiveData<DuplicateUserModel>)//Disposed Temporarily
    fun onCoroutineUserSuccess(existingUserResponse: DuplicateUserModel)
    fun onUserCheckFail()
}

interface FragAuthListener {
    fun onNetworkFragStart()

    // fun onFragSuccess(loginResponse: MutableLiveData<LoginModel>) //Disposed Temporarily
    fun onCoroutineFragSuccess(loginResponse: LoginModel)
    fun onFragFail()
}

interface ResetPasswordListener {
    fun onNetworkCallStarted()
    fun onPasswordChangeSuccess(resetPassResponse: ResetPasswordModel) //Disposed Temporarily

    //fun onPasswordChangeSuccess(resetPassResponse: MutableLiveData<ResetPasswordModel>) //Disposed Temporarily
    fun onPasswordChangeFail()
}

interface SetPasswordListener {
    fun onNetworkCallStarted()
    fun onSetPasswordSuccess(setPasswordModel: SetPasswordModel) //Disposed Temporarily

    //fun onPasswordChangeSuccess(resetPassResponse: MutableLiveData<ResetPasswordModel>) //Disposed Temporarily
    fun onSetPasswordFail()
}

interface MyAuthListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onCoroutineApiSuccess(lang_response: List<LanguageModel>)
    fun onAuthApiFailure()
}

interface LangKeyListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onKeyListSuccess(lang_response: List<LanguageKeyModel>)
    fun onLangKeyApiFailure()
}

interface OtpSendListener {
    fun onOtpNetworkStart()

    //  fun onOtpCallSuccess(otp_response: MutableLiveData<SendOtpModel>)//Disposed Temporarily
    fun onOtpCallSuccess(otp_response: SendOtpModel)
    fun onOtpCallFail()
}

interface ProfileAuthListener {
    fun onProfileNetworkStarted()

    // fun onProfileApiSuccess(profile_response: MutableLiveData<List<ProfileModel>>)//Disposed Temporarily
    fun onCoroutineProfileApiSuccess(profile_response: List<ProfileModel>)
    fun onProfileApiFailure()
}


interface SaveProfileAuthListener {
    fun onProfileNetworkStarted()

    // fun onProfileApiSuccess(profile_response: MutableLiveData<List<ProfileModel>>)//Disposed Temporarily
    fun onSaveSuccess(profile_response: MutableLiveData<SaveProfileModel>)
    fun onProfileApiFailure()
}

interface SignUpAuthListener {
    fun onSignUpNetwork()
    fun onSignUpSuccess(signup_response: SignUpModel)

    // fun onSignUpSuccess(signup_response: MutableLiveData<SignUpModel>) ///Disposed Temporarily
    fun onSignUpFailure()
}

interface LoginWithOtpListener {
    fun onLoginWithOtpSuccess(loginOtpResponse: LoginWithOTPModel)

    // fun onSignUpSuccess(signup_response: MutableLiveData<SignUpModel>) ///Disposed Temporarily
    fun onLoginFailure()
}

interface VerifyOtpListener {
    fun onVerifyNetworkCall()
    fun onVerifyOtpSuccess(verify_otp_response: SendOtpModel)

    // fun onVerifyOtpSuccess(verify_otp_response: MutableLiveData<SendOtpModel>)//Disposed Temporarily
    fun onVerifyOtpFail()
}

interface BottomMenuListener {
    fun onMenuCallStart()
    fun onMenuCallSuccess(menu_response: BottomMenuModel)

    //  fun onMenuCallSuccess(menu_response: MutableLiveData<BottomMenuModel>)//Disposed Temporarily
    fun onMenuCallFail()
}

interface CertificateTypeListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onTypeCallSuccess(certificateTypeModel: List<CertificateTypeModel>)
    fun onTypeApiFailure()
}

interface CertificateListListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onListCallSuccess(certificateModel: CertificateModel)
    fun onListApiFail()
}

interface WebinarByLangListListener {
    fun onWebinarLangStarted()
    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onWebinarLangListSuccess(webinarResponse: WebinarResponse)
    fun onWebinarLangListFailure()
}
interface SlotIdListener {
    fun onSlotIdStarted()
    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onSlotIdSuccess(slotIdResponse: SlotIdResponse)
    fun onSlotIdFailure()
}

interface GenderListListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onListCallSuccess(GenderModel: List<GenderModel>)
    fun onGenderApiFailure()
}

interface StateListListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onStateListCallSuccess(stateModel: List<StateModel>)
    fun onStateApiFailure()
}

interface DistrictListListener {
    fun onNetworkCallStarted()

    //fun onApiSuccess(lang_response: MutableLiveData<List<LanguageModel>>) //Disposed Temporarily
    fun onDistrictListCallSuccess(districtModel: List<DistrictModel>)
    fun onDistrictApiFailure()
}

interface WebinarBookListener{
    fun onWebinarBookingStart()
    fun onWebinarSuccess(booking : WebinarBookingModel)
    fun onWebinarFailure()
}

interface MenuAdapterUpdate {
    fun onMenuAdapterUpdate()
}

interface ProfilePicUpdate {
    fun onProfileSaved(profilePicUrl: String, context: Context)
}

interface WebViewBackPressed {
    fun onWebViewBackPressed()
}

interface FragmentToFragmentBackPress {
    fun onFragmentBacked()
}