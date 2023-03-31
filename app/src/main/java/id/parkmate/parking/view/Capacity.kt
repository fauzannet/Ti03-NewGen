package id.parkmate.parking.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import id.parkmate.R
import kotlinx.android.synthetic.main.parkir.*

class Capacity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parkir)
        val login = findViewById<Button>(R.id.back)

        val userRef = database.getReference("data")
        userRef.child("kapasitas").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nilai = dataSnapshot.value.toString().toInt()
                arc_progress.progress = nilai
                Log.d("kapasitas","nilai = $nilai")
            }
            override fun onCancelled(error: DatabaseError) {
                // An error occurred
            }
        })

        login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}