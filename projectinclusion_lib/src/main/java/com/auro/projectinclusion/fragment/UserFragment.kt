package com.pi.projectinclusion.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pi.projectinclusion.*
import com.pi.projectinclusion.activity.LanguageActivity
import com.pi.projectinclusion.activity.LoginActivity
import com.pi.projectinclusion.activity.ProfileActivity
import com.pi.projectinclusion.activity.ResetPasswordActivity
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.ProfilePicUpdate
import com.pi.projectinclusion.databinding.FragmentUserBinding
import com.pi.projectinclusion.repository.AuroRepository

private const val TAG = "UserFragment"

class UserFragment : Fragment(), ProfilePicUpdate {
    private lateinit var mBinding: FragmentUserBinding
    private lateinit var mContext: Context
    private lateinit var mViewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var hmProfile: HashMap<String, String>
    private lateinit var v: View

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView: ")
        try {
            mContext = requireActivity()
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
            v = mBinding.root
            mViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
            mBinding.viewmodel = mViewModel

            hmProfile = getHashMap(mContext, mContext.getString(R.string.key_profile))
            setLanguage()
            setDetails()

            mBinding.profileDetailLayout.setOnClickListener {
                startActivity(
                    Intent(
                        requireActivity(),
                        ProfileActivity::class.java
                    ).putExtra(getString(R.string.key_lang_hm), hmLangValue)
                        .putExtra(
                            getString(R.string.key_calling_fragment),
                            getString(R.string.fragment_user)
                        )
                )
            }

            mBinding.languageLayout.setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        LanguageActivity::class.java
                    ).putExtra(getString(R.string.key_lang_hm), hmLangValue).putExtra(
                        getString(R.string.key_lang_id),
                        getData(mContext, mContext.getString(R.string.key_lang_id))
                    ).putExtra(
                        mContext.getString(R.string.key_calling_fragment),
                        mContext.getString(R.string.fragment_user)
                    )
                )
            }

            mBinding.changePswdLayout.setOnClickListener {
                startActivity(
                    Intent(
                        context,
                        ResetPasswordActivity::class.java
                    ).putExtra(getString(R.string.key_lang_hm), hmLangValue).putExtra(
                        mContext.getString(R.string.key_calling_fragment),
                        mContext.getString(R.string.fragment_user)
                    )
                )
            }

            mBinding.cvLogOut.setOnClickListener {
                clearData(requireActivity(), "user_token")
                clearData(requireActivity(), mContext.getString(R.string.key_profile))
                clearData(requireActivity(), mContext.getString(R.string.key_phone_number))
                clearData(requireActivity(), "user_id")

                startActivity(
                    Intent(
                        mContext,
                        LanguageActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                )

                activity?.overridePendingTransition(
                    R.anim.slide_in_left_first,
                    R.anim.slide_out_left_first
                )
                activity?.finish()
            }

            //logOut()
            //backNavigation()
        } catch (e: Exception) {
        }
        return v

    }

    private fun setDetails() {
        mBinding.profileNameTxt.text = hmProfile[mContext.getString(R.string.field_fname)]
        val profilePicUrl = hmProfile[mContext.getString(R.string.key_profile_url)]
        if (profilePicUrl != "null") {

            mBinding.pbProfileImage.visibility = View.VISIBLE
            AuroRepository().loadImageWithGlide(
                mContext,
                profilePicUrl!!,
                mBinding.profileImg,
                mBinding.pbProfileImage
            )

        }
    }


    private fun setLanguage() {
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(mContext, "en")
        } else {
            setAppLocale(mContext, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        mBinding.certificateHeading.text = hmLangValue["item_settings"]
        mBinding.profileDetailsTxt.text = hmLangValue["profile_details"]
        mBinding.languageTxt.text = hmLangValue["language"]
        mBinding.chngePswdTxt.text = hmLangValue["change_password"]
        mBinding.crtAcntTxt.text = hmLangValue["logout"]
    }

    private fun logOut() {
        Log.d(TAG, "logOut: logout button clicked")
        mBinding.cvLogOut.setOnClickListener {
            clearData(requireActivity(), "user_token")
            clearData(requireActivity(), mContext.getString(R.string.key_profile))
            clearData(requireActivity(), mContext.getString(R.string.key_phone_number))
            clearData(requireActivity(), "user_id")

            startActivity(
                Intent(
                    mContext,
                    LanguageActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            )

            activity?.overridePendingTransition(
                R.anim.slide_in_left_first,
                R.anim.slide_out_left_first
            )
            activity?.finish()
        }
    }


//    private fun backNavigation() {
//        mBinding.cvBackUser.setOnClickListener {
//            val manager: FragmentManager = requireActivity().supportFragmentManager
//            val trans: FragmentTransaction = manager.beginTransaction()
//            trans.remove(this)
//            trans.commit()
//            manager.popBackStack()
//            activity?.finish()
//        }
//    }

    override fun onResume() {
        super.onResume()
        setLanguage()
        hmProfile = getHashMap(mContext, mContext.getString(R.string.key_profile))
        AuroRepository().loadImageWithGlide(
            mContext,
            hmProfile[mContext.getString(R.string.key_profile_url)].toString(),
            mBinding.profileImg,
            mBinding.pbProfileImage
        )
        mBinding.profileNameTxt.text = hmProfile[mContext.getString(R.string.field_fname)]
        Log.d(TAG, "onResume: ")
    }

    override fun onProfileSaved(profilePicUrl: String, context: Context) {

    }
}