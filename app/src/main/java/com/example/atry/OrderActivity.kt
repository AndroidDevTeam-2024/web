package com.example.atry

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.atry.api.NetworkManager
import com.example.atry.api.NetworkManager.authService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderActivity : ComponentActivity() {
    private var comId = 1
    private var buyerId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this
        // 获取通过 Intent 传递的数据
        val idString = intent.getStringExtra("commodity") ?: ""
        if (idString != "") {
            comId = idString.toInt()
        }
        val buy = intent.getStringExtra("buyId") ?: ""
        if (buy != "") {
            buyerId = buy.toInt()
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = authService.getcommodity(comId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        val commodity = Commodity(it.id,
                            it.name,it.price,it.introduction,it.business_id,
                            it.homepage)
                        setContent {
                            MaterialTheme {
                                OrderScreen(
                                    commodity = commodity, buyerId = buyerId
                                )
                            }
                        }
                    }
                } else {
                    // 处理错误情况
                    Log.e("TalkScreen", "Error send messages: ${response.code()}")
                    context.finish()
                }
            } catch (e: Exception) {
                // 捕获并处理异常
                Log.e("TalkScreen", "Exception: ${e.message}")
                context.finish()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    commodity: Commodity,
    buyerId : Int
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Order Details") },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(commodity.homepage), // 使用商品的主页 URL 或本地资源
                    contentDescription = "商品图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // 设置图片高度
                        .clip(RoundedCornerShape(8.dp)) // 圆角效果
                        .padding(bottom = 16.dp), // 图片与下方内容的间距
                    contentScale = ContentScale.Crop // 确保图片裁剪填充满指定区域
                )

                // Order Details Content
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = commodity.introduction,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // 增加间隔，避免太拥挤

                Text(
                    text = "价格: ¥${commodity.price}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                )
                // Confirm Order Button
                Spacer(modifier = Modifier.weight(1f)) // Adds space between content and button
                Button(
                    onClick = {
                        confirm(commodity.business_id, buyerId, commodity.id)
                        (context as? Activity)?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Confirm Order")
                }


                Button(
                    onClick = {
                        reject(commodity.business_id, buyerId, commodity.id)
                        (context as? Activity)?.finish()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Reject Order")
                }
            }
        }
    )
}

fun confirm(business_id:Int
, buyerId: Int, commodityId : Int) {


    CoroutineScope(Dispatchers.IO).launch {
        try {

            var orderId = -1
            val request1 = NetworkManager.dealRequest(business_id, buyerId, commodityId)
            val response1 = authService.createDeal(request1)
            if (response1.isSuccessful) {
                response1.body()?.let {
                    orderId = it.id
                }
            } else {
                // 处理错误情况
                Log.e("OrderScreen", "Error create deal: ${response1.code()}")
            }
            // 创建发送消息的请求
            val request2 = NetworkManager.SendMessRequest(buyerId, business_id, "我接受了此订单\n:商品ID{$commodityId}\n" +
                    ":订单ID{$orderId}")
            // 发送请求并获取响应
            val response2 = authService.sendMessage(request2)
            // 判断响应是否成功
            if (response2.isSuccessful) {
                response2.body()?.let {

                }
            } else {
                // 处理错误情况
                Log.e("OrderScreen", "Error create deal: ${response2.code()}")
            }
        } catch (e: Exception) {
            // 捕获并处理异常
            Log.e("OrderScreen", "Exception: ${e.message}")
        }
    }
}


fun reject(business_id:Int
           , buyerId: Int, commodityId : Int) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 创建发送消息的请求
            val request =
                NetworkManager.SendMessRequest(buyerId, business_id, "我拒绝了此订单: 商品ID{$commodityId}")
            // 发送请求并获取响应
            val response = authService.sendMessage(request)
            // 判断响应是否成功
            if (response.isSuccessful) {
                response.body()?.let {

                }
            } else {
                // 处理错误情况
                Log.e("TalkScreen", "Error send messages: ${response.code()}")
            }
        } catch (e: Exception) {
            // 捕获并处理异常
            Log.e("TalkScreen", "Exception: ${e.message}")
        }
    }

}

data class Commodity(
    val id: Int,
    val name: String,
    val price: Int,
    val introduction: String,
    val business_id: Int,
    val homepage: String

)