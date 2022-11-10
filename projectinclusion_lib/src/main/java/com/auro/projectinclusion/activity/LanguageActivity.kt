package com.pi.projectinclusion.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.`interface`.GetLanguageID
import com.pi.projectinclusion.adapter.LanguageNewAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.auth.LangKeyListener
import com.pi.projectinclusion.auth.MyAuthListener
import com.pi.projectinclusion.databinding.ActivityLanguageBinding
import com.pi.projectinclusion.model.LanguageKeyModel
import com.pi.projectinclusion.model.LanguageModel
import com.pi.projectinclusion.repository.AuroRepository
import java.lang.NullPointerException

var internetError = "Please Check your Internet Connection"

class LanguageActivity : AppCompatActivity(), MyAuthListener, GetLanguageID,
    LanguageNewAdapter.OnNewLangClickListener, LangKeyListener {

    private lateinit var mLangList: ArrayList<LanguageModel>
    private lateinit var mBinding: ActivityLanguageBinding
    private lateinit var viewModel: AuthViewModel
    private var isForwardNav = true
    private lateinit var langKeyList: HashMap<String, String>
    private var mLangId: Int = 0
    private var mSelectedLang: String = ""
    private var hmLangValue: HashMap<String, String>? = null

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [getIntents]
     * [onContinue]
     * [setLanguage]
     * [getLangId]
     * [onNetworkCallStarted]
     * [onKeyListSuccess]
     * [onLangKeyApiFailure]
     * [onCoroutineApiSuccess]
     * [onAuthApiFailure]
     * [onItemClick]
     * [onBackPressed]
     * [onResume]
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Log.d(TAG, "onCreate:")
        langKeyList = HashMap()
        AuroRepository().setStatusBarColor(this)
        initViewModel()
        getIntents()
        backNavigation()
        onContinue()


    }

    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */


    /**
     * When user clicks continue button
     * if language is selected (i.e mLangId != 0 && hmLangValue != null)
     * then proceed
     * else
     * prompt user to select language
     */
    private fun onContinue() {
        mBinding.langButton.setOnClickListener {
            if (mLangId != 0 && hmLangValue != null && langKeyList.isNotEmpty()) {
                saveData(// save lang id
                    this@LanguageActivity,
                    getString(R.string.key_lang_id),
                    mLangId.toString()
                )
                saveHashMap(
                    this,
                    getString(R.string.key_lang_list),
                    langKeyList
                ) // storing hashmap of selected lang
                try {
                    hmLangValue = HashMap(
                        getHashMap(
                            this,
                            getString(R.string.key_lang_list)
                        )
                    )
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                if (hmLangValue != null) // set internet error variable
                    internetError = hmLangValue!!["error_internet_connection"].toString()
                if (isForwardNav) { // if navigation to choose profile
                    startActivity(
                        Intent(
                            this,
                            ChooseProfileActivity::class.java
                        ).putExtra(getString(R.string.key_lang_hm), hmLangValue)
                    )
                } else { // if called from user fragment
                    finish()
                }
            } else {
                toast(getString(R.string.error_select_language))
            }
        }
    }

    private fun backNavigation() {
        mBinding.cvBackLogin.setOnClickListener {
            finish()
        }
    }

    /**
     * pretty obvious
     */
    private fun getIntents() {
        if (intent.getSerializableExtra(getString(R.string.key_lang_hm)) != null) {
            hmLangValue =
                intent.getSerializableExtra(getString(R.string.key_lang_hm)) as HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */
            setLanguage()
            mLangId = getData(this, getString(R.string.key_lang_id)).toInt()
        }
        if (intent.getStringExtra(getString(R.string.key_calling_fragment)) != null) {
            if (intent.getStringExtra(getString(R.string.key_calling_fragment)) == getString(R.string.fragment_user)) {
                isForwardNav = false
                langKeyList = hmLangValue!!
            }
            mBinding.cvBackLogin.visibility = View.VISIBLE
        }

    }

    private fun setLanguage() {
        mBinding.welcomeText.text = hmLangValue!!["welcome_txt"]
        mBinding.langSelectionText.text = hmLangValue!!["language_choose"]
        mBinding.langBttnTxt.text = hmLangValue!!["cont_text"]
        mBinding.langPrefText.text = hmLangValue!!["lang_pref_string"]

    }

    private fun initViewModel() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
        viewModel.mAuthListener = this
        viewModel.mLangKeyListener = this

        mBinding.languageProgress.visibility = View.VISIBLE
    }

    override fun getLangId(lang_id: Int?) {
        mLangId = lang_id!!
        //  Log.d("LangId", mLangId.toString())
        if (mLangId == 2) {
            setAppLocale(this, "hi")
            mBinding.langSelectionText.setText(R.string.language_choose)
            mBinding.langPrefText.setText(R.string.lang_pref_string)
            mBinding.langBttnTxt.setText(R.string.cont_text)
            mBinding.welcomeText.setText(R.string.welcome_txt)
        } else {
            setAppLocale(this, "en")
            mBinding.langSelectionText.setText(R.string.language_choose)
            mBinding.langPrefText.setText(R.string.lang_pref_string)
            mBinding.langBttnTxt.setText(R.string.cont_text)
            mBinding.welcomeText.setText(R.string.welcome_txt)
        }
    }

    /**
     * --------------------------------------- On Item Clicks & api event listeners-------------------
     */

    override fun onNetworkCallStarted() {
        // Log.d("networkCall", "onNetworkStarted: ")
    }

    /**
     * Successful call to lang key api
     */
    override fun onKeyListSuccess(lang_response: List<LanguageKeyModel>) {
        langKeyList = HashMap()
        for (i in lang_response) {
            langKeyList[i.pageKey!!] = i.keyValue!!
        }
        langKeyList[getString(R.string.key_lang_name)] = mSelectedLang // inflating hashmap

        hmLangValue = HashMap(langKeyList)


        setLanguage()
        // set activity language to fetched key value pairs

    }

    /**
     * Api fail listener, if lang key api fails and network is present then recall api
     */
    override fun onLangKeyApiFailure() {
        //  Log.d(TAG, "onLangKeyApiFailure: api call failed")
        if (isNetwork(this)) {
            viewModel.getLangKeyData(mLangId.toString(), this)
        }
    }

    /**
     * Method gets list of language models called from api
     * @param lang_response: Response of Language api success
     */
    override fun onCoroutineApiSuccess(lang_response: List<LanguageModel>) {
        mLangList = ArrayList()
        mLangList.clear()
        // get body from api response from IO Coroutine to main thread
        for (i in lang_response.indices) { // fill ling with language from api
            val name = lang_response[i].name.toString()
            val translatedName = lang_response[i].translatedName
            val langId = lang_response[i].id
            mLangList.add(LanguageModel(name, langId, "", "", translatedName))
        }
        //Init recycler view
        AuroRepository().runOnMainThread {
            mBinding.languageProgress.visibility = View.GONE
            mBinding.languageRecycler.layoutManager = GridLayoutManager(this, 2)
            mBinding.languageRecycler.adapter =
                LanguageNewAdapter(this, mLangList, mLangId, this)
        }

    }

    /**
     * Api fail listener, if api fails and network is present then recall api
     */
    override fun onAuthApiFailure() {
        // Log.d(TAG, "onProfileApiFailure: api call failed")
        if (isNetwork(this)) {
            viewModel.getCoroutineLanguageData(this)
        }
    }

    /**
     * On click listener for recycler view of languages
     */
    override fun onItemClick(pos: Int, langLayout: ConstraintLayout, langName: TextView) {
        viewModel.getLangKeyData(
            (pos + 1).toString(),
            this
        ) // calling get lang key and value pair based on selected lang (pos)
        mLangId = pos + 1
        mSelectedLang = mLangList[pos].name.toString().lowercase()

        mBinding.languageRecycler.adapter?.notifyItemRangeChanged(0, mLangList.size, pos)
        //getLang.getLangId(langModel.id)
    }

    /**
     * ---------------------------------------LIFECYCLE CALLS---------------------------------------
     */

    override fun onBackPressed() {
        super.onBackPressed()
        if(isForwardNav) {
            finishAffinity();
        }else{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCoroutineLanguageData(this) // calling get languages api

    }

}