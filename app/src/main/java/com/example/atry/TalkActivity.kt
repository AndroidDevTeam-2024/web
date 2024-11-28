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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class TalkActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 从 Intent 中获取发送方ID
        val senderId = intent.getStringExtra("senderId") ?: "-1"
        setContent {
            ChatScreen(senderId.toInt())
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ChatScreen(
        senderId: Int,
    ) {
        val coroutineScope = rememberCoroutineScope()
        // 模拟一些消息数据
        var messages by remember {
            mutableStateOf(
                listOf(
                    Message(1, 100, "Alice", "Hey, how are you?\n" +
                            "if you this message,this means no messages"),

                    )
            )
        }


        val id1 = UserSession.getInstance().id
        val request = NetworkManager.TalkRequest(id1 = id1, id2 = senderId)
        var time = ""
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authService.talk(request = request)
                }

                if (response.isSuccessful) {
                    response.body()?.let {
                        messages = it.messages // 更新消息列表
                        time = it.date
                    }
                } else {
                    Log.e("TalkScreen", "Error fetching messages: ${response.code()}")
                }
            } catch (_: Exception) {

            }
        }
        var newMessage by remember { mutableStateOf("") }
        var avatar1 by remember { mutableStateOf("") } // 发送者头像
        var avatar2 by remember { mutableStateOf("") } // 接收者头像
        runBlocking {
            // 启动两个异步任务并等待结果
            val avatar1Url = async { authService.getAvatarTalk(senderId) }.await().body()?.avator // 获取发送者头像的URL
            val avatar2Url = async { authService.getAvatarTalk(id1) }.await().body()?.avator // 获取接收者头像的URL

            // 将获取到的 URL 更新到 avatar1 和 avatar2 变量
            avatar1 = avatar1Url ?: "" // 如果为空，则使用默认空字符串
            avatar2 = avatar2Url ?: "" // 如果为空，则使用默认空字符串
        }

        startPeriodicRequest(senderId, id1, messages, time)
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
                    modifier = Modifier.fillMaxWidth().height(700.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // 聊天消息列表
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp),
                        reverseLayout = true // 最新消息在底部
                    ) {
                        items(messages) { message ->
                            ChatBubble(message = message, senderId = senderId, context = context,
                                avatar1 = avatar1, avatar2 = avatar2)
                        }
                    }
                }
                // 输入栏
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),

                    ) {
                    TextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        maxLines = 1,
                        colors = TextFieldDefaults.colors(
                            focusedPrefixColor = Color.Blue, // 聚焦时的前缀颜色
                            unfocusedPrefixColor = Color.Gray // 未聚焦时的前缀颜色
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (newMessage.isNotBlank()) {
                            val mess =
                                sendMessage(userId = id1, senderId, newMessage)
                            messages + mess
                            newMessage = "" // 清空输入框
                        }
                    }) {
                        Text("Send")
                    }
                }
                }
            }
        }
    }


// 定义一个函数，每隔 1.5 秒向后端发送请求

    private fun startPeriodicRequest(id: Int, userId: Int, messages: List<Message>,
                                     time: String) {
        var formattedDateTime = time
        // 在后台线程启动一个协程
        lifecycleScope.launch(Dispatchers.IO) {
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
                            messages + it.messages // 更新消息列表
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



    private fun sendMessage(
        userId: Int, id: Int, newMessage: String
    ): Message {
        var message = Message(userId, senderId = id, "Sys", "Send message error!")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 创建发送消息的请求
                val request =
                    NetworkManager.SendMessRequest(id, userId, newMessage)
                // 发送请求并获取响应
                val response = authService.sendMessage(request)
                // 判断响应是否成功
                if (response.isSuccessful) {
                    // 如果成功，处理响应数据
                    response.body()?.let {
                        message = it.message
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
        return message
    }

    @SuppressLint("SuspiciousIndentation")
    @Composable
    fun ChatBubble(message: Message, senderId: Int, context: Context,
                   avatar1: String,
                   avatar2: String) {
        println(avatar1)
        println(avatar2)
        if (isOrderMessage(message.content)) {
            RenderOrderMessage(message = message, senderId = senderId, context = context,
                avatar1 = avatar1, avatar2 = avatar2)
        } else
            RenderTextMessage(message, senderId,avatar1, avatar2)
    }

    @Composable
    fun RenderTextMessage(message: Message, senderId: Int, avatar1:
    String, avatar2: String) {
        val isSentByUser = message.senderId == senderId
        val bubbleColor =
            if (isSentByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        val textColor = if (isSentByUser) Color.White else Color.Black

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isSentByUser) {
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
                horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier
                        .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    color = textColor
                )
            }

            if (isSentByUser) {
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
    fun RenderOrderMessage(message: Message, senderId: Int, context: Context, avatar1: String,
                           avatar2: String) {
        val isSentByUser = message.senderId == senderId
        val bubbleColor = if (isSentByUser) MaterialTheme.colorScheme.primary else Color(0xFFFFE082)
        val textColor = if (isSentByUser) Color.White else MaterialTheme.colorScheme.onSurface
        val commodity = fetchComId(message.content)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isSentByUser) {
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
                horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = "你有一个订单待确认",
                    modifier = Modifier
                        .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp)) // 用于文本和按钮之间的间距

                Button(
                    onClick = {
                        val intent = Intent(context, OrderActivity::class.java).apply {
                            putExtra("commodity",commodity)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp) // 设置按钮的外边距
                ) {
                    Text(text = "查看订单")
                }
            }

            if (isSentByUser) {
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
fun fetchComId(content: String) : String? {
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



fun sendOrderMessage(receiver: Int, acceptor: Int, commodityId: Int) {
    val content = generateOrderMess(commodityId)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 创建发送消息的请求
            val request =
                NetworkManager.SendMessRequest(acceptor, receiver, content)
            // 发送请求并获取响应
            val response = authService.sendMessage(request)
            // 判断响应是否成功
            if (response.isSuccessful) {

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





