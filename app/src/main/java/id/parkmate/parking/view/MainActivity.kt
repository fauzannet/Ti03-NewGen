package id.parkmate.parking.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.sachinvarma.easypermission.EasyPermissionInit
import com.sachinvarma.easypermission.EasyPermissionList
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import id.parkmate.R
import id.parkmate.parking.model.data.Data
import id.parkmate.parking.model.data.ListAdapter
import id.parkmate.parking.model.data.sessionmanager
import id.parkmate.parking.model.data.sessionmanager.Companion.Nama
import id.parkmate.parking.model.data.sessionmanager.Companion.Npm
import id.parkmate.parking.model.data.sessionmanager.Companion.waktucheckIN
import id.parkmate.parking.model.service.ApiClient
import id.parkmate.parking.model.service.waktu
import kotlinx.android.synthetic.main.parkir.*
import java.util.*


class MainActivity : AppCompatActivity() {

        private lateinit var sessionManager: sessionmanager
        private lateinit var apiClient: ApiClient
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var hasilplat: String
        private val MY_PERMISSIONS_REQUEST_CAMERA: Int = 100
        private lateinit var locationRequest: LocationRequest
        private var fusedLocationProviderClient: FusedLocationProviderClient? = null
        private lateinit var textRecognizer: TextRecognizer
        private val tag: String? = "MainActivity"
        val database = FirebaseDatabase.getInstance()
        private lateinit var dbref : DatabaseReference
        private lateinit var userRecyclerview : RecyclerView
        private lateinit var userArrayList : ArrayList<Data>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
        val nama = sharedPreferences.getString(Nama, null)
        setContentView(R.layout.activity_main)

        val generateButton = findViewById<Button>(R.id.generate)
        val scanplatbtn = findViewById<Button>(R.id.scannopol)
        val buttoncheckin = findViewById<CardView>(R.id.card2)
        val scanplatbtns = findViewById<ImageButton>(R.id.card3)
        val show = findViewById<ImageButton>(R.id.card4)
        val checkintbn = findViewById<Button>(R.id.scancheckin)
        val layoutcheckin = findViewById<LinearLayout>(R.id.layoutcheckin)
        val layoutnopol = findViewById<LinearLayout>(R.id.layoutnopol)

        val judul = findViewById<TextView>(R.id.judul)
        judul.text = "$nama"

        val permission: MutableList<String> = ArrayList()
        permission.add(EasyPermissionList.WRITE_EXTERNAL_STORAGE)
        permission.add(EasyPermissionList.READ_EXTERNAL_STORAGE)
        permission.add(EasyPermissionList.ACCESS_FINE_LOCATION)
        permission.add(EasyPermissionList.ACCESS_COARSE_LOCATION)
        permission.add(EasyPermissionList.CAMERA)

        EasyPermissionInit(this@MainActivity, permission)

        checkdatabase()

        userRecyclerview = findViewById(R.id.list_activity)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userArrayList = arrayListOf<Data>()
        getUserData()


