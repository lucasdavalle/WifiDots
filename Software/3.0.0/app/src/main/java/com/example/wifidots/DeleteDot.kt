package com.example.wifidots

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import org.checkerframework.common.subtyping.qual.Bottom

class DeleteDOT : Fragment() {
    private lateinit var spinner: Spinner
    private lateinit var btEliminar: Button
    private lateinit var inflatedView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delete_dot, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        inflatedView = view

        spinner = view.findViewById(R.id.spinnerDelete)
        btEliminar = view.findViewById(R.id.Delete)

        btEliminar.setOnClickListener { showDialogAlert() }
        agregarObjetosAlSpinner(listOf("Patio", "Terraza"))

        return view
    }

    private fun agregarObjetosAlSpinner(nombresObjetos: List<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.spiner, nombresObjetos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun eliminarDot() {
        Toast.makeText(inflatedView.context, "Dot Eliminado", Toast.LENGTH_LONG).show()
    }

    private fun showDialogAlert() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.deletedialog, null)
        val btnAceptar = view.findViewById<Button>(R.id.btnAceptar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        btnAceptar.setOnClickListener {
            eliminarDot()
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
    }

}