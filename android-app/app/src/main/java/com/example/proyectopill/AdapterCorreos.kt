package com.example.proyectopill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopill.api.Cita
import com.example.proyectopill.api.UsuarioRespuesta
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdapterCorreos(val llista: List<UsuarioRespuesta>, val onClick: (UsuarioRespuesta) -> Unit): RecyclerView.Adapter<AdapterCorreos.ViewHolder>() {
    private var selectedPos = RecyclerView.NO_POSITION
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista) {
        val tvEmail = vista.findViewById<TextView>(R.id.tvCorreo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.item_correos, parent, false))
    }

    override fun getItemCount() = llista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = llista[position]
        holder.tvEmail.text = usuario.email
        holder.itemView.isActivated = (position == selectedPos)

        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val old = selectedPos
            selectedPos = pos
            if (old != RecyclerView.NO_POSITION) {
                notifyItemChanged(old)
            }
            notifyItemChanged(selectedPos)

            onClick(llista[pos])
        }
    }
}