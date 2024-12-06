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
import com.iflytek.sparkchain.core.SparkChain
import com.iflytek.sparkchain.core.SparkChainConfig


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showMainScreen = intent.getBooleanExtra("SHOW_MAIN_SCREEN", false)

        if (!showMainScreen) {
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
        }

        /*val  context = this
        val userSession = UserSession.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getUserAvatar(context, userSession.id)
            } catch (e: Exception) {
                Log.e("FetchAvatar", "Error fetching and saving avatar", e)
            }
        }*/

        //配置应用信息
        val config = SparkChainConfig.builder()
            .appID("88f03161")
            .apiKey("abb03df7d1a452516ac868f034252981")
            .apiSecret("Mzc1ZGRiNTFiNzRiNGI3Yzk4ZmJmNjUx")
        val ret = SparkChain.getInst().init(applicationContext,config)

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
        NavigationItem("home", "主页", Icons.Filled.Home),
        NavigationItem("products", "产品", Icons.Filled.ShoppingCart),
        NavigationItem("publish", "发布", Icons.Filled.Add), // 添加“发布”按钮
        NavigationItem("messages", "消息", Icons.AutoMirrored.Filled.Message),
        NavigationItem("profile", "我的", Icons.Filled.Person)
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





