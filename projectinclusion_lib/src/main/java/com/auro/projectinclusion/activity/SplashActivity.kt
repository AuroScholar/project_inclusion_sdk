package com.pi.projectinclusion.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.messaging.FirebaseMessaging
import com.pi.projectinclusion.*
import com.pi.projectinclusion.repository.AuroRepository
import io.branch.referral.Branch


@SuppressLint("CustomSplashScreen")
private const val TAG = "SplashActivity"

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var hmProfileValue: HashMap<String, String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            FirebaseMessaging.getInstance().subscribeToTopic("myNotifs")
            AuroRepository().setStatusBarColor(this)
            val userId = getData(this, getString(R.string.key_user_id))
            hmProfileValue = try {
                getHashMap(
                    this, getString(R.string.key_profile)
                )// Handling back navigation on calling fragment bases
            } catch (e: NullPointerException) {
                null
            }


            if (userId !== "null") {
                Handler().postDelayed({

                    if (isNetwork(this)) {
                        if (hmProfileValue != null &&
                            hmProfileValue!![getString(R.string.field_fname)] != "null" &&
                            hmProfileValue!![getString(R.string.field_lname)] != "null" &&
                            hmProfileValue!![getString(R.string.field_dob)] != "null" &&
                            hmProfileValue!![getString(R.string.field_phone_number)] != "null" &&
                            hmProfileValue!![getString(R.string.field_state)] != "null" &&
                            hmProfileValue!![getString(R.string.field_district)] != "null" &&
                            hmProfileValue!![getString(R.string.key_profile_url)] != "null"
                        ) {
                            val intent = Intent(this, DashBoardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra(getString(R.string.key_profile), hmProfileValue)
                            intent.putExtra(
                                getString(R.string.key_calling_fragment),
                                getString(R.string.key_splash)
                            ).flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        toast(
                            getString(R.string.error_internet_connection)
                        )

                    }

                }, 3000) // 3000 is the delayed time in milliseconds.
            } else {
                Handler().postDelayed({

                    if (isNetwork(this)) {
                        val intent = Intent(this, LanguageActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        toast(
                            getString(R.string.error_internet_connection)
                        )


                    }

                }, 3000)
            }
        } catch (e: Exception) {
            Log.d(TAG, "onCreate: msg:  " + e.message)
        }
    }

    override fun onStart() {
        super.onStart()

        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                Log.i("BRANCH SDK", referringParams.toString())
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.i("BRANCH SDK", error.message)
            }
        }, this.intent.data, this)
    }

   /* override fun onNewIntent(intent: Intent?) {
        intent = intent
    }*/

}




