package com.example.safesporting

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.subtyping.qual.Bottom

class MainActivity : AppCompatActivity() {


    lateinit var addButton: FloatingActionButton
    lateinit var Layout: LinearLayout
    var dbRegister = FirebaseFirestore.getInstance()
    var countDots: Int = 0
    var countScene: Int = 0
    val currentUser = FirebaseAuth.getInstance().currentUser
    val docRef =
        dbRegister.collection("users").document(currentUser?.email.toString()).collection("Dots")
    val database = Firebase.database
    val myRef = database.getReference("lucasdavalle/Dots/Patio")
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.nav_view)
        addButton = findViewById(R.id.floatingActionButton5)
        Layout = findViewById(R.id.lineSwitch)

        updateData()
        initToolBar()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_main -> {
                    startActivity(Intent(this, AddDots::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                R.id.nav_eliminar_dots -> {
                    startActivity(Intent(this, DeleteDots::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                R.id.nav_programar_dots -> {
                    startActivity(Intent(this, ProgramDots::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                R.id.nav_item_clearpreferences -> Toast.makeText(this,
                    "En desarrollo",
                    Toast.LENGTH_SHORT).show()
                R.id.nav_item_signout -> signOut()
                R.id.nav_item_pag -> {
                    startActivity(Intent(this, Web::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    private fun initToolBar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun add(view: View) {
        addDots()
    }

    private fun addDots() {
        val next = Intent(this, AddDots::class.java)
        startActivity(next)
        overridePendingTransition(R.anim.to_left, R.anim.from_rigth)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, SignIn::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun updateData() {
        docRef.addSnapshotListener { value, error ->
            error?.let {
                println("Error " + error.toString())
                return@addSnapshotListener
            }
            value?.let {
                Layout.removeAllViews()
                for (document in it) {
                    println("DocumentSnapshot data: ${document.id} ${document.data.getValue("Estado")}")
                    addSwitch(document.id, document.data.getValue("Estado").toString().toBoolean())
                }
            }
        }
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Aquí puedes obtener los datos que has consultado
                val value = dataSnapshot.getValue(Boolean::class.java)
                println("Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // En caso de que haya algún error en la consulta, se notificará aquí
                println("Failed to read value." + error.toException())
            }
        })
    }

    fun addSwitch(Name: String, Estado: Boolean) {
        val row = LinearLayout(this)
        val newSwitch = Switch(this)
        row.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        newSwitch.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        row.setMargins(40, 20, 40, 20)
        newSwitch.text = Name
        newSwitch.isChecked = Estado
        newSwitch.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        newSwitch.id = countDots
        newSwitch.textSize = 20F
        newSwitch.setBackgroundResource(R.drawable.ic_squarewhite_padding20)
        row.addView(newSwitch)
        Layout.addView(row)
        countDots++
    }

    fun addEcena(Name: String) {
        val row = LinearLayout(this)
        val newBotton = Button(this)
        row.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        newBotton.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                30)
        newBotton.text = Name
        newBotton.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
        newBotton.id = countScene
        newBotton.textSize = 8F
        newBotton.setBackgroundResource(R.drawable.ic_squarewhite_stroke)
        row.addView(newBotton)
        Layout.addView(row)
        countScene++
    }

    fun View.setMargins(
        left: Int? = null,
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null,
    ) {
        val lp = layoutParams as? ViewGroup.MarginLayoutParams
            ?: return

        lp.setMargins(
            left ?: lp.leftMargin,
            top ?: lp.topMargin,
            right ?: lp.rightMargin,
            bottom ?: lp.bottomMargin
        )

        layoutParams = lp
    }

}












