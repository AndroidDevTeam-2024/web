package com.example.atry

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import com.example.atry.api.NetworkManager
import com.example.atry.iomanager.AvatarManager.uploadAvatarWithCommodityId
import com.example.atry.iomanager.AvatarManager.uploadAvatarWithUserId
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileActivity : ComponentActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 注册图片选择器
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
            } else {
                Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            EditProfileScreen()
        }
    }

    @Composable
    fun EditProfileScreen() {
        val coroutineScope = rememberCoroutineScope()
        // 使用 verticalScroll 来使页面可滚动
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),  // 使 Column 支持垂直滚动
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "编辑个人信息",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Username Input
            var username by remember { mutableStateOf(UserSession.getInstance().username) }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Password Input
            var password by remember { mutableStateOf("") }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            // Email Input
            var email by remember { mutableStateOf(UserSession.getInstance().email) }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("邮箱") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .clickable { pickImage() }, // Make the Box clickable to pick an image
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    val bitmap = loadBitmapFromUri(selectedImageUri!!)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text(
                        text = "点击此处更换头像",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val context = LocalContext.current // 获取当前的上下文
            // Save Button
            Button(
                onClick = {
                    if (password == "") {
                        Toast.makeText(context, "请填写密码", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch {
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    NetworkManager.authService.editprofile(
                                        UserSession.getInstance().id,
                                        NetworkManager.EditProfileRequest(
                                            username,
                                            password,
                                            email
                                        )
                                    )
                                }
                                if (response.isSuccessful) {
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        println("success")
                                    } else {
                                        println("success")
                                    }
                                } else {
                                    println("failed")
                                }
                            } catch (e: Exception) {
                                println("send failed")
                            }
                            if (selectedImageUri != null) {
                                try {
                                    val bitmap = loadBitmapFromUri(selectedImageUri!!)
                                    // 使用后台线程执行耗时操作
                                    val response = withContext(Dispatchers.IO) {
                                        if (bitmap != null) {
                                            uploadAvatarWithUserId(UserSession.getInstance().id.toString(), bitmap, "imageuser" + UserSession.getInstance().id)
                                        }
                                    }

                                } catch (e: Exception) {
                                    // 处理异常
                                    e.printStackTrace()
                                }
                            }
                        }
                        Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("保存")
            }

            // Save Button
            Button(
                onClick = { finish() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("返回")
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



}
