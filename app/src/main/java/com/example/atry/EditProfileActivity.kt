package com.example.atry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


class EditProfileActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileScreen(
                onSave = {
                    // 保存编辑后的处理逻辑
                    // 例如，更新用户信息后，你可能想要返回到前一个页面
                    finish()
                },
                onBack = {
                    // 返回上一个页面的处理逻辑
                    finish() // 或者使用导航组件返回
                }
            )
        }
    }


    @Composable
    fun EditProfileScreen(onSave: () -> Unit, onBack: () -> Unit) {
        // 背景渐变和圆角处理
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF3F4F6), Color(0xFFE5E8EC)),
                        startY = 0f,
                        endY = 1000f
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp) // 页面左右间距
                    //.verticalArrangement(Arrangement.spacedBy(20.dp)) // 项目间距
            ) {
                // 标题和用户名输入框
                ProfileSectionTitle("Username")
                ProfileTextField(
                    value = "",
                    onValueChange = { /* 更新用户名的状态 */ },
                    placeholder = "Enter your username"
                )

                // 密码输入框
                ProfileSectionTitle("Password")
                ProfileTextField(
                    value = "",
                    onValueChange = { /* 更新密码的状态 */ },
                    placeholder = "Enter your password",
                    isPassword = true
                )

                // 邮箱输入框
                ProfileSectionTitle("Email")
                ProfileTextField(
                    value = "",
                    onValueChange = { /* 更新邮箱的状态 */ },
                    placeholder = "Enter your email"
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 保存按钮
                SaveButton(onSave)

                // 返回按钮
                BackButton(onBack)
            }
        }
    }

    // 标题部分，使用加粗字体和适当的间距
    @Composable
    fun ProfileSectionTitle(title: String) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    // 统一的输入框样式，可以通过参数自定义密码、是否多行等
    @Composable
    fun ProfileTextField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        isPassword: Boolean = false,
        isMultiline: Boolean = false
    ) {
        val visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF333333),
                fontFamily = FontFamily.Default
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = if (isMultiline) KeyboardCapitalization.None else KeyboardCapitalization.Words
            ),
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .shadow(5.dp, RoundedCornerShape(10.dp))
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                color = Color(0xFFB0B0B0),
                                fontSize = 16.sp
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }

    // 保存按钮样式
    @Composable
    fun SaveButton(onSave: () -> Unit) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(Color(0xFF00A1E1)),
            elevation = ButtonDefaults.elevatedButtonElevation(5.dp)
        ) {
            Text(
                text = "Save Changes",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    // 返回按钮样式
    @Composable
    fun BackButton(onBack: () -> Unit) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF00A1E1))
        ) {
            Text(text = "Back", style = MaterialTheme.typography.bodyLarge)
        }
    }


    @Preview()
    @Composable
    fun EditProfilePreview() {
        EditProfileScreen(
            onSave = {
                // 保存编辑后的处理逻辑
                // 例如，更新用户信息后，你可能想要返回到前一个页面
                finish()
            },
            onBack = {
                // 返回上一个页面的处理逻辑
                finish() // 或者使用导航组件返回
            }
        )
    }

}