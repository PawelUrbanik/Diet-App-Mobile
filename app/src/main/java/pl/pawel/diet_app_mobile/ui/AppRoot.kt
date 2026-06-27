package pl.pawel.diet_app_mobile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import pl.pawel.diet_app_mobile.ui.library.LibraryRoute
import pl.pawel.diet_app_mobile.ui.plan.PlanRoute
import pl.pawel.diet_app_mobile.ui.shopping.ShoppingRoute

private val AppTabSaver: Saver<AppTab, String> =
    Saver(save = { it.name }, restore = { AppTab.valueOf(it) })

@Composable
fun AppRoot() {
    var selectedTab by rememberSaveable(stateSaver = AppTabSaver) {
        mutableStateOf(AppTab.Plan)
    }
    // Zachowuje stan (np. pozycję przewijania) każdej zakładki przy przełączaniu.
    val stateHolder = rememberSaveableStateHolder()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            stateHolder.SaveableStateProvider(selectedTab.name) {
                when (selectedTab) {
                    AppTab.Plan -> PlanRoute()
                    AppTab.Shopping -> ShoppingRoute()
                    AppTab.Library -> LibraryRoute()
                }
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
    Plan(
        label = "Plan",
        icon = Icons.Outlined.CalendarMonth,
        selectedIcon = Icons.Filled.CalendarMonth,
    ),
    Shopping(
        label = "Zakupy",
        icon = Icons.Outlined.ShoppingCart,
        selectedIcon = Icons.Filled.ShoppingCart,
    ),
    Library(
        label = "Biblioteka",
        icon = Icons.AutoMirrored.Outlined.MenuBook,
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
    ),
}
