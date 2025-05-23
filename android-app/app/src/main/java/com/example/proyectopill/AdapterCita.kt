package com.example.proyectopill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.Cita
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdapterCita(val llista: MutableList<Cita>, val onEdit: (Cita, Int) -> Unit,
                  val onDelete: (Cita, Int) -> Unit): RecyclerView.Adapter<AdapterCita.ViewHolder>() {
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista) {
        val tvCita = vista.findViewById<TextView>(R.id.tvCita)
        val btnEdit = vista.findViewById<TextView>(R.id.btnUpdateCita)
        val btnDelete = vista.findViewById<TextView>(R.id.btnDeleteCita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.item_cita, parent, false))
    }

    override fun getItemCount() = llista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cita = llista[position]
        val zoned = LocalDateTime.parse(cita.fechaHora).atZone(ZoneId.systemDefault())
        val diaSemana = zoned.format(DateTimeFormatter.ofPattern("EEEE", Locale("es")))
            .replaceFirstChar { it.uppercase() }
        val fecha = zoned.format(DateTimeFormatter.ofPattern("dd/MM"))
        val hora = zoned.format(DateTimeFormatter.ofPattern("HH:mm"))
        holder.tvCita.text = "$diaSemana $fecha a las $hora"

        holder.btnEdit.setOnClickListener {
            onEdit(cita, position)
        }
        holder.btnDelete.setOnClickListener {
            onDelete(cita, position)
        }
    }
}