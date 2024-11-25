package com.example.atry

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.atry.api.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignupScreen()
        }
    }
}


@Composable
fun SignupScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.go_for),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                // 用户名输入框
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 密码输入框
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 确认密码输入框
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 错误信息显示
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 注册按钮
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null

                            if (password != confirmPassword) {
                                errorMessage = "密码和确认密码不匹配"
                                isLoading = false
                                return@launch
                            }

                            try {
                                // 调用注册接口
                                val response = withContext(Dispatchers.IO) {
                                    NetworkManager.authService.signup(
                                        NetworkManager.SignupRequest(username, password, "123456@qq.com")
                                    )
                                }
                                Log.d("NetworkRequest", "Request was sent successfully")
                                if (response.isSuccessful) {
                                    val signupResponse = response.body()
                                    if (signupResponse != null) {
                                        // 处理注册成功后的逻辑

                                        (context as? ComponentActivity)?.finish()
                                    } else {
                                        errorMessage = "服务器返回数据为空"
                                    }
                                } else {
                                    errorMessage = response.errorBody()?.string() ?: "注册失败"
                                }
                            } catch (e: Exception) {
                                errorMessage = "网络错误: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("注册")
                    }
                }
            }
        }
    )
}

