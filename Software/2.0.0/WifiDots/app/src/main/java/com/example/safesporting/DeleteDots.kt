package com.example.safesporting

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.common.returnsreceiver.qual.This

class DeleteDots : AppCompatActivity() {
    lateinit var spSuprDots:Spinner
    lateinit var btDelete: Button
    var dbRegister = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val docRef = dbRegister.collection("users").document(currentUser?.email.toString()).collection("Dots")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_dots)

        spSuprDots= findViewById(R.id.spinner)
        btDelete = findViewById(R.id.Delete)
        cargarDots()

    }
    private fun cargarDots(){
        var dots = 0
        var cuenta = 0
        docRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                    dots++
                var listaDots: Array<String?> = arrayOfNulls(dots)
                for (document in documents) {
                    listaDots[cuenta]=document.id
                    cuenta++
                }
                var adaptador: ArrayAdapter<String> = ArrayAdapter(this,R.layout.spiner,listaDots)
                spSuprDots.adapter=adaptador
            }
    }
    fun DeleteDots(view: View){
        delete()
    }
    private fun delete(){
        var builder = AlertDialog.Builder(this)
        if (spSuprDots.selectedItem==null)
        {
            Toast.makeText(this,"No tiene nigun Dots para eliminar",Toast.LENGTH_SHORT).show()
        }
        else {
            builder.setIcon(android.R.drawable.ic_dialog_info).setTitle("Confirmacion")
                .setMessage("Esta seguro de eliminar ${spSuprDots.selectedItem.toString()}")
                .setPositiveButton("Si", DialogInterface.OnClickListener { dialogInterface, i ->
                    dbRegister.collection("users").document(currentUser?.email.toString())
                        .collection("Dots").document(spSuprDots.selectedItem.toString())
                        .delete()
                        .addOnSuccessListener {
                            ToAast.makeText(this,
                                "Dots eliminado con exito",
                                Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,
                                "Error eliminando Dots  ${e} ",
                                Toast.LENGTH_SHORT).show()
                        }
                    cargarDots()
                }).setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            }).show()
        }
    }
    fun goHome(view: View){
        Go()
    }
    private fun Go(){
        startActivity(Intent(this,MainActivity::class.java))
    }

}