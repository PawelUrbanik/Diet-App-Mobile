package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class IngredientWithProduct(
    @Embedded
    val ingredient: MealIngredientEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id",
    )
    val product: ProductEntity,
)
