package com.afauria.pushsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //旧版本
//        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
//            Log.d(TAG, "get token: "+it.result?.token)
//        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token: $it")
        }.addOnFailureListener {
            Log.d(TAG, "token error, $it")
        }.addOnCanceledListener {
            Log.d(TAG, "token cancel")
        }.addOnCompleteListener {
            Log.d(TAG, "token complete, $it")
        }
        button.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}
