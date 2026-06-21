package pl.pawel.diet_app_mobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.pawel.diet_app_mobile.data.local.dao.MealDao
import pl.pawel.diet_app_mobile.data.local.dao.MealPlanDao
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.dao.ShoppingListDao
import pl.pawel.diet_app_mobile.data.local.dao.WeekTemplateDao
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase.Companion.MIGRATION_1_2
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase.Companion.MIGRATION_2_3
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase.Companion.MIGRATION_3_4
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase.Companion.MIGRATION_4_5

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): DietAppDatabase = Room.databaseBuilder(
        context,
        DietAppDatabase::class.java,
        "diet_app.db",
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
        .build()

    @Provides
    fun provideProductDao(database: DietAppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideMealDao(database: DietAppDatabase): MealDao = database.mealDao()

    @Provides
    fun provideMealPlanDao(database: DietAppDatabase): MealPlanDao = database.mealPlanDao()

    @Provides
    fun provideShoppingListDao(database: DietAppDatabase): ShoppingListDao =
        database.shoppingListDao()

    @Provides
    fun provideWeekTemplateDao(database: DietAppDatabase): WeekTemplateDao =
        database.weekTemplateDao()
}
