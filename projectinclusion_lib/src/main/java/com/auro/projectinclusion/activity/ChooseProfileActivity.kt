package com.pi.projectinclusion.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.adapter.ChooseProfileAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.ProfileAuthListener
import com.pi.projectinclusion.databinding.ActivityChooseProfileBinding
import com.pi.projectinclusion.model.ProfileModel
import com.pi.projectinclusion.repository.AuroRepository
import com.pi.projectinclusion.url.AllApi


class ChooseProfileActivity : AppCompatActivity(), ProfileAuthListener, ClickProfileId {
    private lateinit var mBinding: ActivityChooseProfileBinding
    private lateinit var mProfileList: ArrayList<ProfileModel>
    private var mProfileId = ""
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [onContinue]
     * [setLanguage]
     * [setBackNavigation]
     * [clickToGetProfileId]
     * [onProfileNetworkStarted]
     * [onCoroutineProfileApiSuccess]
     * [onProfileApiFailure]
     * [onBackPressed]
     * [onResume]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Log.d(TAG, "onCreate:")

        AuroRepository().setStatusBarColor(this)
        initViewModel()
        setLanguage()
        setBackNavigation()
        onContinue()
    }


    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */
    private fun onContinue() {
        mBinding.chooseProfileBttn.setOnClickListener {
            if (mProfileId.isEmpty()) {
                toast(hmLangValue["error_select_profile"].toString())
            } else {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .putExtra(getString(R.string.key_userTypeId), mProfileId)
                )
            }

        }
    }

    private fun setBackNavigation() {
        mBinding.cvBackChoose.setOnClickListener {
            val intent = Intent(this, LanguageActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun setLanguage() {
        if (getData(this, getString(R.string.key_lang_id)) == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(this, getString(R.string.key_lang_list))

        mBinding.selectText.text = hmLangValue["please_select_txt"]
        mBinding.userBttn.text = hmLangValue["cont_text"]
    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_profile)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mProAuthListener = this
        mBinding.usertypeProgress.visibility = View.VISIBLE
        viewModel.getCoroutineProfileData(this)
    }

    override fun clickToGetProfileId(profileId: String) {
        mProfileId = profileId
        //  Log.d(TAG, "clickToGetProfileId: ")
    }

    /**
     * --------------------------------------- On Item Clicks & api event listeners-------------------
     */

    override fun onProfileNetworkStarted() {

    }

    /**
     * When api call is successful, inflate recycler view with response
     */
    override fun onCoroutineProfileApiSuccess(profile_response: List<ProfileModel>) {
        mProfileList = ArrayList()
        mProfileList.clear()

        for (i in profile_response.indices) {
            mBinding.usertypeProgress.visibility = View.GONE

            var name = "null"
            val key = "key_" + profile_response[i].name.toString()
                .lowercase() //setting key using response name to get dynamic language key from saved lang hashmap
            if (hmLangValue.containsKey(key)) {
                name =
                    hmLangValue["key_" + profile_response[i].name.toString().lowercase()].toString()
            }
            val profileId = profile_response[i].id.toString()
            var profileIcon = ""
            if (!profile_response[i].icon.equals(null)) {
                profileIcon = profile_response[i].icon.toString()
            }

            val completeIconUrl = AllApi.IMAGE_URL + profileIcon
            if (name != "null")
                mProfileList.add(ProfileModel(name, profileId, completeIconUrl, ""))

        }
        mBinding.profileRecycler.layoutManager = LinearLayoutManager(this)
        mBinding.profileRecycler.adapter = ChooseProfileAdapter(this, mProfileList, this)
    }

    /**
     * Api fail listener, if api fails and network is present then recall api
     */
    override fun onProfileApiFailure() {
        // Log.d(TAG, "onProfileApiFailure: api call failed")
        if (isNetwork(this)) {
            viewModel.getCoroutineProfileData(this)
        }
    }

    /**
     * ---------------------------------------LIFECYCLE CALLS---------------------------------------
     */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LanguageActivity::class.java)
        startActivity(intent)

        finish()
    }

    override fun onResume() {
        super.onResume()
        setLanguage()
    }
}