package id.parkmate.parking.view

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.dx.dxloadingbutton.lib.LoadingButton
import id.parkmate.R
import id.parkmate.parking.model.service.ApiClient
import id.parkmate.parking.model.data.sessionmanager
import id.parkmate.parking.model.data.sessionmanager.Companion.Nama
import id.parkmate.parking.model.data.sessionmanager.Companion.Npm
import id.parkmate.parking.model.data.sessionmanager.Companion.USER_TOKEN
import id.parkmate.parking.model.data.LoginRespone
import id.parkmate.parking.model.data.profile
import id.parkmate.parking.model.data.sessionmanager.Companion.NpmImg
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Loginpage : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sessionManager: sessionmanager
    private lateinit var apiClient: ApiClient
    private val tag: String? = "Loginpage"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val login = findViewById<LoadingButton>(R.id.login)
        val npm = findViewById<EditText>(R.id.npm_text)
        val password = findViewById<EditText>(R.id.password_text)
        val npms = npm.text.toString()
        val passwords = password.text.toString()

        login.setOnClickListener {
                signin()
                login.startLoading()
        }
//            if(npms.trim().isNotEmpty()) {
//                signin()
//                login.startLoading()
//            }
//            else{
//                AestheticDialog.Builder(this, DialogStyle.TOASTER, DialogType.ERROR)
//                    .setTitle("Error")
//                    .setMessage("Jangan Lupa Masukkan Nim & Password!")
//                    .setOnClickListener(object : OnDialogClickListener {
//                        override fun onClick(dialog: AestheticDialog.Builder) {
//                            dialog.dismiss()
//                        }
//                    })
//                    .show()
//            }
//        }
    }

    private fun signin()
    {
        val npm = findViewById<EditText>(R.id.npm_text)
        val password = findViewById<EditText>(R.id.password_text)
        val usernames = npm.text.toString()
        val keywords = password.text.toString()

        apiClient = ApiClient()
        sessionManager = sessionmanager(this)

        apiClient.getApiService().signin(username = usernames, keyword = keywords)
            .enqueue(object : Callback<LoginRespone> {
                override fun onFailure(call: Call<LoginRespone>, t: Throwable) {
                    login.loadingFailed()
                    Log.d(tag, "Gagal Konek Ke Server")
                }

                override fun onResponse(call: Call<LoginRespone>, response: Response<LoginRespone>) {
                    val loginResponse = response.body()
                    //val error = response.errorBody()!!.string()
                    val respones = response.code()

                    if (respones == 200 && loginResponse?.authToken != null) {
                        sessionManager.saveAuthToken(loginResponse.authToken)
                        Log.d(tag, "token : " + loginResponse.authToken)
                        data()
                        sessionManager.haslogin()

                    } else {
                        login.loadingFailed()
                        Log.d(tag, "token : " + loginResponse?.authToken)
                        Log.d(tag, "Status Code : $respones")
                        //Log.d(tag, "Body : $error")
                        Log.d("response",response.raw().toString())

                    }
                }
            })

    }

    private fun data()
    {

        apiClient = ApiClient()
        sharedPreferences = getSharedPreferences("E-PARKING", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(USER_TOKEN, null)!!

        apiClient.getApiService().fetchdata(token)
            .enqueue(object : Callback<profile> {
                override fun onFailure(call: Call<profile>, t: Throwable) {

                    login.loadingFailed()

                    Log.d(tag, "Gagal")
                }

                override fun onResponse(call: Call<profile>, response: Response<profile>) {
                    val profileResponse = response.body()
                    val respones = response.code()

                    if (respones == 200 && profileResponse?.dataprofile != null) {

                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(Nama, profileResponse.dataprofile.Nama)
                        editor.putString(Npm, profileResponse.dataprofile.Npm)
                        editor.putString(NpmImg, profileResponse.dataprofile.NpmImg)

                        sessionManager.savenama(profileResponse.dataprofile.Nama)
                        sessionManager.savenpm(profileResponse.dataprofile.Npm)
                        sessionManager.savenpmimg(profileResponse.dataprofile.NpmImg)

                        Log.d(tag, "Data : " + profileResponse.dataprofile)
                        Log.d(tag, "Nama : " + profileResponse.dataprofile.Nama)
                        Log.d(tag, "Npm : " + profileResponse.dataprofile.Npm)
                        Log.d(tag, "NpmImg : " + profileResponse.dataprofile.NpmImg)

                        val login = findViewById<LoadingButton>(R.id.login)
                        login.loadingSuccessful()
                        login.animationEndAction = {
                            toNextPage()
                            Unit
                        }

                    } else {

                        Log.d(tag, "Status Code : $respones")
                        Log.d("response",response.raw().toString())

                    }
                }
            })

    }

    private fun toNextPage() {

        val cx = (login.left + login.right) / 2
        val cy = (login.top + login.bottom) / 2

        val animator = ViewAnimationUtils.createCircularReveal(loginss, cx, cy, 0f, resources.displayMetrics.heightPixels * 1.2f)
        animator.duration = 500
        animator.interpolator = AccelerateDecelerateInterpolator()
        loginss.visibility = View.VISIBLE
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                login.postDelayed({
                    login.reset()
                    loginss.visibility = View.INVISIBLE
                },200)
            }

            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                overridePendingTransition(0,0)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })

    }

    override fun onBackPressed() {
    }

    }