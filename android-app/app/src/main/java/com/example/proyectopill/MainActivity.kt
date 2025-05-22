package com.example.proyectopill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopill.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val REQUEST_PER_CAMERA: Int = 100
    private var totsPermisos = false
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        comprobarPermisoCamera()

        if (!totsPermisos){
            Toast.makeText(this, "No tienes permisos de camara, por favor vaya a ajustes y concédalos", Toast.LENGTH_LONG).show()
        }

        binding.root.setOnClickListener {
            val intent = Intent(this, SelectionRolActivity::class.java)
            startActivity(intent)
        }
    }

    private fun comprobarPermisoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED){
            totsPermisos = true
        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                totsPermisos = false
            else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), REQUEST_PER_CAMERA)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PER_CAMERA) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                totsPermisos = true
            else
                totsPermisos = false
        }
    }
}