        val userRef = database.getReference("data")
        userRef.child("kapasitas").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nilai = dataSnapshot.value.toString().toInt()
                Log.d("kapasitass","nilai = $nilai")
                arc_progress.progress = nilai

            }
            override fun onCancelled(error: DatabaseError) {
                // An error occurred
            }
        })

        sessionManager = sessionmanager(this)
        if (this.sessionManager.ischeckin()){
            layoutnopol.visibility = View.VISIBLE
            layoutcheckin.visibility = View.GONE
            Log.d(tag, "statuschekcin : Sudah")
        }else{
            layoutnopol.visibility = View.GONE
            layoutcheckin.visibility = View.VISIBLE
            Log.d(tag, "statuscheckin : Belum")
        }

        scanplatbtns.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, Loginpage::class.java)
            startActivity(intent)
        }

        scanplatbtn.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        checkintbn.setOnClickListener {
//            sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
//            sessionManager.hascheckin()
//
//            val waktus = waktu().waktuString
//
//            val editor: SharedPreferences.Editor = sharedPreferences.edit()
//            editor.putString(waktucheckIN, waktus)
//            sessionManager.waktucheckin(waktus)
//
//            val waktuss = sharedPreferences.getString(waktucheckIN, null)
//            Log.d(tag, "$waktuss")
//            finish()
//            overridePendingTransition(0, 0)
//            startActivity(intent)
//            overridePendingTransition(0, 0)

            val intent = Intent(this, scanqr::class.java)
            startActivity(intent)
        }

        show.setOnClickListener {
            sessionManager.undocheckin()
            Log.d(tag, "statuschekcin : Belum")
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }


        generateButton.setOnClickListener {
            qrgen()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === RESULT_OK) {
                val resultUri = result.uri
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                getTextFromImage(bitmap)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
    private fun getTextFromImage(bitmap: Bitmap) {
        textRecognizer = TextRecognizer.Builder(this).build()
        if (!textRecognizer.isOperational) {
            Toast.makeText(
                this,
                "Dependencies are not loaded yet...please try after few moment!!",
                Toast.LENGTH_SHORT
            )
                .show()
            Log.e(tag, "Dependencies are downloading....try after few moment")
            return
        } else {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val items: SparseArray<TextBlock> = textRecognizer.detect(frame)
            val stringBuilder = StringBuilder()
            for (i in 0 until items.size()) {
                val item = items.valueAt(i)
                stringBuilder.append(item.value)
                stringBuilder.append("\n")
            }
            val editTextnopol = findViewById<EditText>(R.id.nopol) as TextView
            editTextnopol.text = stringBuilder.toString()
        }
    }


    private fun qrgen(){

        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
        val nama = sharedPreferences.getString(Nama, null)
        val npm = sharedPreferences.getString(Npm, null)
        val waktucheckin = sharedPreferences.getString(waktucheckIN, null)
        val editTextnopol = findViewById<EditText>(R.id.nopol)
        val nopol = editTextnopol.text.toString()


        val dialog = BottomSheetDialog(this, R.style.MyTransparentDialog)

        //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = layoutInflater.inflate(R.layout.qrcode, null)

        val waktucheckout = waktu().waktuString
        val qrcodes = view.findViewById<ImageView>(R.id.qrcodes)
        //val encryptedText = crypt().encrypt("$namasplit,$npm,$nopol")
        Log.d(tag, "$nama,$npm,$nopol,$waktucheckin,$waktucheckout")
        val qrEncoder = QRGEncoder("$nama,$npm,$nopol,$waktucheckin,$waktucheckout", null, QRGContents.Type.TEXT, 500)

        Log.d(tag, "Nama : $nama")
        Log.d(tag, "Npm : $npm")
        Log.d(tag, "nopol : $nopol")

        qrcodes.setImageBitmap(qrEncoder.bitmap)

        dialog.setContentView(view)

        if(nopol.trim().isNotEmpty()) {
            dialog.show()
            val btn_close = view.findViewById<Button>(R.id.close)
            val valids = view.findViewById<TextView>(R.id.valid)

            btn_close.setOnClickListener {
                dialog.dismiss()
                checkstatus()
            }
            object : CountDownTimer(60000, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    valids.text = "Berlaku Selama : " + millisUntilFinished / 1000 + " detik"
                }

                override fun onFinish() {
                    dialog.dismiss()
                    checkstatus()
                }
            }.start()

        }else{
            var errorMessage: String = this.resources.getString(R.string.errormessage)
            var errorTitle: String = this.resources.getString(R.string.errortitle)
            AestheticDialog.Builder(this, DialogStyle.TOASTER, DialogType.ERROR)
                .setTitle(errorTitle)
                .setMessage(errorMessage)
                .setOnClickListener(object : OnDialogClickListener {
                    override fun onClick(dialog: AestheticDialog.Builder) {
                        dialog.dismiss()
                    }
                })
                .show()
        }


    }


        fun checkdatabase(){
            sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
            val nama = sharedPreferences.getString(Nama, null)
            val npm = sharedPreferences.getString(Npm, null)
            val npmsplit = npm?.replace(".", "")

            Log.d(tag, "di split = $npmsplit")
            val userRef = database.getReference("user")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild("$npmsplit")) {
                        Log.d("firebase", "NIM ADA")
                    } else {
                        val dataMap: HashMap<String, Any> = HashMap()
                        dataMap["nama"] = "$nama"
                        dataMap["nim"] = "$npmsplit"
                        dataMap["status"] = false
                        userRef.child("$npmsplit").setValue(dataMap)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Failed to read value
                    Log.w("error data firebase", "Failed to read value.", error.toException())
                }
            })
        }

        fun checkstatus(){
            sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
            val npm = sharedPreferences.getString(Npm, null)
            val npmsplit = npm?.replace(".", "")
            val userRef = database.getReference("user")

            userRef.child("$npmsplit").child("status").addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val booleanValue = dataSnapshot.getValue(Boolean::class.java)
                    if (booleanValue == true) {
                        userRef.child("$npmsplit").child("status").setValue(false)
                        sessionManager.undocheckin()
                        sessionManager.hapuscheckin()
                        Log.d(tag, "status true ke false")
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    // An error occurred
                }
            })
        }

    private fun getUserData() {

        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
        val npm = sharedPreferences.getString(Npm, null)
        val npmsplit = npm?.replace(".", "")
        val userRef = database.getReference("user")

        userRef.child("$npmsplit").child("aktivitas").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    snapshot.children.forEach {
                        val item = it.getValue(Data::class.java)
                        userArrayList.add(item!!)
                    }
                    userRecyclerview.adapter = ListAdapter(userArrayList)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    override fun onBackPressed() {
        }


}
