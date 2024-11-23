package com.example.atry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

// 商品类的定义
data class Product(val name: String, val price: String, val imageUrl: String)

class GoodsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodsScreen()
        }
    }

    @Composable
    fun GoodsScreen() {
        // 示例商品数据
        val productList = remember {
            listOf(
                Product("Laptop", "$999", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Headphones", "$199", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Smartphone", "$799", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Watch", "$299", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Watch", "$299", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Watch", "$299", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Watch", "$299", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg"),
                Product("Watch", "$299", "https://pic3.zhimg.com/v2-5fb13110e1de13d4c11e6e7f5b8026da_r.jpg")
            )
        }

        // 商品列表显示
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(productList.size) { index ->
                ProductCard(product = productList[index])
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @Composable
    fun ProductCard(product: Product) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 商品图片（使用 Coil 加载）
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true) // 渐变效果
//                        .placeholder(R.drawable.placeholder) // 替换为你的占位图
//                        .error(R.drawable.error) // 替换为你的错误图
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp),
                    contentScale = ContentScale.Crop
                )

                // 商品信息
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
