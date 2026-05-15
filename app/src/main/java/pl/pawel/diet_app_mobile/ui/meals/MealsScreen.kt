package pl.pawel.diet_app_mobile.ui.meals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealIngredient
import pl.pawel.diet_app_mobile.domain.model.NutritionSummary
import pl.pawel.diet_app_mobile.domain.model.Product

@Composable
fun MealsRoute(
    viewModel: MealsViewModel = hiltViewModel(),
) {
    val meals by viewModel.meals.collectAsState()
    val query by viewModel.query.collectAsState()
    val formState by viewModel.formState.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    val ingredientEditorState by viewModel.ingredientEditorState.collectAsState()
    val ingredientProducts by viewModel.ingredientProducts.collectAsState()

    MealsScreen(
        meals = meals,
        query = query,
        formState = formState,
        editorState = editorState,
        ingredientEditorState = ingredientEditorState,
        ingredientProducts = ingredientProducts,
        onNameChange = viewModel::onNameChange,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCategoryChange = viewModel::onCategoryChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onAddMeal = viewModel::addMeal,
        onMealClick = viewModel::openMealEditor,
        onBackToList = viewModel::closeMealEditor,
        onEditorNameChange = viewModel::onEditorNameChange,
        onEditorCategoryChange = viewModel::onEditorCategoryChange,
        onEditorDescriptionChange = viewModel::onEditorDescriptionChange,
        onAddIngredientClick = viewModel::openAddIngredient,
        onEditIngredientClick = viewModel::openEditIngredient,
        onCloseIngredientEditor = viewModel::closeIngredientEditor,
        onProductQueryChange = viewModel::onProductQueryChange,
        onProductClick = viewModel::selectIngredientProduct,
        onQuantityGramsChange = viewModel::onQuantityGramsChange,
        onSaveIngredient = viewModel::saveIngredient,
        onDeleteIngredient = viewModel::removeIngredientFromEditor,
        onSaveMeal = viewModel::saveMealChanges,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealsScreen(
    meals: List<Meal>,
    query: String,
    formState: MealFormState,
    editorState: MealEditorState?,
    ingredientEditorState: IngredientEditorState?,
    ingredientProducts: List<Product>,
    onNameChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddMeal: () -> Unit,
    onMealClick: (Meal) -> Unit,
    onBackToList: () -> Unit,
    onEditorNameChange: (String) -> Unit,
    onEditorCategoryChange: (String) -> Unit,
    onEditorDescriptionChange: (String) -> Unit,
    onAddIngredientClick: () -> Unit,
    onEditIngredientClick: (Int) -> Unit,
    onCloseIngredientEditor: () -> Unit,
    onProductQueryChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuantityGramsChange: (String) -> Unit,
    onSaveIngredient: () -> Unit,
    onDeleteIngredient: () -> Unit,
    onSaveMeal: () -> Unit,
) {
    val title = when {
        ingredientEditorState != null && ingredientEditorState.isEditing -> "Edycja składnika"
        ingredientEditorState != null -> "Nowy składnik"
        editorState != null -> "Edycja posiłku"
        else -> "Posiłki"
    }
    val onBack: (() -> Unit)? = when {
        ingredientEditorState != null -> onCloseIngredientEditor
        editorState != null -> onBackToList
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text("Wróć")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            ingredientEditorState != null -> IngredientEditorContent(
                state = ingredientEditorState,
                products = ingredientProducts,
                modifier = Modifier.padding(innerPadding),
                onProductQueryChange = onProductQueryChange,
                onProductClick = onProductClick,
                onQuantityGramsChange = onQuantityGramsChange,
                onSaveIngredient = onSaveIngredient,
                onDeleteIngredient = onDeleteIngredient,
                onCancel = onCloseIngredientEditor,
            )
            editorState != null -> MealEditorContent(
                editorState = editorState,
                modifier = Modifier.padding(innerPadding),
                onNameChange = onEditorNameChange,
                onCategoryChange = onEditorCategoryChange,
                onDescriptionChange = onEditorDescriptionChange,
                onAddIngredientClick = onAddIngredientClick,
                onEditIngredientClick = onEditIngredientClick,
                onSaveMeal = onSaveMeal,
                onBackToList = onBackToList,
            )
            else -> MealsListContent(
                meals = meals,
                formState = formState,
                modifier = Modifier.padding(innerPadding),
                onNameChange = onNameChange,
                query = query,
                onSearchQueryChange = onSearchQueryChange,
                onCategoryChange = onCategoryChange,
                onDescriptionChange = onDescriptionChange,
                onAddMeal = onAddMeal,
                onMealClick = onMealClick,
            )
        }
    }
}

@Composable
private fun MealsListContent(
    meals: List<Meal>,
    formState: MealFormState,
    query: String,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddMeal: () -> Unit,
    onMealClick: (Meal) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MealFormCard(
                formState = formState,
                onNameChange = onNameChange,
                onCategoryChange = onCategoryChange,
                onDescriptionChange = onDescriptionChange,
                onAddMeal = onAddMeal,
            )
        }

        item {
            Text(
                text = "Baza posiłków",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Szukaj posiłku") },
                singleLine = true,
            )
        }

        if (meals.isEmpty()) {
            item { EmptyMealsCard(hasActiveQuery = query.isNotBlank()) }
        } else {
            items(
                items = meals,
                key = { meal -> meal.id },
            ) { meal ->
                MealListCard(
                    meal = meal,
                    onEditClick = { onMealClick(meal) },
                )
            }
        }
    }
}

