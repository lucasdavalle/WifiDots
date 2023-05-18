package com.example.wifidots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPass : AppCompatActivity() {
    lateinit var EdMail: EditText
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_forgot_pass)
        EdMail = findViewById(R.id.etEmailF)
    }
    fun sendMail(view: View){
        resetPass()
    }
    private fun resetPass(){
        var Text = EdMail.text.toString()
        if(!TextUtils.isEmpty(Text)) {
            try {
                mAuth.sendPasswordResetEmail(Text)
                    .addOnCompleteListener { Task ->
                        if (Task.isSuccessful) {
                            Toast.makeText(this, "Email enviado a $Text", Toast.LENGTH_SHORT).show()
                            goSignIn()
                        }
                        else
                            Toast.makeText(this,"El Email que ingreso no esta registrado", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception){
                Toast.makeText(this,"Algo salio mal", Toast.LENGTH_SHORT).show()
            }
        }
        else
            Toast.makeText(this,"Ingrese un email", Toast.LENGTH_SHORT).show()
    }
    fun goSignIn(){
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }
}