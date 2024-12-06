package com.example.atry

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atry.api.NetworkManager
import com.example.atry.model.Product
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class GoodsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodsScreen() {
    val searchText = remember { mutableStateOf("") }
    val filteredList = remember { mutableStateOf(emptyList<Product>()) }
    val selectedCategory = remember { mutableStateOf("全部") } // 默认选择“全部”类别
    val categoryOptions = listOf("全部", "书籍", "电子产品", "生活用品", "食品")

    // 使用 LaunchedEffect 加载所有商品数据
    LaunchedEffect(Unit) {
        loadAllProducts(filteredList)
    }

    val context = LocalContext.current

    fun navigateToDetail(product: Product) {
        val intent = Intent(context, GoodsDetailActivity::class.java).apply {
            putExtra("id", product.id)
        }
        context.startActivity(intent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 搜索框
            TextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("搜索商品...") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 类别下拉菜单
            CategoryDropdownMenu(
                selectedCategory = selectedCategory.value,
                onCategorySelected = { selectedCategory.value = it },
                options = categoryOptions
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 搜索按钮
            Button(
                onClick = {
                    searchProductsByCategory(
                        searchText = searchText.value,
                        category = selectedCategory.value,
                        onResult = { filteredProducts -> filteredList.value = filteredProducts }
                    )
                },
                modifier = Modifier.height(40.dp)
            ) {
                Text("搜索")
            }
        }

        // 商品列表
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList.value.size) { index ->
                val product = filteredList.value[index]
                ProductCard(
                    product = product,
                    onClick = { navigateToDetail(product) } // 将跳转逻辑封装到一个函数
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CategoryDropdownMenu(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedCategory)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

private suspend fun loadAllProducts(
    filteredList: MutableState<List<Product>>
) {
    val apiService = NetworkManager.apiService
    try {
        val response = apiService.allGoods()
        if (response.isSuccessful) {
            val products = response.body()?.commodities?.toList() ?: emptyList()
            filteredList.value = products
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun searchProductsByCategory(
    searchText: String,
    category: String,
    onResult: (List<Product>) -> Unit
) {
    val apiService = NetworkManager.apiService

    // 在异步任务中调用 API
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val type = switchType(category);
            val response = apiService.categoryGoods(type)
            if (response.isSuccessful) {
                val filtered = response.body()?.commodities?.toList() ?: emptyList()
                val searchFiltered = if (searchText.isNotBlank()) {
                    filtered.filter { it.name.contains(searchText, ignoreCase = true) }
                } else {
                    filtered
                }
                withContext(Dispatchers.Main) {
                    onResult(searchFiltered)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun switchType(category: String): String {
    return when (category) {
        "全部" -> "all"
        "书籍" -> "books"
        "电子产品" -> "electronics"
        "生活用品" -> "daily_supplies"
        "食品" -> "food"
        else -> "unknown" // 默认值，当类别未匹配时
    }
}


@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() }, // 添加点击事件
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = product.homepage,
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "¥ ${product.price}",
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoodsScreen() {
    GoodsScreen()
}



