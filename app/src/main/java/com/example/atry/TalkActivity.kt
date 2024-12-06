package com.example.atry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.atry.api.NetworkManager

import com.example.atry.api.NetworkManager.Message
import com.example.atry.api.NetworkManager.authService
import com.example.atry.model.UserSession
import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class TalkActivity : ComponentActivity() {

    private var comId = ""
    private var isOrder = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 从 Intent 中获取发送方ID
        val senderId = intent.getStringExtra("senderId") ?: "-1"
        comId = intent.getStringExtra("commodityId") ?: ""
        println(comId)
        isOrder = comId != ""
        setContent {
            ChatScreen(senderId.toInt())
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
    @Composable
    fun ChatScreen(
        senderId: Int,
    ) {
        var messages = remember {
            mutableStateListOf(
                Message(
                    1, 100, "Alice", "Hey, how are you?\n" +
                            "if you this message,this means no messages"
                )
            )
        }
        // 模拟一些消息数据

        val id1 = UserSession.getInstance().id
        val request = NetworkManager.TalkRequest(id1 = id1, id2 = senderId)
        var time = ""
        // 使用LaunchedEffect确保网络请求只执行一次
        LaunchedEffect(senderId) {
            try {
                val response = withContext(Dispatchers.IO) {
                    authService.talk(request = request)
                }

                if (response.isSuccessful) {
                    response.body()?.let {
                        messages.clear()
                        messages.addAll(it.messages.reversed()) // 更新消息列表
                        time = it.date
                    }
                } else {
                    Log.e("TalkScreen", "Error fetching messages: ${response.code()}")
                }
                startPeriodicRequest(senderId, id1, messages, time)
            } catch (_: Exception) {
                // 错误处理
            }
        }
        var newMessage by remember { mutableStateOf("") }
        var avatar1 by remember { mutableStateOf("") } // 发送者头像
        var avatar2 by remember { mutableStateOf("") } // 接收者头像
        LaunchedEffect(senderId) {
            // 启动两个异步任务并等待结果
            val avatar1Url =
                async { authService.getAvatarTalk(senderId) }.await().body()?.avator // 获取发送者头像的URL
            val avatar2Url =
                async { authService.getAvatarTalk(id1) }.await().body()?.avator // 获取接收者头像的URL

            // 将获取到的 URL 更新到 avatar1 和 avatar2 变量
            avatar1 = avatar1Url ?: "" // 如果为空，则使用默认空字符串
            avatar2 = avatar2Url ?: "" // 如果为空，则使用默认空字符串
        }

        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize()) {
            // 背景图片
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.chat_background), // 背景图片资源
                    contentDescription = "Background Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 创建一个Column，用于将聊天消息列表和输入栏放到底部
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom // 让输入框始终在底部
                ) {
                    // 创建一个 LazyListState 来控制滚动
                    val listState = rememberLazyListState()

                    // 聊天消息列表
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // 让LazyColumn占据剩余空间，避免与输入框重叠
                            .padding(8.dp),
                        reverseLayout = false, // 设置列表从底部开始增长
                        state = listState // 关联 ListState
                    ) {
                        items(messages) { message ->
                            ChatBubble(
                                message = message, senderId = senderId, context = context,
                                avatar1 = avatar1, avatar2 = avatar2
                            )
                        }
                    }

                    // 每次消息列表发生变化时，自动滚动到最底部
                    LaunchedEffect(messages.size) {
                        // 确保在列表更新后，自动滚动到底部
                        listState.animateScrollToItem(messages.size - 1)
                    }

                    // 输入框和按钮区域
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween // 确保按钮分布均匀
                    ) {
                        // 输入框
                        TextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            modifier = Modifier.weight(1f), // 输入框占据剩余空间
                            placeholder = { Text("请发送消息...") },
                            maxLines = 1,
                            colors = TextFieldDefaults.colors(
                                focusedPrefixColor = Color.Blue,
                                unfocusedPrefixColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 如果 isOrderButtonVisible 为 true，显示 Order 按钮
                        if (isOrder) {
                            Button(
                                onClick = {
                                    sendOrderMessage(publisher = id1, acceptor = senderId, comId.toInt(), messages)
                                },
                                modifier = Modifier.widthIn(min = 80.dp), // 给按钮设置最小宽度
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Yellow,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("订购")
                            }
                            Spacer(modifier = Modifier.width(4.dp)) // 按钮间距
                        }

                        // 发送消息按钮
                        Button(
                            onClick = {
                                if (newMessage.isNotBlank()) {
                                    sendMessage(userId = id1, senderId, newMessage, messages)
                                    newMessage = "" // 清空输入框
                                }
                            },
                            modifier = Modifier.widthIn(min = 80.dp) // 给按钮设置最小宽度
                        ) {
                            Text("发送")
                        }
                    }
                }
            }
        }
    }


