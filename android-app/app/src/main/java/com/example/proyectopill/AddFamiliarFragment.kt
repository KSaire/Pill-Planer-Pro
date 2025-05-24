package com.example.proyectopill

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.databinding.FragmentAddFamiliarBinding

class AddFamiliarFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentAddFamiliarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterFamiliares

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
        _binding = FragmentAddFamiliarBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        adapter = AdapterFamiliares(mutableListOf()) { familiar ->
            val res = CrudApi().removeFamiliar(userId, familiar.id)
            if (res!!.exito) {
                adapter.remove(familiar)
            }else{
                Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
            }
        }
        binding.rvFamiliares.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFamiliares.adapter = adapter

        CrudApi().getFamiliares(userId)?.let {
            adapter.llista.addAll(it)
            adapter.notifyDataSetChanged()
        }

        binding.btnAddFam.setOnClickListener {
            val et = EditText(requireContext()).apply {
                hint = "Email del familiar"
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Nuevo familiar")
                .setView(et)
                .setPositiveButton("Añadir") { d,_ ->
                    val mail = et.text.toString().trim()
                    val todos = CrudApi().getUsuariosByRol("Familiar")
                    val famUser = todos?.firstOrNull { it.email.equals(mail, true) }
                    if (famUser != null) {
                        CrudApi().addFamiliar(userId, famUser.email)?.let { msg ->
                            Toast.makeText(requireContext(), msg.mensaje, Toast.LENGTH_LONG).show()
                            if (msg.exito) {
                                adapter.llista.add(famUser)
                                adapter.notifyItemInserted(adapter.llista.size-1)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No existe ningún familiar con ese email", Toast.LENGTH_LONG).show()
                    }
                    d.dismiss()
                }
                .setNegativeButton("Cancelar",null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            AddFamiliarFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}