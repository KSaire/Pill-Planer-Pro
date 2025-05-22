package com.example.proyectopill

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.UsuarioPeticion
import com.example.proyectopill.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var rol: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = ThemePrefs.loadMode(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rol = intent.getStringExtra("rol")

        val titulo   = when (rol) {
            "paciente" -> "Crear Paciente"
            "medico" -> "Crear Médico"
            else -> "Crear Familiar"
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

        binding.tituloRegistro.text = titulo
        binding.tiPassword.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.accent_blue)))
        binding.tiConfirmPassword.setEndIconTintList(ColorStateList.valueOf(getColor(R.color.accent_blue)))

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCrearCuenta.setOnClickListener {
            crearCuenta()
        }
    }

    private fun crearCuenta() {
        val nombre = binding.etNombre.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val pwd = binding.etPassword.text.toString().trim()
        val cpwd = binding.etConfirmPassword.text.toString().trim()
        var error = false

        if(nombre.isNullOrBlank()){
            binding.etNombre.error = "Introduce tu nombre de usuario"
            error = true
        }

        if(correo.isNullOrBlank()){
            binding.etCorreo.error = "Introduce tu correo"
            error = true
        } else if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            binding.etCorreo.error = "Introduce un correo válido"
            error = true
        }

        if(pwd.isNullOrBlank()){
            binding.etPassword.error = "Introduce tu contraseña"
            error = true
        } else if(pwd.length < 5){
            binding.etPassword.error = "Mínimo tienen que haber 6 carácteres"
            error = true
        } else if (pwd != cpwd){
            binding.etConfirmPassword.error = "Las contraseñas tienen que coincidir"
            error = true
        }

        if(error){
            return
        }
        try{
            val api = CrudApi()
            val usuario = UsuarioPeticion(nombre = nombre, email =  correo, contrasena =  pwd, rol = rol)
            val resultado = api.registrarUsuario(usuario)
            if(resultado!!.exito){
                Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show()
            }
        }catch(e: Exception){
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