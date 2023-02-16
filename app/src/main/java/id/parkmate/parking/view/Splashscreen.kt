package id.parkmate.parking.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import id.parkmate.R
import id.parkmate.parking.model.service.ApiClient
import id.parkmate.parking.model.data.sessionmanager

class Splashscreen : AppCompatActivity() {
    private lateinit var sessionManager: sessionmanager
    private lateinit var apiClient: ApiClient
    private val tag: String? = "splashscreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        val splashTime: Long = 3000 // lama splashscreen berjalan

        Handler().postDelayed({
            sessionManager = sessionmanager(this)
            if (this.sessionManager.islogin()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Log.d(tag, "status : Sudah")
            }else{
                val intent = Intent(this, Loginpage::class.java)
                startActivity(intent)
                Log.d(tag, "status : Belum")
            }
            finish()
        }, splashTime)


    }

//    override fun onPause() {
//        super.onPause()
//        finish()
//    }


}