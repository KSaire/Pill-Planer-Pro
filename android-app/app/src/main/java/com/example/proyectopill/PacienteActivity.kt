package com.example.proyectopill

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.proyectopill.databinding.ActivityPacienteBinding

class PacienteActivity : AppCompatActivity() {
    private var userId: Int = -1
    private var userName: String? = null
    private var userEmail: String? = null
    private var rol: String? = null
    lateinit var binding: ActivityPacienteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        updateThemeIcon()

        binding.btnCambiarTema.setOnClickListener {
            val newMode = if (delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.MODE_NIGHT_NO
            else
                AppCompatDelegate.MODE_NIGHT_YES

            ThemePrefs.saveMode(this, newMode)
            delegate.localNightMode = newMode
            recreate()
        }

        userId = intent.getIntExtra("userId", -1)
        userName = intent.getStringExtra("userName")
        userEmail = intent.getStringExtra("userEmail")
        rol = intent.getStringExtra("rol")

        if (savedInstanceState == null) {
            cambioFragment(AlarmaMenuFragment.newInstance(userId, rol ?: ""))
            binding.bn.selectedItemId = R.id.alarma
        }

        binding.bn.setOnItemSelectedListener {  item ->
            when (item.itemId ){
                R.id.alarma -> cambioFragment(AlarmaMenuFragment.newInstance(userId , rol ?: ""))
                R.id.camara -> cambioFragment(CamaraMenuFragment.newInstance(userId, rol ?: ""))
                R.id.calendario -> cambioFragment(CalendarioMenuFragment.newInstance(userId, rol ?: ""))
                R.id.history -> cambioFragment(HistorialMenuFragment.newInstance(userId, rol ?: ""))
                else -> cambioFragment(AlarmaMenuFragment.newInstance(userId, rol ?: ""))
            }
        }
    }

    private fun cambioFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fcvAlarma, fragment)
        transaction.commit()
        return true
    }

    private fun updateThemeIcon() {
        val isDark = delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES
        val (iconRes, tintColor) = if (isDark) {
            R.drawable.baseline_nightlight_round_24 to  getColor(R.color.text_terciary)
        } else {
            R.drawable.baseline_light_mode_24 to getColor(R.color.dark_blue)
        }

        binding.btnCambiarTema.setImageResource(iconRes)
        binding.btnCambiarTema.imageTintList = ColorStateList.valueOf(tintColor)
    }
}