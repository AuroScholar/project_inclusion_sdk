package com.pi.projectinclusion.broadcastReciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class OTPReceiver : BroadcastReceiver()
{

    override fun onReceive(p0: Context?, p1: Intent?)
    {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(p1)
        for (sms in messages)
        {
            val msg = sms.messageBody
            Log.d("Broadcast", "onReceive: $msg")
        }
    }

}