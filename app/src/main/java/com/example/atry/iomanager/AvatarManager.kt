package com.example.atry.iomanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.atry.api.NetworkManager.authService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException


object AvatarManager {

    /**
     * 将图片写入本地
     */
    private fun saveAvatarToInternalStorage(context: Context, avatar: Bitmap, filename: String) {
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
                avatar.compress(Bitmap.CompressFormat.PNG, 100, fos) // 将 Bitmap 压缩为 PNG 格式
            }
            Log.d("SaveAvatar", "Avatar saved successfully!")
        } catch (e: IOException) {
            Log.e("SaveAvatar", "Error saving avatar", e)
        }
    }

    /**从本地加载图片
     *
     */
    fun loadAvatarFromInternalStorage(context: Context, filename: String): Bitmap? {
        return try {
            context.openFileInput(filename).use { fis ->
                BitmapFactory.decodeStream(fis) // 从文件流中读取 Bitmap
            }
        } catch (e: IOException) {
            Log.e("LoadAvatar", "Error loading avatar", e)
            null
        }
    }

    /**
     *此函数是一个异步函数，会将头像从后端加载并写入
     */
    suspend fun getUserAvatar(context: Context, id : String) {

        val response = authService.getAvatar(id.toInt())
        if (response.isSuccessful && response.body() != null) {
            // 获取头像的 InputStream
            val inputStream = response.body()?.byteStream()

            // 将 InputStream 转换为 Bitmap
            val avatarBitmap = BitmapFactory.decodeStream(inputStream)

            if (avatarBitmap != null) {
                // 保存到内部存储
                saveAvatarToInternalStorage(context, avatarBitmap, "user_avatar_$id.png")
            } else {
                Log.e("GetUserAvatar", "Failed to decode avatar bitmap")
            }
        } else {
            Log.e("GetUserAvatar", "Failed to fetch avatar: ${response.errorBody()?.string()}")
        }
    }

    private fun createAvatarPartFromBitmap(avatar: Bitmap, filename: String): MultipartBody.Part {
        // 将 Bitmap 压缩为 ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        avatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val avatarBytes = byteArrayOutputStream.toByteArray()

        // 创建 RequestBody
        val requestBody = avatarBytes.toRequestBody("image/png".toMediaTypeOrNull(), 0)

        // 创建 MultipartBody.Part
        return MultipartBody.Part.createFormData("avator", filename, requestBody)
    }

    private fun createUserIdRequestBody(userId: String): RequestBody {
        return userId.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    /**
     *此函数是一个异步函数，会将上传的头像从写入后端
     */
    suspend fun uploadAvatarWithUserId(
        userId: String,
        avatar: Bitmap,
        filename: String
    ) {
        // 创建 Retrofit 实例

        val userIdRequestBody = createUserIdRequestBody(userId)
        val avatarPart = createAvatarPartFromBitmap(avatar, filename)
        // 调用接口上传
        val response = authService.uploadAvatar(userIdRequestBody, avatarPart)

        if (response.isSuccessful) {
            Log.d("UploadAvatar", "Avatar uploaded successfully for userId: $userId")
        } else {
            Log.e("UploadAvatar", "Failed to upload avatar: ${response.code()}")
        }
    }


    //给商品上传图片
    suspend fun uploadAvatarWithCommodityId(
        commodityId: String,
        avatar: Bitmap,
        filename: String
    ) {
        // 创建 Retrofit 实例

        val commodityIdRequestBody = createCommodityIdRequestBody(commodityId)
        val avatarPart = createAvatarPartFromBitmap1(avatar, filename)
        // 调用接口上传
        val response = authService.uploadCommodityAvatar(commodityIdRequestBody, avatarPart)

        if (response.isSuccessful) {
            Log.d("UploadCommodityAvatar", "Avatar uploaded successfully for commodityId: $commodityId")
        } else {
            Log.e("UploadCommodityAvatar", "Failed to upload avatar: ${response.code()}")
        }
    }

    private fun createCommodityIdRequestBody(commodityId: String): RequestBody {
        return commodityId.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createAvatarPartFromBitmap1(avatar: Bitmap, filename: String): MultipartBody.Part {
        // 将 Bitmap 压缩为 ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        avatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val avatarBytes = byteArrayOutputStream.toByteArray()

        // 创建 RequestBody
        val requestBody = avatarBytes.toRequestBody("image/png".toMediaTypeOrNull(), 0)

        // 创建 MultipartBody.Part
        return MultipartBody.Part.createFormData("homepage", filename, requestBody)
    }

}