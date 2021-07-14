package com.zzx.utils.data

import java.nio.charset.Charset
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

/**
 * RSA加、解密算法工具类
 */
object RSAEncryptUtils {
    /** 指定key的大小  */
    private const val KEYSIZE = 512

    /**
     * 生成密钥对
     */
    @Throws(Exception::class)
    fun generateKeyPair(): Map<String, String> {
        /** RSA算法要求有一个可信任的随机数源  */
        val sr = SecureRandom()
        /** 为RSA算法创建一个KeyPairGenerator对象  */
        val kpg = KeyPairGenerator.getInstance("RSA")
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象  */
        kpg.initialize(KEYSIZE, sr)
        /** 生成密匙对  */
        val kp = kpg.generateKeyPair()
        /** 得到公钥  */
        val publicKey: Key = kp.public
        val publicKeyBytes = publicKey.encoded
        val pub = String(Base64.encodeBase64(publicKeyBytes), Charset.forName("UTF-8"))
        /** 得到私钥  */
        val privateKey: Key = kp.private
        val privateKeyBytes = privateKey.encoded
        val pri = String(Base64.encodeBase64(privateKeyBytes), Charset.forName("UTF-8"))
        val map: MutableMap<String, String> = HashMap()
        map["publicKey"] = pub
        map["privateKey"] = pri
        val rsp = kp.public as RSAPublicKey
        val bint = rsp.modulus
        val b = bint.toByteArray()
        val deBase64Value: ByteArray = Base64.encodeBase64(b)
        val retValue = String(deBase64Value)
        map["modulus"] = retValue
        return map
    }

    /**
     * 加密方法 source： 源数据
     */
    @Throws(Exception::class)
    fun encrypt(source: String, publicKey: String): String {
        val key: Key = getPublicKey(publicKey)
        /** 得到Cipher对象来实现对源数据的RSA加密  */
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val b = source.toByteArray()
        /** 执行加密操作  */
        val b1 = cipher.doFinal(b)
        return String(Base64.encodeBase64(b1), Charset.forName("UTF-8"))
    }

    /**
     * 解密算法 cryptograph:密文
     */
    @Throws(Exception::class)
    fun decrypt(cryptograph: String, privateKey: String): String {
        val key: Key = getPrivateKey(privateKey)
        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密  */
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val b1: ByteArray = Base64.decodeBase64(cryptograph.toByteArray())
        /** 执行解密操作  */
        val b = cipher.doFinal(b1)
        return String(b)
    }

    /**
     * 得到公钥
     *
     * @param key
     * 密钥字符串（经过base64编码）
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getPublicKey(key: String): PublicKey {
        val keySpec = X509EncodedKeySpec(
            Base64.decodeBase64(key.toByteArray())
        )
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 得到私钥
     *
     * @param key
     * 密钥字符串（经过base64编码）
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getPrivateKey(key: String): PrivateKey {
        val keySpec =
            PKCS8EncodedKeySpec(
                Base64.decodeBase64(key.toByteArray())
            )
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }

    fun sign(content: String, privateKey: String): String? {
        val charset = "UTF-8"
        try {
            val priPKCS8 = PKCS8EncodedKeySpec(
                Base64.decodeBase64(privateKey.toByteArray())
            )
            val keyf = KeyFactory.getInstance("RSA")
            val priKey = keyf.generatePrivate(priPKCS8)
            val signature = Signature.getInstance("SHA256WithRSA")
            signature.initSign(priKey)
            signature.update(content.toByteArray(charset(charset)))
            val signed = signature.sign()
            return String(Base64.encodeBase64(signed))
        } catch (e: Exception) {
        }
        return null
    }

    fun checkSign(content: String, sign: String?, publicKey: String?): Boolean {
        try {
            val keyFactory =
                KeyFactory.getInstance("RSA")
            val encodedKey: ByteArray = Base64.decode2(publicKey)
            val pubKey =
                keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))
            val signature = Signature
                .getInstance("SHA256WithRSA")
            signature.initVerify(pubKey)
            signature.update(content.toByteArray(charset("utf-8")))
            return signature.verify(Base64.decode2(sign))
        } catch (e: Exception) {
        }
        return false
    }
}