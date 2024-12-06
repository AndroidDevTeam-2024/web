package com.example.atry

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import coil.compose.rememberImagePainter
import com.example.atry.api.NetworkManager
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DealDetailActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commodity: AccountActivity.Deal? = intent.getSerializableExtra("deal") as? AccountActivity.Deal
        // 使用 commodity 对象进行操作
        commodity?.let {
            // Do something with the commodity
            setContent {
                DealDetailScreen(it)
            }
        }
    }

    //data class Deal(val id: Int, val seller: Int, val customer: Int, val commodity: Int, var date: String, var comment: String) : Serializable

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DealDetailScreen(deal: AccountActivity.Deal) {
        val coroutineScope = rememberCoroutineScope()

        // State variables to hold the fetched data
        var commodityName by remember { mutableStateOf("") }
        var commodityUrl by remember { mutableStateOf("") }
        var anotherName by remember { mutableStateOf("") }
        var anotherUrl by remember { mutableStateOf("") }

        // State for the input field
        var commentInput by remember { mutableStateOf("") }

        //date
        val currentDate = LocalDate.now()

        // 定义日期格式
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 格式化当前日期
        val newcommentdate = currentDate.format(formatter)

        // Fetch commodity details
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkManager.authService.getcommodity(deal.commodity)
                }

                if (response.isSuccessful) {
                    val getResponse = response.body()
                    getResponse?.let {
                        commodityName = it.name
                        commodityUrl = it.homepage
                    }
                } else {
                    println("Error fetching commodity data")
                }
            } catch (e: Exception) {
                println("Failed to fetch commodity data")
            }

            // Determine the other party (buyer or seller)
            val anotherId = if (UserSession.getInstance().id == deal.seller) {
                deal.customer
            } else {
                deal.seller
            }

            // Fetch user details
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkManager.authService.getuser(anotherId)
                }

                if (response.isSuccessful) {
                    val getResponse = response.body()
                    getResponse?.let {
                        anotherName = it.name
                        anotherUrl = it.avator
                    }
                } else {
                    println("Error fetching user data")
                }
            } catch (e: Exception) {
                println("Failed to fetch user data")
            }
        }

        val scrollState = rememberScrollState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("订单详情") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6200EA))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(scrollState),  // 使 Column 支持垂直滚动
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title and ID details
                Text(
                    text = "订单编号: ${deal.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                // Display commodity details
                Spacer(modifier = Modifier.height(16.dp))
                Text("商品详情", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display product image
                    if (commodityUrl.isNotEmpty()) {
                        Image(
                            painter = rememberImagePainter(commodityUrl),
                            contentDescription = "Commodity Image",
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = commodityName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                // Display buyer/seller details
                Spacer(modifier = Modifier.height(16.dp))
                Text("对方", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (anotherUrl.isNotEmpty()) {
                        Image(
                            painter = rememberImagePainter(anotherUrl),
                            contentDescription = "Another User Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Gray, shape = MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "姓名: $anotherName",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                // Display deal details
                Spacer(modifier = Modifier.height(16.dp))
                Text("交易详情", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                DetailText("交易日期: ${deal.date}")
                DetailText("评论: ${deal.comment}")

                Spacer(modifier = Modifier.height(16.dp))

                // Add an input field for the user to add a new comment
                Text("添加评论", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    label = { Text("请输入评论...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.White)
                )


                val context = LocalContext.current
                // Button to send the comment
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Send the comment to the backend
                        coroutineScope.launch {
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    NetworkManager.authService.addcomment(
                                        NetworkManager.AddCommentRequest(
                                            deal.id,
                                            commentInput,
                                            newcommentdate
                                        )
                                    )
                                }

                                if (response.isSuccessful) {
                                    // Handle successful response
                                    println("Comment sent successfully")
                                    Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show()
                                    deal.comment = commentInput
                                } else {
                                    println("Failed to send comment")
                                }
                            } catch (e: Exception) {
                                println("Error sending comment: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("发送评论")
                }
            }
        }
    }

    @Composable
    fun DetailText(text: String) {
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp, color = Color.DarkGray)
        )
    }


}