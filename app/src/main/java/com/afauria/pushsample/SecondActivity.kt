package com.afauria.pushsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class SecondActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate, " + intent?.extras)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token: " + it)
        }.addOnFailureListener {
            Log.d(TAG, "token error" + it)
        }.addOnCanceledListener {
            Log.d(TAG, "token cancel")
        }.addOnCompleteListener {
            Log.d(TAG, "token complete" + it)
        }
        setContentView(R.layout.activity_main)
        button.text = TAG
    }
}
