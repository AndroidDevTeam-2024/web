package com.example.atry.api

import com.example.atry.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {


    // 你可以根据需求继续添加其他接口，例如获取所有用户、创建用户等
    //@POST("users")
    //fun createUser(@Body user: User): Call<User>

    // 获取所有商品
    @GET("commodity/find_all")
    suspend fun allGoods(): Response<Array<Product>>

    // 根据类别查找商品
    @GET("commodity/find_by_category/{category_id}")
    suspend fun categoryGoods(@Path("category_id") categoryId: String): Response<Array<Product>>

    // 获取商品详情
    @GET("commodity/detail/{commodity_id}")
    suspend fun detailGoods(@Path("commodity_id") commodityId: Int): Response<NetworkManager.ProductDetail>
}
interface AuthService {
    @POST("user/login") // 登录接口的路径，具体根据你的后端 API 修改
    suspend fun login(@Body request: NetworkManager.LoginRequest): Response<NetworkManager.LoginResponse>
    @POST("user/register")
    suspend fun signup(@Body request: NetworkManager.SignupRequest): Response<NetworkManager.SignupResponse>
    @POST("message/talk")
    suspend fun talk(@Body request: NetworkManager.TalkRequest): Response<NetworkManager.TalkResponse>
    @GET("user/avatar/{userId}")
    suspend fun getAvatar(@Path("userId") userId: String): Response<ResponseBody>
    @Multipart
    @POST("user/upload_avator")
    suspend fun uploadAvatar(
        @Part("id") id: RequestBody,
        @Part avator: MultipartBody.Part
    ): Response<Void>
    @POST("message/send")
    suspend fun sendMessage(@Body request: NetworkManager.SendMessRequest):Response<ResponseBody>
}