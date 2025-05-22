package com.example.proyectopill

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.Alarma
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdapterAlarma(val llista: List<Alarma>, private val onEdit: (pos: Int)->Unit,
                    private val onDelete: (pos: Int) -> Unit,
                    private val onToggle: (pos: Int, isChecked: Boolean) -> Unit): RecyclerView.Adapter<AdapterAlarma.ViewHolder>() {
    private val etiqueta = listOf("L", "M", "M", "J", "V", "S", "D")
    private val parse24 = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())
    private val fmt12   = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    var editMode = false
        set(vista) {
            field = vista
            notifyDataSetChanged()
        }
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista){
        val hora = vista.findViewById<TextView>(R.id.hora)
        val am_pm = vista.findViewById<TextView>(R.id.am_pm)
        val medicamento = vista.findViewById<TextView>(R.id.medicamento)
        val dias = vista.findViewById<TextView>(R.id.dias)
        val activo = vista.findViewById<SwitchCompat>(R.id.activo)
        val btnDelete   = vista.findViewById<ImageButton>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.cardview, parent, false))
    }

    override fun getItemCount() = llista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val time = LocalTime.parse(llista[position].hora, parse24)
        val texto12 = time.format(fmt12)
        val partes  = texto12.split(" ")
        holder.hora.setText(partes[0])
        holder.am_pm.setText(partes[1].replace(".", "").replace(" ", ""))
        holder.medicamento.setText(llista[position].medicamento)
        holder.activo.visibility = if (editMode) View.GONE else View.VISIBLE
        holder.btnDelete.visibility = if (editMode) View.VISIBLE else View.GONE
        when {
            llista[position].dias.isEmpty() -> {
                holder.dias.text = "Una vez"
            }
            llista[position].dias.size == 7 -> {
                holder.dias.text = "Cada día"
            }
            else -> {
                val join = etiqueta.joinToString("  ")
                val span = SpannableString(join)
                var idx = 0
                etiqueta.forEachIndexed { idxEti, eti ->
                    val inicio = idx
                    val fin    = inicio + eti.length
                    val colorRes = if (llista[position].dias.contains(idxEti+1))
                        R.color.accent_blue else R.color.text_secondary
                    val color = ContextCompat.getColor(holder.itemView.context, colorRes)
                    span.setSpan(
                        ForegroundColorSpan(color),
                        inicio, fin,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    span.setSpan(
                        StyleSpan(Typeface.BOLD),
                        inicio, fin,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    idx = fin + 2
                }
                holder.dias.text = span
            }
        }
        holder.activo.setOnCheckedChangeListener(null)
        holder.activo.isChecked = llista[position].activa

        holder.activo.setOnCheckedChangeListener { _, isChecked ->
            holder.itemView.alpha = if (isChecked){ 1f } else { 0.3f }
            onToggle(position, isChecked)
        }
        holder.itemView.alpha = if(llista[position].activa) 1f else 0.3f

        holder.btnDelete.setOnClickListener {
            onDelete(position)
        }

        holder.medicamento.setOnClickListener {
            onEdit(position)
        }
    }

}