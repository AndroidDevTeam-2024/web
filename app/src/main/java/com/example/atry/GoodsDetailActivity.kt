package com.example.atry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atry.api.NetworkManager

class GoodsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取传递过来的商品信息
        val id = intent.getIntExtra("id", 0)

        setContent {
                GoodsDetailScreen(id = id)
        }
    }
}

@Composable
fun GoodsDetailScreen(id: Int) {
    // 商品详情状态
    val productDetail = remember { mutableStateOf<NetworkManager.ProductDetail?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // 使用 LaunchedEffect 调用 API 加载商品详情
    LaunchedEffect(id) {
        try {
            val response = NetworkManager.apiService.detailGoods(id)
            if (response.isSuccessful) {
                productDetail.value = response.body()
            } else {
                errorMessage.value = "Failed to load product details: ${response.errorBody()?.string()}"
            }
        } catch (e: Exception) {
            errorMessage.value = "Network error: ${e.message}"
        } finally {
            isLoading.value = false
        }
    }

    val businessId = productDetail.value?.business_id
    var email by remember { mutableStateOf("") }
    LaunchedEffect(businessId) {
        try {
            val response = businessId?.let { NetworkManager.authService.getuser(it.toInt()) }
            if (response != null) {
                if (response.isSuccessful) {
                    email = response.body()?.email.toString()
                } else {
                    errorMessage.value = "Failed to get email: ${response.errorBody()?.string()}"
                }
            }
        } catch (e: Exception) {
            errorMessage.value = "Network error: ${e.message}"
        } finally {
            isLoading.value = false
        }
    }

    // UI 显示逻辑
    if (isLoading.value) {
        LoadingIndicator()
    } else if (errorMessage.value != null) {
        ErrorText(errorMessage = errorMessage.value!!)
    } else {
        productDetail.value?.let {
            ProductDetailContent(productDetail = it, email = email)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorText(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProductDetailContent(productDetail: NetworkManager.ProductDetail, email: String) {
    println(email)
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 商品图片
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            AsyncImage(
                model = productDetail.homepage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 商品名称
        Text(
            text = productDetail.name,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 商品价格
        Text(
            text = "¥ ${productDetail.price}",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        // 商品描述标题
        Text(
            text = "商品详情",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 商品描述内容
        Text(
            text = productDetail.introduction,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        // 联系方式标题
        Text(
            text = "联系方式",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 联系邮箱
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "邮箱图标",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {  val intent = Intent(context, TalkActivity::class.java).apply {
                putExtra("senderId", productDetail.business_id)
            }
                context.startActivity(intent)
                      },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "联系卖家", style = MaterialTheme.typography.titleMedium)
        }
    }
}
