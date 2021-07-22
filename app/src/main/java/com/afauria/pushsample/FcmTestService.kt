package com.afauria.pushsample

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by Afauria on 2021/2/4.
 */
class FcmTestService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "FcmTestService"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken: $p0")
    }

    override fun handleIntentOnMainThread(intent: Intent?): Boolean {
        Log.e(TAG, "handleIntentOnMainThread before" + intent?.extras)
        val key = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION
        val oldKey = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION.replace(
            Constants.MessageNotificationKeys.NOTIFICATION_PREFIX,
            Constants.MessageNotificationKeys.NOTIFICATION_PREFIX_OLD
        )
        //intent.extras拿到的是拷贝过的Bundle，需要直接修改intent的extras
//        intent?.extras?.remove(key)
        intent?.removeExtra(key)
        intent?.removeExtra(oldKey)
        return super.handleIntentOnMainThread(intent)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }
        val intent = remoteMessage.toIntent()
        val key = Constants.MessageNotificationKeys.ENABLE_NOTIFICATION
        intent.putExtra(key, "1")
        RemoteMessage(intent.extras).notification?.let {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            Handler(Looper.getMainLooper()).post {
                Toast(this).apply {
                    view = LayoutInflater.from(this@FcmTestService)
                        .inflate(R.layout.layout_notification, null, false)
                    setGravity(Gravity.END or Gravity.BOTTOM, 30, 30)
                    duration = Toast.LENGTH_LONG
                }.show()
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
