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
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.LoginPeticion
import com.example.proyectopill.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var rol: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        rol = intent.getStringExtra("rol")

        binding.passwordLayout.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.accent_blue)))

        binding.btnIniciarSesion.setOnClickListener {
            val email = binding.etCorreo.text.toString()
            val contrasena = binding.etPassword.text.toString()
            iniciarSesion(email, contrasena, rol!!)
        }

        binding.btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("rol", rol)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun iniciarSesion(email: String, pwd: String, rol: String){
        if(email.isNullOrBlank() || pwd.isNullOrBlank() || rol.isNullOrBlank()){
            Toast.makeText(this, "Rellena todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val api = CrudApi()
            val resultado = api.loginUsuario(LoginPeticion(email, pwd, rol))
            if (resultado == null) {
                Toast.makeText(this, "El usuario o contraseña no son correctas.", Toast.LENGTH_LONG)
                    .show()
                return
            }

            val (usuario, code) = resultado
            when (code) {
                200 -> {
                    if(usuario == null){
                        Toast.makeText(this, "No se puede conectar con el servidor.", Toast.LENGTH_LONG).show()
                        return
                    }
                    Toast.makeText(this, "Bienvenido ${usuario.nombre}", Toast.LENGTH_LONG).show()
                    val intent = when (rol) {
                        "paciente" -> Intent(this, PacienteActivity::class.java)
                        "medico" -> Intent(this, MedicoActivity::class.java)
                        else -> Intent(this, FamiliarActivity::class.java)
                    }
                    intent.putExtra("userId", usuario.id)
                    intent.putExtra("userName", usuario.nombre)
                    intent.putExtra("userEmail", usuario.email)
                    intent.putExtra("rol", rol)
                    startActivity(intent)
                }
                401 -> {
                    Toast.makeText(this, "Email o contraseña son incorrectos.", Toast.LENGTH_LONG).show()
                }
                403 -> {
                    Toast.makeText(this, "El usuario no está registrado con este rol.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this, "Error $code al procesar el login.", Toast.LENGTH_LONG).show()
                }
            }
        } catch(e: Exception){
            Toast.makeText(this, "No se puede conectar con el servidor.", Toast.LENGTH_LONG).show()
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