package com.example.atry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.atry.api.NetworkManager
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun MainScreen() {
        val coroutineScope = rememberCoroutineScope()
        val commoditySample = remember { mutableStateListOf<Commodity>() }
        val dealSample = remember { mutableStateListOf<Deal>() }




        //获取自己所有商品
        coroutineScope.launch {
            try {
                // 第一次请求，获取商品列表
                val response = withContext(Dispatchers.IO) {
                    NetworkManager.authService.loadmycommodity(UserSession.getInstance().id)
                }

                if (response.isSuccessful) {
                    val loadResponse = response.body()
                    if (loadResponse != null) {
                        println("获取商品列表成功")

                        // 使用 `for` 遍历商品 ID
                        for (item in loadResponse.commodities) {
                            // 发起新的请求来获取每个商品的详细信息
                            if (commoditySample.none { it.id == item.id }) {
                                commoditySample.add(
                                    Commodity(
                                        id = item.id,
                                        name = item.name,
                                        price = item.price,
                                        introduction = item.introduction,
                                        homepage = item.homepage,
                                        business_id = item.business_id,
                                        exist = item.exist
                                    )
                                )
                            }
                        }
                    } else {
                        println("商品列表返回为空")
                    }
                } else {
                    println("获取商品列表失败: ${response.message()}")
                }
            } catch (e: Exception) {
                println("请求失败: ${e.message}")
            }
        }


        //获取自己所有交易
        coroutineScope.launch {
            try {
                // 第一次请求，获取商品列表
                val response = withContext(Dispatchers.IO) {
                    NetworkManager.authService.loadmydeal(UserSession.getInstance().id)
                }

                if (response.isSuccessful) {
                    val loadResponse = response.body()
                    if (loadResponse != null) {
                        println("获取交易列表成功")

                        // 使用 `for` 遍历商品 ID
                        for (item in loadResponse.deals) {
                            // 发起新的请求来获取每个商品的详细信息
                            if (dealSample.none { it.id == item.id }) {
                                dealSample.add(
                                    Deal(
                                        id = item.id,
                                        seller = item.seller,
                                        customer = item.customer,
                                        commodity = item.commodity,
                                        date = convertToLocalDateTime(item.date),
                                        comment = item.comment
                                    )
                                )
                            }
                        }
                    } else {
                        println("商品列表返回为空")
                    }
                } else {
                    println("获取商品列表失败: ${response.message()}")
                }
            } catch (e: Exception) {
                println("请求失败: ${e.message}")
            }
        }

//        if (dealSample.none { it.id == 1 }) {
//            dealSample.add(
//                Deal(
//                    id = 1,
//                    seller = 1,
//                    customer = 2,
//                    commodity = 21,
//                    date = "2024-11-01",
//                    comment = "ok"
//                )
//            )
//        }
//        if (dealSample.none { it.id == 2 }) {
//            dealSample.add(
//                Deal(
//                    id = 2,
//                    seller = 2,
//                    customer = 1,
//                    commodity = 25,
//                    date = "2024-11-02",
//                    comment = "okok"
//                )
//            )
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
        ) {
            // 传入 navigateToSettings 参数，定义按钮点击后的行为
            HeaderSection(dealSample)
            Spacer(modifier = Modifier.height(20.dp))
            MySection(commoditySample, dealSample)
        }
    }

    @Composable
    fun HeaderSection(dealSample: List<Deal>) {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                    .padding(8.dp) // 调整顶部和右侧的间距
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_preferences), // 使用默认图标
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp), // 使顶部内容稍微下移，避免与设置按钮重叠
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：头像
                ProfileImage()

                Spacer(modifier = Modifier.width(16.dp)) // 增加间距使布局更宽松

                // 右侧：用户信息与统计数据
                Column(
                    modifier = Modifier.weight(1f), // 使用 weight 分配宽度
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    // 用户名
                    Text(
                        text = UserSession.getInstance().username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp)) // 增加更合适的间距

                    // 邮箱
                    Text(
                        text = UserSession.getInstance().email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // 更大的间距让布局更清晰

                    // 交易单数与更新按钮
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatItem(label = "交易单数", value = dealSample.size.toString())
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
        // 获取头像的 URL
        val avatarUrl = UserSession.getInstance().avatar

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            // 使用 rememberImagePainter 来加载网络图片
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)  // 网络图片 URL
                    .build(),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // 让图片裁剪为圆形
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
    fun MySection(commoditySample: List<Commodity>, dealSample: List<Deal>) {
        // 记录当前选中的标签页
        var selectedTabIndex by remember { mutableStateOf(0) }

        // 商品和订单的示例数据
        val commodityData = commoditySample
        val orderData = dealSample // 假设订单数据已存在

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // TabRow 用于切换选项
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("我的商品", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("我的订单", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 根据选中的标签页动态显示内容
            if (selectedTabIndex == 0) {
                MyCommodity(commodityData)
            } else {
                MyOrders(orderData)
            }
        }
    }



    @Composable
    fun MyCommodity(commodities: List<Commodity>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                items(commodities) { commodity ->
                    ItemCard(commodity)
                }
            }
        }
    }



    @Composable
    fun MyOrders(orders: List<Deal>) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                items(orders) { order ->
                    DealCard(order)
                }
            }
        }
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun DealCard(deal: Deal) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val currentUserId = UserSession.getInstance().id
        var commodityname by remember { mutableStateOf("") }
        var commodityurl by remember { mutableStateOf("") }


        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkManager.authService.getcommodity(deal.commodity)
                }

                if (response.isSuccessful) {
                    val getResponse = response.body()
                    if (getResponse != null) {
                        commodityname = getResponse.name;
                        commodityurl = getResponse.homepage
                    } else {
                        println("返回信息为空")
                    }
                } else {
                    println("返回错误")
                }
            } catch (e: Exception) {
                println("发送失败")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { navigateToDealDetail(deal, context) },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "订单编号: ${deal.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Display either the seller or buyer details based on the user's role
                if (currentUserId == deal.seller) {
                    // User is the seller
                    Text(
                        text = "你是卖家",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Green
                    )
                } else if (currentUserId == deal.customer) {
                    // User is the buyer
                    Text(
                        text = "你是买家",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Blue
                    )
                } else {
                    // The user is neither the buyer nor the seller (e.g., for admins or other roles)
                    Text(
                        text = "卖家 ID: ${deal.seller}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "买家 ID: ${deal.customer}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "商品: $commodityname",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Display the image if available
                Image(
                    painter = rememberImagePainter(commodityurl),
                    contentDescription = "Commodity Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "交易日期: ${deal.date}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "交易评论: ${deal.comment}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(commodity.homepage)  // 网络图片 URL
                        .build(),
                    contentDescription = "Item Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // 让图片裁剪为圆形
                )
//                Image(
//                    painter = painterResource(id = R.drawable.profile_test), // 替换为实际资源
//                    contentDescription = "Item Image",
//                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop
//                )
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

    private fun navigateToDealDetail(deal: Deal, context: Context) {
        val intent = Intent(context, DealDetailActivity::class.java)
        intent.putExtra("deal", deal)  // 将 commodity 对象传递给 Intent
        context.startActivity(intent)
    }






    object SampleData {
        val commoditySample = mutableListOf(
            Commodity(
                id = 1,
                name = "Apple iPhone 15 Pro Max",
                price = 9999,
                introduction = "The ultimate smartphone with cutting-edge technology.",
                homepage = "https://www.apple.com/iphone-15-pro",
                business_id = UserSession.getInstance().id,
                exist = true
            )
        )
        // 示例交易数据
        val dealSample = mutableListOf(
            Deal(
                id = 1,
                seller = 101,
                customer = 201,
                commodity = 301,
                date = "2024-11-01",
                comment = "商品质量不错，满意！"
            )
        )
    }

    data class Commodity(val id: Int, val name: String, val price: Int, val introduction: String, val business_id: Int, val homepage: String, val exist: Boolean) : Serializable

    data class Deal(val id: Int, val seller: Int, val customer: Int, val commodity: Int, var date: String, var comment: String) : Serializable

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocalDateTime(utcDate: String): String {
        return try {
            // 定义解析 UTC 时间的 DateTimeFormatter
            val utcFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))

            // 使用 Instant 解析 UTC 时间字符串
            val instant = Instant.from(utcFormatter.parse(utcDate))

            // 格式化为本地时间，使用系统默认时区
            val localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())

            // 转换并返回本地时间
            localFormatter.format(instant)
        } catch (e: Exception) {
            // 错误处理，若格式解析失败，则返回原字符串
            utcDate
        }
    }

}
