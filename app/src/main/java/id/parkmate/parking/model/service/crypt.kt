package id.parkmate.parking.model.service

import org.jasypt.util.text.AES256TextEncryptor

class crypt {
    private val secretKey = "JjeRK@gf7QmwsR_k8xM#^DgEqqeY7w/g"
    private val aes256 = AES256TextEncryptor()

    fun encrypt(text: String): String {
        aes256.setPassword(secretKey)
        return aes256.encrypt(text)
    }

    fun decrypt(text: String): String {
        aes256.setPassword(secretKey)
        return aes256.decrypt(text)
    }
}