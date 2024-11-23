package com.example.atry.api

import com.example.atry.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkManager {

    // 定义后端 API 的基础 URL
    private const val BASE_URL = "http://10.0.2.2:4523/m1/5507544-5183784-default/"

    // 创建日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 设置日志级别
    }

    // 使用 lazy 初始化 Retrofit，确保只有一个实例
    private val retrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // 添加日志拦截器
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)  // 设置基础 URL
            .client(client)     // 使用带有日志拦截器的 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())  // 添加 Gson 转换器，用于解析 JSON 数据
            .build()
    }

    data class LoginRequest(
        val username: String,
        val password: String
    )

    data class LoginResponse(
        val token: String
    )

    data class SignupRequest(
        val username: String,
        val password: String
    )

    data class SignupResponse(
        val code: String,
    )

    // 获取 ApiService 实例
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)  // 创建 ApiService 的实现
    }

    val authService: AuthService = retrofit.create(AuthService::class.java)
}