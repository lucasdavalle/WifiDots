package com.example.wifidots.Fragments

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        getFirebaseCollections(MainActivity.uMail.substringBefore("@")) { nombresObjetos ->
            agregarObjetosAlSpinner(nombresObjetos)
        }
        return view
    }

    private fun agregarObjetosAlSpinner(nombresObjetos: List<String>) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.spiner, nombresObjetos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
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

    private fun eliminarDot(mail: String) {
        val database = FirebaseDatabase.getInstance()
        spinner = inflatedView.findViewById(R.id.spinnerDelete)
        if (spinner.selectedItem==null)
        {
            Toast.makeText(inflatedView.context,"No tiene nigun Dots para eliminar",Toast.LENGTH_SHORT).show()
        }
        else{
            val reference = database.getReference("$mail/Dots/${spinner.selectedItem}")
            reference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(inflatedView.context, "Dot Eliminado", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { error ->
                    // Ocurrió un error al eliminar la colección
                    Log.e("Eliminar colección", error.message.toString())
                }
        }


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
            eliminarDot(MainActivity.uMail.substringBefore("@"))
            getFirebaseCollections(MainActivity.uMail.substringBefore("@")) { nombresObjetos ->
                agregarObjetosAlSpinner(nombresObjetos)
            }
            dialog.dismiss()
        }
        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
    }

}