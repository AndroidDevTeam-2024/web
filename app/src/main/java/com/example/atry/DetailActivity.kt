package com.example.atry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.atry.AccountActivity.Commodity
import com.example.atry.R // 引入资源包（替换为你的资源包）
import com.example.atry.api.NetworkManager
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commodity: AccountActivity.Commodity? = intent.getSerializableExtra("commodity") as? AccountActivity.Commodity
        // 使用 commodity 对象进行操作
        commodity?.let {
            // Do something with the commodity
            println(it.name)
            println(it.price)
            println(it.introduction)
            setContent {
                DetailScreen(it)
            }
        }
    }

    @Composable
    fun DetailScreen(commodity: AccountActivity.Commodity) {
        val coroutineScope = rememberCoroutineScope()
        // 使用 Column 布局展示商品详情
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
        ) {
            // 商品图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(commodity.homepage)  // 网络图片 URL
                    .build(),
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp)) // 更柔和的圆角
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 商品名称
            Text(
                text = commodity.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 商品价格
            Text(
                text = "￥${commodity.price}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE54B46), // 红色
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 商品描述框，背景颜色更柔和
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(8.dp)) // 圆角
                    .background(Color(0xFFF5F5F5)) // 更柔和的背景色
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)) // 边框更细
            ) {
                Text(
                    text = commodity.introduction,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(16.dp), // 内部 padding
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Spacer to push buttons to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // 添加购物车和立即购买按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Ensure there's padding at the bottom
            ) {
                // 将该商品下架按钮
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // 第一次请求，获取商品列表
                                val response = withContext(Dispatchers.IO) {
                                    NetworkManager.authService.deletemycommodity(commodity.id)
                                }

                                if (response.isSuccessful) {
                                    println("删除商品成功")
                                } else {
                                    println("删除商品失败: ${response.message()}")
                                }
                            } catch (e: Exception) {
                                println("请求失败: ${e.message}")
                            }
                        }
                        finish()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, Color(0xFFE54B46), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Delete", color = Color(0xFFE54B46))
                }

                // 立即购买按钮
                Button(
                    onClick = { Edit(commodity) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, Color(0xFF0A74DA), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Edit", color = Color(0xFF0A74DA))
                }
            }
        }
    }

    private fun Edit(commodity: AccountActivity.Commodity) {
        val intent = Intent(this, EditCommodityActivity::class.java)
        intent.putExtra("commodity", commodity)  // 将 commodity 对象传递给 Intent
        this.startActivity(intent)
        finish()
    }


    // 提示消息
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
