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
import java.time.format.DateTimeFormatterBuilder
import java.time.format.TextStyle
import java.util.Locale

class AdapterHistorialMed(private val context: Context, val meds: MutableList<MedicamentoHistorial>, private val onClick: (MedicamentoHistorial) -> Unit): RecyclerView.Adapter<AdapterHistorialMed.ViewHolder>() {
    private val expandedPositions = mutableSetOf<Int>()

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
        holder.tvNombre.text = med.nombreMedicamento
        holder.eventosLayout.removeAllViews()

        val eventosAMostrar = if (expandedPositions.contains(position)) {
            med.historial
        } else {
            med.historial.take(3)
        }

        val localeEs = Locale("es", "ES")
        val formatterDia = DateTimeFormatterBuilder()
            .appendPattern("d ")
            .appendText(java.time.temporal.ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .toFormatter(localeEs)
        val formatterHora = DateTimeFormatter.ofPattern("HH:mm", localeEs)

        eventosAMostrar.forEach { ev ->
            val tv = TextView(context).apply {
                val icon = if (ev.tomado) "✅" else "❌"

                val dt = LocalDateTime.parse(ev.fechaHora).atZone(ZoneId.systemDefault()).toLocalDateTime()
                val dia = dt.format(formatterDia)
                val hora = dt.format(formatterHora)

                text = if (ev.tomado)
                    "$icon $dia – Tomado a las $hora"
                else
                    "$icon $dia – Olvidado"
                setTextColor(context.getColor(R.color.text_primary))
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            holder.eventosLayout.addView(tv)
        }

        if (med.historial.size > 3) {
            val tvToggle = TextView(context).apply {
                text = if (expandedPositions.contains(position)) "Mostrar menos ↑" else "Mostrar más ↓"
                setTextColor(context.getColor(R.color.accent_blue))
                textSize = 14f
                setPadding(0, 8, 0, 0)
            }
            holder.eventosLayout.addView(tvToggle)
        }

        holder.card.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    fun updateList(filtered: List<MedicamentoHistorial>) {
        meds.clear()
        meds.addAll(filtered)
        expandedPositions.clear()
        notifyDataSetChanged()
    }
}


