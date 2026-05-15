package pl.pawel.diet_app_mobile.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pl.pawel.diet_app_mobile.domain.model.Product

@Composable
fun ProductsRoute(
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val products by viewModel.products.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    val query by viewModel.query.collectAsState()

    ProductsScreen(
        products = products,
        query = query,
        editorState = editorState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
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
        onDeleteProduct = viewModel::deleteProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    products: List<Product>,
    query: String,
    editorState: ProductFormState?,
    onSearchQueryChange: (String) -> Unit,
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
    onDeleteProduct: (Product) -> Unit,
) {
    var productPendingDelete by remember { mutableStateOf<Product?>(null) }

    productPendingDelete?.let { product ->
        DeleteProductDialog(
            product = product,
            onDismiss = { productPendingDelete = null },
            onConfirm = {
                onDeleteProduct(product)
                productPendingDelete = null
            },
        )
    }

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
                        TextButton(onClick = onCloseEditor) {
                            Text("Wróć")
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
        if (editorState != null) {
            ProductEditorContent(
                formState = editorState,
                modifier = Modifier.padding(innerPadding),
                onNameChange = onNameChange,
                onCaloriesChange = onCaloriesChange,
                onProteinChange = onProteinChange,
                onFatChange = onFatChange,
                onCarbsChange = onCarbsChange,
                onSaveProduct = onSaveProduct,
                onDeleteProduct = onDeleteProductFromEditor,
                onCancel = onCloseEditor,
            )
        } else {
            ProductsListContent(
                products = products,
                query = query,
                modifier = Modifier.padding(innerPadding),
                onSearchQueryChange = onSearchQueryChange,
                onOpenAddProduct = onOpenAddProduct,
                onProductClick = onProductClick,
                onProductDeleteClick = { productPendingDelete = it },
            )
        }
    }
}

@Composable
private fun ProductsListContent(
    products: List<Product>,
    query: String,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit,
    onOpenAddProduct: () -> Unit,
    onProductClick: (Product) -> Unit,
    onProductDeleteClick: (Product) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Baza produktów",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Szukaj produktu") },
                singleLine = true,
            )
        }

        if (products.isEmpty()) {
            item {
                EmptyProductsCard(hasActiveQuery = query.isNotBlank())
            }
        } else {
            items(
                items = products,
                key = { product -> product.id },
            ) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onDeleteClick = { onProductDeleteClick(product) },
                )
            }
        }
    }
}

@Composable
private fun ProductEditorContent(
    formState: ProductFormState,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onSaveProduct: () -> Unit,
    onDeleteProduct: () -> Unit,
    onCancel: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
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
        item {
            TextButton(
                onClick = onCancel,
                enabled = !formState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Anuluj")
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
private fun EmptyProductsCard(hasActiveQuery: Boolean) {
    Card {
        Text(
            text = if (hasActiveQuery) {
                "Brak produktów pasujących do wyszukiwania."
            } else {
                "Brak produktów. Dodaj pierwszy produkt, żeby później budować posiłki i liczyć makroskładniki."
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
private fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${product.caloriesPer100g.format()} kcal / 100 g",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "B: ${product.proteinPer100g.format()} g | T: ${product.fatPer100g.format()} g | W: ${product.carbsPer100g.format()} g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Edytuj")
                }
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Usuń")
                }
            }
        }
    }
}

@Composable
private fun DeleteProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Usunąć produkt?") },
        text = {
            Text(
                text = "Produkt \"${product.name}\" zostanie usunięty z bazy produktów.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Usuń")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
    )
}

private fun Double.format(): String =
    if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        "%.1f".format(this)
    }
