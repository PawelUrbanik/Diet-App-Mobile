package pl.pawel.diet_app_mobile.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.preferences.ShoppingRangeDates
import pl.pawel.diet_app_mobile.data.preferences.UserPreferencesRepository
import pl.pawel.diet_app_mobile.domain.model.ShoppingListItem
import pl.pawel.diet_app_mobile.domain.repository.ShoppingListRepository

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val currentRange: StateFlow<ShoppingRangeDates?> = userPreferencesRepository.lastShoppingRange
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )
    val groups: StateFlow<List<ShoppingGroup>> = shoppingListRepository.observeItems()
        .map { items -> items.toGroups() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _generateDialog = MutableStateFlow<GenerateDialogState?>(null)
    val generateDialog: StateFlow<GenerateDialogState?> = _generateDialog.asStateFlow()

    private val _manualDialog = MutableStateFlow<ManualDialogState?>(null)
    val manualDialog: StateFlow<ManualDialogState?> = _manualDialog.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun openGenerateDialog() {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        _generateDialog.value = GenerateDialogState(
            selectedRange = ShoppingRange.CurrentWeek,
            customStart = monday,
            customEnd = monday.plusDays(6),
        )
    }

    fun closeGenerateDialog() {
        _generateDialog.value = null
    }

    fun onRangeChange(range: ShoppingRange) {
        _generateDialog.value = _generateDialog.value?.copy(selectedRange = range)
    }

    fun onCustomStartChange(date: LocalDate) {
        _generateDialog.update { state ->
            if (state == null) return@update null
            val newEnd = if (state.customEnd.isBefore(date)) date else state.customEnd
            state.copy(customStart = date, customEnd = newEnd)
        }
    }

    fun onCustomEndChange(date: LocalDate) {
        _generateDialog.update { state ->
            if (state == null) return@update null
            val newStart = if (date.isBefore(state.customStart)) date else state.customStart
            state.copy(customStart = newStart, customEnd = date)
        }
    }

    fun confirmGenerate() {
        val dialog = _generateDialog.value ?: return
        val (start, end) = if (dialog.selectedRange == ShoppingRange.Custom) {
            dialog.customStart to dialog.customEnd
        } else {
            dialog.selectedRange.resolve(LocalDate.now())
        }
        if (end.isBefore(start)) {
            _message.value = "Data „do\" jest wcześniejsza niż data „od\"."
            return
        }
        _generateDialog.value = null
        viewModelScope.launch {
            _isGenerating.value = true
            runCatching { shoppingListRepository.generateFromPlan(start, end) }
                .onSuccess { count ->
                    userPreferencesRepository.setLastShoppingRange(start, end)
                    _message.value = if (count == 0) {
                        "Brak zaplanowanych posiłków w wybranym zakresie."
                    } else {
                        "Zaktualizowano listę ($count produktów)."
                    }
                }
                .onFailure { _message.value = "Nie udało się wygenerować listy." }
            _isGenerating.value = false
        }
    }

    fun toggleChecked(item: ShoppingListItem) {
        viewModelScope.launch {
            runCatching { shoppingListRepository.setChecked(item.id, !item.isChecked) }
        }
    }

    fun removeItem(itemId: Long) {
        viewModelScope.launch {
            runCatching { shoppingListRepository.removeItem(itemId) }
        }
    }

    fun openManualDialog() {
        _manualDialog.value = ManualDialogState()
    }

    fun closeManualDialog() {
        _manualDialog.value = null
    }

    fun onManualNameChange(value: String) {
        _manualDialog.value = _manualDialog.value?.copy(name = value, errorMessage = null)
    }

    fun confirmManual() {
        val dialog = _manualDialog.value ?: return
        val name = dialog.name.trim()
        if (name.isBlank()) {
            _manualDialog.value = dialog.copy(errorMessage = "Podaj nazwę.")
            return
        }
        _manualDialog.value = null
        viewModelScope.launch {
            runCatching { shoppingListRepository.addManualItem(name = name, category = "Inne") }
        }
    }

    fun clearChecked() {
        viewModelScope.launch { runCatching { shoppingListRepository.clearChecked() } }
    }

    fun clearAll() {
        viewModelScope.launch {
            runCatching { shoppingListRepository.clearAll() }
            userPreferencesRepository.clearLastShoppingRange()
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

data class ShoppingGroup(
    val category: String,
    val items: List<ShoppingListItem>,
)

data class GenerateDialogState(
    val selectedRange: ShoppingRange,
    val customStart: LocalDate,
    val customEnd: LocalDate,
)

data class ManualDialogState(
    val name: String = "",
    val errorMessage: String? = null,
)

enum class ShoppingRange(val label: String) {
    CurrentWeek("Bieżący tydzień"),
    NextWeek("Następny tydzień"),
    Next7Days("Najbliższe 7 dni"),
    Today("Tylko dziś"),
    Custom("Własny zakres"),
    ;

    fun resolve(today: LocalDate): Pair<LocalDate, LocalDate> = when (this) {
        CurrentWeek -> {
            val monday = today.with(DayOfWeek.MONDAY)
            monday to monday.plusDays(6)
        }
        NextWeek -> {
            val monday = today.with(DayOfWeek.MONDAY).plusWeeks(1)
            monday to monday.plusDays(6)
        }
        Next7Days -> today to today.plusDays(6)
        Today -> today to today
        Custom -> today to today
    }
}

private fun List<ShoppingListItem>.toGroups(): List<ShoppingGroup> =
    groupBy { it.category.ifBlank { "Inne" } }
        .toSortedMap(compareBy { it.lowercase() })
        .map { (category, items) ->
            ShoppingGroup(
                category = category,
                items = items.sortedWith(
                    compareBy({ it.isChecked }, { it.name.lowercase() }),
                ),
            )
        }
