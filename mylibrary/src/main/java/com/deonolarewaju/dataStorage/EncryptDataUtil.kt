package com.deonolarewaju.dataStorage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object EncryptDataUtil {

    private val TRANSFORMDATA = "AES/GCM/NoPadding"
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val ENCRYPT_ALIAS = "encrypt_key"


    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (keyStore.containsAlias(ENCRYPT_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                ENCRYPT_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setUserAuthenticationRequired(false)
                build()
            }
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
        return keyStore.getKey(ENCRYPT_ALIAS, null) as SecretKey
    }

    fun encryptData(data: String): String? {
        try {
            val cipher = Cipher.getInstance(TRANSFORMDATA)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val encryptBytes = cipher.doFinal(data.toByteArray())
            return Base64.encodeToString(encryptBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun deCryptData(data: String): String? {
        try {
            val cipher = Cipher.getInstance(TRANSFORMDATA)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
            val encryptBytes = Base64.decode(data, Base64.DEFAULT)
            val decryptBytes = cipher.doFinal(encryptBytes)
            return String(decryptBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}