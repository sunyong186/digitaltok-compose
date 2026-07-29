package com.yourcompany.digitaltok.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private object AppColors {
    val Point = Color(0xFF3AADFF)
    val Gray1 = Color(0xFFA0A0A0)
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomNavBar(
    navController: NavController,
    onItemClick: (String) -> Boolean = { true }
) {
    val items = listOf(
        BottomItem("home", "홈", Icons.Outlined.Home),
        BottomItem("device", "기기 연결", Icons.Outlined.PhoneAndroid),
        BottomItem("decorate", "꾸미기", Icons.Outlined.StarOutline),
        BottomItem("settings", "설정", Icons.Outlined.PersonOutline),
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {

                        if (onItemClick(item.route)) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                            }
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.Point,
                    selectedTextColor = AppColors.Point,
                    unselectedIconColor = AppColors.Gray1,
                    unselectedTextColor = AppColors.Gray1,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
