package com.example.proyectopill.api

import com.google.gson.annotations.SerializedName
import java.sql.Date

data class UsuarioPeticion(
    @SerializedName("Nombre")
    var nombre: String? = null,
    @SerializedName("Email")
    var email: String,
    @SerializedName("Contrasena")
    var contrasena: String,
    @SerializedName("FechaNacimiento")
    var fechaNacimiento: Date? = null,
    @SerializedName("Telefono")
    var telefono: String? = null,
    @SerializedName("IdConfig")
    var idConfig: Int? = null,
    @SerializedName("Rol")
    var rol: String? = null
)

data class UsuarioRespuesta(
    val id: Int,
    val nombre: String,
    val email: String,
    val contrasena: String,
    @SerializedName("idRols")
    val roles: List<String> = emptyList()
)


data class LoginPeticion(
    @SerializedName("Email")
    val email: String,
    @SerializedName("Contrasena")
    val contrasena: String,
    @SerializedName("Rol")
    var rol: String
)

data class Alarma(
    var hora: String,
    var medicamento: String,
    var dias: List<Int>,
    var activa: Boolean
)

data class AlarmaPeticion(
    @SerializedName("IdUsuario")
    val idUsuario: Int,
    @SerializedName("CnMed")
    val cnMed: String?,
    @SerializedName("Hora")
    val hora: String,
    @SerializedName("Sonido")
    val sonido: Boolean,
    @SerializedName("Vibracion")
    val vibracion: Boolean,
    @SerializedName("Frecuencia")
    val frecuencia: String,
    @SerializedName("Dias")
    val dias: String,
    @SerializedName("NombreAlarma")
    val nombre: String,
    @SerializedName("Activa")
    var activa: Boolean? = null
)


data class AlarmaRespuesta(
    val id: Int,
    val idUsuario: Int?,
    val cnMed: String?,
    val hora: String,
    val sonido: Boolean,
    val vibracion: Boolean,
    val frecuencia: String,
    val dias: String,
    val nombre: String,
    var activa: Boolean
)

data class UsuarioMedicamentoPeticion(
    @SerializedName("UserId")
    val idUsuario: Int,
    @SerializedName("CnMed")
    val cnMed: String
)

data class Medicamento(
    val cn: String,
    var nregistro: String,
    var nombre: String?,
    var dosis: String?,
    var formaFarmaceutica: String?,
    var viaAdministracion: String?,
    var recetaMedica: Boolean?
)

data class Cita(
    val idCita: Int,
    val idUsuario: Int,
    val fechaHora: String,
    val descripcion: String?
)
data class CitaPeticion(
    @SerializedName("IdUsuario")
    val idUsuario: Int,
    @SerializedName("FechaHora")
    val fechaHora: String,
    @SerializedName("Descripcion")
    val descripcion: String?
)

data class EventoMedicacion(
    val fechaHora: String,
    val tomado: Boolean
)

data class MedicamentoHistorial(
    val id: Int,
    val nombre: String,
    val historial: List<EventoMedicacion>
)

data class HistorialPeticion(
    val idUsuarioMedicamento: Int,
    val idHorario: Int,
    val fechaHora: String,
    val tomado: Boolean
)

data class Historial(
    val idHistorial: Int,
    val idUsuarioMedicamento: Int,
    val idHorario: Int,
    val fechaHora: String,
    val tomado: Boolean
)

data class Mensaje(
    var mensaje: String,
    var exito: Boolean
)