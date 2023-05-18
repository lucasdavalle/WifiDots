package com.example.wifidots


import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Date
import java.text.SimpleDateFormat
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragment: FrameLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val homeFragment = home()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_wapper, homeFragment)
            .commit()
        bottomNavigationView=findViewById(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when(item.itemId){
                R.id.navigation_HOME -> home()
                R.id.navigation_ADD -> AddDot()
                R.id.navigation_DELETE -> DeleteDOT()
                R.id.navigation_PROGR -> ProgramDot()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fl_wapper, it)
                    .commit()
            }

            return@setOnItemSelectedListener true
        }

    }


}