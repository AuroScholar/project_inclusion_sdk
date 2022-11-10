package com.pi.projectinclusion

import android.app.Application
import com.instabug.apm.APM
import com.instabug.bug.BugReporting
import com.instabug.crash.CrashReporting
import com.instabug.library.Feature
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import com.instabug.library.ui.onboarding.WelcomeMessage
import com.instabug.library.visualusersteps.State
import io.branch.referral.Branch


class InclusionApp : Application() {
    companion object {
        lateinit var mContext: InclusionApp
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this

        // Branch object initialization
        Branch.getAutoInstance(this)

       /* Instabug.Builder(this, mContext.getString(R.string.instabug_app_token))
            .setInvocationEvents(
                InstabugInvocationEvent.NONE
            )
            .build()*/
        /*InstabugInvocationEvent.SHAKE,
                InstabugInvocationEvent.FLOATING_BUTTON*/

        Instabug.Builder(this, mContext.getString(R.string.instabug_app_token))
            .setInvocationEvents(InstabugInvocationEvent.NONE).build()
        Instabug.setReproStepsState(State.ENABLED)
//        Instabug.show();
        //        Instabug.show();
        BugReporting.setShakingThreshold(800)
        Instabug.setSessionProfilerState(Feature.State.ENABLED)
        CrashReporting.setState(Feature.State.ENABLED)
        CrashReporting.setAnrState(Feature.State.ENABLED)
        CrashReporting.reportException(NullPointerException("Test issue"))
        CrashReporting.reportException(NullPointerException("Test issue"), "Exception identifier")
        CrashReporting.setNDKCrashesState(Feature.State.ENABLED)
        APM.setColdAppLaunchEnabled(true)
        APM.setHotAppLaunchEnabled(true)
        APM.endAppLaunch()
        Instabug.setTrackingUserStepsState(Feature.State.ENABLED)
        Instabug.setWelcomeMessageState(WelcomeMessage.State.LIVE)
        Instabug.showWelcomeMessage(WelcomeMessage.State.LIVE)
    }
}