package com.example.wifidots

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime


class home : Fragment() {

    lateinit var Layout: LinearLayout
    lateinit var lySettings: LinearLayout
    lateinit var btsettings: Button
    lateinit var btComprar: Button
    lateinit var btCerraSecion: Button
    var countDots: Int = 0
    private lateinit var inflatedView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        inflatedView = view
        btsettings = view.findViewById(R.id.btSettings)
        btCerraSecion = view.findViewById(R.id.btCerrarSecion)
        btComprar= view.findViewById(R.id.btComprarDots)
        lySettings = view.findViewById(R.id.lySetting)

        showMyLinearLayoutUp(view, 1500)
        btComprar.setOnClickListener {
            val url = "https://www.example.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent) }
        btCerraSecion.setOnClickListener { signOut() }
        btsettings.setOnClickListener { toggleVisibility(lySettings) }
        addSwitch("Frente", false, 1, view)
        addSwitch("Patio", false, 2, view)

        return view
    }

    private fun showMyLinearLayoutUp(view: View, duration: Long) {
        val myLinearLayout = view.findViewById<LinearLayout>(R.id.lyScroll)
        myLinearLayout.visibility = View.VISIBLE

        val slideUpAnimation = TranslateAnimation(0f, 0f, 1500F, 0f)
        slideUpAnimation.duration = duration
        myLinearLayout.startAnimation(slideUpAnimation)
    }

    private fun addSwitch(Name: String, Estado: Boolean, Imagen: Int, view: View) {
        Layout = view.findViewById<LinearLayout>(R.id.lineSwitch)
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
            1 -> newSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_light, 0, 0, 0)
            2 -> newSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plugicon, 0, 0, 0)
            else -> { /* no se agrega imagen */
            }
        }

        row.addView(newSwitch)
        Layout.addView(row)
        countDots++
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
            AlphaAnimation1.duration = 700
            AlphaAnimation1.fillAfter = true
            view.visibility = View.GONE
            view.startAnimation(AlphaAnimation1)
            // Agregar el listener a la animación de salida
            AlphaAnimation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    tvGris.visibility = View.INVISIBLE
                    btsettings.setBackgroundResource(R.drawable.ic_icon_settings)
                }
                override fun onAnimationEnd(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        } else {
            val AlphaAnimation = AlphaAnimation(0f, 1f)
            AlphaAnimation.duration = 700
            AlphaAnimation.fillAfter = true
            view.visibility = View.VISIBLE
            view.startAnimation(AlphaAnimation)
            // Agregar el listener a la animación de entrada
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
    /*
        private fun backgroundChange(view: View) {
        saludo = view.findViewById(R.id.tv_saludo)
        ContraintLayout = view.findViewById(R.id.background)
        val hora = LocalTime.now()

        if (hora in LocalTime.of(7,0)..LocalTime.of(18,0)) {
            ContraintLayout.setBackgroundResource(R.drawable.background_manana)
            saludo.text = "Buenos dias"
            saludo.setTextColor(Color.DKGRAY)
        } else if (hora in LocalTime.of(18,0)..LocalTime.of(21,0)) {
            ContraintLayout.setBackgroundResource(R.drawable.background_manana)
            saludo.text = "Buenas tardes"
            saludo.setTextColor(Color.DKGRAY)
        } else {
            ContraintLayout.setBackgroundResource(R.drawable.background_noche)
            saludo.text = "Buenas noches"
            saludo.setTextColor(Color.WHITE)
        }
    }
     */


}