package com.example.wifidots.Login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.wifidots.MainActivity
import com.example.wifidots.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class SignIn : AppCompatActivity() {

    companion object {
        lateinit var userMail: String
        lateinit var provideSeccion: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tRegistro: TextView
    private lateinit var mAuth: FirebaseAuth

    private val REQ_ONE_TAP = 2
    private val RC_SIGN_IN = 1
    private var showOneTapUI = true
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
        FirebaseApp.initializeApp(this)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tRegistro = findViewById(R.id.registrarse)
        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth

        //Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("544935354865-6l6ejgi561tg7heumtqq3bla6jbvoj1o.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun goHome(email: String, provider: String) {
        userMail = email
        provideSeccion = provider
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Mail", email)
        if (email != null)
            startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun SignInGoogle(view: View) {
        signInGogle()
    }

    private fun signInGogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun login(view: View) {
        loginUser()
    }

    private fun loginUser() {
        var validacion: Int = 0
        if (etEmail.text.isNullOrEmpty() || !etEmail.text.contains("@") || !etEmail.text.contains("."))
            Toast.makeText(this, "Correo no valido", Toast.LENGTH_SHORT).show()
        else {
            email = etEmail.text.toString()
            validacion++
        }
        if (etPassword.text.isNullOrEmpty() || etPassword.text.length < 6)
            Toast.makeText(this, "Contraseña no valida", Toast.LENGTH_SHORT).show()
        else {
            password = etPassword.text.toString()
            validacion++
        }

        if (validacion == 2) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        goHome(email, password)
                    } else {
                        Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun registro(view: View) {
        registroUser()
    }

    private fun registroUser() {
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
    }

    fun forgotPass(view: View) {
        forgotPassword()
    }

    private fun forgotPassword() {
        val intent = Intent(this, ForgotPass::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserG = auth.currentUser
        if (currentUser != null)
            goHome(currentUser.email.toString(), currentUser.providerId)
        if (currentUserG != null)
            goHome(currentUserG.email.toString(), currentUserG.providerId)
    }
}