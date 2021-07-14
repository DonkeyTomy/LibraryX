package com.zzx.utils.data

import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**@author Tomy
 * Created by Tomy on 13/7/2021.
 */
object EncryptUtil {

    fun encryptRSA(msg: String, publicKey: String): String {
        val decoded = Base64.decode(publicKey, Base64.NO_WRAP)
        val pubKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(decoded))
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)
        return Base64.encodeToString(cipher.doFinal(msg.toByteArray()), Base64.NO_WRAP)
    }

}