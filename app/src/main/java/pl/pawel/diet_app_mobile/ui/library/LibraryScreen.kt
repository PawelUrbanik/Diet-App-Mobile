package pl.pawel.diet_app_mobile.ui.library

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pl.pawel.diet_app_mobile.ui.meals.MealsRoute
import pl.pawel.diet_app_mobile.ui.products.ProductsRoute

private enum class LibrarySection(val label: String) {
    Meals("Posiłki"),
    Products("Produkty"),
}

private val LibrarySectionSaver: Saver<LibrarySection, String> =
    Saver(save = { it.name }, restore = { LibrarySection.valueOf(it) })

/**
 * Wspólny „magazyn" rzadko zmienianych danych: Posiłki i Produkty.
 * Przełącznik sekcji żyje w pasku górnym danego ekranu (slot tytułu),
 * dzięki czemu nie ma dwóch pasków ani zdublowanego nagłówka.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryRoute() {
    var section by rememberSaveable(stateSaver = LibrarySectionSaver) {
        mutableStateOf(LibrarySection.Meals)
    }

    val sectionSwitcher: @Composable () -> Unit = {
        SingleChoiceSegmentedButtonRow {
            LibrarySection.entries.forEachIndexed { index, entry ->
                SegmentedButton(
                    selected = section == entry,
                    onClick = { section = entry },
                    shape = SegmentedButtonDefaults.itemShape(index, LibrarySection.entries.size),
                ) {
                    Text(entry.label)
                }
            }
        }
    }

    when (section) {
        LibrarySection.Meals -> MealsRoute(listHeader = sectionSwitcher)
        LibrarySection.Products -> ProductsRoute(listHeader = sectionSwitcher)
    }
}
