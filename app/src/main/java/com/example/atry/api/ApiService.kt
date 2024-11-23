package com.example.atry.api

import com.example.atry.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 获取用户信息接口，传入用户 ID
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Call<User>

    // 你可以根据需求继续添加其他接口，例如获取所有用户、创建用户等
    //@POST("users")
    //fun createUser(@Body user: User): Call<User>
}

interface AuthService {
    @POST("localhost/login") // 登录接口的路径，具体根据你的后端 API 修改
    suspend fun login(@Body request: NetworkManager.LoginRequest): Response<NetworkManager.LoginResponse>
    @POST("user/register")
    suspend fun signup(@Body request: NetworkManager.SignupRequest): Response<NetworkManager.SignupResponse>
}