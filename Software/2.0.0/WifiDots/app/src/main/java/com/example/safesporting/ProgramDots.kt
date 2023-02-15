package com.example.safesporting

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.Toast
import java.util.Calendar

class ProgramDots : AppCompatActivity() {
    lateinit var etHoraE:EditText
    lateinit var etHoraA:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_program_dots)
        etHoraE= findViewById(R.id.edTimeE)
        etHoraA=findViewById(R.id.edTimeA)
        etHoraE.setOnClickListener { showTimePickerDialogE() }
        etHoraA.setOnClickListener { showTimePickerDialogA() }

    }
    private fun showTimePickerDialogE() {
        val timePicker = TimePickerFragment {onTimeSelectedE(it)}
        timePicker.show(supportFragmentManager, "timePicker")
    }
    private fun onTimeSelectedE(time: String) {
        etHoraE.setText(time)
    }
    private fun showTimePickerDialogA() {
        val timePicker = TimePickerFragment {onTimeSelectedA(it)}
        timePicker.show(supportFragmentManager, "timePicker")
    }
    private fun onTimeSelectedA(time: String) {
        etHoraA.setText(time)
    }
    private fun sendTimeESP(){
        Toast.makeText(this,"En Desarrollo",Toast.LENGTH_LONG).show()
    }
}
/*
   Para poder hacer que el dots se prenda a una hora y dia predeterminado le enviaremos al dots los strings de con los dia de encendido y  las horas
   luego el Esp con la libreir Time va a estar consultando siempre la hora para saber si conside con la hora que guardo programada por la app y si conside va a
   hacer un pull en la base de datos cambiando el stado de la base, esa y la tecla va a ser la unica forma de hacer un pull y mientras no se haga un pull va a estar
   actualizando siempre el estado del Dots.
 */