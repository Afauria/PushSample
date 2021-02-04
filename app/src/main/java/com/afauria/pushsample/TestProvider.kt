package com.afauria.pushsample

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp

/**
 * Created by Afauria on 2021/1/27.
 * e-mail : zhangweiyue@cvte.com
 */
class TestProvider : ContentProvider() {

    /** Called before [Application.onCreate].  */
    override fun onCreate(): Boolean {
        if (FirebaseApp.initializeApp(context!!) == null) {
            Log.i(TAG, "FirebaseApp initialization unsuccessful")
        } else {
            Log.i(TAG, "FirebaseApp initialization successful")
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(
        uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        private const val TAG = "TestProvider"
    }
}