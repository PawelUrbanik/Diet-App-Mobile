package pl.pawel.diet_app_mobile.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.pawel.diet_app_mobile.data.repository.RoomMealPlanRepository
import pl.pawel.diet_app_mobile.data.repository.RoomMealRepository
import pl.pawel.diet_app_mobile.data.repository.RoomProductRepository
import pl.pawel.diet_app_mobile.domain.repository.MealPlanRepository
import pl.pawel.diet_app_mobile.domain.repository.MealRepository
import pl.pawel.diet_app_mobile.domain.repository.ProductRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindProductRepository(
        repository: RoomProductRepository,
    ): ProductRepository

    @Binds
    abstract fun bindMealRepository(
        repository: RoomMealRepository,
    ): MealRepository

    @Binds
    abstract fun bindMealPlanRepository(
        repository: RoomMealPlanRepository,
    ): MealPlanRepository
}
