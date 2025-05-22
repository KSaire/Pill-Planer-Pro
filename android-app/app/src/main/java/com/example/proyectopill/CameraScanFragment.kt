package com.example.proyectopill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.databinding.FragmentCameraScanBinding
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.runBlocking

class CameraScanFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentCameraScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var scanner: DecoratedBarcodeView
    private var lastCn: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("userId")
            rol = it.getString("rol")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        startScanning()

        binding.btnSave.setOnClickListener {
            val cn = lastCn
            if (userId == -1 || rol.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cn.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Aún no ha escaneado un código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            runBlocking {
                val res = CrudApi().addUserMed(userId, cn)
                if (res!!.exito) {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fcvAlarma,AlarmaEditFragment.newInstance(userId, rol!!))
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startScanning() {
        scanner = binding.barcodeScanner
        scanner.decodeContinuous { result ->
            result.text?.let {
                lastCn = it
                Toast.makeText(requireContext(), "CN detectado = $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::scanner.isInitialized) scanner.resume()
    }

    override fun onPause() {
        if (::scanner.isInitialized) scanner.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            CameraScanFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}