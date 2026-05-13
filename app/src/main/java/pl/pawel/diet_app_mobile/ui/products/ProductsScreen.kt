package pl.pawel.diet_app_mobile.ui.products

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val formState by viewModel.formState.collectAsState()

    ProductsScreen(
        products = products,
        formState = formState,
        onNameChange = viewModel::onNameChange,
        onCaloriesChange = viewModel::onCaloriesChange,
        onProteinChange = viewModel::onProteinChange,
        onFatChange = viewModel::onFatChange,
        onCarbsChange = viewModel::onCarbsChange,
        onAddProduct = viewModel::addProduct,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsScreen(
    products: List<Product>,
    formState: ProductFormState,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onAddProduct: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Produkty") },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ProductFormCard(
                    formState = formState,
                    onNameChange = onNameChange,
                    onCaloriesChange = onCaloriesChange,
                    onProteinChange = onProteinChange,
                    onFatChange = onFatChange,
                    onCarbsChange = onCarbsChange,
                    onAddProduct = onAddProduct,
                )
            }

            item {
                Text(
                    text = "Baza produktów",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (products.isEmpty()) {
                item {
                    EmptyProductsCard()
                }
            } else {
                items(
                    items = products,
                    key = { product -> product.id },
                ) { product ->
                    ProductCard(product = product)
                }
            }
        }
    }
}

@Composable
private fun ProductFormCard(
    formState: ProductFormState,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onAddProduct: () -> Unit,
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
            Text(
                text = "Dodaj produkt",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Wartości podawaj w przeliczeniu na 100 g.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = formState.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nazwa") },
                singleLine = true,
            )
            NutritionFields(
                formState = formState,
                onCaloriesChange = onCaloriesChange,
                onProteinChange = onProteinChange,
                onFatChange = onFatChange,
                onCarbsChange = onCarbsChange,
            )
            formState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Button(
                onClick = onAddProduct,
                enabled = !formState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (formState.isSaving) "Zapisywanie..." else "Dodaj produkt")
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
private fun EmptyProductsCard() {
    Card {
        Text(
            text = "Brak produktów. Dodaj pierwszy produkt, żeby później budować posiłki i liczyć makroskładniki.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProductCard(product: Product) {
    Card {
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
                text = "${product.caloriesPer100g.format()} kcal / 100 g",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "B: ${product.proteinPer100g.format()} g | T: ${product.fatPer100g.format()} g | W: ${product.carbsPer100g.format()} g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun Double.format(): String =
    if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        "%.1f".format(this)
    }
