package com.example.proyectopill

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.UsuarioRespuesta
import com.example.proyectopill.databinding.ActivityFamiliarBinding

class FamiliarActivity : AppCompatActivity() {
    private var userId: Int = -1
    private var userName: String? = null
    private var userEmail: String? = null
    private var rol: String? = null
    private lateinit var binding: ActivityFamiliarBinding
    private var selectedPaciente: UsuarioRespuesta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFamiliarBinding.inflate(layoutInflater)
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

        userId = intent.getIntExtra("userId", -1)
        userName = intent.getStringExtra("userName")
        userEmail = intent.getStringExtra("userEmail")
        rol = intent.getStringExtra("rol")

        binding.tvBienvenida.text = userName
            ?.let { "Bienvenido, \n$it" }
            ?: "Bienvenido, Familiar"

        val pacientes = CrudApi().getPacientesDeGestor(userId) ?: emptyList()

        val adapter = AdapterCorreos(pacientes) { paciente ->
            selectedPaciente = paciente
        }
        binding.rvPacientes.layoutManager = LinearLayoutManager(this)
        binding.rvPacientes.adapter = adapter

        binding.btnContinuar.setOnClickListener {
            selectedPaciente?.let { pac ->
                val intent = Intent(this, HistorialGestorActivity::class.java).apply {
                    putExtra("userId", pac.id)
                    putExtra("rol", rol)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "Selecciona un paciente", Toast.LENGTH_LONG).show()
            }
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