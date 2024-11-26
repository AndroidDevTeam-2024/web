package com.example.atry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

import com.example.atry.api.NetworkManager.Message



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)

        /*val  context = this
        val userSession = UserSession.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getUserAvatar(context, userSession.id)
            } catch (e: Exception) {
                Log.e("FetchAvatar", "Error fetching and saving avatar", e)
            }
        }*/


        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("products") { GoodsScreen() }
            composable("publish") { TurnToRegisterScreen() }
            composable("messages") { MessagesScreen() }
            composable("profile") { AccountActivity().MainScreen() }
        }
    }
}

@Composable
fun TurnToRegisterScreen() {
    val context = LocalContext.current
    val intent = Intent(context, RegisterCommodityActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", "Home", Icons.Filled.Home),
        NavigationItem("products", "Products", Icons.Filled.ShoppingCart),
        NavigationItem("publish", "Publish", Icons.Filled.Add), // 添加“发布”按钮
        NavigationItem("messages", "Messages", Icons.AutoMirrored.Filled.Message),
        NavigationItem("profile", "Profile", Icons.Filled.Person)
    )
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: ImageVector)

// Screens for each navigation item
@Composable
fun HomeScreen() {
    CenteredContent("Products Screen")
}

@Composable
fun MessagesScreen() {


    val messages by remember {
        mutableStateOf(
            listOf(
                Message(1, 100, "Alice", "Hey, how are you?"),
                Message(2, 101, "Bob", "Meeting at 3 PM tomorrow."),
                Message(3, 102, "Charlie", "Don't forget the project deadline!"),
                Message(4, 103, "David", "Are you coming to the party?"),
                Message(5, 104, "Eva", "Can you send me the report?")
            )
        )
    }


    SwipeToDismissMessage(messages = messages)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissMessage(
    messages: List<Message>
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
                            intent.putExtra("senderId", message.senderId) // 可选：传递 message 数据
                            intent.putExtra("senderName", message.senderName)
                            context.startActivity(intent) }
                    )
                }
            )

            LaunchedEffect(swipeState.currentValue) {
                if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                    delay(100) // 延迟以确保动画完成
                    messageList.remove(message) // 安全地更新数据源
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
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
            painter = painterResource(id = R.drawable.profile_test),
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


// 消息数据类
@Composable
fun ProfileScreen() {
    CenteredContent("Profile Screen")
}

// Utility Composable to display centered text
@Composable
fun CenteredContent(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}





