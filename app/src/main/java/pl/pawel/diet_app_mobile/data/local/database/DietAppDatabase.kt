package pl.pawel.diet_app_mobile.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.pawel.diet_app_mobile.data.local.dao.MealDao
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.dao.ShoppingListDao
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
    version = 3,
    exportSchema = true,
)
abstract class DietAppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    abstract fun mealDao(): MealDao

    abstract fun mealPlanDao(): MealPlanDao

    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN category TEXT NOT NULL DEFAULT 'Inne'")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `shopping_list_items`")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `shopping_list_items` (" +
                        "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "`productId` INTEGER, " +
                        "`name` TEXT NOT NULL, " +
                        "`category` TEXT NOT NULL, " +
                        "`quantityGrams` REAL NOT NULL, " +
                        "`isChecked` INTEGER NOT NULL, " +
                        "`isManual` INTEGER NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL, " +
                        "`updatedAt` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`productId`) REFERENCES `products`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_shopping_list_items_productId` " +
                        "ON `shopping_list_items` (`productId`)",
                )
            }
        }
    }
}
