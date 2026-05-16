package pl.pawel.diet_app_mobile.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

fun mealCategoryIcon(category: String): ImageVector = when (category) {
    "Śniadanie" -> Icons.Filled.WbSunny
    "Drugie śniadanie" -> Icons.Filled.EmojiFoodBeverage
    "Obiad" -> Icons.Filled.LunchDining
    "Kolacja" -> Icons.Filled.DinnerDining
    "Przekąska" -> Icons.Filled.Cookie
    else -> Icons.Filled.Restaurant
}

fun productCategoryIcon(category: String): ImageVector = when (category) {
    "Mięso i ryby" -> Icons.Filled.SetMeal
    "Nabiał i jaja" -> Icons.Filled.Egg
    "Pieczywo i produkty zbożowe" -> Icons.Filled.BakeryDining
    "Przyprawy i dodatki" -> Icons.Filled.LocalDining
    "Warzywa" -> Icons.Filled.Eco
    else -> Icons.Filled.Restaurant
}
