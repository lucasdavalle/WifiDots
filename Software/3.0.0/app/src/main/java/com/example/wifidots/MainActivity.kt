package com.example.wifidots


import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.wifidots.Fragments.AddDot
import com.example.wifidots.Fragments.DeleteDOT
import com.example.wifidots.Fragments.ProgramDot
import com.example.wifidots.Fragments.home


class MainActivity : AppCompatActivity(), AddDot.OnGoHomeListener {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragment: FrameLayout

    companion object {
        lateinit var uMail: String
    }

    // Agrega una variable para llevar el seguimiento del fragmento actual
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uMail = intent.getStringExtra("Mail").toString()

        // Establece el fragmento inicial y la variable currentFragment
        val homeFragment = home()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_wapper, homeFragment)
            .commit()
        currentFragment = homeFragment

        bottomNavigationView = findViewById(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val newFragment = when (item.itemId) {
                R.id.navigation_HOME -> home()
                R.id.navigation_ADD -> AddDot()
                R.id.navigation_DELETE -> DeleteDOT()
                R.id.navigation_PROGR -> ProgramDot()
                //R.id.navigation_ACCOUNT -> Account()
                else -> null
            }

            newFragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fl_wapper, it)
                    .commit()
                currentFragment = it // Actualiza el fragmento actual
            }

            return@setOnItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
    override fun onGoHome() {
        bottomNavigationView.selectedItemId = R.id.navigation_HOME
    }
}

