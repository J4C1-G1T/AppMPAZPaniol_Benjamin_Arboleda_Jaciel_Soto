package com.proyecto.appmpazpaniol.data.remote

import retrofit2.http.*

// 1. Microservicio de Equipos (Puerto 8082)
interface EquiposApiService {
    @GET("api/equipos")
    suspend fun obtenerTodosLosEquipos(): List<EquipoDto>

    @GET("api/equipos/{id}")
    suspend fun obtenerEquipoPorId(@Path("id") id: String): EquipoDto

    @GET("api/equipos/categoria/{categoria}")
    suspend fun obtenerEquiposPorCategoria(@Path("categoria") categoria: String): List<EquipoDto>

    @POST("api/equipos")
    suspend fun crearEquipo(@Body equipo: EquipoDto): EquipoDto

    @PUT("api/equipos/{id}")
    suspend fun actualizarEquipo(@Path("id") id: String, @Body equipo: EquipoDto): EquipoDto
}

// 2. Microservicio de Inventario (Puerto 8081)
interface InventarioApiService {
    @GET("api/inventario")
    suspend fun obtenerTodoElInventario(): List<InventarioDto>

    @GET("api/inventario/{equipoId}")
    suspend fun obtenerInventarioPorEquipo(@Path("equipoId") equipoId: String): InventarioDto

    @POST("api/inventario")
    suspend fun crearInventario(@Body inventario: InventarioDto): InventarioDto

    @PUT("api/inventario/{equipoId}")
    suspend fun actualizarInventario(
        @Path("equipoId") equipoId: String,
        @Body inventario: InventarioDto
    ): InventarioDto

    @PATCH("api/inventario/{equipoId}/estado")
    suspend fun cambiarEstadoInventario(
        @Path("equipoId") equipoId: String,
        @Query("valor") valor: String
    ): InventarioDto

    @PATCH("api/inventario/{equipoId}/stock")
    suspend fun actualizarStockInventario(
        @Path("equipoId") equipoId: String,
        @Query("valor") valor: Int
    ): InventarioDto

    @PATCH("api/inventario/{equipoId}/baja")
    suspend fun darDeBajaEquipo(@Path("equipoId") equipoId: String): InventarioDto
}

// 3. Microservicio de Solicitudes (Puerto 8080)
interface SolicitudesApiService {
    @GET("api/solicitudes")
    suspend fun obtenerTodasLasSolicitudes(): List<SolicitudDto>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): SolicitudDto

    @PUT("api/solicitudes/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Query("nuevoEstado") nuevoEstado: String
    ): SolicitudDto

    @PUT("api/solicitudes/{id}/rechazar")
    suspend fun rechazarSolicitud(
        @Path("id") id: Long,
        @Query("motivo") motivo: String
    ): SolicitudDto
}