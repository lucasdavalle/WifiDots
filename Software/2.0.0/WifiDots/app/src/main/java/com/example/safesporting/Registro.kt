package com.example.safesporting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Registro : AppCompatActivity() {

    lateinit var sNombre: String
    lateinit var sApellido: String
    lateinit var sEmail: String
    lateinit var sPass: String
    lateinit var sRpass: String
    private lateinit var etnombreNewUserR: EditText
    private lateinit var etapellidoNewUserR: EditText
    private lateinit var etEmailR: EditText
    private lateinit var etPasswordR: EditText
    private lateinit var etPassword2R: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        etnombreNewUserR = findViewById(R.id.etnombreNewUserR)
        etapellidoNewUserR = findViewById(R.id.etapellidoNewUserR)
        etEmailR = findViewById(R.id.etEmailR)
        etPasswordR = findViewById(R.id.etPasswordR)
        etPassword2R = findViewById(R.id.etPassword2R)
    }
    fun newUser(view: View)
    {
        newUserP()
    }
    private fun newUserP(){
        var validacion: Int = 0
        if(etnombreNewUserR.text.isNullOrEmpty()||
           etapellidoNewUserR.text.isNullOrEmpty()||
           etEmailR.text.isNullOrEmpty()||
           etPasswordR.text.isNullOrEmpty()||
           etPassword2R.text.isNullOrEmpty())
            Toast.makeText(this,"Faltan Datos",Toast.LENGTH_SHORT).show()
        else {
            sNombre = etnombreNewUserR.text.toString()
            sApellido = etapellidoNewUserR.text.toString()
            sEmail = etEmailR.text.toString()
            sPass = etPasswordR.text.toString()
            sRpass = etPassword2R.text.toString()
            validacion++
            if (!sPass.equals(sRpass))
                Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show()
            else
                validacion++
        }

        if (validacion==2)
        {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(sEmail,sPass)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        var dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                        var dbRegister = FirebaseFirestore.getInstance()
                        dbRegister.collection("users").document(sEmail).set(hashMapOf(
                            "User" to sEmail,
                            "Nombre" to sNombre,
                            "Apellido" to sApellido,
                            "FechaDeRegisro" to dateRegister
                        ))
                        Toast.makeText(this,"Usuario agregado con exito",Toast.LENGTH_SHORT).show()
                        goSignIn()
                    }
                    else
                        Toast.makeText(this,"Algo salio mal",Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun goSignIn(){
        val intent = Intent(this,SignIn::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
    }


}