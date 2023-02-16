package id.parkmate.parking.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import id.parkmate.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.scanplat.*

class scanplat : AppCompatActivity(){

    val keyResult: String = "key_result"
    private val MY_PERMISSIONS_REQUEST_CAMERA: Int = 101
    private lateinit var mCameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer
    private val tag: String? = "MainActivity"

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanplat)

        val platbtn = findViewById<Button>(R.id.platbtn)
        val surface_camera_preview = findViewById<SurfaceView>(R.id.surface_camera_preview)
        val hasilplat = findViewById<TextView>(R.id.hasilplat)

        val nimss= intent.getStringExtra("nims")

        requestForPermission()

        textRecognizer = TextRecognizer.Builder(this@scanplat).build()
        if (!textRecognizer.isOperational) {
            Toast.makeText(this, "Dependencies are not loaded yet...please try after few moment!!", Toast.LENGTH_SHORT)
                .show()
            Log.e(tag, "Dependencies are downloading....try after few moment")
            return
        }

        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(720, 720)
            .setAutoFocusEnabled(true)
            .setRequestedFps(30.0f)
            .build()

        surface_camera_preview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }
            override fun surfaceDestroyed(p0: SurfaceHolder) {
                mCameraSource.stop()
            }
         @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(surface_camera_preview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    toast("Error:" + e.message)
                }
            }

        }
        )

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems

                if (items.size() <= 0) {
                    return
                }

                hasilplat.post {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    hasilplat.text = stringBuilder.toString()

                }
            }
        })

        platbtn.setOnClickListener {
            val intent = Intent(this, qrcode::class.java)
            val hasilplats = findViewById<TextView>(R.id.hasilplat)
                intent.putExtra("nims",nimss)
                intent.putExtra("hasil",hasilplats.text.toString())
                startActivity(intent)
//            val intent = Intent()
//            val result = hasilplat.text.toString()
//            intent.putExtra("result", result)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
        }
    }

    private fun requestForPermission() {

        if (ContextCompat.checkSelfPermission(
                this@scanplat,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@scanplat,
                    Manifest.permission.CAMERA
                )
            ) {
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this@scanplat,
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
        } else {
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@scanplat,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun toast(text: String) {
        Toast.makeText(this@scanplat, text, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                } else {
                    requestForPermission()
                }
                return
            }else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraSource.release()
    }

}