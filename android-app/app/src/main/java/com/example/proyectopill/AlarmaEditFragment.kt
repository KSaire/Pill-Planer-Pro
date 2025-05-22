package com.example.proyectopill

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.proyectopill.api.AlarmaPeticion
import com.example.proyectopill.api.CrudApi
import com.example.proyectopill.api.Medicamento
import com.example.proyectopill.api.Mensaje
import com.example.proyectopill.api.UsuarioMedicamentoPeticion
import com.example.proyectopill.databinding.FragmentAlarmaEditBinding
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmaEditFragment : Fragment() {
    private var userId: Int = -1
    private var rol: String? = null
    private var _binding: FragmentAlarmaEditBinding? = null
    private val binding get() = _binding!!
    private val selectedDays = mutableSetOf<Int>()
    private var meds: List<Medicamento> = emptyList()

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
        val view = inflater.inflate(R.layout.fragment_alarma_edit, container, false)
        _binding = FragmentAlarmaEditBinding.bind(view)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            meds = CrudApi().getMedicamentosUsuario(userId) ?: emptyList()
        }
        val nombres = meds.map { it.nombre }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            nombres
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.actvMedicamento.setAdapter(adapter)

        binding.actvMedicamento.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) (v as AutoCompleteTextView).showDropDown()
        }

        val horarioId = arguments?.getInt("horarioId", -1) ?: -1
        if (horarioId != -1) {
            binding.timePicker.hour   = requireArguments().getString("hora")!!.split(":")[0].toInt()
            binding.timePicker.minute = requireArguments().getString("hora")!!.split(":")[1].toInt()
            binding.etMedName.setText(requireArguments().getString("nombreAlarma"))
            binding.swSound.isChecked = requireArguments().getBoolean("sonido")
            binding.swVibration.isChecked = requireArguments().getBoolean("vibracion")
            binding.actvRepetition.setText(requireArguments().getString("frecuencia"), false)
            val diasCsv = requireArguments().getString("dias")!!
            diasCsv.split(",").mapNotNull(String::toIntOrNull).forEach { day ->
                when(day){
                    1 -> binding.btnL.isChecked = true
                    2 -> binding.btnM.isChecked = true
                    3 -> binding.btnX.isChecked = true
                    4 -> binding.btnJ.isChecked = true
                    5 -> binding.btnV.isChecked = true
                    6 -> binding.btnS.isChecked = true
                    7 -> binding.btnD.isChecked = true
                }
                selectedDays.add(day)
            }
            val cnArg = requireArguments().getString("cnMed")!!
            val selMed = meds.find { it.cn == cnArg }
            if (selMed != null) {
                binding.actvMedicamento.setText(selMed.nombre, false)
            } else {
                binding.actvMedicamento.setText(cnArg, false)
            }

        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("EEEE – dd MMMM", Locale("es"))
        val raw = formatter.format(today)
        binding.tvFecha.text = raw.replaceFirstChar { it.uppercase() }

        val repeticiones = listOf(
            "No repetir",
            "Cada 5 minutos, 3 veces",
            "Cada 10 minutos, 2 veces",
            "Cada 15 minutos, 5 veces",
            "Cada 30 minutos, 3 veces"
        )

        val repetitionAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            repeticiones
        )
        repetitionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.actvRepetition.setAdapter(repetitionAdapter)

        binding.actvRepetition.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) (v as AutoCompleteTextView).showDropDown()
        }

        val dayMap = mapOf(
            R.id.btnL to 1,
            R.id.btnM to 2,
            R.id.btnX to 3,
            R.id.btnJ to 4,
            R.id.btnV to 5,
            R.id.btnS to 6,
            R.id.btnD to 7
        )

        binding.toggleGroupDays.addOnButtonCheckedListener { group, checkedId, isChecked ->
            dayMap[checkedId]?.let { day ->
                if (isChecked) selectedDays.add(day) else selectedDays.remove(day)
            }
        }

        binding.btnHecho.setOnClickListener {
            if (userId < 0 || rol.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Datos de usuario no disponibles", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textSeleccionado = binding.actvMedicamento.text.toString()
            val idx1 = nombres.indexOf(textSeleccionado)

            if (idx1 == -1 ) {
                binding.tilMedicamento.error = "Selecciona un medicamento válido"
                return@setOnClickListener
            }
            val selMed = meds[idx1]
            val repeSeleccionada = binding.actvRepetition.text.toString()
            val idx2 = repeticiones.indexOf(repeSeleccionada)
            if (idx2 == -1) {
                binding.tilRepetition.error = "Selecciona un frecuencia"
                return@setOnClickListener
            }

            val diasCsv = when {
                selectedDays.isEmpty() -> "Una vez"
                selectedDays.size == 7    -> "Cada día"
                else -> selectedDays.sorted().joinToString(",")
            }
            val horario = AlarmaPeticion(
                idUsuario = userId,
                cnMed = selMed.cn,
                hora = "%02d:%02d".format(binding.timePicker.hour, binding.timePicker.minute),
                sonido = binding.swSound.isChecked,
                vibracion = binding.swVibration.isChecked,
                frecuencia = binding.actvRepetition.text.toString(),
                dias = diasCsv,
                nombre = binding.etMedName.text.toString())
            val res : Mensaje?
            if (horarioId != -1) {
                runBlocking {
                    res = CrudApi().updateHorario(horarioId, horario)
                }
            } else {
                runBlocking {
                    res = CrudApi().crearHorarios(horario)
                }
            }
            Toast.makeText(requireContext(), res!!.mensaje, Toast.LENGTH_LONG).show()
            if (res.exito){
                parentFragmentManager.setFragmentResult("nuevoHorario", bundleOf())
                parentFragmentManager.popBackStack()
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
            AlarmaEditFragment().apply {
                arguments = Bundle().apply {
                    putInt("userId", userId)
                    putString("rol", rol)
                }
            }
    }
}