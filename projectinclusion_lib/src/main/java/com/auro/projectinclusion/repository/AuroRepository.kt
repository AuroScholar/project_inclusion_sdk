package com.pi.projectinclusion.repository

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import com.auro.projectinclusion.model.WebinarBookingModel
import com.pi.projectinclusion.*
import com.pi.projectinclusion.model.*
import com.pi.projectinclusion.url.ServiceApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class AuroRepository {
    var TAG = "AuroRepository"

    /**
     * Coroutine Api Calls through view model scope
     *
     *[runOnMainThread]
     *[getCoroutineLanguageList]
     *[getLangKeyList]
     *[getCoroutineProfileList]
     *[completeCoroutineLogin]
     *[coroutineCheckUser]
     *[sendCoroutineOtpFromMobile]
     *[onCoroutineVerifyOtp]
     *[coroutineSignup]
     *[loginWithOtp]
     *[getCoroutineBottomMenu]
     *[resetCoroutinePassword]
     *[setPassword]
     *[getCertificateTypeList]
     *[getCertificatesList]
     *[getGendersList]
     *[getStateList]
     *[getDistrictList]
     *[saveBearerToken]
     *
     * EXTRA METHODS CALLED AT MULTIPLE LOCATIONS WITH SAME PARAMETERS
     * [loadImageWithGlide]
     * [correctImageRotation]
     * [inflateProfileHashMap]
     */


    /**
     * Coroutine Api Calls through view model scope
     */

    /**
     * Added By Sahil
     * @param body: code that needs to run on main thread
     */
    fun runOnMainThread(body: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            body.invoke() //SHORTCUT TO RUN CODE ON MAIN THREAD BY PUTTING IN A BODY
        }
    }

    /**
     * Added By Sahil
     */
    suspend fun getCoroutineLanguageList(): List<LanguageModel> {
        var langData: List<LanguageModel> = listOf()
        val response = ServiceApi().getCoroutineLanguage()
        if (response.isSuccessful) {
            langData = response.body()!!
        } else {
            Log.d(TAG, "getLangList: Api Call Failed" + response.raw())
        }

        return langData
    }


    /**
     * Added By Sahil
     */
    suspend fun getLangKeyList(LanguageId: String): List<LanguageKeyModel> {
        Log.d(TAG, "getLangKeyList: Thread Name: " + Thread.currentThread().name)

        var result_data: List<LanguageKeyModel> = listOf()

        val response = ServiceApi().getLangKeys(LanguageId)
        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "getLangKeyList: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun getCoroutineProfileList(): List<ProfileModel> {
        Log.d(TAG, "getCoroutineProfileList: Thread Name: " + Thread.currentThread().name)

        var result_data: List<ProfileModel> = listOf()
        val response = ServiceApi().getCoroutineProfile()
        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "getCoroutineProfileList: Api Call Failed" + response.raw())
        }

        return result_data
    }


    /**
     * Added By Sahil
     */
    suspend fun completeCoroutineLogin(
        userName: String,
        password: String,
        userType: String
    ): LoginModel {
        Log.d(TAG, "completeCoroutineLogin: Thread Name: " + Thread.currentThread().name)

        var result_data = LoginModel()

        val map = HashMap<String, String>()
        map["userName"] = userName
        map["password"] = password
        map["userTypeId"] = userType

        val response = ServiceApi().doCoroutineLogin(map)

        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "completeCoroutineLogin: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun coroutineCheckUser(
        userName: String,
        userType: String
    ): DuplicateUserModel {
        Log.d(TAG, "coroutineCheckUser: Thread Name: " + Thread.currentThread().name)

        var result_data = DuplicateUserModel()


        val response = ServiceApi().coroutineCheckForUser(userName, userType)

        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "coroutineCheckUser: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun sendCoroutineOtpFromMobile(mobile_no: String): SendOtpModel {

        Log.d(TAG, "sendCoroutineOtpFromMobile: Thread Name: " + Thread.currentThread().name)
        var result_data = SendOtpModel()

        val response = ServiceApi().sendCoroutineOtp(mobile_no)

        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "sendCoroutineOtpFromMobile: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun onCoroutineVerifyOtp(mobile: String, otp: String): SendOtpModel {

        Log.d(TAG, "onCoroutineVerifyOtp: Thread Name: " + Thread.currentThread().name)
        var result_data = SendOtpModel()

        try {
            val response = ServiceApi().verifyCoroutineOtp(mobile, otp)

            if (response.isSuccessful) {
                result_data = response.body()!!
            } else {
                Log.d(TAG, "onCoroutineVerifyOtp: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return result_data

    }

    /**
     * Added By Sahil
     */
    suspend fun coroutineSignup(
        userName: String,
        userType: String,
        password: String,
        mobileNo: String
    ): SignUpModel {
        Log.d(TAG, "coroutineSignup: Thread Name: " + Thread.currentThread().name)
        var result_data = SignUpModel()

        try {
            val response =
                ServiceApi().createCoroutineAccount(userName, userType, password, mobileNo)

            if (response.isSuccessful) {
                result_data = response.body()!!
            } else {
                Log.d(TAG, "coroutineSignup: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return result_data

    }

    /**
     * Added By Sahil
     */
    suspend fun loginWithOtp(userName: String, otp: Int, userTypeId: Int): LoginWithOTPModel {
        Log.d(TAG, "loginWithOtp: Thread Name: " + Thread.currentThread().name)
        var result_data = LoginWithOTPModel()

        val hmOtpCredentials = HashMap<String, Any>(3)
        hmOtpCredentials.put("userName", userName)
        hmOtpCredentials.put("otp", otp)
        hmOtpCredentials.put("userType", userTypeId)
        Log.d(TAG, "loginWithOtp: hmOtpCredentials: " + hmOtpCredentials)
        val response = ServiceApi().loginWithOTP(hmOtpCredentials)

        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "coroutineSignup: Api Call Failed" + response.raw())
        }

        return result_data

    }

    /**
     * Added By Sahil
     */
    suspend fun getCoroutineBottomMenu(): BottomMenuModel {
        Log.d(TAG, "getBottomMenu: Thread Name: " + Thread.currentThread().name)
        var result_data = BottomMenuModel()

        try {
            val response = ServiceApi().bottomMenu()

            if (response.isSuccessful) {
                result_data = response.body()!!
            } else {
                Log.d(TAG, "getBottomMenu: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun resetCoroutinePassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ): ResetPasswordModel {

        Log.d(TAG, "resetPassword: Thread Name: " + Thread.currentThread().name)
        var result_data = ResetPasswordModel()
        val hmCredentials = HashMap<String, String>(3)

        hmCredentials["userId"] = userId
        hmCredentials["oldPassword"] = oldPassword
        hmCredentials["newPassword"] = newPassword
        val response = ServiceApi().resetCoroutinePassword(hmCredentials)
        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "resetPassword: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun setPassword(
        userId: String,
        newPassword: String
    ): SetPasswordModel {

        Log.d(TAG, "resetPassword: Thread Name: " + Thread.currentThread().name)
        var result_data = SetPasswordModel()
        val response = ServiceApi().setPassword(userId, newPassword)
        if (response.isSuccessful) {
            result_data = response.body()!!
        } else {
            Log.d(TAG, "resetPassword: Api Call Failed" + response.raw())
        }

        return result_data
    }

    /**
     * Added By Sahil
     */
    suspend fun getCertificateTypeList(): List<CertificateTypeModel> {
        Log.d(TAG, "getCertificateTypeList: Thread Name: " + Thread.currentThread().name)

        var resultData: List<CertificateTypeModel> = listOf()
        try {
            val response = ServiceApi().getCertificateTypes()
            if (response.isSuccessful) {
                resultData = response.body()!!
            } else {
                Log.d(TAG, "getCertificateTypeList: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getCertificatesList(
        userId: String,
        certificateId: String,
        authorization: String
    ): CertificateModel {
        Log.d(TAG, "getCertificatesList: Thread Name: " + Thread.currentThread().name)

        var resultData = CertificateModel()
        Log.d(TAG, "getCertificatesList: certificateId: " + certificateId)
        val response = ServiceApi().getCertificateList(authorization, userId, certificateId)
        if (response.isSuccessful) {
            resultData = response.body()!!
        } else {
            Log.d(TAG, "getCertificatesList: Api Call Failed" + response.raw())
        }

        Log.d(TAG, "getCertificatesList: resultData: $resultData")
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getWebinarListByLang(
        langId: Int,
        authorization: String
    ): WebinarResponse {
        Log.d(TAG, "getWebinarListByLang: Thread Name: " + Thread.currentThread().name)

        var resultData = WebinarResponse()
        val response = ServiceApi().getWebinarsByLang(authorization, langId)
        if (response.isSuccessful) {
            resultData = response.body()!!
        } else {
            Log.d(TAG, "    suspend fun getWebinarListByLang(\n: Api Call Failed" + response.raw())
        }

        Log.d(TAG, "    suspend fun getWebinarListByLang(\n: resultData: $resultData")
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getSlotIdByWebinarId(
        webinarId: Int,
        authorization: String
    ): SlotIdResponse {
        Log.d(TAG, "getSlotIdByWebinarId: Thread Name: " + Thread.currentThread().name)

        var resultData = SlotIdResponse()
        val response = ServiceApi().getSlotById(authorization, webinarId)
        if (response.isSuccessful) {
            resultData = response.body()!!
        } else {
            Log.d(TAG, "    suspend fun getSlotIdByWebinarId(\n: Api Call Failed" + response.raw())
        }

        Log.d(TAG, "    suspend fun getSlotIdByWebinarId(\n: resultData: $resultData")
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getGendersList(): List<GenderModel> {
        Log.d(TAG, "getGendersList: Thread Name: " + Thread.currentThread().name)

        var resultData: List<GenderModel> = listOf()
        try {
            val response = ServiceApi().getGenders()
            if (response.isSuccessful) {
                resultData = response.body()!!
            } else {
                Log.d(TAG, "getGendersList: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getStateList(): List<StateModel> {
        Log.d(TAG, "getStateList: Thread Name: " + Thread.currentThread().name)

        var resultData: List<StateModel> = listOf()
        try {
            val response = ServiceApi().getStates()
            if (response.isSuccessful) {
                resultData = response.body()!!
            } else {
                Log.d(TAG, "getStateList: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun getDistrictList(stateId: String): List<DistrictModel> {
        Log.d(TAG, "getDistrictList: Thread Name: " + Thread.currentThread().name)

        var resultData: List<DistrictModel> = listOf()
        try {
            val response = ServiceApi().getDistricts(stateId)
            if (response.isSuccessful) {
                resultData = response.body()!!
            } else {
                Log.d(TAG, "getDistrictList: Api Call Failed" + response.raw())
            }
        } catch (e: Exception) {
        }
        return resultData
    }

    /**
     * Added By Sahil
     */
    suspend fun saveBearerToken(
        userName: String,
        password: String,
        userType: String,
        mContext: Activity
    ) {
        Log.d(TAG, "saveBearerToken: Thread Name: " + Thread.currentThread().name)
        val map = HashMap<String, String>()
        map["userName"] = userName
        map["password"] = password
        map["userTypeId"] = userType
        val response = ServiceApi().getAuthToken(map)
        if (response.isSuccessful) {
            val resultData = response.body()!!
            Log.d(TAG, "onResponse: token:  " + resultData.token)
            CoroutineScope(Dispatchers.Main).launch {
                saveData(
                    mContext,
                    mContext.getString(R.string.key_user_token),
                    resultData.token!!
                )
                Log.d(
                    TAG,
                    "saveBearerToken: user token: " + getData(
                        mContext,
                        mContext.getString(R.string.key_user_token)
                    )
                )
            }
        } else {
            Log.d(TAG, "getDistrictList: Api Call Failed" + response.raw())
        }

    }

    /**
     * Added By Rishabh
     * */
    suspend fun getWebinarBooking(tokenId : String,
    params : HashMap<String,Any>) : WebinarBookingModel {
        Log.d(TAG, "getWebinarBooking: Thread Name: " + Thread.currentThread().name)
        var resultdata = WebinarBookingModel()

        val response = ServiceApi().bookingWebinarForSlot(tokenId,  params)
        if (response.isSuccessful)
            {
            resultdata = response.body()!!
        }
        else{
            Log.d(TAG, "getWebinarBooking: Api Call Failed" + response.raw())
        }
        return resultdata
    }

    /**
     * EXTRA METHODS CALLED AT MULTIPLE LOCATIONS WITH SAME PARAMETERS
     */
    fun loadImageWithGlide(
        context: Context,
        url: String,
        iv: ImageView,
        pb: ProgressBar?,
    ) {
        val glide = Glide.with(context).load(url)
        glide.addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Log.d(TAG, "onLoadFailed ${context}: ")
                if (pb != null)
                    pb.visibility = View.GONE
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                Log.d(TAG, "onResourceReady ${context}: ")
                if (pb != null) {
                    pb.visibility = View.GONE
                }
                iv.setImageDrawable(resource)
                return false
            }

        }).into(iv)

    }

    fun loadBitmapWithGlide(
        context: Context,
        url: String,
        fileName: String,
        iv: SubsamplingScaleImageView?,
        saveImage: ((bitmap: Bitmap, string: String) -> Unit?)?
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    @NonNull resource: Bitmap,
                    @Nullable transition: Transition<in Bitmap?>?
                ) {
                    if (iv == null) {
                        saveImage!!(resource, fileName)
                    } else {
                        iv.setImage(ImageSource.bitmap(resource))
                    }
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun correctImageRotation(imageBytes: ByteArray, oldBitmap: Bitmap, iv: ImageView): Bitmap {
        val matrix = android.graphics.Matrix()
        val exifInterface = ExifInterface(ByteArrayInputStream(imageBytes))
        val orientation =
            exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        val newBitmap: Bitmap?
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90F)
                newBitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0,
                    oldBitmap.width, oldBitmap.height,
                    matrix, true
                )
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180F)
                newBitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0,
                    oldBitmap.width, oldBitmap.height,
                    matrix, true
                )
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270F)
                newBitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0,
                    oldBitmap.width, oldBitmap.height,
                    matrix, true
                )
            }
            else -> {
                newBitmap = oldBitmap
//                matrix.postRotate(-90F)
//                newBitmap = Bitmap.createBitmap(
//                    oldBitmap, 0, 0,
//                    oldBitmap.width, oldBitmap.height,
//                    matrix, true
//                )
            }
        }
        iv.setImageBitmap(newBitmap)
        return newBitmap!!
    }

    fun inflateProfileHashMap(
        context: Context,
        userResponse: UserDataModel
    ): HashMap<String, String> {
        val hmProfile = HashMap<String, String>(10)
        hmProfile.put(
            context.getString(R.string.field_fname),
            if (userResponse.firstName != null) userResponse.firstName.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_lname),
            if (userResponse.lastName != null) userResponse.lastName.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_email),
            if (userResponse.emailId != null) userResponse.emailId.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_phone_number),
            if (userResponse.mobileNo != null) userResponse.mobileNo.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_whatsapp_number),
            if (userResponse.whatsAppNo != null) userResponse.whatsAppNo.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_dob),
            if (userResponse.dob != null) userResponse.dob.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_gender),
            if (userResponse.gender != null) userResponse.gender.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_state),
            if (userResponse.stateId != null) userResponse.stateId.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.field_district),
            if (userResponse.districtId != null) userResponse.districtId.toString() else "null"
        )
        hmProfile.put(
            context.getString(R.string.key_profile_url),
            if (userResponse.profilePicURL != null) userResponse.profilePicURL.toString() else "null"
        )
        saveHashMap(context, context.getString(R.string.key_profile), hmProfile)

        return hmProfile
    }

    fun validateProfileComplete(userResponse: UserDataModel): Boolean {
        if (userResponse.firstName != null && userResponse.dob != null && userResponse.mobileNo != null && userResponse.stateId != null && userResponse.districtId != null && userResponse.profilePicURL != null) {
            return true
        } else {
            return false
        }
    }

    /**
     * ======================================UNUSED BUT MIGHT NEED LATER================================================
     *
     */

    /**
     * Added By Sahil
     */

    fun saveProfile(
        stateId: RequestBody,
        userId: RequestBody,
        districtId: RequestBody,
        mobileNo: RequestBody,
        whatsAppNo: RequestBody,
        dob: RequestBody,
        emailId: RequestBody,
        gender: RequestBody,
        lastName: RequestBody,
        firstName: RequestBody,
        filePart: MultipartBody.Part?,

        ): MutableLiveData<SaveProfileModel> {
        Log.d(TAG, "saveProfile: Thread Name: " + Thread.currentThread().name)
        val saveProfileResponse = MutableLiveData<SaveProfileModel>()
        ServiceApi().updateUserProfileWithPicture(
            filePart,
            stateId,
            districtId,
            firstName,
            lastName,
            gender,
            mobileNo,
            userId,
            whatsAppNo,
            emailId,
            dob
        ).enqueue(object : Callback<SaveProfileModel> {

            override fun onResponse(
                call: Call<SaveProfileModel>,
                response: Response<SaveProfileModel>
            ) {
                if (response.isSuccessful) {
                    saveProfileResponse.value = response.body()
                    Log.d("Respo", "onResponse: " + response.raw())
                    val body = response.body()
                    val msg = body!!.result!!.message
                    Log.d("Respo", "onMsgResponse: " + msg)
                } else {
                    Log.d("Respo", "onResponsee: " + response.raw())
                }
            }

            override fun onFailure(call: Call<SaveProfileModel>, t: Throwable) {
                Log.d("Respo", "onFailure: " + t.toString())
            }

        })
        return saveProfileResponse
    }


    fun copyStreamToFile(uri: Uri, context: Context): String {
        val outputFile = File.createTempFile("temp", null)

        context.contentResolver.openInputStream(uri)?.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        return outputFile.absolutePath
    }

    fun mLoad(string: String?, mContext: Context, error: String): Bitmap? {

        if (isNetwork(mContext)) {
            if (string != null) {
                val url: URL? = mStringToURL(string)
                val connection: HttpURLConnection?
                try {
                    if (url != null) {
                        connection = url.openConnection() as HttpURLConnection
                        connection.connect()
                        val inputStream: InputStream = connection.inputStream
                        val bufferedInputStream = BufferedInputStream(inputStream)

                        return BitmapFactory.decodeStream(bufferedInputStream)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        mContext.toast(error)
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                mContext.toast(error)
            }

        }
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun createScreeningJSon(
        marks: Int,
        message: String,
        intervention: String,
        qList: ArrayList<String>
    ): JSONArray {
        val section = JSONObject()
        val qaList = JSONArray()
        try {
            section.put("marks", marks)
            section.put("automatedMessage", message)
            section.put("interventions", intervention)

            for (i in qList) {
                qaList.put(i)
                qaList.put("Yes")
                qaList.put("No")
            }
            section.put("qaList", qaList)
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val sectionArray = JSONArray()

        sectionArray.put(section)
        return sectionArray
    }

    fun setStatusBarColor(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        val wic = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        wic.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        wic.isAppearanceLightStatusBars = false
        activity.window.statusBarColor =
            ResourcesCompat.getColor(activity.resources, R.color.background_color, null)

    }

}