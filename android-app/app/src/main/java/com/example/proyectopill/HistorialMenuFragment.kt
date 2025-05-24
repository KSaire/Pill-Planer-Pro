package com.example.proyectopill

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.MedicamentoHistorial
import com.example.proyectopill.databinding.FragmentHistorialMenuBinding

class HistorialMenuFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentHistorialMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterHistorialMed
    private val allMeds = mutableListOf<MedicamentoHistorial>()

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
        _binding = FragmentHistorialMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterHistorialMed(requireContext(), mutableListOf()) { med ->

        }

        binding.medicamentosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.medicamentosRecyclerView.adapter = adapter

        val meds = CrudApi().getHistorialUsuario(userId)
        if (meds != null) {
            adapter.updateList(meds)
        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().lowercase()
                val filtrat = allMeds.filter { it.nombre.lowercase().contains(text) }
                adapter.updateList(filtrat)
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) = Unit
        })
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