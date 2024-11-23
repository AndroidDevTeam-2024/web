package com.example.atry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    // Bottom navigation items
    val navItems = listOf(
        NavigationItem("Home", Icons.Filled.Home),
        NavigationItem("Products", Icons.Filled.ShoppingCart),
        NavigationItem("Messages", Icons.AutoMirrored.Filled.Message),
        NavigationItem("Profile", Icons.Filled.Person)
    )

    // Navigation state to track selected item
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = navItems,
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Display the selected screen
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> ProductsScreen()
                2 -> MessagesScreen()
                3 -> ProfileScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<NavigationItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) }
            )
        }
    }
}

data class NavigationItem(val label: String, val icon: ImageVector)

// Screens for each navigation item
@Composable
fun HomeScreen() {
    CenteredContent("Home Screen")
}

@Composable
fun ProductsScreen() {
    CenteredContent("Products Screen")
}

@Composable
fun MessagesScreen() {
    CenteredContent("Messages Screen")
}

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

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}


