package com.example.atry

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.atry.api.NetworkManager
import com.example.atry.iomanager.AvatarManager.uploadAvatarWithCommodityId
import com.example.atry.iomanager.AvatarManager.uploadAvatarWithUserId
import com.example.atry.model.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterCommodityActivity : ComponentActivity() {

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
            Surface(modifier = Modifier.fillMaxSize()) {
                RegisterScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RegisterScreen() {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var introduction by remember { mutableStateOf("") }
        var tag by remember { mutableStateOf("all") } // 默认选中 "全部"
        val options = listOf("all", "books", "electronics", "daily_supplies", "food", "else") // 选项列表
        var expanded by remember { mutableStateOf(false) } // 控制下拉菜单的显示状态
        val coroutineScope = rememberCoroutineScope()
        var commodityid by remember { mutableIntStateOf(0) }
        val scrollState = rememberScrollState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "发布商品",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 商品名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("商品名") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 商品价格
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("价格") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 商品介绍
            OutlinedTextField(
                value = introduction,
                onValueChange = { introduction = it },
                label = { Text("介绍") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 商品种类选择题
            Text(text = "标签", style = MaterialTheme.typography.bodyMedium)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = tag,
                    onValueChange = {},
                    label = { Text("选择标签") },
                    readOnly = true, // 只读，防止手动输入
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                tag = option // 更新选中值
                                expanded = false // 关闭菜单
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 图片展示区域
            Text(
                text = "上传图片",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    val bitmap = loadBitmapFromUri(selectedImageUri!!)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "选择图片",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text(
                        text = "未选择图片",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 按钮区域
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 上传图片按钮
                Button(
                    onClick = { pickImage() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("选择图片")
                }

                val context = LocalContext.current
                // 提交按钮
                Button(
                    onClick = {
                        coroutineScope.launch{
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    NetworkManager.authService.registercommodity(
                                        NetworkManager.RegisterCommodityRequest(
                                            name,
                                            price.toInt(),
                                            introduction,
                                            UserSession.getInstance().id,
                                            tag
                                        )
                                    )
                                }
                                if (response.isSuccessful) {
                                    val registerResponse = response.body()
                                    if (registerResponse != null) {
                                        commodityid = registerResponse.id
                                        Toast.makeText(context, "商品注册成功", Toast.LENGTH_SHORT).show()
                                        println("first step success")
                                        println(registerResponse.id)
                                    } else {
                                        println("back id error")
                                    }
                                } else {
                                    println("bug")
                                }
                            } catch (e: Exception) {
                                println("failed")
                            }

                            try {
                                val bitmap = loadBitmapFromUri(selectedImageUri!!)
                                // 使用后台线程执行耗时操作
                                val response = withContext(Dispatchers.IO) {
                                    if (bitmap != null) {
                                        uploadAvatarWithCommodityId(commodityid.toString(), bitmap,
                                            "imagecommodity$commodityid"
                                        )
                                    }
                                }

                            } catch (e: Exception) {
                                // 处理异常
                                e.printStackTrace()
                            }

                            // 跳转到 MainActivity 并显示 mainScreen
                            val intent = Intent(this@RegisterCommodityActivity, MainActivity::class.java).apply {
                                putExtra("SHOW_MAIN_SCREEN", true) // 传递标识
                            }
                            startActivity(intent)
                            finish() // 销毁当前活动
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("提交")
                }
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

