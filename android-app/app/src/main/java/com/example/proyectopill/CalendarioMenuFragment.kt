package com.example.proyectopill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopill.api.Cita
import com.example.proyectopill.api.CitaPeticion
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.databinding.FragmentCalendarioMenuBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarioMenuFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentCalendarioMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterCita
    private var fechaSeleccionada: Long = 0L

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

        fechaSeleccionada = binding.calendarView.date

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            fechaSeleccionada = cal.timeInMillis
        }

        binding.btnSave.setOnClickListener {
            val desc    = binding.etDescripcion.text.toString().trim()
            val horaStr = binding.etHora.text.toString().trim()
            if (desc.isEmpty() || horaStr.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val cal = Calendar.getInstance().apply {
                timeInMillis = fechaSeleccionada
                val (h, m) = horaStr.split(":").map { it.toInt() }
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
            }
            val dto = CitaPeticion(
                idUsuario   = userId,
                fechaHora   = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    .format(cal.time),
                descripcion = desc
            )
            val nueva = CrudApi().crearCita(dto)
            if (nueva != null) {
                Toast.makeText(requireContext(), "Cita creada", Toast.LENGTH_LONG).show()
                rellenarCitas()
            } else {
                Toast.makeText(requireContext(), "Error creando cita", Toast.LENGTH_LONG).show()
            }
        }

        rellenarCitas()
    }

    private fun rellenarCitas() {
        val lista = CrudApi().getCitasUsuario(userId) ?: emptyList()
        if (lista.isEmpty()) {
            return
        }

        val mutable = lista.toMutableList()
        adapter = AdapterCita(
            llista    = mutable,
            onEdit   = { cita, pos -> mostrarDialogEdicion(cita, pos) },
            onDelete = { cita, pos ->
                val msg = CrudApi().borrarCita(cita.idCita)
                if (msg?.exito == true) {
                    mutable.removeAt(pos)
                    adapter.notifyItemRemoved(pos)
                } else {
                    Toast.makeText(requireContext(), msg?.mensaje ?: "Error", Toast.LENGTH_LONG).show()
                }
            }
        )
        try {
            binding.rvCitas.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@CalendarioMenuFragment.adapter
            }
        } catch (e: Exception) {
            Log.e("RV_ERROR", "Error al inicializar RecyclerView", e)
            Toast.makeText(requireContext(), "Error cargando citas", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogEdicion(cita: Cita, pos: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_editar_cita, null)
        val etDesc = view.findViewById<TextInputEditText>(R.id.etDialogDescripcion)
        val etHora = view.findViewById<TextInputEditText>(R.id.etDialogHora)
        etDesc.setText(cita.descripcion)
        etHora.setText(cita.fechaHora.substringAfter("T").take(5))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar cita")
            .setView(view)
            .setPositiveButton("Guardar") { _, _ ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = fechaSeleccionada
                    val (h, m) = etHora.text.toString().split(":").map(String::toInt)
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                    set(Calendar.SECOND, 0)
                }
                val dto = CitaPeticion(
                    idUsuario   = userId,
                    fechaHora   = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        .format(cal.time),
                    descripcion = etDesc.text.toString().trim()
                )
                val upd = CrudApi().actualizarCita(cita.idCita, dto)
                if (upd != null) {
                    Toast.makeText(requireContext(), "Cita actualizada", Toast.LENGTH_LONG).show()
                    rellenarCitas()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int, rol: String) =
            CalendarioMenuFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}