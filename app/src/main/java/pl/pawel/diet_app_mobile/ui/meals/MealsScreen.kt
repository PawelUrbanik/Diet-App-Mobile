package pl.pawel.diet_app_mobile.ui.meals

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.pawel.diet_app_mobile.domain.model.Meal
import pl.pawel.diet_app_mobile.domain.model.MealIngredient
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.ui.components.AppSearchBar
import pl.pawel.diet_app_mobile.ui.components.NutritionMacroBars
import pl.pawel.diet_app_mobile.ui.components.SwipeToDeleteContainer
import pl.pawel.diet_app_mobile.ui.components.mealCategoryIcon
import pl.pawel.diet_app_mobile.ui.components.productCategoryIcon
import pl.pawel.diet_app_mobile.ui.products.ProductSortBy
import pl.pawel.diet_app_mobile.ui.theme.MealColorDrugieSniadanie
import pl.pawel.diet_app_mobile.ui.theme.MealColorKolacja
import pl.pawel.diet_app_mobile.ui.theme.MealColorObiad
import pl.pawel.diet_app_mobile.ui.theme.MealColorPodwieczorek
import pl.pawel.diet_app_mobile.ui.theme.MealColorSniadanie

private enum class MealsNavTarget(val depth: Int) {
    List(0), AddMeal(1), EditMeal(1), AddIngredient(2), EditIngredient(2)
}

