package com.pi.projectinclusion.activity

import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.auth.*
import com.pi.projectinclusion.databinding.ActivityProfileDetailBinding
import com.pi.projectinclusion.fragment.UserFragment
import com.pi.projectinclusion.model.DistrictModel
import com.pi.projectinclusion.model.GenderModel
import com.pi.projectinclusion.model.SaveProfileModel
import com.pi.projectinclusion.model.StateModel
import com.pi.projectinclusion.repository.AuroRepository
import com.pi.projectinclusion.url.ServiceApi
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "ProfileActivity"

class ProfileActivity : AppCompatActivity(),
    DistrictListListener,
    StateListListener {
    private lateinit var mContext: Context
    private lateinit var mBinding: ActivityProfileDetailBinding
    private lateinit var mViewModel: AuthViewModel
    private var callingFragment: String? = null
    private lateinit var optionDialog: Dialog
    private var phoneNumber: String = ""
    private var selectedState: Int? = 0
    private var selectedDistrict: Int? = 0
    private var selectedGender: Int? = 0
    private lateinit var password: String
    private lateinit var alsGenderList: ArrayList<GenderModel>
    private lateinit var alsStateList: ArrayList<StateModel>
    private lateinit var alsDistrictList: ArrayList<DistrictModel>
    private var hmLangValue = HashMap<String, String>()
    private var hmProfileValue: HashMap<String, String>? = null
    private var byteArray: ByteArray? = null
    private var fileName: String? = null
    private lateinit var bitmap: Bitmap

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_profile)

        AuroRepository().setStatusBarColor(this)
        initViewModel()
        mContext = this

        //ARGUMENTS
        setArguments()


        // Handling back navigation on calling fragment bases


        setLanguage()/* = java.util.HashMap<kotlin.String, kotlin.String> */
        backNavigation()

        if (hmProfileValue != null) {
            try {
                setProfileDetail()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setGenderSpinner()

        saveProfile() // METHOD TO CHECK ALL NULL FIELDS, SET ERRORS AND SAVE PROFILE BY CALLING API

        openGalleryOrCamera()
        optionDialog = createChooseDialog()

        logOut()

    }

    private fun logOut() {
        /*changed to new Image*/
        mBinding.cvLogout.setOnClickListener {
            clearData(this, "user_token")
            clearData(this, mContext.getString(R.string.key_profile))
            clearData(this, mContext.getString(R.string.key_phone_number))
            clearData(this, "user_id")
            val i = Intent(
                this,
                LanguageActivity::class.java
            )
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            overridePendingTransition(
                R.anim.slide_in_left_first,
                R.anim.slide_out_left_first
            )
            finish()
        }
    }


    private fun setArguments() {

        callingFragment = intent.getStringExtra(mContext.getString(R.string.key_calling_fragment))

        if (intent.getStringExtra(mContext.getString(R.string.key_phone_number)) != null) {
            phoneNumber =
                intent.getStringExtra(mContext.getString(R.string.key_phone_number)) as String // Handling back navigation on calling fragment bases
            mBinding.etPhoneNumber.setText(phoneNumber)
        } else {
            phoneNumber = getData(mContext, mContext.getString(R.string.key_phone_number))
            mBinding.etPhoneNumber.setText(phoneNumber)
        }

        mBinding.etUsername.setText(getData(mContext, mContext.getString(R.string.key_username)))

        hmProfileValue = try {
            getHashMap(
                mContext,
                mContext.getString(R.string.key_profile)
            )// Handling back navigation on calling fragment bases
        } catch (e: NullPointerException) {
            null
        }

        if (intent.getStringExtra(mContext.getString(R.string.password_txt)) != null) {
            password =
                intent.getStringExtra(mContext.getString(R.string.password_txt)) as String
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2404) {
            if (resultCode == RESULT_OK) {
                try {
                    val selectedImage: Uri? = data?.data
                    val image_path = selectedImage?.path
                    bitmap = BitmapFactory.decodeFile(image_path)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
                    byteArray = stream.toByteArray()
                    //  mBinding.profileImg.setImageBitmap(bitmap)
                    fileName =
                        image_path?.substring(image_path.toString().lastIndexOf("/") + 1)
                    Log.d(TAG, "onActivityResult: fileName: " + fileName)
                    val bytes: ByteArray = encodeToBase64(bitmap, 100)!!
                    /*    if (file_size >= 500) {
                            studentProfileModel.setImageBytes(AppUtil.encodeToBase64(picBitmap, 50))
                        } else {
                            studentProfileModel.setImageBytes(bytes)
                        }*/
                    loadimage(bitmap)
                } catch (e: java.lang.Exception) {
                    //AppLogger.e("StudentProfile", "fragment exception=" + e.message)
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                //showSnackbarError(ImagePicker.getError(data));
            } else {
            }
        }

    }
    /* if (requestCode == 2 && resultCode == RESULT_OK) {

         val uri: Uri = attr.data.getData()
         AppLogger.e("StudentProfile", "image path=" + uri.path)
         image_path = uri.path

         Log.d(TAG, "imagepathon: $image_path")
         val picBitmap = BitmapFactory.decodeFile(uri.path)
         val bytes: ByteArray = AppUtil.encodeToBase64(picBitmap, 100)
         val mb: Long = AppUtil.bytesIntoHumanReadable(bytes.size)
         val file_size = (bytes.size / 1024).toString().toInt()

         AppLogger.e("StudentProfile", "image size=" + uri.path)
//            val selectedImage: Uri? = data?.data
//            Log.d(TAG, "onActivityResult: ")
//
//
//            //mBinding.profileImg.setImageURI(selectedImage)
////            mBinding.profileImg.invalidate()
////            val drawable = mBinding.profileImg.drawable
////            val oldBitmap = drawable.toBitmap()
//
//            val filePathColumn = arrayOf(
//                Images.Media.DATA,
//                Images.Media.DISPLAY_NAME
//            )
//            val cursor: Cursor? =
//                selectedImage?.let {
//                    mContext.contentResolver.query(it, filePathColumn, null, null, null)
//                }
//
//            if (cursor!!.moveToFirst()) {
//                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//                val filePath = cursor.getString(columnIndex)
//                bitmap = BitmapFactory.decodeFile(filePath)
//                val fileNameIndex = cursor.getColumnIndex(filePathColumn[1])
//                val imgPath = cursor.getString(fileNameIndex)
//                fileName =
//                    imgPath.toString().substring(imgPath.toString().lastIndexOf("/") + 1)
//                mBinding.profileImg.setImageBitmap(bitmap)
//                compressImage(bitmap, filePath)
//                val original = BitmapFactory.decodeStream(fileName?.let { assets.open(it) })
//                val out = ByteArrayOutputStream()
//                original.compress(Bitmap.CompressFormat.PNG, 50, out)
//                bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
     }
     cursor.close()


//
//            mBinding.profileImg.setImageBitmap(bitmap)
     // val stream = ByteArrayOutputStream()

 } else if (requestCode == 1 && resultCode == RESULT_OK)
 {
     val photo: Bitmap = data?.extras?.get("data") as Bitmap
     Glide.with(mContext).load(photo).into(mBinding.profileImg)
     val stream = ByteArrayOutputStream()
     val tempUri = getImageUri(applicationContext, photo)
     mBinding.profileImg.setImageURI(tempUri)
     fileName = getRealPathFromURI(tempUri)
     photo.compress(Bitmap.CompressFormat.PNG, 50, stream)
     byteArray = stream.toByteArray()
 }*/
    // }

    private fun loadimage(picBitmap: Bitmap) {
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(
            mBinding.profileImg.getContext().getResources(),
            picBitmap
        )
        circularBitmapDrawable.isCircular = true
        mBinding.profileImg.setImageDrawable(circularBitmapDrawable)
        //binding.editImage.setVisibility(View.VISIBLE)
    }

    private fun encodeToBase64(image: Bitmap, quality: Int): ByteArray? {
        val byteArrayOS = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOS)
        return byteArrayOS.toByteArray()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun compressImage(lbitmap: Bitmap, imagePath: String) {
        val simpleDateFormat = SimpleDateFormat()
        val f = File(Uri.parse(imagePath).toString())
        try {
            f.createNewFile()
            val bos = ByteArrayOutputStream()
            val oldBitmap = lbitmap
            lbitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
            bitmap = lbitmap
            val bitmapdata = bos.toByteArray()
            var fos: FileOutputStream? = null
            fos = FileOutputStream(f)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            val stream = ByteArrayOutputStream()
            oldBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
            byteArray = stream.toByteArray()
            try {
                bitmap = AuroRepository().correctImageRotation(
                    byteArray!!,
                    oldBitmap,
                    mBinding.profileImg
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
        }
        fileName = f.toString()
        Log.i("response", "FILE : $f")
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true)
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            Images.Media.insertImage(inContext.contentResolver, OutImage, "profile_pic", null)

        return Uri.parse(path)
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var path = ""
        if (contentResolver != null) {
            val cursor = contentResolver.query(uri!!, null, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
                path = cursor.getString(idx)
                cursor.close()
            }
        }
        return path.substring(path.lastIndexOf("/") + 1)
    }

    private fun setGenderSpinner() {
        alsGenderList.add(GenderModel(name = hmLangValue["hint_gender"], id = 0))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_male"], id = 1))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_female"], id = 2))
        alsGenderList.add(GenderModel(name = hmLangValue["gender_other"], id = 3))

        val alsGender = ArrayList<String>(5)
        for (i in alsGenderList) {
            alsGender.add(i.name.toString())
        }
        val genderAdapter =
            object :
                ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, alsGender) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        view.setTextColor(Color.BLACK)
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }
                    return view
                }
            }
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spGender.adapter = genderAdapter

        if (hmProfileValue != null) {
            for (i in alsGenderList) {
                if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_gender)]) {
                    selectedGender = i.id
                    mBinding.spGender.setSelection(alsGenderList.indexOf(i))
                    break
                }

            }
        }
    }

    /**
     * Method for forward navigation after all compulsory fields are filled
     */
    private fun saveProfile() {
        mBinding.cvContinue.setOnClickListener {

            if (fieldValidation()) {
                mBinding.pbProfileDetail.visibility = View.VISIBLE

                val stateId =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), selectedState.toString())
                val userId = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    getData(mContext, mContext.getString(R.string.key_user_id))
                )
                val districtId =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        selectedDistrict.toString()
                    )
                val firstName = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.etFirstName.text.toString()
                )
                val lastName = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.etLastName.text.toString()
                )
                val genderId =
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        selectedGender.toString()
                    )
                val mobileNo = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.etPhoneNumber.text.toString()
                )
                val whatsAppNo = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.etWhatsappNumber.text.toString()
                )
                val emailid = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.etEmail.text.toString()
                )
                val dob = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    mBinding.tvDOB.text.toString()
                )
                val image_body: MultipartBody.Part?
                if (byteArray != null) {
                    val req_file =
                        byteArray?.let { it1 ->
                            RequestBody.create(
                                "image/*".toMediaTypeOrNull(),
                                it1
                            )
                        }
                    image_body =
                        MultipartBody.Part.createFormData("userImage", fileName, req_file!!)

                    ServiceApi().updateUserProfileWithPicture(
                        image_body,
                        stateId,
                        districtId,
                        firstName,
                        lastName,
                        genderId,
                        mobileNo,
                        userId,
                        whatsAppNo,
                        emailid,
                        dob

                    )
                        .enqueue(object : Callback<SaveProfileModel> {
                            override fun onResponse(
                                call: Call<SaveProfileModel>,
                                response: Response<SaveProfileModel>
                            ) {
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    mBinding.pbProfileDetail.visibility = View.GONE
                                    var profileUrl = if (hmProfileValue != null)
                                        hmProfileValue!![mContext.getString(R.string.key_profile_url)]
                                    else
                                        "null"

                                    if (response.body()?.result?.response?.size != 0) {
                                        profileUrl =
                                            response.body()?.result?.response?.get(0)?.profilePicURL.toString()
                                    }

                                    val hmProfile = HashMap<String, String>(10)
                                    hmProfile.put(
                                        mContext.getString(R.string.field_state),
                                        selectedState.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_district),
                                        selectedDistrict.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_fname),
                                        mBinding.etFirstName.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_lname),
                                        mBinding.etLastName.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_phone_number),
                                        mBinding.etPhoneNumber.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_gender),
                                        selectedGender.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_whatsapp_number),
                                        mBinding.etWhatsappNumber.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_email),
                                        mBinding.etEmail.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_dob),
                                        mBinding.tvDOB.text.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.key_profile_url),
                                        profileUrl.toString()
                                    )
                                    hmProfile.put(
                                        mContext.getString(R.string.field_filename),
                                        fileName.toString()
                                    )
                                    saveHashMap(
                                        mContext,
                                        mContext.getString(R.string.key_profile),
                                        hmProfile
                                    )

                                    if (callingFragment == mContext.getString(R.string.fragment_user)) {
                                        finish()
                                    } else {
                                        val intent = Intent(mContext, DashBoardActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                        overridePendingTransition(
                                            R.anim.slide_in_left_first,
                                            R.anim.slide_out_left_first
                                        )
                                    }
                                    toast(hmLangValue["save_profile_success_text"].toString())
                                    //finish()

                                } else {
                                    mBinding.pbProfileDetail.visibility = View.GONE
                                    Log.d(TAG, "onResponse: response: " + response.raw())
                                }
                            }

                            override fun onFailure(call: Call<SaveProfileModel>, t: Throwable) {
                                Log.d(TAG, "onFailure: ")
                            }

                        })
                } else {
                    mContext.toast(hmLangValue["upload_profile_txt"].toString())
                    mBinding.pbProfileDetail.visibility = View.GONE
                }


                // }
            }
        }
    }


    private fun fieldValidation(): Boolean {

        val firstName = mBinding.etFirstName.text.toString()
        val lastName = mBinding.etLastName.text.toString()
        val dob = mBinding.tvDOB.text.toString()
        val whatsappNo = mBinding.etWhatsappNumber.text.toString()
        val phNo = mBinding.etPhoneNumber.text.toString()
        val email = mBinding.etEmail.text.toString()

        val validate = true

        if (firstName.isNotEmpty()) {
            if (firstName.startsWith(" ")) {

                toast(hmLangValue["error_space_first_name"].toString())
                return false
            }
        } else {
            toast(hmLangValue["error_first_name"].toString())
            return false
        }

        if (lastName.isNotEmpty()) {
            if (lastName.startsWith(" ")) {
                toast(hmLangValue["error_space_last_name"].toString())
                return false
            }
        } else {
            toast(hmLangValue["error_last_name"].toString())
            return false
        }

        if (dob.isEmpty()) {

            toast(hmLangValue["error_dob"].toString())
            return false
        }

        if (selectedGender == 0) {
            toast(hmLangValue["error_gender"].toString())
            return false
        }

        if (phNo.isNotEmpty()) {
            if (phNo.length != 10 && phNo.matches(Regex("[0-9]+"))) {
                toast(hmLangValue["error_valid_mobile_no"].toString())
                return false
            }
        } else {

            toast(hmLangValue["error_phone_number"].toString())
            return false
        }

        if (whatsappNo.isNotEmpty()) {
            if (whatsappNo.length != 10 && whatsappNo.matches(Regex("[0-9]+"))) {

                toast(hmLangValue["error_valid_mobile_no"].toString())
                return false
            }
        }

        if (email.isNotEmpty()) {
            if (email.startsWith(" ")) {
                toast(hmLangValue["error_space_email"].toString())
                return false
            } else if (!checkEmail(mBinding.etEmail.text.toString())) {
                toast(hmLangValue["error_email"].toString())
                return false
            }
        }

        if (selectedState == 0) {
            toast(hmLangValue["error_state"].toString())
            return false
        }
        if (selectedDistrict == 0) {
            toast(hmLangValue["error_district"].toString())
            return false
        }

        return validate
    }

    private fun checkEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setSpinnerOnClick() {
        mBinding.spState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDistrict = 0
                val value = parent!!.getItemAtPosition(position).toString()
                if (value == alsStateList[0].name) {
                    (view as TextView).setTextColor(Color.GRAY)
                }
                mViewModel.getDistrictList(alsStateList.get(position).id.toString(), mContext)
                selectedState =
                    if (alsStateList[position].id == null) 0 else alsStateList[position].id
            }
        }

        mBinding.spDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val value = parent!!.getItemAtPosition(position).toString()
                    if (value == alsDistrictList[0].name) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    selectedDistrict =
                        if (alsDistrictList[position].id == null) 0 else alsDistrictList[position].id
                }
            }

        mBinding.spGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val value = parent!!.getItemAtPosition(position).toString()
                    if (value == alsGenderList[0].name) {
                        (view as TextView).setTextColor(Color.GRAY)
                    }
                    selectedGender =
                        if (alsGenderList[position].id == null) 0 else alsGenderList[position].id
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setProfileDetail() {

        if (hmProfileValue!![mContext.getString(R.string.field_fname)] != "null")
            mBinding.etFirstName.setText(hmProfileValue!![mContext.getString(R.string.field_fname)])


        if (hmProfileValue!![mContext.getString(R.string.key_profile_url)] != "null") {
            mBinding.pbProfileImage.visibility = View.VISIBLE
            AuroRepository().loadImageWithGlide(
                mContext,
                hmProfileValue!![mContext.getString(R.string.key_profile_url)].toString(),
                mBinding.profileImg,
                mBinding.pbProfileImage
            )
            fileName = "null"
            byteArray = ByteArray(0)
        } else {
            mBinding.profileImg.setImageResource(R.drawable.ic_default_user)
        }
        if (hmProfileValue!![mContext.getString(R.string.field_lname)] != "null")
            mBinding.etLastName.setText(hmProfileValue!![mContext.getString(R.string.field_lname)])

        if (hmProfileValue!![mContext.getString(R.string.field_dob)] != "null") {
            val dob = hmProfileValue!![mContext.getString(R.string.field_dob)]
            if (dob?.length!! > 11) {
                mBinding.tvDOB.text =
                    dob.substring(0, dob.indexOf("T"))
            } else {
                mBinding.tvDOB.text = dob
            }
        }
//        mBinding.tvGender.text =
//            hmProfileValue.get(getFromHashMap(mContext.getString(R.string.field_gender)))
        if (phoneNumber != "" && phoneNumber != "null") {
            mBinding.etPhoneNumber.setText(phoneNumber)
        } else {
            mBinding.etPhoneNumber.setText(hmProfileValue!![mContext.getString(R.string.field_phone_number)])
        }
        if (hmProfileValue!![mContext.getString(R.string.field_whatsapp_number)] != "null") {
            mBinding.etWhatsappNumber.setText(hmProfileValue!![mContext.getString(R.string.field_whatsapp_number)])
            if (hmProfileValue!![mContext.getString(R.string.field_whatsapp_number)] == hmProfileValue!![mContext.getString(
                    R.string.field_phone_number
                )]
            ) {
                mBinding.cbSameNo.isChecked = true
                mBinding.etWhatsappNumber.isEnabled = false
            } else {
                mBinding.cbSameNo.isChecked = false
                mBinding.etWhatsappNumber.isEnabled = true
            }
        }
        if (hmProfileValue!![mContext.getString(R.string.field_email)] != "null")
            mBinding.etEmail.setText(hmProfileValue!![mContext.getString(R.string.field_email)])
    }

    private fun setLanguage() {
        val langId = getData(mContext, mContext.getString(R.string.key_lang_id))
        if (langId == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))

        mBinding.certificateHeading.text = hmLangValue["field_edit_profile"]
        mBinding.tvFName.text = hmLangValue["field_fname"]
        mBinding.etFirstName.hint = hmLangValue["hint_enter_your_fname"]
        mBinding.tvLname.text = hmLangValue["field_lname"]
        mBinding.etLastName.hint = hmLangValue["hint_enter_your_lname"]
        mBinding.tvDOBTitle.text = hmLangValue["field_dob"]
        mBinding.tvDOB.hint = hmLangValue["hint_dob"]
        mBinding.tvGender.text = hmLangValue["field_gender"]
        mBinding.tvPhNo.text = hmLangValue["field_phone_number"]
        mBinding.tvWpNo.text = hmLangValue["field_whatsapp_number"]
        mBinding.tvSame.text = hmLangValue["txt_same_as_your_mobile_number"]
        mBinding.tvEmail.text = hmLangValue["field_email"]
        mBinding.tvStateId.text = hmLangValue["field_state"]
        mBinding.tvDistrictId.text = hmLangValue["field_district"]
        mBinding.crtAcntTxt.text = hmLangValue["cont_text"]
        /*removed as changed to a image*/
        //mBinding.acbLogout.text = hmLangValue["logout"]
        mBinding.tvUsername.text = hmLangValue["field_username"]
    }

    /**
     * Supporting methods
     */


    /**
     * init date picker dialog with current date and max date as current date
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDatePicker(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val currYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val cal = Calendar.getInstance()

        val dpd = DatePickerDialog(mContext, { _, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dobFormat = "yyyy-MM-dd" // mention the format you need
            val simpleDateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat(dobFormat, Locale.ENGLISH)
            } else {
                TODO("VERSION.SDK_INT < N")
            }

            mBinding.tvDOB.text = simpleDateFormat.format(cal.time)

        }, currYear, month, day)

        dpd.datePicker.maxDate = System.currentTimeMillis()
        return dpd
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askPermission()
            } else {
                toast(hmLangValue["perm_gallery"].toString())
            }
        }
    }

    private fun openGalleryOrCamera() {
        mBinding.cvProfileView.setOnClickListener {
            askPermission()
        }
    }

    private fun askPermission() {
        if (checkSelfPermission(
                mContext,
                READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(
                mContext,
                WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(mContext, permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                    permission.CAMERA
                ), 100
            )
        } else {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
//            val intent =
//                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, 2)
        }
    }

    private fun createChooseDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_gallery_camera)
        val ivGallery =
            dialog.findViewById<ImageView>(R.id.ivGallery)
        val ivCamera =
            dialog.findViewById<ImageView>(R.id.ivCamera)

        ivGallery.setOnClickListener {
            /*val intent =
                Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
          */

        }
        ivCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 1)
            dialog.cancel()

        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }


    /**
     * INITIALIZING VIEW MODEL WITH {@param fragment_profile_detail} layout
     */
    /**
     * BACK NAVIGATION, DATE PICKER & SAME AS MOBILE NUMBER CHECKBOX
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun backNavigation() {
        mBinding.cvBackEditProfile.setOnClickListener {
            if (callingFragment != mContext.getString(R.string.fragment_user)) {
                finish()
            } else {
                finish()
            }
        }
        // DATE PICKER ON CLICK INIT
        val dpd = setDatePicker()
        mBinding.tvDOB.setOnClickListener {
            dpd.show()
        }
        // SAME AS MOBILE NUMBER CHECK BOX
        mBinding.cbSameNo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.etWhatsappNumber.setText(mBinding.etPhoneNumber.text.toString())
                mBinding.etWhatsappNumber.isEnabled = false
            } else {
                mBinding.etWhatsappNumber.setText("")
                mBinding.etWhatsappNumber.isEnabled = true
            }
        }
    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail)
        mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = mViewModel

        mViewModel.getStateList(this)
        // mViewModel.mSaveProListener = this
        mViewModel.mStateListListener = this
        // mViewModel.mGenderListListener = this
        mViewModel.mDistrictListListener = this

        alsGenderList = ArrayList(5)
        alsStateList = ArrayList(29)
        alsDistrictList = ArrayList(29)
    }

    override fun onResume() {
        super.onResume()
        setLanguage()
        // mViewModel.getGenderList()
//        if (selectedState != -1) {
//            mBinding.spState.setSelection(selectedState!!)
//        }
//        if (selectedDistrict != -1) {
//            mBinding.spDistrict.setSelection(selectedDistrict!!)
//        }
//        if (selectedState == -1)
//            mViewModel.getStateList(this)


    }

    override fun onNetworkCallStarted() {
    }

    override fun onDistrictListCallSuccess(districtModel: List<DistrictModel>) {

        alsDistrictList.clear()
        val alsDistrictNameList = ArrayList<String>(20)
        alsDistrictNameList.add(
            hmLangValue["error_district"].toString()
        )
        alsDistrictList.add(
            DistrictModel(
                hmLangValue["error_district"].toString(), 0
            )
        )
        if (districtModel.isNotEmpty()) {
            for (i in districtModel) {
                alsDistrictList.add(i)
                i.name?.let { alsDistrictNameList.add(it) }
            }
        }
        val districtAdapter =
            object : ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                alsDistrictNameList
            ) {

                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    //set the color of first item in the drop down list to gray
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {

                        view.setTextColor(Color.BLACK)
                    }
                    return view
                }

            }
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spDistrict.adapter = districtAdapter

        if (hmProfileValue != null) {
            for (i in alsDistrictList) {
                if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_district)]) {
                    selectedDistrict = alsDistrictList.indexOf(i)
                    mBinding.spDistrict.setSelection(alsDistrictList.indexOf(i))
                    break
                }

            }
        }

    }

    override fun onDistrictApiFailure() {
        // Log.d(TAG, "onDistrictApiFailure: api call failed")
        //if (isNetwork(this)) {
        /*mViewModel.getDistrictList(
            alsStateList.get(selectedState!! - 1).id.toString(),
            mContext
        )*/
        toast(internetError)
        //}
    }

    override fun onStateListCallSuccess(stateModel: List<StateModel>) {

        alsStateList.clear()
        val alsStateNameList = ArrayList<String>(10)
        alsStateNameList.add(hmLangValue["error_state"].toString())
        alsStateList.add(StateModel(hmLangValue["error_state"].toString(), 0))
        for (i in stateModel) {
            alsStateList.add(i)
            i.name?.let { alsStateNameList.add(it) }
        }
        val stateAdapter =
            object :
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, alsStateNameList) {

                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    //set the color of first item in the drop down list to gray
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                        view.setTextColor(Color.BLACK)
                    }
                    return view
                }

            }
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spState.adapter = stateAdapter

//        Log.d(TAG, "onStateListCallSuccess: hmProfileValue: " + hmProfileValue)
        if (hmProfileValue != null) {
            for (i in alsStateList) {

                if (i.id.toString() == hmProfileValue!![mContext.getString(R.string.field_state)]) {
                    selectedState = alsStateList.indexOf(i)
                    mBinding.spState.setSelection(alsStateList.indexOf(i))

                    mViewModel.getDistrictList(
                        alsStateList[alsStateList.indexOf(i)].id.toString(),
                        this
                    )
                    break
                }

            }
        }
        setSpinnerOnClick()


    }

    override fun onStateApiFailure() {
        //  Log.d(TAG, "onStateApiFailure: api call failed")
        if (isNetwork(this)) {
            mViewModel.getStateList(this)
        }
    }

//    override fun onProfileNetworkStarted() {
//    }

//    override fun onSaveSuccess(profile_response: MutableLiveData<SaveProfileModel>) {
//        mBinding.pbProfileDetail.visibility = View.GONE
//
//        val intent = Intent(this, DashBoardActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(intent)
//
//
//    }

//    override fun onProfileApiFailure() {
//    }

}