package com.example.wifidots

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.wifidots.MainActivity.Companion.uMail
import com.example.wifidots.SignIn.Companion.userMail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL

class AddDot : Fragment() {
    private lateinit var Ssid: String
    private lateinit var Pass: String
    private lateinit var inflatedView: View
    private lateinit var dotName: String
    private lateinit var url: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val view = inflater.inflate(R.layout.fragment_add_dot, container, false)
        inflatedView = view
        writebase()



        return view
    }

    private fun writebase() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference: DatabaseReference = database.reference
        val etNombre = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val dispositivos = inflatedView.findViewById<RadioGroup>(R.id.rgDispositivo)
        var dispositivo: Long = 0
        etPass.visibility = View.INVISIBLE
        tvInfo.text = "Eliga el tipo de dot y su nombre!"
        btnAdd.text = "Agregar"
        etNombre.hint = "Nombre del Dot"

        val selectedRadioButtonId = dispositivos.checkedRadioButtonId

        when(selectedRadioButtonId){
            R.id.lamp->dispositivo = 0
            R.id.plug->dispositivo = 1
        }
        etNombre.visibility = View.VISIBLE
        btnAdd.setOnClickListener {
            dotName = etNombre.text.toString()
            val nuevaColeccionReference: DatabaseReference =
                reference.child(uMail.substringBefore("@"))
                    .child("Dots").child(dotName).child("Estado")
            val nuevaColeccionReference2: DatabaseReference =
                reference.child(uMail.substringBefore("@"))
                    .child("Dots").child(dotName).child("Dispositivo")
            nuevaColeccionReference.setValue(false)
            nuevaColeccionReference2.setValue(dispositivo)
            wifiAdress()
        }
    }


    private fun wifiAdress(){
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val dispositivos = inflatedView.findViewById<RadioGroup>(R.id.rgDispositivo)
        tvInfo.text="Escane el codigo QR que se encuentra en la caja de el Dots, luego siga los pasos para poder conectarse a su dot"
        dispositivos.visibility = View.INVISIBLE
        etSsid.visibility = View.INVISIBLE
        etPass.visibility = View.INVISIBLE
        btnAdd.text = "Escanear"
        btnAdd.setOnClickListener {initScaner()}

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

    private  fun sendData() {
        val tvInfo = inflatedView.findViewById<TextView>(R.id.tvInfo)
        val etSsid = inflatedView.findViewById<EditText>(R.id.etSsid)
        val etPass = inflatedView.findViewById<EditText>(R.id.etPass)
        val btnAdd = inflatedView.findViewById<Button>(R.id.btnAdd)
        val ssid = etSsid.text.toString()
        val pass = etPass.text.toString()
        url = "http://192.168.4.1/?parametro1=$ssid&parametro2=$pass&parametro3=${uMail.substringBefore("@")}&parametro4=$dotName"

        etSsid.visibility = View.GONE
        btnAdd.visibility = View.GONE
        etPass.visibility = View.GONE
        tvInfo.text = "Enviando..."
        showDialogAlert()
        Toast.makeText(inflatedView.context, "Dot Agregado con Exito", Toast.LENGTH_LONG).show()
        //goHome()
    }


    private fun goHome() {
        val homeFragment = home() // Reemplaza `home()` con el Fragment de inicio que deseas mostrar
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_wapper, homeFragment) // Reemplaza `R.id.fragmentContainer` con el ID del contenedor de Fragment en tu diseño XML
        fragmentTransaction.addToBackStack(null) // Opcional: Agregar la transacción a la pila de retroceso
        fragmentTransaction.commit()
    }

    private fun showDialogAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.adddialog, null)
        val btnAceptar = view.findViewById<Button>(R.id.btOk)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        btnAceptar.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
            goHome()
            dialog.dismiss()
        }
    }
}

