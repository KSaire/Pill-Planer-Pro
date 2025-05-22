package com.example.proyectopill.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("Usuarios/login")
    suspend fun loginUsuario(@Body user: LoginPeticion): Response<UsuarioRespuesta>
    @POST("Usuarios")
    suspend fun registrarUsuario(@Body user: UsuarioPeticion): Response<UsuarioRespuesta>
    @GET("Horarios/usuario/{userId}")
    suspend fun getHorariosByUser(@Path("userId") userId: Int): Response<List<AlarmaRespuesta>>
    @POST("Horarios")
    suspend fun createHorario(@Body body: AlarmaPeticion): Response<Mensaje>
    @POST("UsuarioMedicamentos")
    suspend fun addUserMed(@Body body: UsuarioMedicamentoPeticion): Response<Mensaje>
    @GET("Medicamentos/usuario/{userId}")
    suspend fun getMedicamentosUsuario(@Path("userId") userId: Int): Response<List<Medicamento>>
    @DELETE("Horarios/{id}")
    suspend fun deleteHorario(@Path("id") id: Int): Response<Mensaje>
    @PUT("Horarios/{id}")
    suspend fun updateHorario(@Path("id") id: Int, @Body alarma: AlarmaPeticion): Response<Mensaje>
    @PATCH("Horarios/{id}/activo")
    suspend fun patchHorarioActivo(@Path("id") id: Int, @Body activa: Boolean): Response<Mensaje>
    @DELETE("UsuarioMedicamentos")
    suspend fun deleteMedUser(@Query("userId") userId: Int, @Query("medCn") medCn: String): Response<Mensaje>
}