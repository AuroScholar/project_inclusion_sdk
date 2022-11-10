package com.pi.projectinclusion.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.ProfileActivity
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.LangKeyListener
import com.pi.projectinclusion.auth.MenuAdapterUpdate
import com.pi.projectinclusion.auth.MyAuthListener
import com.pi.projectinclusion.databinding.FragmentCoursesBinding
import com.pi.projectinclusion.model.LanguageKeyModel
import com.pi.projectinclusion.model.LanguageModel
import com.pi.projectinclusion.repository.AuroRepository


private const val TAG = "CoursesFragment"

class CoursesFragment : Fragment(), MyAuthListener, LangKeyListener {
    private lateinit var mContext: Context
    private lateinit var mBinding: FragmentCoursesBinding
    private lateinit var mViewModel: AuthViewModel
    private lateinit var langId: String
    private lateinit var mSelectedLang: String
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var hmProfileDetail: HashMap<String, String>
    private lateinit var mLangList: ArrayList<LanguageModel>
    private lateinit var v: View

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ")
        try {
            mContext = requireActivity()
            mLangList = ArrayList(10)
            mBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_courses, container, false)
            v = mBinding.root
            mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
            mBinding.viewmodel = mViewModel
            mViewModel.mAuthListener = this
            mViewModel.mLangKeyListener = this
            try {
                hmProfileDetail = getHashMap(
                    mContext,
                    mContext.getString(R.string.key_profile)
                )// Handling back navigation on calling fragment bases
            } catch (e: NullPointerException) {
                e.message?.let { mContext.toast(it) }
            }
            setLanguage()
            Log.d(
                TAG,
                "onCreateView: profilePicUrl: " + hmProfileDetail[mContext.getString(R.string.key_profile_url)]
            )

            if (hmProfileDetail[mContext.getString(R.string.key_profile_url)] != "null") {

                mBinding.pbProfileImage.visibility = View.VISIBLE
                AuroRepository().loadImageWithGlide(
                    mContext,
                    hmProfileDetail[mContext.getString(R.string.key_profile_url)].toString(),
                    mBinding.imgProfileLayout,
                    mBinding.pbProfileImage
                )
            }
            mBinding.imgProfileLayout.setOnClickListener {

                startActivity(
                    Intent(
                        requireActivity(),
                        ProfileActivity::class.java
                    )
                        .putExtra(
                            getString(R.string.key_calling_fragment),
                            getString(R.string.fragment_user)
                        )
                )

//                  startActivity(
//                      Intent(
//                          context!!,
//                          UserProfileActivity::class.java
//                      ).putExtra(
//                          mContext.getString(R.string.first_name),
//                          hmProfileDetail[mContext.getString(R.string.field_fname)]
//                      ).putExtra(
//                          mContext.getString(R.string.key_profile_url),
//                          hmProfileDetail[mContext.getString(
//                              R.string.key_profile_url
//                          )]
//                      )
//                  )
            }
        } catch (e: Exception) {
        }
        return v
    }

    override fun onResume() {
        super.onResume()

        mBinding.MlCourses.setTransition(R.id.TRANS_WELCOME_REVERSE)
        mBinding.MlCourses.transitionToEnd()

        mBinding.MlCourses.setTransition(R.id.TRANS_WELCOME)
        mBinding.MlCourses.transitionToEnd()

        mViewModel.getCoroutineLanguageData(mContext)
        setLanguage()
        hmProfileDetail = getHashMap(mContext, mContext.getString(R.string.key_profile))
        Log.d(TAG, "onResume: ")
        AuroRepository().loadImageWithGlide(
            mContext,
            hmProfileDetail[mContext.getString(R.string.key_profile_url)].toString(),
            mBinding.imgProfileLayout,
            mBinding.pbProfileImage
        )

        mBinding.nameTxt.text =
            hmLangValue["key_hello"] + ", " + hmProfileDetail[mContext.getString(R.string.first_name)]

    }

    private fun openLangMenu() {
        mBinding.ivLangSelect.setOnClickListener {
            val popupMenu = PopupMenu(mContext, mBinding.ivLangSelect)
            for (i in mLangList) {
                popupMenu.menu.add(i.translatedName)
            }
            popupMenu.setOnMenuItemClickListener { item ->
                for (i in mLangList) {
                    if (i.translatedName == item?.title) {
                        Log.d(TAG, "onMenuItemClick: i.id: " + i.id)
                        mViewModel.getLangKeyData(
                            (i.id).toString(),
                            mContext
                        ) // calling get lang key and value pair based on selected lang (pos)
                        langId = i.id.toString()
                        mSelectedLang = i.name.toString()
                        saveData(// save lang id
                            mContext,
                            getString(R.string.key_lang_id),
                            i.id.toString()
                        )
                    }
                }

                false
            }
            popupMenu.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setLanguage() {

        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        /* mBinding.ongoingTxt.text = hmLangValue["ongoing_courses"]
         mBinding.editSearchCourse.hint = hmLangValue["search_courses"]
         mBinding.availableCoursesTxt.text = hmLangValue["courses_available_for_you"]*/
        mBinding.tvWelcome.text = hmLangValue["welcome_txt"]
        mBinding.tvProfileHint.text = hmLangValue["txt_profile"]
        mBinding.nameTxt.text =
            hmLangValue["key_hello"] + ", " + hmProfileDetail[mContext.getString(R.string.first_name)]
        Log.e(TAG,">> $")
    }

    override fun onNetworkCallStarted() {
    }

    override fun onKeyListSuccess(lang_response: List<LanguageKeyModel>) {
        //Log.d(TAG, "onKeyListSuccess: langResponse: $lang_response")
        val langKeyList: HashMap<String, String> = HashMap()
        for (i in lang_response) {
            langKeyList[i.pageKey!!] = i.keyValue!!
        }
        langKeyList[getString(R.string.key_lang_name)] = mSelectedLang // inflating hashmap

        hmLangValue = HashMap(langKeyList)
        saveHashMap(
            mContext,
            getString(R.string.key_lang_list),
            langKeyList
        ) // storing hashmap of selected lang
        try {
            hmLangValue = HashMap(
                getHashMap(
                    mContext,
                    getString(R.string.key_lang_list)
                )
            )
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        }

        setLanguage()
        val require = requireActivity() as MenuAdapterUpdate
        require.onMenuAdapterUpdate()
    }

    override fun onLangKeyApiFailure() {
        Log.d(TAG, "onProfileApiFailure: api call failed")
        if (isNetwork(mContext)) {
            mViewModel.getLangKeyData(
                (langId),
                mContext
            )
        }
    }

    override fun onCoroutineApiSuccess(lang_response: List<LanguageModel>) {
        mLangList = ArrayList()
        mLangList.clear()
        // get body from api response from IO Coroutine to main thread
        for (i in lang_response.indices) { // fill ling with langs from api
            val name = lang_response[i].name.toString()
            val translatedName = lang_response[i].translatedName
            val langId = lang_response[i].id
            mLangList.add(LanguageModel(name, langId, "", "", translatedName))
        }
        //Init recycler view
        AuroRepository().runOnMainThread {
            openLangMenu()
        }

    }

    override fun onAuthApiFailure() {
        Log.d(TAG, "onProfileApiFailure: api call failed")
        if (isNetwork(mContext)) {
            mViewModel.getCoroutineLanguageData(mContext)
        }
    }

}