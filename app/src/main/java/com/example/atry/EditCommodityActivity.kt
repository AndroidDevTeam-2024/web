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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.atry.api.NetworkManager
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.atry.iomanager.AvatarManager.uploadAvatarWithCommodityId
import kotlinx.coroutines.CoroutineScope


class EditCommodityActivity : ComponentActivity() {

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


        val commodity: AccountActivity.Commodity? =
            intent.getSerializableExtra("commodity") as? AccountActivity.Commodity

        commodity?.let {
            setContent {
                EditScreen(it)
            }
        }
    }

    @Composable
    fun EditScreen(commodity: AccountActivity.Commodity) {
        var name by remember { mutableStateOf(commodity.name) }
        var price by remember { mutableStateOf(commodity.price.toString()) }
        var introduction by remember { mutableStateOf(commodity.introduction) }
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
            Text(text = "Edit Commodity", style = MaterialTheme.typography.headlineSmall)

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Price Field
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Introduction Field
            OutlinedTextField(
                value = introduction,
                onValueChange = { introduction = it },
                label = { Text("Introduction") },
                modifier = Modifier.fillMaxWidth()
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
                        text = "click here to change image if necessary",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }


            val context = LocalContext.current // 获取当前的上下文

            // Save Button
            Button(
                onClick = {
                    // 保存数据逻辑
                    coroutineScope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                NetworkManager.authService.editcommodity(
                                    commodity.id,
                                    NetworkManager.EditCommodityRequest(
                                        name,
                                        price.toInt(),
                                        introduction
                                    )
                                )
                            }
                            if (response.isSuccessful) {
                                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show()
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
                                        uploadAvatarWithCommodityId(commodity.id.toString(), bitmap, "imagecommodity${commodity.id}")
                                    }
                                }

                            } catch (e: Exception) {
                                // 处理异常
                                e.printStackTrace()
                            }
                        }
                    }
                    finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
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