package com.proyecto.appmpazpaniol.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ÚNICA LÍNEA QUE DEBES CAMBIAR CUANDO REINICIES EL LAB DE AWS ACADEMY
    private const val SERVER_IP = "34.238.37.196"

    private const val URL_SOLICITUDES = "http://$SERVER_IP:8080/"
    private const val URL_INVENTARIO  = "http://$SERVER_IP:8081/"
    private const val URL_EQUIPOS     = "http://$SERVER_IP:8082/"

    // Interceptor para ver en Logcat las peticiones HTTP y respuestas JSON
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con tiempos de espera tolerantes para instancias t3.micro de AWS
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val solicitudesRetrofit: Retrofit by lazy { buildRetrofit(URL_SOLICITUDES) }
    val inventarioRetrofit: Retrofit by lazy { buildRetrofit(URL_INVENTARIO) }
    val equiposRetrofit: Retrofit by lazy { buildRetrofit(URL_EQUIPOS) }

    val solicitudesApi: SolicitudesApiService by lazy {
        solicitudesRetrofit.create(SolicitudesApiService::class.java)
    }

    val inventarioApi: InventarioApiService by lazy {
        inventarioRetrofit.create(InventarioApiService::class.java)
    }

    val equiposApi: EquiposApiService by lazy {
        equiposRetrofit.create(EquiposApiService::class.java)
    }
}