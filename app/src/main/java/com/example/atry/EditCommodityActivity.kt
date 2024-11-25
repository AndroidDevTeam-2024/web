package com.example.atry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.Serializable

class EditCommodityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val commodity: AccountActivity.Commodity? =
            intent.getSerializableExtra("commodity") as? AccountActivity.Commodity

        commodity?.let {
            setContent {
                EditScreen(it)
            }
        }
    }

    @Composable
    fun EditScreen(commodity: AccountActivity.Commodity) {
        var name by remember { mutableStateOf(commodity.name) }
        var price by remember { mutableStateOf(commodity.price.toString()) }
        var introduction by remember { mutableStateOf(commodity.introduction) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Edit Commodity", style = MaterialTheme.typography.headlineSmall)

            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Price Field
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Introduction Field
            OutlinedTextField(
                value = introduction,
                onValueChange = { introduction = it },
                label = { Text("Introduction") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    // 保存数据逻辑
                    val updatedCommodity = commodity.copy(
                        name = name,
                        price = price.toIntOrNull() ?: commodity.price,
                        introduction = introduction
                    )
                    // 这里可以将 updatedCommodity 传递回调用者 Activity 或保存到数据库
                    println("Updated Commodity: $updatedCommodity")
                    finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }

}