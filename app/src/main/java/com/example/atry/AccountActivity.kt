package com.example.atry

import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp


class AccountActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityScreen()
        }
    }

    @Composable
    fun ActivityScreen() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            item { HeaderSection() }
            item { Spacer(modifier = Modifier.height(12.dp)); AccountStats() }
            item { Spacer(modifier = Modifier.height(24.dp)); PostSection() }
            item { Spacer(modifier = Modifier.height(24.dp)); SettingsButton() }
        }

    }

    @Composable
    fun SettingsButton() {
        Text("Not yet implemented")
    }

    data class Commodity(val author: String, val body: String)

    object SampleData {
        val commoditySample = listOf(
            Commodity("Alice", "Hi, how are you?"),
            Commodity("Bob", "I'm good, thanks! How about you?"),
            Commodity("Alice", "Pretty good, just working on a project."),
            Commodity("Bob", "That sounds great. Let me know if you need any help!"),
            Commodity("Alice", "Sure, thanks!"),
        )
    }

    @Composable
    fun Conversation(commodities: List<Commodity>) {
        LazyColumn {
            items(commodities) { commodity ->
                MessageCard(commodity)
            }
        }
    }

    @Composable
    fun MessageCard(msg: Commodity) {
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.profile_test),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }

            // We toggle the isExpanded variable when we click on this Column
            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(
                    text = msg.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                ) {
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    @Composable
    fun PostSection() {
        MaterialTheme {
            Conversation(SampleData.commoditySample)
        }
    }



    @Composable
    fun AccountStats() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 关注数
            StatItem(
                label = "关注",
                count = "123"
            )

            // 粉丝数
            StatItem(
                label = "粉丝",
                count = "456"
            )

            // 获赞数
            StatItem(
                label = "售出单数",
                count = "789"
            )
        }
    }

    @Composable
    fun StatItem(label: String, count: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 数值
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            // 标签
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )
        }
    }

    @Composable
    fun HeaderSection() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray, CircleShape)
            ) {
                // 使用静态图片替代加载器
                Image(
                    painter = painterResource(id = R.drawable.profile_test), // 替换为实际资源
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 用户名
            Text(
                text = "UserName", // 替换为实际用户名
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 个性签名
            Text(
                text = "This is a bio or signature", // 替换为实际个性签名
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 编辑按钮
            OutlinedButton(
                onClick = { /* 点击逻辑 */ },
                modifier = Modifier
                    .fillMaxWidth(0.6f) // 按钮宽度为屏幕的60%
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
            }
        }
    }


}