package com.example.proyectopill.api

import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

class CrudApi : CoroutineScope {
    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // private val urlApi = "http://172.16.24.151:5135/api/"
    private val urlApi = "http://192.168.1.15:5135/api/"

    private fun getClient(): OkHttpClient {
        val loggin = HttpLoggingInterceptor()
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder().addInterceptor(loggin).build()
    }

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder().baseUrl(urlApi).client(getClient())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    fun loginUsuario(login: LoginPeticion): Pair<UsuarioRespuesta?, Int>{
        var respuestaUser: UsuarioRespuesta? = null
        var code = 400
        runBlocking {
            var respuesta:  Response<UsuarioRespuesta>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).loginUsuario(login)
                code = respuesta!!.code()
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaUser = respuesta!!.body()
            } else {
                respuestaUser = null
            }

        }
        return Pair(respuestaUser, code)
    }

    fun registrarUsuario(usuario: UsuarioPeticion): Mensaje?{
        var registrado: Mensaje? = null
        runBlocking {
            var respuesta : Response<UsuarioRespuesta>? = null
            val cor= launch {
                respuesta = getRetrofit().create(ApiService::class.java).registrarUsuario(usuario)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                registrado = Mensaje("Usuario creado correctamente", true)
            }else if(respuesta!!.code() == 409){
                registrado = Mensaje("Correo ya registrado con este rol", false)
            }else{
                registrado = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return registrado
    }

    fun getHorarios(userId: Int): List<AlarmaRespuesta>?{
        var respuestaHorario: List<AlarmaRespuesta>? = null
        runBlocking {
            var respuesta: Response<List<AlarmaRespuesta>>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).getHorariosByUser(userId)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = respuesta!!.body()
            } else {
                respuestaHorario = null
            }
        }
        return respuestaHorario
    }

    fun crearHorarios(horario: AlarmaPeticion): Mensaje?{
        var respuestaHorario: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).createHorario(horario)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = Mensaje("Se ha creado la alarma correctamente", true)
            }else{
                respuestaHorario = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return respuestaHorario
    }

    fun addUserMed(userId: Int, cnMed: String): Mensaje? {
        var resultado: Mensaje? = null
        runBlocking {
            var respuesta : Response<Mensaje>? = null
            val cor= launch {
                respuesta = getRetrofit().create(ApiService::class.java).addUserMed(UsuarioMedicamentoPeticion(userId, cnMed))
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                resultado = Mensaje("Medicamento asociado al usuario", true)
            }else if(respuesta!!.code() == 409){
                resultado = Mensaje("Ese medicamento ya estaba añadido", false)
            }else if(respuesta!!.code() == 404){
                resultado = Mensaje("No se ha encontrado un medicamento con ese código", false)
            }else{
                resultado = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return resultado
    }

    fun getMedicamentosUsuario(userId: Int): List<Medicamento>?{
        var respuestaHorario: List<Medicamento>? = null
        runBlocking {
            var respuesta: Response<List<Medicamento>>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).getMedicamentosUsuario(userId)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = respuesta!!.body()
            } else {
                respuestaHorario = null
            }
        }
        return respuestaHorario
    }

    fun borrarHorario(id: Int): Mensaje?{
        var respuestaHorario: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).deleteHorario(id)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = Mensaje("Horario eliminado correctamente", true)
            } else if(respuesta!!.code() == 404){
                respuestaHorario = Mensaje("El horario no se ha podido encontrar", false)
            } else{
                respuestaHorario = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return respuestaHorario
    }

    fun updateHorario(horarioId: Int, horario: AlarmaPeticion): Mensaje? {
        var respuestaHorario: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).updateHorario(horarioId, horario)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = Mensaje("Horario editado correctamente", true)
            } else if(respuesta!!.code() == 404){
                respuestaHorario = Mensaje("El horario no se ha podido encontrar", false)
            } else{
                respuestaHorario = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return respuestaHorario
    }

    fun actualizarActivo(horarioId: Int, act: Boolean): Mensaje? {
        var respuestaHorario: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).patchHorarioActivo(horarioId, act)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaHorario = Mensaje("Horario editado correctamente", true)
            } else if(respuesta!!.code() == 404){
                respuestaHorario = Mensaje("El horario no se ha podido encontrar", false)
            } else{
                respuestaHorario = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return respuestaHorario
    }

    fun borrarMedUsuario(userId: Int, medCn: String): Mensaje?{
        var respuestaMed: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).deleteMedUser(userId, medCn)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                respuestaMed = Mensaje("Horario eliminado correctamente", true)
            } else if(respuesta!!.code() == 404){
                respuestaMed = Mensaje("El horario no se ha podido encontrar", false)
            } else{
                respuestaMed = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return respuestaMed
    }

    fun getAllCitas(): List<Cita>? {
        var result: List<Cita>? = null
        runBlocking {
            var respuesta: Response<List<Cita>>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).getAllCitas()
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                result = respuesta!!.body()
            } else {
                result = null
            }
        }
        return result
    }

    fun getCitasUsuario(userId: Int): List<Cita>? {
        var result: List<Cita>? = null
        runBlocking {
            var respuesta: Response<List<Cita>>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).getCitasByUser(userId)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                result = respuesta!!.body()
            } else {
                result = null
            }
        }
        return result
    }

    fun crearCita(cita: CitaPeticion): Cita? {
        var result: Cita? = null
        runBlocking {
            var respuesta: Response<Cita>? = null
            val cor = launch {
                respuesta = getRetrofit().create(ApiService::class.java).createCita(cita)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                result = respuesta!!.body()
            } else {
                result = null
            }
        }
        return result
    }

    fun actualizarCita(id: Int, cita: CitaPeticion): Cita? {
        var result: Cita? = null
        runBlocking {
            var respuesta: Response<Cita>? = null
            val cor = launch {
                respuesta = getRetrofit()
                    .create(ApiService::class.java)
                    .updateCita(id, cita)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                result = respuesta!!.body()
            } else {
                result = null
            }
        }
        return result
    }

    fun borrarCita(id: Int): Mensaje? {
        var result: Mensaje? = null
        runBlocking {
            var respuesta: Response<Mensaje>? = null
            val cor = launch {
                respuesta = getRetrofit()
                    .create(ApiService::class.java)
                    .deleteCita(id)
            }
            cor.join()
            if (respuesta!!.isSuccessful) {
                result = respuesta!!.body() ?: Mensaje("Cita eliminada correctamente", true)
            } else if (respuesta!!.code() == 404) {
                result = Mensaje("No se encontró la cita", false)
            } else {
                result = Mensaje("Error: ${respuesta!!.code()}", false)
            }
        }
        return result
    }
}