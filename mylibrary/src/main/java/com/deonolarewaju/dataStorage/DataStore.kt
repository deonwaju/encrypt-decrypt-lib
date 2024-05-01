package com.deonolarewaju.dataStorage

import android.content.Context
import android.content.SharedPreferences

object DataStore {

    private val SHARED_PREF_KEY = "sharedPrefKey"

    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String){
        val encrypted = EncryptDataUtil.encryptData(value)
        sharedPref.edit().putString(key, encrypted).apply()
    }

    fun getString(key: String, defaultValue: String): String?{
        val encryptedValue = sharedPref.getString(key, null)
        return encryptedValue?.let{
            EncryptDataUtil.deCryptData(it) ?: defaultValue
        }
    }
}