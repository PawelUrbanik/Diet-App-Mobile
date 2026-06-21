package pl.pawel.diet_app_mobile.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.pawel.diet_app_mobile.data.local.dao.MealDao
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.dao.ShoppingListDao
import pl.pawel.diet_app_mobile.data.local.dao.WeekTemplateDao
import pl.pawel.diet_app_mobile.data.local.entity.MealEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealIngredientEntity
import pl.pawel.diet_app_mobile.data.local.entity.MealPlanEntity
import pl.pawel.diet_app_mobile.data.local.entity.PlannedMealEntity
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity
import pl.pawel.diet_app_mobile.data.local.entity.ShoppingListItemEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateEntity
import pl.pawel.diet_app_mobile.data.local.entity.WeekTemplateSlotEntity

@Database(
    entities = [
        ProductEntity::class,
        MealEntity::class,
        MealIngredientEntity::class,
        MealPlanEntity::class,
        PlannedMealEntity::class,
        ShoppingListItemEntity::class,
        WeekTemplateEntity::class,
        WeekTemplateSlotEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
abstract class DietAppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    abstract fun mealDao(): MealDao

    abstract fun mealPlanDao(): MealPlanDao

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun weekTemplateDao(): WeekTemplateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN category TEXT NOT NULL DEFAULT 'Inne'")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `week_templates` (" +
                        "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "`name` TEXT NOT NULL, " +
                        "`createdAt` INTEGER NOT NULL)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `week_template_slots` (" +
                        "`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "`templateId` INTEGER NOT NULL, " +
                        "`dayOffset` INTEGER NOT NULL, " +
                        "`mealType` TEXT NOT NULL, " +
                        "`mealId` INTEGER NOT NULL, " +
                        "`servings` REAL NOT NULL, " +
                        "`position` INTEGER NOT NULL, " +
                        "FOREIGN KEY(`templateId`) REFERENCES `week_templates`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE, " +
                        "FOREIGN KEY(`mealId`) REFERENCES `meals`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_week_template_slots_templateId` " +
                        "ON `week_template_slots` (`templateId`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_week_template_slots_mealId` " +
                        "ON `week_template_slots` (`mealId`)",
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ujednolicenie kategorii: dawna "Przekąska" staje się "Podwieczorek",
                // zgodnie z kategoriami używanymi w szablonach planu.
                db.execSQL(
                    "UPDATE `meals` SET `category` = 'Podwieczorek' " +
                        "WHERE `category` = 'Przekąska'",
                )
                db.execSQL(
                    "UPDATE `planned_meals` SET `mealType` = 'Podwieczorek' " +
                        "WHERE `mealType` = 'Przekąska'",
                )
                db.execSQL(
                    "UPDATE `week_template_slots` SET `mealType` = 'Podwieczorek' " +
                        "WHERE `mealType` = 'Przekąska'",
                )
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
