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

class AdapterFamiliares(val llista: MutableList<UsuarioRespuesta>, val onDelete: (UsuarioRespuesta) -> Unit): RecyclerView.Adapter<AdapterFamiliares.ViewHolder>() {
    class ViewHolder (vista: View): RecyclerView.ViewHolder(vista) {
        val tvEmail = vista.findViewById<TextView>(R.id.tvCorreoFam)
        val btnBorrar= vista.findViewById<ImageButton>(R.id.btnBorrarFam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
        return ViewHolder(layout.inflate(R.layout.item_correo_fam, parent, false))
    }

    override fun getItemCount() = llista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = llista[position]
        holder.tvEmail.text = u.email
        holder.btnBorrar.setOnClickListener {
            onDelete(u)
        }
    }

    fun remove(item: UsuarioRespuesta) {
        val pos = llista.indexOf(item).takeIf { it >= 0 } ?: return
        llista.removeAt(pos)
        notifyItemRemoved(pos)
    }
}