@Composable
fun MealsRoute(
    viewModel: MealsViewModel = hiltViewModel(),
) {
    val meals by viewModel.meals.collectAsState()
    val query by viewModel.query.collectAsState()
    val addMealState by viewModel.addMealState.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    val ingredientEditorState by viewModel.ingredientEditorState.collectAsState()
    val ingredientProducts by viewModel.ingredientProducts.collectAsState()
    val mealPendingDelete by viewModel.mealPendingDelete.collectAsState()

    MealsScreen(
        meals = meals,
        query = query,
        addMealState = addMealState,
        editorState = editorState,
        ingredientEditorState = ingredientEditorState,
        ingredientProducts = ingredientProducts,
        mealPendingDelete = mealPendingDelete,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onOpenAddMeal = viewModel::openAddMeal,
        onCloseAddMeal = viewModel::closeAddMeal,
        onNameChange = viewModel::onNameChange,
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
        onRemoveIngredient = viewModel::removeIngredientFromDraft,
        onCloseIngredientEditor = viewModel::closeIngredientEditor,
        onProductQueryChange = viewModel::onProductQueryChange,
        onSortByChange = viewModel::onSortByChange,
        onProductClick = viewModel::selectIngredientProduct,
        onQuantityGramsChange = viewModel::onQuantityGramsChange,
        onSaveIngredient = viewModel::saveIngredient,
        onDeleteIngredient = viewModel::removeIngredientFromEditor,
        onSaveMeal = viewModel::saveMealChanges,
        onRequestDeleteMeal = viewModel::requestDeleteMeal,
        onConfirmDeleteMeal = viewModel::confirmDeleteMeal,
        onCancelDeleteMeal = viewModel::cancelDeleteMeal,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealsScreen(
    meals: List<Meal>,
    query: String,
    addMealState: MealFormState?,
    editorState: MealEditorState?,
    ingredientEditorState: IngredientEditorState?,
    ingredientProducts: List<Product>,
    mealPendingDelete: Meal?,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddMeal: () -> Unit,
    onCloseAddMeal: () -> Unit,
    onNameChange: (String) -> Unit,
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
    onRemoveIngredient: (Int) -> Unit,
    onCloseIngredientEditor: () -> Unit,
    onProductQueryChange: (String) -> Unit,
    onSortByChange: (ProductSortBy) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuantityGramsChange: (String) -> Unit,
    onSaveIngredient: () -> Unit,
    onDeleteIngredient: () -> Unit,
    onSaveMeal: () -> Unit,
    onRequestDeleteMeal: (Meal) -> Unit,
    onConfirmDeleteMeal: () -> Unit,
    onCancelDeleteMeal: () -> Unit,
) {
    mealPendingDelete?.let { meal ->
        AlertDialog(
            onDismissRequest = onCancelDeleteMeal,
            title = { Text("Usunąć posiłek?") },
            text = { Text("Posiłek \"${meal.name}\" zostanie usunięty.") },
            confirmButton = { TextButton(onClick = onConfirmDeleteMeal) { Text("Usuń") } },
            dismissButton = { TextButton(onClick = onCancelDeleteMeal) { Text("Anuluj") } },
        )
    }

    val navTarget = when {
        ingredientEditorState != null && ingredientEditorState.isEditing -> MealsNavTarget.EditIngredient
        ingredientEditorState != null -> MealsNavTarget.AddIngredient
        editorState != null -> MealsNavTarget.EditMeal
        addMealState != null -> MealsNavTarget.AddMeal
        else -> MealsNavTarget.List
    }

    val title = when (navTarget) {
        MealsNavTarget.List -> "Posiłki"
        MealsNavTarget.AddMeal -> "Nowy posiłek"
        MealsNavTarget.EditMeal -> "Edycja posiłku"
        MealsNavTarget.AddIngredient -> "Nowy składnik"
        MealsNavTarget.EditIngredient -> "Edycja składnika"
    }
    val onBack: (() -> Unit)? = when (navTarget) {
        MealsNavTarget.List -> null
        MealsNavTarget.AddMeal -> onCloseAddMeal
        MealsNavTarget.EditMeal -> onBackToList
        MealsNavTarget.AddIngredient, MealsNavTarget.EditIngredient -> onCloseIngredientEditor
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Wróć",
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (navTarget == MealsNavTarget.List) {
                FloatingActionButton(onClick = onOpenAddMeal) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj posiłek")
                }
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = navTarget,
            transitionSpec = {
                val forward = targetState.depth >= initialState.depth
                (slideInHorizontally { if (forward) it else -it } + fadeIn()) togetherWith
                    (slideOutHorizontally { if (forward) -it else it } + fadeOut())
            },
            label = "meals_nav",
            modifier = Modifier.padding(innerPadding),
        ) { target ->
            when (target) {
                MealsNavTarget.AddIngredient, MealsNavTarget.EditIngredient ->
                    IngredientEditorContent(
                        state = ingredientEditorState ?: return@AnimatedContent,
                        products = ingredientProducts,
                        onProductQueryChange = onProductQueryChange,
                        onSortByChange = onSortByChange,
                        onProductClick = onProductClick,
                        onQuantityGramsChange = onQuantityGramsChange,
                        onSaveIngredient = onSaveIngredient,
                        onDeleteIngredient = onDeleteIngredient,
                    )
                MealsNavTarget.EditMeal ->
                    MealEditorContent(
                        editorState = editorState ?: return@AnimatedContent,
                        onNameChange = onEditorNameChange,
                        onCategoryChange = onEditorCategoryChange,
                        onDescriptionChange = onEditorDescriptionChange,
                        onAddIngredientClick = onAddIngredientClick,
                        onEditIngredientClick = onEditIngredientClick,
                        onRemoveIngredient = onRemoveIngredient,
                        onSaveMeal = onSaveMeal,
                    )
                MealsNavTarget.AddMeal ->
                    MealCreatorContent(
                        formState = addMealState ?: return@AnimatedContent,
                        onNameChange = onNameChange,
                        onCategoryChange = onCategoryChange,
                        onDescriptionChange = onDescriptionChange,
                        onAddMeal = onAddMeal,
                    )
                MealsNavTarget.List ->
                    MealsListContent(
                        meals = meals,
                        query = query,
                        onSearchQueryChange = onSearchQueryChange,
                        onMealClick = onMealClick,
                        onRequestDeleteMeal = onRequestDeleteMeal,
                        onOpenAddMeal = onOpenAddMeal,
                    )
            }
        }
    }
}

@Composable
private fun MealsListContent(
    meals: List<Meal>,
    query: String,
    onSearchQueryChange: (String) -> Unit,
    onMealClick: (Meal) -> Unit,
    onRequestDeleteMeal: (Meal) -> Unit,
    onOpenAddMeal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AppSearchBar(
                query = query,
                onQueryChange = onSearchQueryChange,
                placeholder = "Szukaj posiłku",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (meals.isEmpty()) {
            item {
                EmptyMealsState(
                    hasActiveQuery = query.isNotBlank(),
                    onAddClick = onOpenAddMeal,
                )
            }
        } else {
            items(items = meals, key = { it.id }) { meal ->
                SwipeToDeleteContainer(onDeleteRequest = { onRequestDeleteMeal(meal) }) {
                    MealListCard(meal = meal, onEditClick = { onMealClick(meal) })
                }
            }
        }
    }
}

@Composable
private fun EmptyMealsState(
    hasActiveQuery: Boolean,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
        )
        Text(
            text = if (hasActiveQuery) "Brak wyników" else "Brak posiłków",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = if (hasActiveQuery) {
                "Żaden posiłek nie pasuje do wyszukiwanej frazy."
            } else {
                "Dodaj posiłki, przypisz im składniki i śledź wartości odżywcze."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (!hasActiveQuery) {
            Button(onClick = onAddClick) { Text("Dodaj posiłek") }
        }
    }
}

@Composable
private fun MealCreatorContent(
    formState: MealFormState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddMeal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nazwa") },
                singleLine = true,
            )
        }
        item {
            MealCategorySegmentedButton(
                selectedCategory = formState.category,
                onCategoryChange = onCategoryChange,
            )
        }
        item {
            OutlinedTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Opis opcjonalny") },
                minLines = 2,
            )
        }
        formState.errorMessage?.let { item { ErrorText(it) } }
        item {
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
private fun MealEditorContent(
    editorState: MealEditorState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddIngredientClick: () -> Unit,
    onEditIngredientClick: (Int) -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onSaveMeal: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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
                onRemoveIngredient = onRemoveIngredient,
                onAddIngredientClick = onAddIngredientClick,
            )
        }
        editorState.errorMessage?.let { item { ErrorText(it) } }
        item {
            Button(
                onClick = onSaveMeal,
                enabled = !editorState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (editorState.isSaving) "Zapisywanie..." else "Zapisz zmiany")
            }
        }
    }
}

