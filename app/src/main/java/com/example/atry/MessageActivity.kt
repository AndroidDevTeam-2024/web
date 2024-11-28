package com.example.atry

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.atry.api.NetworkManager
import com.example.atry.api.NetworkManager.AvaMessage
import com.example.atry.api.NetworkManager.authService
import com.example.atry.model.UserSession
import kotlinx.coroutines.delay

@SuppressLint("SuspiciousIndentation")
@Composable
fun MessagesScreen() {
    // 初始消息数据
    var messages by remember {
        mutableStateOf(
            listOf(
                AvaMessage(1, 100, "Alice", "Hey, how are you?", "123"),
                AvaMessage(2, 100, "Alice", "Hey, how are you?", "123")
            )
        )
    }

    val userSession = UserSession.getInstance()
    val userId = userSession.id

    // 使用 mutableStateOf 来管理加载状态
    var isLoading by remember { mutableStateOf(true) }

    // 启动协程来获取消息
    LaunchedEffect(userId) {
        try {
            val response = authService.fetchMessages(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    messages = it.messages // 更新消息列表
                    println(messages)
                }
            } else {
                // 处理错误情况
                messages = emptyList()
                Log.e("MessageScreen", "Error fetching messages: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MessageScreen", "Exception: ${e.message}")
        } finally {
            isLoading = false  // 加载完成
        }
    }

    // 如果正在加载，显示加载指示器
    if (isLoading) {
          // 或者其他加载中的 UI
    } else {
        // 根据消息列表是否为空决定显示的内容
        if (messages.isEmpty()) {
            DisplayNoMessage()
        } else {
            SwipeToDismissMessage(messages = messages)
        }
    }
}

@Composable
fun DisplayNoMessage() {
     // 可选：清空消息列表

    // 显示提示信息和图片
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .padding(16.dp)
    ) {
        // 显示图片
        Image(
            painter = painterResource(id = R.drawable.no_messages),
            contentDescription = "No messages",
            modifier = Modifier
                .size(150.dp)  // 增加图片的尺寸
                .align(Alignment.CenterHorizontally)
        )

// 显示提示文字
        Text(
            text = "似乎没有人给你发消息呢",
            style = TextStyle(
                fontFamily = FontFamily.Serif, // 设置艺术感字体，Serif 是一种有艺术感的字体
                fontWeight = FontWeight.Bold,  // 设置加粗
                fontSize = 24.sp  // 字体大小，可以调整为适合的大小
            ),
            color = MaterialTheme.colorScheme.secondary, // 使用主题的 secondary 颜色
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissMessage(
    messages: List<AvaMessage>
) {

    val context = LocalContext.current
    val messageList = remember { mutableStateListOf(*messages.toTypedArray()) }
    LazyColumn {
        items(messageList, key = { it.id }) { message -> // 确保唯一键
            val swipeState = rememberSwipeToDismissBoxState()

            SwipeToDismissBox(
                state = swipeState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // 避免动态测量问题
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    when (swipeState.targetValue) {
                        SwipeToDismissBoxValue.Settled -> { // 未滑动时
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color.White)
                            )
                        }
                        SwipeToDismissBoxValue.StartToEnd -> { // 左滑时（假设不启用该方向）
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color.Green) // 可以改为其他内容
                            )
                        }
                        SwipeToDismissBoxValue.EndToStart -> { // 右滑时
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.CenterEnd // 将内容对齐到右侧
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete), // 替换为你的垃圾桶图标资源
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(end = 16.dp),
                                    tint = Color.Black// 图标颜色，可调整
                                )
                            }
                        }
                        else -> {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(Color.White)
                            )
                        }
                    }

                },
                content = {
                    MessageItem(
                        message = message,
                        onClick = {
                            val intent = Intent(context, TalkActivity::class.java)
                            intent.putExtra("senderId", message.senderId.toString()) // 可选：传递 message 数据
                            context.startActivity(intent)
                        }
                    )
                }
            )

            LaunchedEffect(swipeState.currentValue) {
                if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    val id1 = UserSession.getInstance().id
                    val id2 = message.senderId
                    val request = NetworkManager.TalkRequest(id1 = id1, id2 = id2)
                    try {
                        val response = authService.deleteTalk(request)
                        if (response.isSuccessful) {
                            delay(100) // 延迟以确保动画完成
                            messageList.remove(message) // 安全地更新数据源
                        } else {
                            Log.e("MessageScreen", "Error delete talk: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("MessageScreen", "Exception: ${e.message}")
                    }

                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: AvaMessage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        // 头像
        Image(
            painter = rememberAsyncImagePainter(model = message.avator),
            contentDescription = "Sender Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 消息内容部分
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
