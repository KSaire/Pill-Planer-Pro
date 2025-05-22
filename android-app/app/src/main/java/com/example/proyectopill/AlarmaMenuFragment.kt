package com.example.proyectopill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.Alarma
import com.example.proyectopill.api.AlarmaRespuesta
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.Medicamento
import com.example.proyectopill.api.Mensaje
import com.example.proyectopill.databinding.FragmentAlarmaMenuBinding
import kotlinx.coroutines.runBlocking

class AlarmaMenuFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentAlarmaMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterAlarma
    private lateinit var medAdapter: AdapterMeds
    private var editMode = false

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
        _binding = FragmentAlarmaMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rellenarAlarma()
        setupCurrentMeds()

        parentFragmentManager.setFragmentResultListener("nuevoHorario", viewLifecycleOwner) { _, bundle ->
            onViewCreated(view, savedInstanceState)  // recarga
        }

        binding.ivMenu.setOnClickListener {
            editMode = !editMode
            adapter.editMode = editMode
            adapter.notifyDataSetChanged()
            val icon = if (editMode) R.drawable.baseline_close_24 else R.drawable.baseline_edit_square_24
            binding.ivMenu.setImageResource(icon)
        }

        binding.fabAddAlarm.setOnClickListener {
            if (userId == -1 || rol.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val fragment = AlarmaEditFragment.newInstance(userId, rol!!)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fcvAlarma, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupCurrentMeds() {
        val fetched = runBlocking { CrudApi().getMedicamentosUsuario(userId) ?: emptyList() }
        val mutableMeds = if (fetched.isEmpty()) {
            mutableListOf(
                Medicamento(
                    cn = "",
                    nregistro = "",
                    nombre = "No hay medicamentos",
                    dosis = null,
                    formaFarmaceutica = null,
                    viaAdministracion = null,
                    recetaMedica = null
                )
            )
        } else {
            fetched.toMutableList()
        }

        medAdapter = AdapterMeds(mutableMeds) { med ->
            if (med.cn.isNotBlank()) {
                val res = runBlocking { CrudApi().borrarMedUsuario(userId, med.cn) }
                if (res!!.exito == true) {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                    rellenarAlarma()
                }else{
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.rvCurrentMeds.apply {
            adapter = medAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

                override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                    medAdapter.removeAt(vh.adapterPosition)
                }
            }).attachToRecyclerView(this)
        }
    }

    private fun rellenarAlarma(){
        val dtos: List<AlarmaRespuesta> = runBlocking {
            CrudApi().getHorarios(userId) ?: emptyList()
        }
        if (dtos.isEmpty()) return

        val alarmas = dtos.map {
            Alarma(
                hora = it.hora,
                medicamento = it.nombre,
                dias = it.dias.split(",").mapNotNull { it.toIntOrNull() },
                activa = it.activa
            )
        }.toMutableList()

        adapter = AdapterAlarma(
            llista = alarmas,
            onDelete = { pos ->
                val dto = dtos[pos]
                val res : Mensaje?
                runBlocking {
                    res = CrudApi().borrarHorario(dto.id)
                }
                Toast.makeText(requireContext(), res!!.mensaje, Toast.LENGTH_LONG).show()
                if(res.exito){
                    alarmas.removeAt(pos)
                    adapter.notifyItemRemoved(pos)
                }
            },
            onEdit = { pos ->
                val dto = dtos[pos]
                val args = Bundle().apply {
                    putInt("userId", dto.idUsuario ?: -1)
                    putString("rol", rol)
                    putInt("horarioId", dto.id)
                    putString("hora", dto.hora)
                    putString("dias", dto.dias)
                    putString("frecuencia", dto.frecuencia)
                    putBoolean("sonido", dto.sonido)
                    putBoolean("vibracion", dto.vibracion)
                    putString("nombreAlarma", dto.nombre)
                    putString("cnMed", dto.cnMed)
                }
                val frag = AlarmaEditFragment().apply { arguments = args }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fcvAlarma, frag)
                    .addToBackStack(null)
                    .commit()
            },
            onToggle = { pos, isChecked ->
                val dto = dtos[pos]
                val res = runBlocking { CrudApi().actualizarActivo(dto.id, isChecked) }
                if (res!!.exito) {
                    alarmas[pos].activa = isChecked
                } else {
                    Toast.makeText(requireContext(), res.mensaje, Toast.LENGTH_LONG).show()
                    alarmas[pos].activa = !isChecked
                    adapter.notifyItemChanged(pos)
                }
            }
        )

        binding.rvAlarmas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AlarmaMenuFragment.adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            AlarmaMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}
