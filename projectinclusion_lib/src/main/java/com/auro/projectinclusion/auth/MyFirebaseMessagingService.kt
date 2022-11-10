package com.pi.projectinclusion.auth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pi.projectinclusion.R
import com.pi.projectinclusion.getData
import com.pi.projectinclusion.saveData
import com.google.android.gms.common.util.PlatformVersion
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val channelName = "com.pi.projectinclusion"

private const val TAG = "MyFirebaseMessagingServ"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        saveData(this, getString(R.string.key_fcm_token), token)
        Log.d(TAG, "onNewToken: token: " + getData(this,getString(R.string.key_fcm_token)))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: data: " + remoteMessage.data)
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("notifs")
            val data = remoteMessage.data
            val title = data["title"]
            val message = data["message"]
            val click_action = data["click_action"]
            sendNotification(this, title, message, click_action)
        } catch (e: Exception) {
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    fun sendNotification(
        context: Context,
        title: String?,
        message: String?,
        click_action: String?
    ) {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 26) {
                val notificationChannel = NotificationChannel(
                    channelId, channelName, IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }

            val defaultSoundUri: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder
            if (PlatformVersion.isAtLeastN()) {
                notificationBuilder =
                    NotificationCompat.Builder(context, channelId).setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setSmallIcon(R.drawable.app_logo)
                        .setPriority(IMPORTANCE_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLargeIcon(
                            BitmapFactory.decodeResource(
                                context.resources, R.drawable.app_logo
                            )
                        )

            } else {
                notificationBuilder = NotificationCompat.Builder(context).setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(R.drawable.app_logo)
                    .setPriority(IMPORTANCE_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources, R.drawable.app_logo
                        )
                    )
            }

            Log.d(TAG, "sendNotification: click_action: $click_action")
            val intent = Intent(click_action)
            // intent.action = Intent.ACTION_VIEW
            // intent.data = Uri.parse(deepLink)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            notificationBuilder.setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
            notificationManager.notify(0, notificationBuilder.build())

    }


// show notification
//    override fun onMessageReceived(message: RemoteMessage) {
//
//        if (message.notification != null) {
//            Log.d(
//                TAG,
//                "onMessageReceived: selected Activity:  " + message.data["activity"].toString()
//            )
//            generateNotification(
//                message.notification!!.title!!,
//                message.notification!!.body!!,
//                message.data["activity"].toString()
//            )
//        }
//    }
//
//    //attach notification to custom layout
//    private fun getRemoteView(title: String, subTitle: String): RemoteViews {
//
//        val remoteView = RemoteViews("com.pi.projectinclusion", R.layout.layou_notification)
//        remoteView.setTextViewText(R.id.tvNotifTitle, title)
//        remoteView.setTextViewText(R.id.tvNotifSubTitle, subTitle)
//        remoteView.setImageViewResource(R.id.ivNotifIcon, R.drawable.app_logo)
//
//        return remoteView
//    }
//
//
//    //Generate notification
//    private fun generateNotification(title: String, subTitle: String, mActivity: String) {
//        val intent = Intent(this, getSelectedActivity(mActivity)::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//        var builder = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.app_logo)
//            .setAutoCancel(true)
//            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//
//        builder = builder.setContent(getRemoteView(title, subTitle))
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel =
//                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        notificationManager.notify(0, builder.build())
//    }
//
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//    }
//
//
//    fun getSelectedActivity(selectedActivity: String): Activity {
//        var activity: Activity = SplashActivity()
//        if (selectedActivity.equals("ChooseProfileActivity")) {
//            activity = ChooseProfileActivity()
//        }
//        return activity
//    }

}