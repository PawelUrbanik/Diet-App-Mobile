package pl.pawel.diet_app_mobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.database.DietAppDatabase

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
    ).build()

    @Provides
    fun provideProductDao(database: DietAppDatabase): ProductDao = database.productDao()
}
