package id.parkmate.parking.model.data

import android.content.Context
import android.content.SharedPreferences


class sessionmanager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val Nama = "TestNama"
        const val Npm = "TestNpm"
        const val NpmImg = "TestNpmImg"
        const val waktucheckIN = "waktucheckin"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    fun savenama(nama: String) {
        val editor = prefs.edit()
        editor.putString(Nama, nama)
        editor.apply()
    }
    fun savenpm(npm: String) {
        val editor = prefs.edit()
        editor.putString(Npm, npm)
        editor.apply()
    }
    fun savenpmimg(npmimg: String) {
        val editor = prefs.edit()
        editor.putString(NpmImg, npmimg)
        editor.apply()
    }

    fun waktucheckin(waktucheckin: String) {
        val editor = prefs.edit()
        editor.putString(waktucheckIN, waktucheckin)
        editor.apply()
    }

    fun hapuscheckin() {
        val editor = prefs.edit()
        editor.remove("waktucheckin")
    }
    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun islogin(): Boolean {
        return prefs.getBoolean("login", false)
    }

    fun haslogin(){
        val editor = prefs.edit()
        editor.putBoolean("login", true).apply()
    }

    fun ischeckin(): Boolean {
        return prefs.getBoolean("checkin", false)
    }

    fun hascheckin(){
        val editor = prefs.edit()
        editor.putBoolean("checkin", true).apply()
    }

    fun undocheckin(){
        val editor = prefs.edit()
        editor.putBoolean("checkin", false).apply()
    }

    fun logout(){
        val editor = prefs.edit()
        editor.clear().apply()
    }
}