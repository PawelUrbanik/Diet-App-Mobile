package pl.pawel.diet_app_mobile.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithIngredients(
    @Embedded
    val meal: MealEntity,
    @Relation(
        entity = MealIngredientEntity::class,
        parentColumn = "id",
        entityColumn = "mealId",
    )
    val ingredients: List<IngredientWithProduct>,
)
