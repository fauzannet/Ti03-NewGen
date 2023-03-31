package id.parkmate.parking.view


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import id.parkmate.R
import id.parkmate.parking.model.data.sessionmanager
import id.parkmate.parking.model.service.waktu

class scanqr : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var sessionManager: sessionmanager
    private lateinit var sharedPreferences: SharedPreferences
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.scanqr)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val animationView = findViewById<LottieAnimationView>(R.id.animasiqr)
        animationView.alpha = 0.3f

        sessionManager = sessionmanager(this)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                pindahdata(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    fun pindahdata(query: String) {
        var string = query

        if (string == "12345") {
            var errorMessage: String = this.resources.getString(R.string.errormessage)
            var errorTitle: String = this.resources.getString(R.string.errortitle)
            AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
                .setTitle(errorTitle)
                .setMessage(errorMessage)
                .setOnClickListener(object : OnDialogClickListener {
                    override fun onClick(dialog: AestheticDialog.Builder) {

                        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
                        sessionManager.hascheckin()
                        val waktus = waktu().waktuString
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(sessionmanager.waktucheckIN, waktus)
                        sessionManager.waktucheckin(waktus)
                        val waktuss = sharedPreferences.getString(sessionmanager.waktucheckIN, null)
                        Log.d("scanqr", "$waktuss")

                        pindah()
                        kapasitas()
                        dialog.dismiss()

                    }
                })
                .show()
        }else{
            var errorMessage: String = this.resources.getString(R.string.errormessage)
            var errorTitle: String = this.resources.getString(R.string.errortitle)
            AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.ERROR)
                .setTitle(errorTitle)
                .setMessage(errorMessage)
                .setOnClickListener(object : OnDialogClickListener {
                    override fun onClick(dialog: AestheticDialog.Builder) {
                        pindah()
                        dialog.dismiss()
                    }
                })
                .show()
        }
    }

    fun pindah(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    fun kapasitas()
    {
        val userRef = database.getReference("data")
        userRef.child("kapasitas").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nilai = dataSnapshot.value.toString().toInt()
                val nilaiBaru = nilai - 1
                Log.d("kapasitas","nilai = $nilai")
                Log.d("kapasitas","nilai pengurangan = $nilaiBaru")
                val stringnilai: String = nilaiBaru.toString()
                userRef.child("kapasitas").setValue(stringnilai)
                Log.d("kapasitas",stringnilai)
            }
            override fun onCancelled(error: DatabaseError) {
                // An error occurred
            }
        })
    }

}
