package com.example.safesporting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AddDots : AppCompatActivity() {
    lateinit var addDots: Button
    lateinit var lugarTexto: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dots)

        addDots = findViewById(R.id.button2)
        lugarTexto = findViewById(R.id.etLugar)
    }
    fun goMain(view: View) {
        add()
    }
    private fun add() {
        val next = Intent(this, MainActivity::class.java)
        var dbRegister = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var database = FirebaseDatabase.getInstance().getReference()
        if (currentUser != null) {
            if (!lugarTexto.text.isNullOrEmpty()) {
                dbRegister.collection("users").document(currentUser.email.toString())
                    .collection("Dots").document(lugarTexto.text.toString()).set(hashMapOf(
                    "Estado" to false
                ))
                startActivity(next)
            }
            else{
                Toast.makeText(this,"Escriba un lugar",Toast.LENGTH_SHORT).show()
            }
        }
    }
}