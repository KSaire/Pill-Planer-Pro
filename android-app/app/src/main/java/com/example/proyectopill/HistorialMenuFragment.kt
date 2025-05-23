package com.example.proyectopill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.proyectopill.databinding.FragmentCalendarioMenuBinding

class HistorialMenuFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentCalendarioMenuBinding? = null
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
        _binding = FragmentCalendarioMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            HistorialMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}