@Composable
private fun MealEditorContent(
    editorState: MealEditorState,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddIngredientClick: () -> Unit,
    onEditIngredientClick: (Int) -> Unit,
    onSaveMeal: () -> Unit,
    onBackToList: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MealEditCard(
                editorState = editorState,
                onNameChange = onNameChange,
                onCategoryChange = onCategoryChange,
                onDescriptionChange = onDescriptionChange,
            )
        }

        item {
            IngredientsCard(
                ingredients = editorState.ingredients,
                onEditIngredient = onEditIngredientClick,
                onAddIngredientClick = onAddIngredientClick,
            )
        }

        item {
            EditorActions(
                editorState = editorState,
                onSaveMeal = onSaveMeal,
                onBackToList = onBackToList,
            )
        }
    }
}

@Composable
private fun IngredientEditorContent(
    state: IngredientEditorState,
    products: List<Product>,
    modifier: Modifier = Modifier,
    onProductQueryChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuantityGramsChange: (String) -> Unit,
    onSaveIngredient: () -> Unit,
    onDeleteIngredient: () -> Unit,
    onCancel: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = if (state.isEditing) "Edytuj składnik" else "Dodaj składnik",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        item {
            OutlinedTextField(
                value = state.productQuery,
                onValueChange = onProductQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Szukaj produktu") },
                singleLine = true,
            )
        }
        item {
            ProductResults(
                products = products,
                selectedProductId = state.selectedProductId,
                onProductClick = onProductClick,
            )
        }
        item {
            OutlinedTextField(
                value = state.quantityGrams,
                onValueChange = onQuantityGramsChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Ilość w gramach") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        state.errorMessage?.let { item { ErrorText(it) } }
        item {
            Button(
                onClick = onSaveIngredient,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (state.isEditing) "Zapisz zmiany" else "Dodaj składnik")
            }
        }
        if (state.isEditing) {
            item {
                OutlinedButton(
                    onClick = onDeleteIngredient,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Usuń składnik")
                }
            }
        }
        item {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Anuluj")
            }
        }
    }
}

@Composable
private fun MealFormCard(
    formState: MealFormState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddMeal: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Dodaj posiłek", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nazwa") },
                singleLine = true,
            )
            MealCategoryChips(
                selectedCategory = formState.category,
                onCategoryChange = onCategoryChange,
            )
            OutlinedTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Opis opcjonalny") },
                minLines = 2,
            )
            formState.errorMessage?.let { ErrorText(it) }
            Button(
                onClick = onAddMeal,
                enabled = !formState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (formState.isSaving) "Zapisywanie..." else "Dodaj posiłek")
            }
        }
    }
}

@Composable
private fun MealEditCard(
    editorState: MealEditorState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Dane posiłku", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = editorState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nazwa") },
                singleLine = true,
            )
            MealCategoryChips(
                selectedCategory = editorState.category,
                onCategoryChange = onCategoryChange,
            )
            OutlinedTextField(
                value = editorState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Opis opcjonalny") },
                minLines = 2,
            )
            Text(
                text = editorState.nutrition.format(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            editorState.errorMessage?.let { ErrorText(it) }
        }
    }
}

@Composable
private fun MealCategoryChips(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Typ posiłku",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        MEAL_CATEGORIES.chunked(2).forEach { rowCategories ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMealsCard(hasActiveQuery: Boolean) {
    Card {
        Text(
            text = if (hasActiveQuery) {
                "Brak posiłków pasujących do wyszukiwania."
            } else {
                "Brak posiłków. Dodaj pierwszy posiłek, a potem przejdź do edycji i przypisz składniki."
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MealListCard(
    meal: Meal,
    onEditClick: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(meal.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = meal.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            meal.description?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = meal.nutrition.format(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Składniki: ${meal.ingredients.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Edytuj")
            }
        }
    }
}

@Composable
private fun IngredientsCard(
    ingredients: List<MealIngredient>,
    onEditIngredient: (Int) -> Unit,
    onAddIngredientClick: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Składniki", style = MaterialTheme.typography.titleMedium)
            if (ingredients.isEmpty()) {
                Text(
                    text = "Ten posiłek nie ma jeszcze składników.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                ingredients.forEachIndexed { index, ingredient ->
                    IngredientRow(
                        ingredient = ingredient,
                        onEditClick = { onEditIngredient(index) },
                    )
                }
            }
            Button(
                onClick = onAddIngredientClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Dodaj składnik")
            }
        }
    }
}

@Composable
private fun ProductResults(
    products: List<Product>,
    selectedProductId: Long?,
    onProductClick: (Product) -> Unit,
) {
    if (products.isEmpty()) {
        Text(
            text = "Brak produktów do wyboru.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        products.forEach { product ->
            FilterChip(
                selected = selectedProductId == product.id,
                onClick = { onProductClick(product) },
                label = {
                    Text("${product.name} (${product.caloriesPer100g.format()} kcal / 100 g)")
                },
            )
        }
    }
}

@Composable
private fun IngredientRow(
    ingredient: MealIngredient,
    onEditClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${ingredient.product.name} - ${ingredient.quantityGrams.format()} g",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = ingredient.nutrition.format(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Edytuj składnik")
        }
    }
}

@Composable
private fun EditorActions(
    editorState: MealEditorState,
    onSaveMeal: () -> Unit,
    onBackToList: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onSaveMeal,
            enabled = !editorState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (editorState.isSaving) "Zapisywanie..." else "Zapisz zmiany")
        }
        OutlinedButton(
            onClick = onBackToList,
            enabled = !editorState.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Wróć do listy")
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
    )
}

private fun NutritionSummary.format(): String =
    "${calories.format()} kcal | B: ${protein.format()} g | T: ${fat.format()} g | W: ${carbs.format()} g"

private fun Double.format(): String =
    if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        "%.1f".format(this)
    }
