package com.example.proyectopill

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopill.databinding.ActivitySelectionRolBinding

class SelectionRolActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionRolBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelectionRolBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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

        binding.btnPaciente.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("rol", "paciente")
            startActivity(intent)
        }

        binding.btnFamiliar.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("rol", "familiar")
            startActivity(intent)
        }

        binding.btnMedico.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("rol", "medico")
            startActivity(intent)
        }
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