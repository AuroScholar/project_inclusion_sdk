package com.pi.projectinclusion.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pi.projectinclusion.*
import com.pi.projectinclusion.adapter.ScreeningAnsAdapter
import com.pi.projectinclusion.adapter.ScreeningQuesAdapter
import com.pi.projectinclusion.auth.AuthViewModel
import com.pi.projectinclusion.databinding.ActivityScreeningCertificatesBinding
import com.pi.projectinclusion.fragment.ScreeningResultFragment
import com.pi.projectinclusion.model.ScreeningQuesModel
import com.pi.projectinclusion.repository.AuroRepository


/**
 * TO ADD A NEW CERTIFICATE TYPE TO THE RECYCLER VIEW
 * PLEASE FOLLOW THESE STEPS:
 * STEP 1: Add a new string for the name of the new type
 * STEP 2: Add logic in if statements in adapter if it has different layout
 * STEP 3:
 *
 * API TO BE IMPLEMENTED:
 * 1. Certificate Type Api
 * 2. List of certificates
 *

 */

private const val TAG = "ScreeningCertificateActivity"


class ScreeningCertificateActivity : AppCompatActivity(),
    ScreeningQuesAdapter.OnSubmitClickListener, ScreeningAnsAdapter.OnAnsSelectedListener {
    private lateinit var mContext: Context
    private lateinit var mBinding: ActivityScreeningCertificatesBinding
    private var sectionId: Int = 1
    private var sectionName: String? = null
    private var marks: Int = 0
    private var totalQues: Int = 0
    private var quesCount: Int = 0
    private var message: String? = null
    private var intervention: String? = null
    private lateinit var viewModel: AuthViewModel
    private lateinit var hmLangValue: HashMap<String, String>
    private lateinit var hmMessage: HashMap<String, String>
    private lateinit var hmAnsCount: LinkedHashMap<String, String>
    private lateinit var hmIntervention: HashMap<String, String>
    private lateinit var hmSelectedAnswers: HashMap<String, String>
    private lateinit var quesModel: ScreeningQuesModel
    private lateinit var alsQuesList: ArrayList<ArrayList<String>>
    private var mAnsAdapter: ScreeningAnsAdapter? =
        null// List of certificates of currently selected certificate type of the user

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [initViewModel]
     * [setLanguage]
     * [setBackNavigation]
     * [checkAnsCount]
     * [onNextClick]
     * [inflateQuesList]
     * [initRecyclerView]
     * [onItemClick]
     * [onItemClick]
     * [onBackPressed]
     * [onResume]
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this
        hmMessage = HashMap()
        hmAnsCount = LinkedHashMap()
        hmIntervention = HashMap()
        hmSelectedAnswers = HashMap()
        //alsAnsList = ArrayList()

        //INIT
        AuroRepository().setStatusBarColor(this)
        initViewModel() // initializing view model details
        setLanguage()
        setBackNavigation()
        inflateQuesList(
            2,
            "Student has difficulty in visual Domain . ",
            "Kindly Take child to ENT specialist",
            "SECTION A (Visual Domain)",
            R.array.sectionA
        )



        onNextClick()
        mBinding.acbSubmit.setOnClickListener {
            if (checkAnsCount()) {
                mBinding.flScreeningResult.visibility = View.VISIBLE
                val manager: FragmentManager = supportFragmentManager
                val fragmentPopped: Boolean =
                    manager.popBackStackImmediate(getString(R.string.fragment_screening_result), 0)

                if (!fragmentPopped) { //fragment not in back stack, create it.
                    val fragment = ScreeningResultFragment()
                    val bundle = Bundle()
                    bundle.putSerializable(getString(R.string.key_interventions), hmIntervention)
                    bundle.putSerializable(getString(R.string.key_screening_messages), hmMessage)
                    bundle.putSerializable(getString(R.string.key_ans_count), hmAnsCount)
                    fragment.arguments = bundle

                    val frag: FragmentTransaction = manager.beginTransaction()
                    frag.replace(R.id.flScreeningResult, fragment)
                    frag.addToBackStack(getString(R.string.fragment_screening_result))
                    frag.commit()
                }
            } else {
                toast(hmLangValue["error_all_ques"].toString())
            }
        }
    }

    /**
     * -------------------------------------- CUSTOM METHODS-----------------------------
     */


    private fun onNextClick() {
        mBinding.acbNext.setOnClickListener {

            when (sectionId) {
                1 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }

                    inflateQuesList(
                        2,
                        "Student has difficulty in Auditory Domain .",
                        "\n• Practice Sequencing with Sounds\n" +
                                "• Name the Mistake\n" +
                                "• Repeat After Me\n" +
                                "• I Went to the Market and I Bought…\n" +
                                "• Comprehension Check",
                        "SECTION B (Auditory Domain)",
                        R.array.sectionB
                    )
                    sectionId = 2
                }
                2 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }
                    inflateQuesList(
                        5,
                        "Student has difficulty in Social  Domain.",
                        "\n• Roll the Ball\n" +
                                "• Staring Contest \n" +
                                "• Emotion Charades\n" +
                                "• Improvisational Stories\n" +
                                "• Simon Says\n" +
                                "• Scavenger Hunts",
                        "SECTION C (Social Domain)",
                        R.array.sectionC
                    )
                    sectionId = 3
                }
                3 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }
                    inflateQuesList(
                        4,
                        "Student has difficulty in Academic Domain.",
                        "\n• Take your children to the kitchen to help prepare food. Depending on what you're doing and how interested your child is, you can let them help you or just give them their own plastic bowl and spoon to mimic your actions.\n" +
                                "• Make a tray of water or sand for your child to play with different textures. You can also add toys.\n" +
                                "• Play with dough, either clay or homemade. Use molds and have fun cutting and assembling shapes. Cookie cutters or large cups with large handles may be easier for your child to grasp.\n" +
                                "• Read books together\n" +
                                "•  Sing, dance and make noise with your child. Your little one can use any cooking pot and homemade instruments to keep rhythm with you.\n",
                        "SECTION D (Academic Domain)",
                        R.array.sectionD
                    )
                    sectionId = 4
                }
                4 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }
                    inflateQuesList(
                        4,
                        "Student has difficulty in Language and Communication Domain.",
                        "\n• Play Telephone\n" +
                                "• Emotional Charades\n" +
                                "• 20 Questions\n" +
                                "• Identify the Object\n" +
                                "• Changing the Leader",
                        "SECTION E (Language and Communication Domain)",
                        R.array.sectionE
                    )
                    sectionId = 5
                }
                5 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }
                    inflateQuesList(
                        3,
                        "Student has difficulty in Motor Domain.",
                        "\n• Cutting and Pasting\n" +
                                "• Playdough\n" +
                                "• Threading or Lacing\n" +
                                "• Paper Tearing\n" +
                                "• Paper Folding\n" +
                                "• Self-Care Tasks\n" +
                                "• Blocks",
                        "SECTION F (Motor Domain)",
                        R.array.sectionF
                    )
                    sectionId = 6
                }
                6 -> {
                    if (!checkAnsCount()) {
                        toast(hmLangValue["error_all_ques"].toString())
                        return@setOnClickListener
                    }
                    inflateQuesList(
                        4,
                        "Student has difficulty in Sensory Domain.",
                        "\n• Create a texture-filled scavenger hunt \n" +
                                "• Explore sensory bins\n" +
                                "• Play with water\n" +
                                "• “Cook” with your hands",
                        "SECTION G (Sensory Domain)",
                        R.array.sectionG
                    )
                    sectionId = 7
                    mBinding.acbNext.visibility = View.GONE
                    mBinding.acbSubmit.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun checkAnsCount(): Boolean {
        Log.d(TAG, "onNextClick: hmSelectedAnswers: $hmSelectedAnswers")
        val alsAnsList: ArrayList<String> = ArrayList(hmSelectedAnswers.values.toList())
        val yesPredicate: (String) -> Boolean = { it == "Yes" }
        val noPredicate: (String) -> Boolean = { it == "No" }

        Log.d(TAG, "onNextClick: count: " + alsAnsList.count(yesPredicate))
        if (alsAnsList.count(yesPredicate) >= marks) {
            hmMessage[sectionName.toString()] = message!!
            hmIntervention[sectionName.toString()] = intervention!!

        }
        val countMessage =
            "Yes: " + alsAnsList.count(yesPredicate) + ", No: " + alsAnsList.count(noPredicate)
        hmAnsCount[sectionName.toString()] = countMessage
        Log.d(TAG, "checkAnsCount: hmAnsCount: $hmAnsCount")
        if (alsAnsList.size < totalQues) {
            return false
        }
        return true
    }

    private fun inflateQuesList(
        mark: Int,
        messages: String,
        interventions: String,
        sectionNames: String,
        arraySectionId: Int
    ) {
        //TODO Call api here & Remove this temp code
        hmSelectedAnswers.clear()
        val alsTemp: ArrayList<String> =
            ArrayList(resources.getStringArray(arraySectionId).toList())
        val alsParentTemp: ArrayList<ArrayList<String>> = ArrayList(10)

        for (i in alsTemp) {
            val dummy: ArrayList<String> = ArrayList(3)
            dummy.add(i)
            dummy.add("Yes")
            dummy.add("No")
            alsParentTemp.add(dummy)
        }
        marks = mark
        totalQues = alsTemp.size
        message = messages
        intervention = interventions
        sectionName = sectionNames
        mBinding.tvSectionName.text = sectionName
        Log.d(TAG, "inflateQuesList: alsParentTemp: $alsParentTemp")

        quesModel = ScreeningQuesModel(alsParentTemp)
        alsQuesList = ArrayList(quesModel.response!!)
        initRecyclerView()// Setting back navigation by finishing activity
        mBinding.nsvQues.smoothScrollTo(0, 0)
        Log.d(TAG, "onNextClick: hmMessage: $hmMessage")
        Log.d(TAG, "onNextClick: hmIntervention: $hmIntervention")
        //Log.d(TAG, "onNextClick: alsAnsList: $alsAnsList)
        Log.d(TAG, "onNextClick: sectionId: $sectionId")
    }

    private fun setLanguage() {
        //Setting language keys from api based for hindi or english
        if (getData(mContext, mContext.getString(R.string.key_lang_id)) == "1") {
            setAppLocale(this, "en")
        } else {
            setAppLocale(this, "hi")
        }
        hmLangValue = getHashMap(mContext, mContext.getString(R.string.key_lang_list))

        mBinding.tvCertHeading.text = hmLangValue["screening_test"]
        mBinding.acbReset.text = hmLangValue["reset_btn"]
        mBinding.acbSubmit.text = hmLangValue["submit_btn"]
        mBinding.acbSubmit.text = hmLangValue["submit_btn"]
        mBinding.acbNext.text = hmLangValue["next_txt"]
        /* mBinding.tvCertHeading.text = hmLangValue["lms_certificate"]
         mBinding.tvNoCert.text = hmLangValue["no_cert_txt"]*/
    }

    private fun initViewModel() { // View model binding
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screening_certificates)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        mBinding.viewmodel = viewModel
    }

    private fun setBackNavigation() { // Back navigation on click
        mBinding.cvBackCert.setOnClickListener {
            finish()
        }
    }


    private fun initRecyclerView() { // Recycler view for certificates of currently selected type
        mBinding.rvQues.layoutManager =
            LinearLayoutManager(this)
        // Passing context = mContext, certificates list = alsCertificateList, certificate type = certType, activity = this
        mBinding.rvQues.adapter = //TODO VALIDATE FOR NULL VALUE OF RESPONSE
            ScreeningQuesAdapter(mContext, alsQuesList, mAnsAdapter, this, this)

    }


    /**
     * -------------------------------------- OVERRIDE API LISTENER IMPLEMENTATIONS------------------------------
     */


    /**
     * -------------------------------------- LIFECYCLE AND OVERRIDE FUNCTIONS------------------------------
     */

    override fun onBackPressed() {
        // When back button is pressed inside Certificate Fragment
        if (supportFragmentManager.backStackEntryCount > 0) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setLanguage()

    }

    override fun onItemClick(pos: Int) {
        Log.d(TAG, "onItemClick: pos: $pos")

        //  alsQuesList.removeAt(pos)
        // mBinding.rvQues.smoothScrollToPosition(pos + 1)
//        if (pos != 0)
//            mBinding.tvQuesNo.text = getString(R.string.ques_no) + " " + (pos + 1) + ":"
//        else


    }

    @SuppressLint("NewApi", "SetTextI18n")
    override fun onItemClick(
        pos: Int,
        answer: String,
        ques: String
    ) {
        Log.d(TAG, "onItemClick: answer pos: $pos")
        Log.d(TAG, "onItemClick: answer string: $answer")

        if (hmSelectedAnswers.containsKey(ques)) {
            hmSelectedAnswers.replace(ques, answer)
        } else {
            hmSelectedAnswers[ques] = answer

            quesCount++

            mBinding.tvQuesNumber.text = "${hmLangValue["ques_no"]}: $quesCount/39"
        }
        //alsAnsList.add(answer)

        //  mAnsAdapter?.notifyItemRangeChanged(0, listSize, pos)

    }
}