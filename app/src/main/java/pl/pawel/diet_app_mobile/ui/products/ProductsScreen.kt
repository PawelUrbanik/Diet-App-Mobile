package pl.pawel.diet_app_mobile.ui.products

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.pawel.diet_app_mobile.domain.model.NutritionSummary
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.ui.components.AppSearchBar
import pl.pawel.diet_app_mobile.ui.components.NutritionMacroBars
import pl.pawel.diet_app_mobile.ui.components.SwipeToDeleteContainer
import pl.pawel.diet_app_mobile.ui.components.productCategoryIcon

private enum class ProductsNavTarget(val depth: Int) {
    List(0), Editor(1)
}

@Composable
fun ProductsRoute(
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val products by viewModel.products.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    val query by viewModel.query.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val productPendingDelete by viewModel.productPendingDelete.collectAsState()

    ProductsScreen(
        products = products,
        query = query,
        sortBy = sortBy,
        editorState = editorState,
        productPendingDelete = productPendingDelete,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortByChange = viewModel::onSortByChange,
        onOpenAddProduct = viewModel::openAddProduct,
        onProductClick = viewModel::openEditProduct,
        onCloseEditor = viewModel::closeEditor,
        onNameChange = viewModel::onNameChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onProteinChange = viewModel::onProteinChange,
        onFatChange = viewModel::onFatChange,
        onCarbsChange = viewModel::onCarbsChange,
        onSaveProduct = viewModel::saveProduct,
        onDeleteProductFromEditor = viewModel::deleteProductFromEditor,
        onRequestDeleteProduct = viewModel::requestDeleteProduct,
        onConfirmDeleteProduct = viewModel::confirmDeleteProduct,
        onCancelDeleteProduct = viewModel::cancelDeleteProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    products: List<Product>,
    query: String,
    sortBy: ProductSortBy,
    editorState: ProductFormState?,
    productPendingDelete: Product?,
    onSearchQueryChange: (String) -> Unit,
    onSortByChange: (ProductSortBy) -> Unit,
    onOpenAddProduct: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCloseEditor: () -> Unit,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onSaveProduct: () -> Unit,
    onDeleteProductFromEditor: () -> Unit,
    onRequestDeleteProduct: (Product) -> Unit,
    onConfirmDeleteProduct: () -> Unit,
    onCancelDeleteProduct: () -> Unit,
) {
    productPendingDelete?.let { product ->
        AlertDialog(
            onDismissRequest = onCancelDeleteProduct,
            title = { Text("Usunąć produkt?") },
            text = { Text("Produkt \"${product.name}\" zostanie usunięty z bazy produktów.") },
            confirmButton = { TextButton(onClick = onConfirmDeleteProduct) { Text("Usuń") } },
            dismissButton = { TextButton(onClick = onCancelDeleteProduct) { Text("Anuluj") } },
        )
    }

    val navTarget = if (editorState != null) ProductsNavTarget.Editor else ProductsNavTarget.List

    val title = when {
        editorState != null && editorState.isEditing -> "Edycja produktu"
        editorState != null -> "Nowy produkt"
        else -> "Produkty"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (editorState != null) {
                        IconButton(onClick = onCloseEditor) {
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
            if (editorState == null) {
                FloatingActionButton(onClick = onOpenAddProduct) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj produkt")
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
            label = "products_nav",
            modifier = Modifier.padding(innerPadding),
        ) { target ->
            when (target) {
                ProductsNavTarget.Editor ->
                    ProductEditorContent(
                        formState = editorState ?: return@AnimatedContent,
                        onNameChange = onNameChange,
                        onCaloriesChange = onCaloriesChange,
                        onProteinChange = onProteinChange,
                        onFatChange = onFatChange,
                        onCarbsChange = onCarbsChange,
                        onSaveProduct = onSaveProduct,
                        onDeleteProduct = onDeleteProductFromEditor,
                    )
                ProductsNavTarget.List ->
                    ProductsListContent(
                        products = products,
                        query = query,
                        sortBy = sortBy,
                        onSearchQueryChange = onSearchQueryChange,
                        onSortByChange = onSortByChange,
                        onOpenAddProduct = onOpenAddProduct,
                        onProductClick = onProductClick,
                        onRequestDeleteProduct = onRequestDeleteProduct,
                    )
            }
        }
    }
}

@Composable
private fun ProductsListContent(
    products: List<Product>,
    query: String,
    sortBy: ProductSortBy,
    onSearchQueryChange: (String) -> Unit,
    onSortByChange: (ProductSortBy) -> Unit,
    onOpenAddProduct: () -> Unit,
    onProductClick: (Product) -> Unit,
    onRequestDeleteProduct: (Product) -> Unit,
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
                placeholder = "Szukaj produktu",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
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

        if (products.isEmpty()) {
            item {
                EmptyProductsState(
                    hasActiveQuery = query.isNotBlank(),
                    onAddClick = onOpenAddProduct,
                )
            }
        } else {
            items(
                items = products,
                key = { product -> product.id },
            ) { product ->
                SwipeToDeleteContainer(onDeleteRequest = { onRequestDeleteProduct(product) }) {
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyProductsState(
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
            imageVector = Icons.Filled.LocalGroceryStore,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
        )
        Text(
            text = if (hasActiveQuery) "Brak wyników" else "Brak produktów",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = if (hasActiveQuery) {
                "Żaden produkt nie pasuje do wyszukiwanej frazy."
            } else {
                "Dodaj produkty, aby budować posiłki i śledzić wartości odżywcze."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (!hasActiveQuery) {
            Button(onClick = onAddClick) { Text("Dodaj produkt") }
        }
    }
}

@Composable
private fun ProductEditorContent(
    formState: ProductFormState,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onSaveProduct: () -> Unit,
    onDeleteProduct: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Wartości podawaj w przeliczeniu na 100 g.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
            NutritionFields(
                formState = formState,
                onCaloriesChange = onCaloriesChange,
                onProteinChange = onProteinChange,
                onFatChange = onFatChange,
                onCarbsChange = onCarbsChange,
            )
        }
        formState.errorMessage?.let { message ->
            item {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            Button(
                onClick = onSaveProduct,
                enabled = !formState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        formState.isSaving -> "Zapisywanie..."
                        formState.isEditing -> "Zapisz zmiany"
                        else -> "Dodaj produkt"
                    },
                )
            }
        }
        if (formState.isEditing) {
            item {
                OutlinedButton(
                    onClick = onDeleteProduct,
                    enabled = !formState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Usuń produkt")
                }
            }
        }
    }
}

@Composable
private fun NutritionFields(
    formState: ProductFormState,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DecimalTextField(
                value = formState.calories,
                onValueChange = onCaloriesChange,
                label = "kcal",
                modifier = Modifier.weight(1f),
            )
            DecimalTextField(
                value = formState.protein,
                onValueChange = onProteinChange,
                label = "Białko",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DecimalTextField(
                value = formState.fat,
                onValueChange = onFatChange,
                label = "Tłuszcz",
                modifier = Modifier.weight(1f),
            )
            DecimalTextField(
                value = formState.carbs,
                onValueChange = onCarbsChange,
                label = "Węgle",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DecimalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
) {
    val nutrition = NutritionSummary(
        calories = product.caloriesPer100g,
        protein = product.proteinPer100g,
        fat = product.fatPer100g,
        carbs = product.carbsPer100g,
    )
    Card(onClick = onClick) {
        ListItem(
            headlineContent = {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
            },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${product.category} · na 100 g",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    NutritionMacroBars(nutrition = nutrition)
                }
            },
            leadingContent = {
                Icon(
                    imageVector = productCategoryIcon(product.category),
                    contentDescription = product.category,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )
    }
}
