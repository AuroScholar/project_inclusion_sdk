package com.pi.projectinclusion.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.pi.projectinclusion.R
import com.pi.projectinclusion.fragment.UserFragment


class UserProfileActivity : AppCompatActivity() {

    private var firstname: String? = null
    private var profilePicUrl: String? = null

    /**
     * NAVIGATION TO METHODS AND FUNCTIONS:
     * @return
     * [onBackPressed]
     * [loadFragment]
     * [onResume]
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        profilePicUrl = intent.getStringExtra(getString(R.string.key_profile_url))
        firstname = intent.getStringExtra(getString(R.string.first_name))

        loadFragment(UserFragment())

    }
    /**
     * -----------------------------CUSTOM METHODS AND FUNCTIONS--------------------------
     */
    private fun loadFragment(fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragmentPopped: Boolean =
            manager.popBackStackImmediate(getString(R.string.fragment_user), 0)

        if (!fragmentPopped) { //fragment not in back stack, create it.
            val bundle = Bundle()
            bundle.putString(getString(R.string.first_name), firstname)
            bundle.putString(getString(R.string.key_profile_url), profilePicUrl)
            fragment.arguments = bundle

            val frag: FragmentTransaction = manager.beginTransaction()
            frag.replace(R.id.fragment_container, fragment)
            frag.addToBackStack(getString(R.string.fragment_user))
            frag.commit()
        }
    }

    /**
     * -------------------------------------- LIFECYCLE AND OVERRIDE FUNCTIONS------------------------------
     */
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {

            finish()
            super.onBackPressed()
        }
    }
}