@Composable
private fun IngredientEditorContent(
    state: IngredientEditorState,
    products: List<Product>,
    onProductQueryChange: (String) -> Unit,
    onSortByChange: (ProductSortBy) -> Unit,
    onProductClick: (Product) -> Unit,
    onQuantityGramsChange: (String) -> Unit,
    onSaveIngredient: () -> Unit,
    onDeleteIngredient: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AppSearchBar(
                query = state.productQuery,
                onQueryChange = onProductQueryChange,
                placeholder = "Szukaj produktu",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            ProductSortChips(
                sortBy = state.sortBy,
                onSortByChange = onSortByChange,
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
            Button(onClick = onSaveIngredient, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.isEditing) "Zapisz zmiany" else "Dodaj składnik")
            }
        }
        if (state.isEditing) {
            item {
                OutlinedButton(onClick = onDeleteIngredient, modifier = Modifier.fillMaxWidth()) {
                    Text("Usuń składnik")
                }
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
            OutlinedTextField(
                value = editorState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nazwa") },
                singleLine = true,
            )
            MealCategorySegmentedButton(
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
            NutritionMacroBars(nutrition = editorState.nutrition)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealCategorySegmentedButton(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Typ posiłku",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            MEAL_CATEGORIES.forEachIndexed { index, category ->
                SegmentedButton(
                    selected = selectedCategory == category,
                    onClick = { onCategoryChange(category) },
                    shape = SegmentedButtonDefaults.itemShape(index, MEAL_CATEGORIES.size),
                    icon = { SegmentedButtonDefaults.Icon(active = selectedCategory == category) },
                    label = {
                        Icon(
                            imageVector = mealCategoryIcon(category),
                            contentDescription = category,
                        )
                    },
                )
            }
        }
        Text(
            text = selectedCategory,
            style = MaterialTheme.typography.bodyMedium,
            color = mealCategoryColor(selectedCategory),
        )
    }
}

@Composable
private fun MealListCard(meal: Meal, onEditClick: () -> Unit) {
    val categoryColor = mealCategoryColor(meal.category)
    Card(onClick = onEditClick) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(categoryColor),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = meal.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                    )
                }
                meal.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (meal.ingredients.isEmpty()) {
                    Text(
                        text = "Brak składników",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    NutritionMacroBars(nutrition = meal.nutrition)
                    Text(
                        text = "Składniki: ${meal.ingredients.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientsCard(
    ingredients: List<MealIngredient>,
    onEditIngredient: (Int) -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onAddIngredientClick: () -> Unit,
) {
    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Składniki", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onAddIngredientClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(" Dodaj")
                }
            }
            if (ingredients.isEmpty()) {
                Text(
                    text = "Ten posiłek nie ma jeszcze składników.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            } else {
                HorizontalDivider()
                ingredients.forEachIndexed { index, ingredient ->
                    SwipeToDeleteContainer(onDeleteRequest = { onRemoveIngredient(index) }) {
                        IngredientRow(
                            ingredient = ingredient,
                            onEditClick = { onEditIngredient(index) },
                        )
                    }
                    if (index < ingredients.lastIndex) HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(ingredient: MealIngredient, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = ingredient.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${ingredient.quantityGrams.format()} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            NutritionMacroBars(nutrition = ingredient.nutrition)
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Edytuj składnik",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProductSortChips(
    sortBy: ProductSortBy,
    onSortByChange: (ProductSortBy) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProductSortBy.entries.forEach { sort ->
            FilterChip(
                selected = sortBy == sort,
                onClick = { onSortByChange(sort) },
                label = { Text(sort.label) },
            )
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
            text = "Wpisz nazwę produktu, aby wyszukać.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    Card {
        products.forEachIndexed { index, product ->
            val isSelected = selectedProductId == product.id
            ListItem(
                headlineContent = {
                    Text(product.name, style = MaterialTheme.typography.bodyMedium)
                },
                supportingContent = {
                    Text(
                        text = "${product.caloriesPer100g.format()} kcal / 100 g",
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = productCategoryIcon(product.category),
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
                trailingContent = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Wybrany",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                } else {
                    null
                },
                colors = ListItemDefaults.colors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                ),
                modifier = Modifier.clickable { onProductClick(product) },
            )
            if (index < products.lastIndex) HorizontalDivider()
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

private fun mealCategoryColor(category: String): Color = when (category) {
    "Śniadanie" -> MealColorSniadanie
    "Drugie śniadanie" -> MealColorDrugieSniadanie
    "Obiad" -> MealColorObiad
    "Kolacja" -> MealColorKolacja
    "Podwieczorek" -> MealColorPodwieczorek
    else -> MealColorObiad
}

private fun Double.format(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)
