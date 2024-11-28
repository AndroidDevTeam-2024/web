package com.example.atry

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iflytek.sparkchain.core.LLM
import com.iflytek.sparkchain.core.LLMConfig
import com.iflytek.sparkchain.core.LLMFactory
import com.iflytek.sparkchain.core.LLMOutput
import com.iflytek.sparkchain.core.Memory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    // 对话框控制状态
    val showDialog = remember { mutableStateOf(false) }

    // 动画控制变量，控制对话框的透明度和位置
    val offsetY = remember { Animatable(0f) }
    val opacity = remember { Animatable(0f) }

    // 获取CoroutineScope
    val coroutineScope = rememberCoroutineScope()

    // 主界面布局
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 右下角按钮
        FloatingActionButton(
            onClick = {
                // 点击按钮控制对话框的显示或隐藏
                if (showDialog.value) {
                    // 隐藏对话框
                    coroutineScope.launch {
                        offsetY.animateTo(0f) // 动画收回对话框
                        opacity.animateTo(0f)  // 动画淡出
                    }
                } else {
                    // 显示对话框
                    coroutineScope.launch {
                        offsetY.animateTo(-100f) // 动画弹出对话框
                        opacity.animateTo(1f)     // 动画淡入
                    }
                }
                showDialog.value = !showDialog.value
            },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Chat Button",
                tint = Color.White
            )
        }
    }

    // 弹出聊天对话框
    if (showDialog.value) {
        ChatDialog(
            offsetY = offsetY.value,
            opacity = opacity.value,
            onDismiss = { showDialog.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDialog(onDismiss: () -> Unit, offsetY: Float, opacity: Float) {
    // 聊天记录
    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>() } // Pair(消息内容, 是否为用户)
    val userInput = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(1.dp)
                .height(680.dp) // 增加聊天框的高度
                .graphicsLayer {
                    this.translationY = offsetY  // 使用动画控制 Y 轴的偏移
                    this.alpha = opacity // 控制对话框的透明度
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // 对话框标题
                Text(
                    text = "与大模型对话",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp), // 减小字体
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 0.dp)
                        .align(Alignment.CenterHorizontally) // 水平居中
                        .padding(top = 10.dp) // 向下移动标题
                )

                // 聊天记录区域
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(4.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.TopStart
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = false // 最新消息在底部
                    ) {
                        items(chatMessages) { message ->
                            ChatBubble(message)
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                // 输入区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = userInput.value,
                        onValueChange = { userInput.value = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("请输入消息...") },
                        maxLines = 10,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 发送按钮
                    Button(
                        onClick = {
                            if (userInput.value.isNotBlank()) {
                                val userMessage = userInput.value.trim()
                                chatMessages.add(Pair(userMessage, true)) // 添加用户消息
                                userInput.value = ""

                                // 模拟回复
                                CoroutineScope(Dispatchers.IO).launch {
                                    val response = mockModelResponse(userMessage)
                                    withContext(Dispatchers.Main) {
                                        chatMessages.add(Pair(response, false)) // 添加模型消息
                                    }
                                }
                            }
                        },
                        modifier = Modifier.height(40.dp), // 减小按钮高度
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("发送", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)) // 减小字体
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Pair<String, Boolean>) {
    val (text, isUser) = message
    val paddingStart = if (isUser) 64.dp else 4.dp // 用户消息右侧有16dp，模型消息左侧有64dp
    val paddingEnd = if (isUser) 4.dp else 64.dp // 用户消息左侧有64dp，模型消息右侧有16dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd) // 给每个气泡的左右边距设置不同的值
        ,
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart // 控制气泡对齐
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(6.dp)
                .widthIn(max = 300.dp), // 限制气泡的最大宽度
            color = if (isUser) Color.White else Color.Black,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
    }
}

suspend fun mockModelResponse(question: String): String {
//    Log.i("SparkChain", question)
//    val window_memory: Memory = Memory.windowMemory(5)
//    val chat_llmConfig: LLMConfig = LLMConfig.builder().maxToken(2048)
//    Log.i("SparkChain", "hello")
//    val chat_llm: LLM = LLMFactory.textGeneration(chat_llmConfig, window_memory);
//    val syncOutput: LLMOutput = chat_llm.run(question)
//    val content = syncOutput.content
//    Log.i("SparkChain", content)
//    val errCode = syncOutput.errCode
//    Log.i("SparkChain", errCode.toString())
//    val errMsg = syncOutput.errMsg
//    Log.i("SparkChain", errMsg.toString())
//    return content.toString()
    return "okok"
}

