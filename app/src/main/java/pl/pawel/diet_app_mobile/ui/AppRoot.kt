package pl.pawel.diet_app_mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    label = { Text(tab.label) },
                    icon = {},
                )
            }
        }
    }
}

private enum class AppTab(val label: String) {
    Products("Produkty"),
    Meals("Posiłki"),
}
