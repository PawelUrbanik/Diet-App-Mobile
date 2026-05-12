package pl.pawel.diet_app_mobile.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.entity.MealEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealIngredientEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealPlanEntity
import pl.pawel.diet_app_mobile.data.local.entity.PlannedMealEntity
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity
import pl.pawel.diet_app_mobile.data.local.entity.ShoppingListItemEntity

@Database(
    entities = [
        ProductEntity::class,
        MealEntity::class,
        MealIngredientEntity::class,
        MealPlanEntity::class,
        PlannedMealEntity::class,
        ShoppingListItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class DietAppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
