package com.example.atry
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import coil.compose.AsyncImage
import com.example.atry.api.NetworkManager
import com.example.atry.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




class LoginActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {
        var name by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = R.drawable.topo, // GIF 的网络 URL
                contentDescription = "Sample GIF",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // 根据需要调整宽高比
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            val response = withContext(Dispatchers.IO) {
                                NetworkManager.authService.login(
                                    NetworkManager.LoginRequest(
                                        name,
                                        password
                                    )
                                )
                            }
                            Log.d("NetworkRequest", "Request was sent successfully")
                            if (response.isSuccessful) {
                                UserSession.getInstance().username = name
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    onLoginSuccess(loginResponse)
                                } else {
                                    errorMessage = "Unexpected response from server"
                                }
                            } else {
                                errorMessage = response.errorBody()?.string() ?: "Login failed"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Network error: ${e.message}"
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
                    Text("登录")
                }
            }
            Button(
                onClick = {
                    navigateToSignScreen()
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("注册")
            }
        }

        }

    private fun onLoginSuccess(loginResponse: NetworkManager.LoginResponse) {

        // 保存用户 Token
        saveToken(loginResponse)
        // 显示登录成功提示
        showSuccessMessage()

        // 跳转到主界面
        navigateToHomeScreen()
    }

    private fun saveToken(loginResponse: NetworkManager.LoginResponse) {
        val userSession = UserSession.getInstance()
        userSession.id = loginResponse.id
        userSession.email = loginResponse.email
        userSession.avatar = loginResponse.avator
    }

    private fun showSuccessMessage() {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHomeScreen() {
        // 如果希望登录界面不再保留
        (this as? Activity)?.finish()
    }
    private fun navigateToSignScreen() {
        val intent = Intent(this, SignupActivity::class.java)
        this.startActivity(intent)
        // 如果希望登录界面不再保留
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}