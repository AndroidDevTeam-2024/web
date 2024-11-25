package com.example.atry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
        ) {
            // 传入 navigateToSettings 参数，定义按钮点击后的行为
            HeaderSection()
            Spacer(modifier = Modifier.height(20.dp))
            MyCommodity()
        }
    }









    @Composable
    fun HeaderSection() { // 接收一个跳转的 lambda
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2C2C2C), Color(0xFF262626)), // 渐变从稍微浅的灰到深灰
                        startY = 0f,
                        endY = 300f
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 右上角：设置按钮
            IconButton(
                onClick = { navigateToSettings(context) },
                modifier = Modifier
                    .align(Alignment.TopEnd) // 按钮靠右上角对齐
                    .padding(top = 8.dp, end = 8.dp) // 调整顶部和右侧的间距，按钮上移
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_preferences), // 使用默认图标
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：头像
                ProfileImage()

                Spacer(modifier = Modifier.width(12.dp))

                // 右侧：用户信息与统计数据
                Column(
                    modifier = Modifier.weight(1f), // 使用 weight 分配宽度
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "name",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "123@qq.com",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatItem(label = "关注", value = "83")
                        StatItem(label = "粉丝", value = "4")
                        StatItem(label = "售出单数", value = "0")
                    }
                }

            }
        }
    }


    private fun navigateToSettings(context: Context) {

        val intent = Intent(context, EditProfileActivity::class.java)
        context.startActivity(intent)
    }


    @Composable
    fun ProfileImage() {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            // 可替换成实际图片资源
            Image(
                painter = painterResource(id = R.drawable.profile_test),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }

    @Composable
    fun StatItem(label: String, value: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }




    @Composable
    fun MyCommodity() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "我的商品",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                items(SampleData.commoditySample) { commodity ->
                    ItemCard(commodity)
                }
            }
        }
    }

    @Composable
    fun ItemCard(commodity: Commodity) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
                .clickable { navigateToDetail(commodity, context) },
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_test), // 替换为实际资源
                    contentDescription = "Item Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = commodity.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Price: $${commodity.price}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = commodity.introduction,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )
            }
        }
    }

    private fun navigateToDetail(commodity: Commodity, context: Context) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("commodity", commodity)  // 将 commodity 对象传递给 Intent
        context.startActivity(intent)
    }







    object SampleData {
        val commoditySample = listOf(
            Commodity(
                id = 1,
                name = "Apple iPhone 15 Pro Max",
                price = 9999,
                introduction = "The ultimate smartphone with cutting-edge technology.",
                homepage = "https://www.apple.com/iphone-15-pro",
                exist = true
            ),
            Commodity(
                id = 2,
                name = "Samsung Galaxy S23 Ultra",
                price = 7999,
                introduction = "Experience the power of innovation with the Galaxy series.",
                homepage = "https://www.samsung.com/galaxy-s23-ultra",
                exist = true
            ),
            Commodity(
                id = 3,
                name = "Sony WH-1000XM5",
                price = 2999,
                introduction = "Premium noise-cancelling headphones for audiophiles.",
                homepage = "https://www.sony.com/wh-1000xm5",
                exist = true
            ),
            Commodity(
                id = 4,
                name = "Dell XPS 15 Laptop",
                price = 12999,
                introduction = "High-performance laptop with stunning 4K display.",
                homepage = "https://www.dell.com/xps-15",
                exist = true
            ),
            Commodity(
                id = 5,
                name = "Nintendo Switch OLED Model",
                price = 2199,
                introduction = "Play anytime, anywhere with the vibrant OLED display.",
                homepage = "https://www.nintendo.com/switch-oled",
                exist = true
            ),
            Commodity(
                id = 6,
                name = "Canon EOS R6 Mark II",
                price = 14999,
                introduction = "Professional-grade mirrorless camera for stunning photography.",
                homepage = "https://www.canon.com/eos-r6-mk2",
                exist = true
            ),
            Commodity(
                id = 7,
                name = "Dyson V15 Detect Vacuum",
                price = 4599,
                introduction = "Effortless cleaning with advanced laser detection technology.",
                homepage = "https://www.dyson.com/v15-detect",
                exist = true
            ),
            Commodity(
                id = 8,
                name = "Bose SoundLink Revolve+ II",
                price = 2199,
                introduction = "Portable Bluetooth speaker with immersive 360° sound.",
                homepage = "https://www.bose.com/soundlink-revolve-plus-2",
                exist = true
            ),
            Commodity(
                id = 9,
                name = "Garmin Fenix 7X Sapphire Solar",
                price = 8999,
                introduction = "Premium smartwatch with solar charging for outdoor adventurers.",
                homepage = "https://www.garmin.com/fenix-7x",
                exist = true
            ),
            Commodity(
                id = 10,
                name = "Herman Miller Aeron Chair",
                price = 12999,
                introduction = "Ergonomic office chair designed for maximum comfort.",
                homepage = "https://www.hermanmiller.com/aeron",
                exist = true
            )
        )
    }

    data class Commodity(val id: Int, val name: String, val price: Int, val introduction: String, val homepage: String, val exist: Boolean) : Serializable

}
