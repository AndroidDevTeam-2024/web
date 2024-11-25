package com.example.atry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.atry.api.NetworkManager.OrderMessage

import com.example.atry.api.NetworkManager.Message


class TalkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 从 Intent 中获取发送方昵称
        val senderId = intent.getStringExtra("senderId") ?: "-1"
        val senderName = intent.getStringExtra("senderName") ?: "Unknown Sender"

        setContent {
            ChatScreen(senderId.toInt(), senderName)
        }
    }

    @Composable
    fun ChatScreen(senderId: Int,
                   senderName: String) {
        // 模拟一些消息数据
        val messages = remember {
            mutableStateListOf(
                Message(1, 101, "Alice", "Hello!"),
                Message(2, 102, "You", "Hi!"),
                Message(3, 101, "Alice", "How are you?"),
                OrderMessage(
                    baseMessage = Message(2, 2, "Bob", "Order Info"),
                    orderId = "12345",
                    orderDetails = "2x Coffee, 1x Sandwich"
                )
            )
        }
        var newMessage by remember { mutableStateOf("") }

        val  context = LocalContext.current
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // 聊天消息列表
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp),
                        reverseLayout = true // 最新消息在底部
                    ) {
                        items(messages.reversed()) { message ->
                            ChatBubble(message = message, senderId = senderId, context = context)
                        }
                    }

                    // 输入栏
                    Row(
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
                                messages.add(Message(3, 101, "You", newMessage)) // 添加用户消息
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

    @Composable
    fun ChatBubble(message: Any, senderId: Int, context: Context) {
        when (message) {
            is Message -> RenderTextMessage(message, senderId)
            is OrderMessage -> RenderOrderMessage(message, senderId, context)
            else -> throw IllegalArgumentException("Unsupported message type")
        }
    }

    @Composable
    fun RenderTextMessage(message: Message, senderId: Int) {
        val isSentByUser = message.senderId == senderId
        val bubbleColor = if (isSentByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        val textColor = if (isSentByUser) Color.White else Color.Black

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            Text(
                text = message.content,
                modifier = Modifier
                    .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                    .padding(12.dp),
                color = textColor
            )
        }
    }

    @Composable
    fun RenderOrderMessage(message: OrderMessage, senderId: Int, context: Context) {
        val isSentByUser = message.baseMessage.senderId == senderId
        val bubbleColor = if (isSentByUser) MaterialTheme.colorScheme.primary else Color(0xFFFFE082)
        val textColor = if (isSentByUser) Color.White else MaterialTheme.colorScheme.onSurface
        val alignment = if (isSentByUser) Alignment.End else Alignment.Start

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .wrapContentWidth(alignment)
                .background(bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Order ID: ${message.orderId}",
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Details: ${message.orderDetails}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    val intent = Intent(context, OrderActivity::class.java).apply {
                        putExtra("orderId", message.orderId)
                        putExtra("orderDetails", message.orderDetails)
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "View Order")
            }
        }
    }
}


