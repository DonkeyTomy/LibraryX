package com.zzx.utils.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

class ContentProviderSaver(context: Context): IDataSaver<String> {


    override fun saveInt(key: String, value: Int) {
        TODO("Not yet implemented")
    }

    override fun saveString(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override fun saveFloat(key: String, value: Float) {
        TODO("Not yet implemented")
    }

    override fun saveBoolean(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun saveLong(key: String, value: Long) {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String, defValue: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getString(key: String, defValue: String): String {
        TODO("Not yet implemented")
    }

    override fun getLong(key: String, defValue: Long): Long {
        TODO("Not yet implemented")
    }

    override fun getFloat(key: String, defValue: Float): Float {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }

    class DataContentProvider: ContentProvider() {

        override fun onCreate(): Boolean {
            TODO("Not yet implemented")
        }

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? {
            TODO("Not yet implemented")
        }

        override fun getType(uri: Uri): String? {
            TODO("Not yet implemented")
        }

        override fun insert(uri: Uri, values: ContentValues?): Uri? {
            TODO("Not yet implemented")
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
            TODO("Not yet implemented")
        }

        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ): Int {
            TODO("Not yet implemented")
        }

    }

}