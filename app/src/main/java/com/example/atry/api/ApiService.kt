package com.example.atry.api

import com.example.atry.model.Product
import com.example.atry.model.UserSession
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {


    // 你可以根据需求继续添加其他接口，例如获取所有用户、创建用户等
    //@POST("users")
    //fun createUser(@Body user: User): Call<User>

    // 获取所有商品
    @GET("commodity/find_all")
    suspend fun allGoods(): Response<NetworkManager.AllGoodsResponse>

    // 根据类别查找商品
    @GET("commodity/find_by_category/{category_id}")
    suspend fun categoryGoods(@Path("category_id") categoryId: String): Response<NetworkManager.AllGoodsResponse>

    // 获取商品详情
    @GET("commodity/find_by_id/{id}")
    suspend fun detailGoods(@Path("id") commodityId: Int): Response<NetworkManager.ProductDetail>
}
interface AuthService {
    @POST("user/login") // 登录接口的路径，具体根据你的后端 API 修改
    suspend fun login(@Body request: NetworkManager.LoginRequest): Response<NetworkManager.LoginResponse>
    @POST("user/register")
    suspend fun signup(@Body request: NetworkManager.SignupRequest): Response<NetworkManager.SignupResponse>
    @GET("user/avatar/{userId}")
    suspend fun getAvatar(@Path("userId") userId: Int): Response<ResponseBody>
    @Multipart
    @POST("user/upload_avator")
    suspend fun uploadAvatar(
        @Part("id") id: RequestBody,
        @Part avator: MultipartBody.Part
    ): Response<Void>
    @POST("message/send")
    suspend fun sendMessage(@Body request: NetworkManager.SendMessRequest):Response<NetworkManager.messageResponse>

    @POST("commodity/register")
    suspend fun registercommodity(@Body request: NetworkManager.RegisterCommodityRequest):Response<NetworkManager.RegisterCommodityResponse>

    @Multipart
    @POST("commodity/upload_homepage")
    suspend fun uploadCommodityAvatar(
        @Part("id") id: RequestBody,
        @Part homepage: MultipartBody.Part
    ): Response<Void>


    @GET("commodity/find_by_publisher/{id}")
    suspend fun loadmycommodity(@Path("id") id: Int): Response<NetworkManager.LoadmycommodityResponse>

    @GET("deal/find_by_person/{id}")
    suspend fun loadmydeal(@Path("id") id: Int): Response<NetworkManager.LoadmydealResponse>

    @PUT("/user/update_by_id/{id}")
    suspend fun editprofile(@Path("id") id: Int, @Body request: NetworkManager.EditProfileRequest): Response<Void>

    @POST("commodity/update_by_id/{id}")
    suspend fun editcommodity(@Path("id") id: Int, @Body request: NetworkManager.EditCommodityRequest): Response<Void>

    @DELETE("commodity/delete_by_id/{id}")
    suspend fun deletemycommodity(@Path("id") id: Int): Response<Void>


    @GET("/commodity/find_by_id/{id}")
    suspend fun getcommodity(@Path("id") id: Int): Response<NetworkManager.GetCommodityResponse>

    @GET("/user/find_by_id/{id}")
    suspend fun getuser(@Path("id") id: Int): Response<NetworkManager.GetUserResponse>

    @POST("/deal/comment")
    suspend fun addcomment(@Body request: NetworkManager.AddCommentRequest):Response<Void>

    @GET("/message/find_by_receiver/{id}")
    suspend fun fetchMessages(@Path("id") userId: Int) :Response<NetworkManager.FetchMessResponse>

    @POST("message/delete/talk")
    suspend fun deleteTalk(@Body request: NetworkManager.TalkRequest): Response<ResponseBody>

    @POST("message/talk")
    suspend fun talk(@Body request: NetworkManager.TalkRequest): Response<NetworkManager.TalkResponse>

    @POST("message/refresh")
    suspend fun refreshTalk(@Body request: NetworkManager.RefreshRequest): Response<NetworkManager.TalkResponse>

    @GET("user/get_avator/{userId}")
    suspend fun getAvatarTalk(@Path("userId") userId: Int): Response<NetworkManager.avatarResponse>
    @POST("/deal/post")
    suspend fun createDeal(@Body request: NetworkManager.dealRequest): Response<NetworkManager.dealResponse>

}