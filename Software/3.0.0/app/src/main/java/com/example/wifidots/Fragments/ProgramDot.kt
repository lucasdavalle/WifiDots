package com.example.wifidots.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.wifidots.MainActivity
import com.example.wifidots.R
import com.example.wifidots.TimePickerFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProgramDot : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var etHoraE: EditText
    private lateinit var etHoraA: EditText
    private lateinit var Programar: Button
    private lateinit var textView: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var inflatedView: View
    private var Estado = 1
    private lateinit var sharedPrefGlobal : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_program_dot, container, false)
        val sharedPref = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPrefGlobal = sharedPref
        inflatedView = view
        tableLayout = view.findViewById(R.id.tableLayout)
        textView = view.findViewById(R.id.tvTextoAyuda)
        spinner = view.findViewById(R.id.spinnerProgram)
        etHoraE = view.findViewById(R.id.edTimeE)
        etHoraA = view.findViewById(R.id.edTimeA)
        Programar = view.findViewById(R.id.Program)

        spinner.visibility = View.INVISIBLE
        etHoraE.visibility = View.INVISIBLE
        etHoraA.visibility = View.INVISIBLE
        tableLayout.visibility = View.INVISIBLE

        changeSelecion()

        etHoraE.setOnClickListener { showTimePickerDialogE() }
        etHoraA.setOnClickListener { showTimePickerDialogA() }
        Programar.setOnClickListener { sendTimeESP() }

        getFirebaseCollections(MainActivity.uMail.substringBefore("@")) { nombresObjetos ->
            agregarObjetosAlSpinner(nombresObjetos)
        }
        return view
    }

    fun getFirebaseCollections(mail: String, callback: (List<String>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("$mail/Dots")
        val nombresObjetos: MutableList<String> = mutableListOf()

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    nombresObjetos.add(childSnapshot.key.toString())
                }
                callback(nombresObjetos)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error reading collections:", databaseError.message)
            }
        })
    }
    private fun agregarObjetosAlSpinner(nombresObjetos: List<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.spiner, nombresObjetos)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun showTimePickerDialogE() {
        val timePicker = TimePickerFragment { onTimeSelectedE(it) }
        timePicker.show(childFragmentManager, "timePicker")
    }

    private fun onTimeSelectedE(time: String) {
        etHoraE.setText(time)
    }

    private fun showTimePickerDialogA() {
        val timePicker = TimePickerFragment { onTimeSelectedA(it) }
        timePicker.show(childFragmentManager, "timePicker")
    }

    private fun onTimeSelectedA(time: String) {
        etHoraA.setText(time)
    }

    private fun changeSelecion() {
        when (Estado) {
            1 -> {
                if (sharedPrefGlobal.getBoolean("Informacion", true))
                    showAlertDialogWithCheckBox()
                textView.text = "Selecione el Dot"
                spinner.visibility = View.VISIBLE
                Programar.text = "Siguiente"
            }
            2 -> {
                textView.text = "Selecione Dia"
                spinner.visibility = View.INVISIBLE
                tableLayout.visibility = View.VISIBLE
                Programar.text = "Siguiente"
            }
            3 -> {
                textView.text = "Selecione Hora"
                spinner.visibility = View.INVISIBLE
                tableLayout.visibility = View.INVISIBLE
                etHoraE.visibility = View.VISIBLE
                etHoraA.visibility = View.VISIBLE
                Programar.text = "Programar"
            }
            4 -> {
                Toast.makeText(inflatedView.context, "Programando Dot", Toast.LENGTH_LONG).show()
                etHoraE.visibility = View.INVISIBLE
                etHoraA.visibility = View.INVISIBLE
                tableLayout.visibility = View.INVISIBLE
                Estado = 1
                changeSelecion()
            }
        }
    }

    private fun sendTimeESP() {
        Estado++
        changeSelecion()
    }

    private fun showAlertDialogWithCheckBox() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.alertdialog, null)
        val btnAceptar = view.findViewById<Button>(R.id.btnAceptar)
        val checkBox = view.findViewById<CheckBox>(R.id.chNoMostrar)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val editor = sharedPrefGlobal.edit()
        btnAceptar.setOnClickListener {
            if (checkBox.isChecked) {
                editor.putBoolean("Informacion", false)
                editor.commit()
                Log.d("SharedPreferences", "Informacion guardada en SharedPreferences: ${sharedPrefGlobal.getBoolean("Informacion", true)}")
            }
            dialog.dismiss()
        }
    }
}