// 定义一个函数，每隔 1.5 秒向后端发送请求

    private fun startPeriodicRequest(
        id: Int, userId: Int, messages: MutableList<Message>,
        time: String
    ) {
        var formattedDateTime = time
        // 在后台线程启动一个协程
        lifecycleScope.launch {
            while (isActive) {  // isActive 确保协程在生命周期结束时取消
                try {
                    // 创建发送消息的请求
                    val request = NetworkManager.RefreshRequest(userId, id, formattedDateTime)

                    // 发送请求并获取响应
                    val response = authService.refreshTalk(request)

                    // 判断响应是否成功
                    if (response.isSuccessful) {
                        // 如果成功，处理响应数据
                        response.body()?.let {
                            messages.addAll(it.messages) // 更新消息列表
                            formattedDateTime = it.date
                        }
                    } else {
                        // 处理错误情况
                        Log.e("TalkScreen", "Error refresh message: ${response.code()}")
                    }
                } catch (e: Exception) {
                    // 捕获并处理异常
                    Log.e("TalkScreen", "Exception: ${e.message}")
                }

                // 每隔 2 秒执行一次
                delay(2000L)
            }
        }
    }


    private fun sendMessage(userId: Int, id: Int, newMessage: String,
                            messages: MutableList<Message>){

        // 使用 runBlocking 来同步等待协程完成
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 创建发送消息的请求
                val request = NetworkManager.SendMessRequest(id, userId, newMessage)

                // 发送请求并获取响应，使用 suspend 函数
                val response = authService.sendMessage(request)

                // 判断响应是否成功
                if (response.isSuccessful) {
                    // 如果成功，处理响应数据
                    response.body()?.let {
                        println(it.message)
                        messages.add(it.message)
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun ChatBubble(
    message: Message, senderId: Int, context: Context,
    avatar1: String,
    avatar2: String
) {
    if (isOrderMessage(message.content)) {
        RenderOrderMessage(
            message = message, senderId = senderId, context = context,
            avatar1 = avatar1, avatar2 = avatar2
        )
    } else
        RenderTextMessage(message, senderId, avatar1, avatar2)
}

@Composable
fun RenderTextMessage(
    message: Message, senderId: Int, avatar1:
    String, avatar2: String
) {
    val isNotSentByUser = message.senderId != senderId
    val bubbleColor =
        if (isNotSentByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isNotSentByUser) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isNotSentByUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isNotSentByUser) {
            // 左侧显示头像和名字
            Column(
                modifier = Modifier.padding(end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = rememberAsyncImagePainter(avatar1),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 显示聊天气泡
        Column(
            horizontalAlignment = if (isNotSentByUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                    .padding(12.dp),
                color = textColor
            )
        }

        if (isNotSentByUser) {
            // 右侧显示头像和名字
            Column(
                modifier = Modifier.padding(start = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = rememberAsyncImagePainter(avatar2),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun RenderOrderMessage(
    message: Message, senderId: Int, context: Context, avatar1: String,
    avatar2: String
) {
    val isNotSentByUser = message.senderId != senderId
    val bubbleColor = if (isNotSentByUser) MaterialTheme.colorScheme.primary else Color(0xFFFFE082)
    val textColor = if (isNotSentByUser) Color.White else MaterialTheme.colorScheme.onSurface
    val commodity = fetchComId(message.content)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isNotSentByUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isNotSentByUser) {

            Column(
                modifier = Modifier.padding(end = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = rememberAsyncImagePainter(avatar1),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }


        Column(
            horizontalAlignment = if (isNotSentByUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = "我发起了一个订单",
                modifier = Modifier
                    .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                    .padding(12.dp),
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp)) // 用于文本和按钮之间的间距

            if(!isNotSentByUser) {
                Button(
                    onClick = {
                        val intent = Intent(context, OrderActivity::class.java).apply {
                            putExtra("commodity", commodity.toString())
                            putExtra("buyId", senderId.toString())
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp) // 设置按钮的外边距
                ) {
                    Text(text = "查看订单")
                }
            }
        }

        if (isNotSentByUser) {
            // 右侧显示头像和名字
            Column(
                modifier = Modifier.padding(start = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = rememberAsyncImagePainter(avatar2),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

}


}


fun isOrderMessage(content: String): Boolean {
    // 检查字符串的前两个字符
    return content.startsWith("\u00FF\u00FF") // 0xFF 是十六进制的 255
}

//必须在校验后获取
fun fetchComId(content: String): String? {
    val string = content.substring(2)
    val orderMessage = decrypt(string, UserSession.encryptionKey)
    val regex = Regex("""\d+""") // 匹配一个或多个数字
    val matchResult = regex.find(orderMessage)

    val number = matchResult?.value // 提取匹配到的数字部分
    return number
}

fun decrypt(encryptedData: String, key: String): String {
    val secretKey = SecretKeySpec(key.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
    val decryptedData = cipher.doFinal(decodedData)
    return String(decryptedData)
}

fun encrypt(data: String, key: String): String {
    val secretKey = SecretKeySpec(key.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedData = cipher.doFinal(data.toByteArray())
    return Base64.encodeToString(encryptedData, Base64.DEFAULT)
}


fun sendOrderMessage(publisher: Int, acceptor: Int, commodityId: Int, messages: MutableList<Message>) {
    val content = generateOrderMess(commodityId)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 创建发送消息的请求
            val request =
                NetworkManager.SendMessRequest(acceptor, publisher, content)
            // 发送请求并获取响应
            val response = authService.sendMessage(request)
            // 判断响应是否成功
            if (response.isSuccessful) {
                response.body()?.let {
                    println(it.message)
                    messages.add(it.message)
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

fun generateOrderMess(id: Int): String {
    val init = "order_$id"
    return "\u00ff\u00ff" + encrypt(init, UserSession.encryptionKey)
}





