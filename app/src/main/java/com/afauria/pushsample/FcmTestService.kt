package com.afauria.pushsample

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Created by Afauria on 2021/2/4.
 * e-mail : zhangweiyue@cvte.com
 */
class FcmTestService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "FcmTestService"
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken: $p0")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}