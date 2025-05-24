package com.example.proyectopill

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopill.databinding.ActivityHistorialGestorBinding

class HistorialGestorActivity : AppCompatActivity() {
    private var userId: Int = -1
    private var rol: String? = null
    lateinit var binding: ActivityHistorialGestorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHistorialGestorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateThemeIcon()

        userId = intent.getIntExtra("userId", -1)
        rol    = intent.getStringExtra("rol")

        binding.btnCambiarTema.setOnClickListener {
            val newMode = if (delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.MODE_NIGHT_NO
            else
                AppCompatDelegate.MODE_NIGHT_YES

            ThemePrefs.saveMode(this, newMode)
            delegate.localNightMode = newMode
            recreate()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.historial_container,
                HistorialMenuFragment.newInstance(userId, rol!!)
            )
            .commit()
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