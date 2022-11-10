package com.pi.projectinclusion.url


import com.auro.projectinclusion.model.WebinarBookingModel
import com.pi.projectinclusion.model.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


interface ServiceApi {


    @POST("Login/UserAuthentication")
    suspend fun getAuthToken(
        @Body params: HashMap<String, String>
    ): Response<UserAuthenticationModel>

    companion object {
        operator fun invoke(): ServiceApi {
            /*return Retrofit.Builder()
                .baseUrl(AllApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServiceApi::class.java)*/
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(50000, TimeUnit.SECONDS)
                .readTimeout(50000, TimeUnit.SECONDS).build()

            return Retrofit.Builder()
                .baseUrl(AllApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServiceApi::class.java)
        }
    }


    // ------------------------------------------------- Added By Sahil -----------------------------------------------
    @GET("Language")
    suspend fun getCoroutineLanguage(): Response<List<LanguageModel>>

    @GET("UserType")
    suspend fun getCoroutineProfile(): Response<List<ProfileModel>>


    @POST("Login/UserLogin")
    suspend fun doCoroutineLogin(@Body params: HashMap<String, String>): Response<LoginModel>

    @GET("Login/ValidateUser/{userName}/{userTypeId}")
    suspend fun coroutineCheckForUser(
        @Path("userName") userName: String, @Path("userTypeId") userTypeId: String
    ): Response<DuplicateUserModel>

    @POST("Login/SendOTP/{mobile_no}")
    suspend fun sendCoroutineOtp(@Path("mobile_no") mobile_no: String): Response<SendOtpModel>

    @POST("Login/VerifyOTP/{mobile_no},{otp}")
    suspend fun verifyCoroutineOtp(
        @Path("mobile_no") mobile_no: String,
        @Path("otp") otp: String
    ): Response<SendOtpModel>

    @POST("Login/UserRegistration/{username},{usertype},{password},{MobileNo}")
    suspend fun createCoroutineAccount(
        @Path("username") username: String,
        @Path("usertype") user_type: String,
        @Path("password") password: String,
        @Path("MobileNo") MobileNo: String
    ): Response<SignUpModel>

    @POST("Login/LoginwithOTP")
    suspend fun loginWithOTP(
        @Body params: HashMap<String, Any>
    ): Response<LoginWithOTPModel>

    @GET("bottom_menu")
    suspend fun bottomMenu(): Response<BottomMenuModel>

    @POST("Login/ChangeUserPassword")
    suspend fun resetCoroutinePassword(
        @Body params: HashMap<String, String>
    ): Response<ResetPasswordModel>

    @POST("Login/SetPassword")
    suspend fun setPassword(
        @Body userId: String,
        @Query("Password") Password: String
    ): Response<SetPasswordModel>

    @GET("CertificateType")
    suspend fun getCertificateTypes(): Response<List<CertificateTypeModel>>

    @GET("Certificate/GetUserCertificateList/{userId}/{CertificateTypeId}") // TODO put correct annotation from api link
    suspend fun getCertificateList(
        @Header("Authorization") Authorization: String,
        @Path("userId") userId: String,
        @Path(value = "CertificateTypeId") CertificateTypeId: String,
    ): Response<CertificateModel>

    @POST("Webinar/GetWebinarByLanguageId") // TODO put correct annotation from api link
    suspend fun getWebinarsByLang(
        @Header("Authorization") Authorization: String,
        @Body langId: Int,
    ): Response<WebinarResponse>

    @POST("WebinarSlots/GetWebinarSlotByWebinarId") // TODO put correct annotation from api link
    suspend fun getSlotById(
        @Header("Authorization") Authorization: String,
        @Body webinarId: Int,
    ): Response<SlotIdResponse>

    @GET("Gender") // TODO put correct annotation from api link
    suspend fun getGenders(): Response<List<GenderModel>>

    @GET("State") // TODO put correct annotation from api link
    suspend fun getStates(): Response<List<StateModel>>

    @GET("ApplicationTranslatedText/GetApplicationTextByLanguage/{LanguageId}")
    suspend fun getLangKeys(
        @Path("LanguageId") languageId: String
    ): Response<List<LanguageKeyModel>>

    @GET("District/GetDistrictByState/{StateId}")
    suspend fun getDistricts(
        @Path("StateId") StateId: String
    ): Response<List<DistrictModel>>

    @Multipart
    @POST("Login/UpdateUserProfile")
    fun updateUserProfileWithPicture(
        @Part image: MultipartBody.Part?,
        @Part("stateId") stateId: RequestBody?,
        @Part("districtId") districtId: RequestBody?,
        @Part("firstName") firstName: RequestBody?,
        @Part("lastName") lastName: RequestBody?,
        @Part("Gender") gender: RequestBody?,
        @Part("mobileNo") mobileNo: RequestBody?,
        @Part("userId") userId: RequestBody?,
        @Part("whatsAppNo") whatsAppNo: RequestBody?,
        @Part("emailId") emailId: RequestBody?,
        @Part("dob") dob: RequestBody?
    ): retrofit2.Call<SaveProfileModel>

    /*TO Book Webinar for a slot*/
    @POST("WebinarBooking")
    suspend fun bookingWebinarForSlot(
        @Header("Authorization") Authorization: String,
        @Body params: HashMap<String, Any>
    ): Response<WebinarBookingModel>


//-----------------------------------------------OLD CODE ---------------------------------------------------
    /*
      @GET("Language")
    fun getLanguage(): Call<List<LanguageModel>>

    @GET("UserType")
    fun getProfile(): Call<List<ProfileModel>>

    @POST("Login/UserLogin")
    fun doLogin(@Body params: HashMap<String, String>): Call<LoginModel>

    @GET("Login/ValidateUser/{userName}/{userTypeId}")
    fun checkForUser(
        @Path("userName") userName: String, @Path("userTypeId") userTypeId: String
    ): Call<DuplicateUserModel>

    @POST("Login/SendOTP/{mobile_no}")
    fun sendOtp(@Path("mobile_no") mobile_no: String): Call<SendOtpModel>

    @POST("Login/VerifyOTP/{mobile_no},{otp}")
    fun verifyOtp(
        @Path("mobile_no") mobile_no: String,
        @Path("otp") otp: String
    ): Call<SendOtpModel>

    @POST("Login/RegisterUser/{username},{usertype},{password}")
    fun createAccount(
        @Path("username") username: String,
        @Path("usertype") user_type: String,
        @Path("password") password: String
    ): Call<SignUpModel>

       @POST("ChangePassword")
    fun resetPassword(
        @Path("userId") userId: String,
        @Path("oldPassword") oldPassword: String,
        @Path("newPassword") newPassword: String
    ): Call<ResetPasswordModel>

     @POST("ChangePassword")
    fun resetPassword(
        @Path("userId") userId: String,
        @Path("oldPassword") oldPassword: String,
        @Path("newPassword") newPassword: String
    ): Call<ResetPasswordModel>

     */

}