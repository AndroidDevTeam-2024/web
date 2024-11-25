package com.example.atry.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object NetworkManager {

    // 定义后端 API 的基础 URL
    private const val BASE_URL = "http://47.93.172.156:8080"

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
        val name: String,
        val password: String
    )

    data class LoginResponse(
        val token: String
    )

    data class SignupRequest(
        val name: String,
        val password: String,
        val email: String
    )

    data class SignupResponse(
        val code: String,
    )

    data class TalkRequest(
        val id1: Int,
        val id2: Int
    )
    data class TalkResponse(
        val messages: List<Message>
    )

    data class SendMessRequest (
        val message : Message,
        val acceptor : String
    )


    val authService: AuthService = retrofit.create(AuthService::class.java)
    val apiService : ApiService = retrofit.create(ApiService::class.java)
    data class Message(
        val id: Int,
        val senderId: Int,
        val senderName: String,
        val content: String
    )
    data class OrderMessage(
        val baseMessage: Message, // 原始消息
        val orderId: String,
        val orderDetails: String
    )
    data class ProductDetail(
        val id: Int,
        val information: String,
        val price: String,
        val email: String,
        val imageUrl: String
    )



}