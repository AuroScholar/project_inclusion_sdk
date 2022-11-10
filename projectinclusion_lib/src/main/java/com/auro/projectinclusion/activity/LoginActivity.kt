package com.pi.projectinclusion.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.pi.projectinclusion.fragment.EmailFragment
import com.pi.projectinclusion.R
import com.pi.projectinclusion.getData
import com.pi.projectinclusion.saveData
import com.pi.projectinclusion.toast
import java.lang.Exception
import kotlin.system.exitProcess


private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var mUserTypeId: String
    private lateinit var context: Context
    private lateinit var flContainer: FrameLayout

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [onBackPressed]
     * [setBackNavigation]
     * [loadFragment]
     * [onResume]
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: ")
        val window = this.window
        context = this
        setBackNavigation()
        flContainer = findViewById(R.id.flProfileContainer)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.background_color)
        mUserTypeId = intent.getStringExtra(getString(R.string.key_userTypeId)).toString()
        saveData(this, getString(R.string.key_userTypeId), mUserTypeId)
        Log.d(TAG, "onCreate: $mUserTypeId")
        loadFragment(EmailFragment())

    }

    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */

    private fun setBackNavigation() {
        val backButton = findViewById<CardView>(R.id.cvBackLogin)
        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragManager = supportFragmentManager
        val fragTransaction = fragManager.beginTransaction()
        val bundle = Bundle()
        fragment.arguments = bundle

        fragManager.addOnBackStackChangedListener {
            if (fragManager.backStackEntryCount > 1) {
                Log.d(TAG, "loadFragment: backstack count: ${fragManager.backStackEntryCount}")
                flContainer.visibility = View.VISIBLE
            } else {
                flContainer.visibility = View.GONE
            }
        }
        fragTransaction.add(R.id.frame_frag_container, fragment).commit()
    }


    /**
     * ---------------------------------------LIFECYCLE CALLS---------------------------------------
     */
    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed: backstack count: " + supportFragmentManager.backStackEntryCount)
    }


}