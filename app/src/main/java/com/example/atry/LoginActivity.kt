package com.example.atry
import android.app.Activity
import android.content.Context
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
import com.example.atry.api.NetworkManager
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
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
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
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    onLoginSuccess(loginResponse.token)
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
                    Text("Login")
                }
            }
            Button(
                onClick = {
                    navigateToSignScreen()
                          },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Signup")
            }
        }

        }

    private fun onLoginSuccess(token: String) {

        // 保存用户 Token
        saveToken(token)

        // 显示登录成功提示
        showSuccessMessage()

        // 跳转到主界面
        navigateToHomeScreen()
    }

    private fun saveToken(token: String) {
        val sharedPreferences = this.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
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
}