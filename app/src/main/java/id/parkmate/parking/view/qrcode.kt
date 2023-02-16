package id.parkmate.parking.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import id.parkmate.R


class qrcode : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    private val tag: String? = "QR-CODE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode)
        //val backButton = findViewById<Button>(R.id.back)

        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
        val nama = sharedPreferences.getString("nama", null)!!
        val npm = sharedPreferences.getString("npm", null)!!

        val nims= intent.getStringExtra("nims")
        val nopols= intent.getStringExtra("nopols")
        val qrcodes = findViewById<ImageView>(R.id.qrcodes)
//        val encryptedText = crypt().encrypt("$nims,$nopols")
//        val qrEncoder = QRGEncoder(encryptedText, null, QRGContents.Type.TEXT, 500)
        val qrEncoder = QRGEncoder("$nama,$npm,$nopols", null, QRGContents.Type.TEXT, 500)
        qrcodes.setImageBitmap(qrEncoder.bitmap)
        Log.d(tag, "Nama : $nama")
        Log.d(tag, "Npm : $npm")
        Log.d(tag, "nopol : $nopols")

//        backButton.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

    }
}