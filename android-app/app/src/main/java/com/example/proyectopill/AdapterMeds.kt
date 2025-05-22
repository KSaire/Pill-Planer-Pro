package com.example.proyectopill

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.Medicamento
import com.google.android.material.button.MaterialButton

class AdapterMeds(val meds: MutableList<Medicamento>, val onRemove: (Medicamento) -> Unit): RecyclerView.Adapter<AdapterMeds.ViewHolder>() {
    private fun placeholder(): Medicamento = Medicamento(
        cn = "",
        nregistro = "",
        nombre = "No hay medicamentos",
        dosis = null,
        formaFarmaceutica = null,
        viaAdministracion = null,
        recetaMedica = null
    )

    init {
        if (meds.isEmpty()) {
            meds.add(placeholder())
        }
    }
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista){
        val nombre = vista.findViewById<TextView>(R.id.tvCurrentMedName)
        val status = vista.findViewById<TextView>(R.id.tvCurrentMedStatus)
        val boton = vista.findViewById<MaterialButton>(R.id.btnEndPill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.item_current_med, parent, false))
    }

    override fun getItemCount() = meds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val med = meds[position]
        if (med.cn.isBlank()) {
            holder.nombre.text = med.nombre
            holder.status.text = "Desactivado"
            holder.boton.isEnabled = false
            holder.status.setTextColor(
                ContextCompat.getColor(holder.status.context, R.color.red)
            )
        } else {
            holder.nombre.text = med.nombre
            holder.status.text = "En uso"
            holder.status.setTextColor(ContextCompat.getColor(holder.status.context, R.color.green))
            holder.boton.isEnabled = true
            holder.boton.setOnClickListener {
                removeAt(position)
            }
        }
    }

    fun removeAt(idx: Int) {
        val med = meds.removeAt(idx)
        notifyItemRemoved(idx)
        onRemove(med)
        if (meds.isEmpty()) {
            meds.add( placeholder() )
            notifyItemInserted(0)
        }
    }
}