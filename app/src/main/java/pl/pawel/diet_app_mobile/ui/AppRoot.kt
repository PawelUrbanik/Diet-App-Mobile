package pl.pawel.diet_app_mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import pl.pawel.diet_app_mobile.ui.meals.MealsRoute
import pl.pawel.diet_app_mobile.ui.products.ProductsRoute

@Composable
fun AppRoot() {
    var selectedTab by remember { mutableStateOf(AppTab.Products) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            when (selectedTab) {
                AppTab.Products -> ProductsRoute()
                AppTab.Meals -> MealsRoute()
            }
        }
        NavigationBar {
            AppTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { selectedTab = tab },
                    label = { Text(tab.label) },
                    icon = {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.icon,
                            contentDescription = tab.label,
                        )
                    },
                )
            }
        }
    }
}

private enum class AppTab(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Products(
        label = "Produkty",
        icon = Icons.Outlined.LocalGroceryStore,
        selectedIcon = Icons.Filled.LocalGroceryStore,
    ),
    Meals(
        label = "Posiłki",
        icon = Icons.Outlined.Restaurant,
        selectedIcon = Icons.Filled.Restaurant,
    ),
}
