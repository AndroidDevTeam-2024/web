package com.example.atry.api

import com.example.atry.AccountActivity
import com.example.atry.model.Product
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
        val id : Int,
        val email: String,
        val avator: String
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
    data class  TalkResponse(
        val messages: List<Message>,
        val date: String
    )

    data class SendMessRequest (
        val acceptor: Int,
        val publisher: Int,
        val content: String
    )

    //商品注册
    data class RegisterCommodityRequest (
        val name: String,
        val price: Int,
        val introduction: String,
        val business_id: Int,
        val category: String
    )

    data class RegisterCommodityResponse (
        val id: Int,
        val homepage: String,
    )

    //加载商品
    data class LoadmycommodityResponse (
        val commodities: Array<AccountActivity.Commodity>
    )

    //加载交易
    data class LoadmydealResponse (
        val deals: Array<AccountActivity.Deal>
    )

    data class EditProfileRequest (
        val name: String,
        val password: String,
        val email: String
    )

    data class EditCommodityRequest (
        val name: String,
        val price: Int,
        val introduction: String
    )

    data class GetCommodityResponse(
        val id: Int,
        val name: String,
        val price: Int,
        val introduction: String,
        val business_id: Int,
        val homepage: String,
        val exist: Boolean
    )

    data class GetUserResponse(
        val id: Int,
        val name: String,
        val email: String,
        val avator: String,
    )

    data class AddCommentRequest(
        val deal_id: Int,
        val comment: String,
        val date: String
    )

    data class AllGoodsResponse(
        val commodities: Array<Product>
    )

//    data class GetcommodityResponse (
//        val id: Int,
//        val name: String,
//        val price: Int,
//        val introduction: String,
//        val business_id: Int,
//        val homepage: String,
//        val exist: Boolean
//    )


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
        val name:String,
        val price: String,
        val introduction: String,
        val business_id: Int,
        val homepage: String,
        val exist: Boolean
    )

    data class FetchMessResponse(
        val messages : List<AvaMessage>
    )

    data class RefreshRequest(
        val id1 : Int,
        val id2 : Int,
        val date: String

    )

    data class dealRequest(
        val seller:Int,
        val customer:Int,
        val commodity: Int
    )

    data class dealResponse(
        val id :Int
    )

    data class messageResponse(
        val message: Message
    )

    data class AvaMessage(
        val id: Int,
        val senderId: Int,
        val senderName: String,
        val content: String,
        val avator: String
    )

    data class avatarResponse(
        val avator: String
    )





}