package com.example.wifidots

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.wifidots.MainActivity.Companion.uMail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalTime


class home : Fragment() {

    lateinit var Layout: LinearLayout
    lateinit var lySettings: LinearLayout
    lateinit var btsettings: Button
    lateinit var btComprar: Button
    lateinit var btCerraSecion: Button
    var countDots: Int = 0
    private lateinit var inflatedView: View

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val name = MainActivity.uMail
        inflatedView = view
        btsettings = view.findViewById(R.id.btSettings)
        btCerraSecion = view.findViewById(R.id.btCerrarSecion)
        btComprar = view.findViewById(R.id.btComprarDots)
        lySettings = view.findViewById(R.id.lySetting)

        if (getFirebaseCollections(name.substringBefore("@")))
            showMyLinearLayoutUp(view, 1500)
        btComprar.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        if (countDots!=0)
        {

        }
        btCerraSecion.setOnClickListener { signOut() }
        btsettings.setOnClickListener { toggleVisibility(lySettings) }
        return view
    }

    private fun showMyLinearLayoutUp(view: View, duration: Long) {
        val myLinearLayout = view.findViewById<LinearLayout>(R.id.lyScroll)
        myLinearLayout.visibility = View.VISIBLE

        val slideUpAnimation = TranslateAnimation(0f, 0f, 1500F, 0f)
        slideUpAnimation.duration = duration
        myLinearLayout.startAnimation(slideUpAnimation)
    }

    fun getFirebaseCollections(mail: String): Boolean {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference(mail + "/" + "Dots")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val collectionName = childSnapshot.key
                    val Estado = childSnapshot.child("Estado").getValue()
                    val Dispositivo = childSnapshot.child("Dispositivo").getValue()
                    addSwitch(collectionName.toString(),
                        Estado.toString().toBoolean(),
                        Dispositivo.toString().toInt(),
                        inflatedView)
                    Log.d("Coleciones", collectionName.toString())
                    Log.d("Estado", Estado.toString())
                    Log.d("Dispositivo", Dispositivo.toString())

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Error reading collections:", databaseError.message)
            }
        })
        return true
    }

    private fun addSwitch(Name: String, Estado: Boolean, Imagen: Int, view: View) {
        Layout = view.findViewById<LinearLayout>(R.id.lineSwitch)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference(uMail.substringBefore("@") + "/" + "Dots/"+Name+"/Estado")
        val customTypeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold)
        val row = LinearLayout(requireContext())
        val newSwitch = Switch(requireContext())
        row.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        newSwitch.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        newSwitch.typeface = customTypeface
        newSwitch.text = "   $Name"
        newSwitch.isChecked = Estado
        newSwitch.setPadding(0, 20, 0, 20)
        newSwitch.id = countDots
        newSwitch.textSize = 20F
        newSwitch.setTextColor(Color.DKGRAY)
        newSwitch.setBackgroundResource(R.drawable.ic_squarewhite_stroke_padding20)
        newSwitch.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_switch)
        newSwitch.trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_track)

        when (Imagen) {
            0 -> newSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_light, 0, 0, 0)
            1 -> newSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plugicon, 0, 0, 0)
            else -> { /* no se agrega imagen */
            }
        }

        row.addView(newSwitch)
        Layout.addView(row)
        countDots++

        newSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Lógica para manejar el cambio de estado del Switch
            if (isChecked) {
                reference.setValue(true)
            } else {
                reference.setValue(false)
            }
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireContext(), SignIn::class.java))
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun toggleVisibility(view: LinearLayout) {
        val tvGris = inflatedView.findViewById<TextView>(R.id.tvGris)
        if (view.visibility == View.VISIBLE) {
            val AlphaAnimation1 = AlphaAnimation(1f, 0f)
            AlphaAnimation1.duration = 500
            AlphaAnimation1.fillAfter = false
            view.startAnimation(AlphaAnimation1)
            view.visibility = View.GONE
            AlphaAnimation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    tvGris.visibility = View.INVISIBLE
                    btsettings.setBackgroundResource(R.drawable.ic_icon_settings)
                }

                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        } else if (view.visibility == View.GONE) {
            val AlphaAnimation = AlphaAnimation(0f, 1f)
            AlphaAnimation.duration = 500
            AlphaAnimation.fillAfter = false
            view.startAnimation(AlphaAnimation)
            view.visibility = View.VISIBLE
            AlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    tvGris.visibility = View.VISIBLE
                    btsettings.setBackgroundResource(R.drawable.ic_action_cancel)
                }

                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }
}