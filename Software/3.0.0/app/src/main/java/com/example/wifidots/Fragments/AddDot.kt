package com.example.wifidots.Fragments

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.wifidots.MainActivity.Companion.uMail
import com.example.wifidots.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AddDot : Fragment() {
    private lateinit var Ssid: String
    private lateinit var Pass: String
    private lateinit var inflatedView: View
    private lateinit var dotName: String
    private lateinit var url: String
    private var dispositivo: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val view = inflater.inflate(R.layout.fragment_add_dot, container, false)
        inflatedView = view
        firstStep()



        return view
    }

    private fun firstStep() {

        val etNombre = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val dispositivos = inflatedView.findViewById<RadioGroup>(R.id.rgDispositivo)
        val progressBar = inflatedView.findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.INVISIBLE
        etPass.visibility = View.INVISIBLE
        tvInfo.text = "Eliga el tipo de dot y su nombre!"
        btnAdd.text = "Agregar"
        etNombre.hint = "Nombre del Dot"

        val selectedRadioButtonId = dispositivos.checkedRadioButtonId

        when (selectedRadioButtonId) {
            R.id.lamp -> dispositivo = 0
            R.id.plug -> dispositivo = 1
        }
        etNombre.visibility = View.VISIBLE
        btnAdd.setOnClickListener {
            dotName = etNombre.text.toString()
            wifiAdress()
        }
    }
    private fun writebaseIfNetworkAvailable() {
        val progressBar = inflatedView.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        if (isNetworkConnected()) {
            progressBar.visibility = View.INVISIBLE
            // Si hay conectividad a Internet, realiza la inserción en Firebase
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = database.reference
            val nuevaColeccionReference: DatabaseReference =
                reference.child(uMail.substringBefore("@"))
                    .child("Dots").child(dotName).child("Estado")
            val nuevaColeccionReference2: DatabaseReference =
                reference.child(uMail.substringBefore("@"))
                    .child("Dots").child(dotName).child("Dispositivo")
            nuevaColeccionReference.setValue(false)
            nuevaColeccionReference2.setValue(dispositivo)
            Toast.makeText(inflatedView.context,
                "Dot " +dotName +" agregado con exito",
                Toast.LENGTH_LONG).show()
            goHome()
        } else {
            writebaseIfNetworkAvailable()
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun wifiAdress() {
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val dispositivos = inflatedView.findViewById<RadioGroup>(R.id.rgDispositivo)
        tvInfo.text =
            "Escane el codigo QR que se encuentra en la caja de el Dots, luego siga los pasos para poder conectarse a su dot"
        dispositivos.visibility = View.INVISIBLE
        etSsid.visibility = View.INVISIBLE
        etPass.visibility = View.INVISIBLE
        btnAdd.text = "Escanear"
        btnAdd.setOnClickListener { initScaner() }

    }

    private fun initScaner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el codigo QR")
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val tvSsid = inflatedView.findViewById<TextView>(R.id.tvSsid)
        val tvPass = inflatedView.findViewById<TextView>(R.id.tvPass)
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            Log.d("QR Code", result.contents)
            val Credeciales = result.contents.split("-")
            Ssid = Credeciales[0]
            Pass = Credeciales[1]
            tvSsid.text = "Red: $Ssid"
            tvPass.text = "Contraseña: $Pass"
            tvInfo.text = "Conectese al anterior wifi, cuando este pulse el boton"
            btnAdd.text = "Listo!"
            btnAdd.setOnClickListener { readData() }
        } else {
            Log.d("QR Code", "Escaneo cancelado")
        }
    }

    private fun readData() {
        val tvSsid = inflatedView.findViewById<TextView>(R.id.tvSsid)
        val tvPass = inflatedView.findViewById<TextView>(R.id.tvPass)
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        etSsid.setText("")
        etSsid.hint = "Nombre de la Red"
        etSsid.visibility = View.VISIBLE
        etPass.visibility = View.VISIBLE
        tvPass.visibility = View.GONE
        tvSsid.visibility = View.GONE
        tvInfo.text = "Ingrese los datos de su wifi para que el dot se conecte"
        btnAdd.text = "Siguiente"
        btnAdd.setOnClickListener { sendData() }
    }

    private fun sendData() {
        val database = FirebaseDatabase.getInstance()
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val ssid = etSsid.text.toString()
        val pass = etPass.text.toString()
        url = "http://192.168.4.1/?parametro1=$ssid&parametro2=$pass&parametro3=${
            uMail.substringBefore("@")
        }&parametro4=$dotName"

        etSsid.visibility = View.GONE
        btnAdd.visibility = View.GONE
        etPass.visibility = View.GONE
        tvInfo.text = "Enviando..."
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        tvInfo.text = "Conectese a internet para terminar la configuracion"
                        writebaseIfNetworkAvailable()
                    }
                } else {
                    requireActivity().runOnUiThread {
                        tvInfo.text = "Error al agregar Dot"
                        Toast.makeText(inflatedView.context,
                            "Error al agregar Dot",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Error en la solicitud
                requireActivity().runOnUiThread {
                    tvInfo.text = "Error de red: ${e.message}"
                    Toast.makeText(inflatedView.context,
                        "Error de red al agregar Dot",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
        //goHome()
    }

    interface OnGoHomeListener {
        fun onGoHome()
    }

    private fun goHome() {
        val homeFragment = home() // Reemplaza `home()` con el Fragment de inicio que deseas mostrar
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_wapper, homeFragment) // Reemplaza `R.id.fragmentContainer` con el ID del contenedor de Fragment en tu diseño XML
        fragmentTransaction.addToBackStack(null) // Opcional: Agregar la transacción a la pila de retroceso
        fragmentTransaction.commit()
        // Notifica al MainActivity para actualizar la selección del menú
        if (requireActivity() is OnGoHomeListener) {
            (requireActivity() as OnGoHomeListener).onGoHome()
        }
    }


}

