package com.example.proyectopill

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.EventoMedicacion
import com.example.proyectopill.api.MedicamentoHistorial
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdapterHistorialMed(private val context: Context, val meds: MutableList<MedicamentoHistorial>, private val onClick: (MedicamentoHistorial) -> Unit): RecyclerView.Adapter<AdapterHistorialMed.ViewHolder>() {
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista){
        val tvNombre = vista.findViewById<TextView>(R.id.tvNombreMedicamento)
        val eventosLayout = vista.findViewById<LinearLayout>(R.id.eventosLayout)
        val card: CardView = vista as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.item_medicamento, parent, false))
    }

    override fun getItemCount() = meds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val med = meds[position]
        holder.tvNombre.text = med.nombre
        holder.eventosLayout.removeAllViews()
        med.historial.take(3).forEach { ev ->
            val tv = TextView(context).apply {
                val icon = if (ev.tomado) "✅" else "❌"

                val dt = LocalDateTime.parse(ev.fechaHora).atZone(ZoneId.systemDefault())
                val dia = dt.format(DateTimeFormatter.ofPattern("d MMM", Locale("es")))
                val hora = dt.format(DateTimeFormatter.ofPattern("HH:mm"))

                text = "$icon $dia – ${if (ev.tomado) "Tomado a las" else "Olvidado"} $hora"
                setTextColor(context.getColor(R.color.text_primary))
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            holder.eventosLayout.addView(tv)
        }

        holder.card.setOnClickListener { onClick(med) }
    }

    fun updateList(filtered: List<MedicamentoHistorial>) {
        meds.clear()
        meds.addAll(filtered)
        notifyDataSetChanged()
    }
}


