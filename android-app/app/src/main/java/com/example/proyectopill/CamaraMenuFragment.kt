package com.example.proyectopill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.Mensaje
import com.example.proyectopill.databinding.FragmentCamaraMenuBinding
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.runBlocking

class CamaraMenuFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentCamaraMenuBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentCamaraMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnScan.setOnClickListener {
            if (userId == -1 || rol.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fragment = CameraScanFragment.newInstance(userId, rol!!)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fcvAlarma, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnSave.setOnClickListener {
            val cn = binding.etCn.text.toString().trim()
            if (cn.isEmpty()) {
                binding.etCn.error = "Introduce un CN válido"
                return@setOnClickListener
            }

            runBlocking {
                val api = CrudApi()
                val res = api.addUserMed(userId, cn)
                if (res!!.exito) {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fcvAlarma, AlarmaEditFragment.newInstance(userId, rol!!))
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            CamaraMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}