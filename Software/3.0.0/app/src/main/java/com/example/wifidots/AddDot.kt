package com.example.wifidots

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.net.HttpURLConnection
import java.net.URL

class AddDot : Fragment() {
    private lateinit var Ssid: String
    private lateinit var Pass: String
    private lateinit var inflatedView: View
    private var Step = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val view = inflater.inflate(R.layout.fragment_add_dot, container, false)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val etSsid = view.findViewById<EditText>(R.id.etSsid)
        val etPass = view.findViewById<EditText>(R.id.etPass)
        etSsid.visibility = View.GONE
        etPass.visibility = View.GONE
        inflatedView = view
        progressBar.visibility = View.GONE
        btnAdd.setOnClickListener {
            Log.d("Estado: ", Step.toString())
            when (Step) {
                0 -> initScaner()
                1 -> readData()
                2 -> sendData()
                else -> Step=0
            }
            Step++
        }

        return view
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
        } else {
            Log.d("QR Code", "Escaneo cancelado")
        }
    }

    private fun initScaner() {
        val integrator = IntentIntegrator.forSupportFragment(this)

        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el codigo QR")
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    private fun readData() {
        val tvSsid = inflatedView.findViewById<TextView>(R.id.tvSsid)
        val tvPass = inflatedView.findViewById<TextView>(R.id.tvPass)
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        etSsid.visibility = View.VISIBLE
        etPass.visibility = View.VISIBLE
        tvPass.visibility = View.GONE
        tvSsid.visibility = View.GONE
        tvInfo.text = "Ingrese los datos de su wifi para que el dot se conecte"
    }
    private fun sendData(){
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val progressBar = inflatedView.findViewById<ProgressBar>(R.id.progressBar)
        val ssid = etSsid.text.toString() // Obtiene el valor del primer parámetro
        val pass = etPass.text.toString() // Obtiene el valor del segundo parámetro
        val url = "http://192.168.4.1/?parametro1=$ssid&parametro2=$pass"  // URL del servidor web en el ESP8266

        etSsid.visibility = View.GONE
        btnAdd.visibility = View.GONE
        etPass.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        tvInfo.text = "Enviando..."

        Thread(Runnable {
            try {
                // Crea una URL a partir de la cadena de URL
                val serverUrl = URL(url)

                // Crea una conexión HTTP con la URL
                val conn = serverUrl.openConnection() as HttpURLConnection

                // Configura la conexión HTTP
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                // Realiza la solicitud HTTP y obtiene la respuesta
                conn.inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        // Maneja la respuesta del servidor aquí
                    }
                }

                // Actualiza la interfaz de usuario en el hilo principal
                activity?.runOnUiThread {
                    progressBar.visibility = View.GONE
                    tvInfo.text = "Enviado exitosamente"
                }
            } catch (e: Exception) {
                // Maneja los errores aquí
                e.printStackTrace()
                activity?.runOnUiThread {
                    progressBar.visibility = View.GONE
                    tvInfo.text = "Error al enviar: ${e.message}"
                }
            }
        }).start()
    }


}