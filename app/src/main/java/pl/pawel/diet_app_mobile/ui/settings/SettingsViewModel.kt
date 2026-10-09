package pl.pawel.diet_app_mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.preferences.UserPreferencesRepository
import pl.pawel.diet_app_mobile.domain.model.MealReminder
import pl.pawel.diet_app_mobile.notifications.MealReminderNotifier
import pl.pawel.diet_app_mobile.notifications.MealReminderScheduler

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val scheduler: MealReminderScheduler,
    private val notifier: MealReminderNotifier,
) : ViewModel() {

    val reminders: StateFlow<List<MealReminder>> = preferences.mealReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _canPostNotifications = MutableStateFlow(notifier.canPostNotifications())
    val canPostNotifications: StateFlow<Boolean> = _canPostNotifications.asStateFlow()

    private val _exactAlarmsAllowed = MutableStateFlow(scheduler.canScheduleExactAlarms())
    val exactAlarmsAllowed: StateFlow<Boolean> = _exactAlarmsAllowed.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    /** Wywoływane po powrocie na ekran — użytkownik mógł zmienić uprawnienia w ustawieniach systemu. */
    fun refreshPermissions() {
        _canPostNotifications.value = notifier.canPostNotifications()
        val exactAllowed = scheduler.canScheduleExactAlarms()
        if (exactAllowed != _exactAlarmsAllowed.value) {
            _exactAlarmsAllowed.value = exactAllowed
            viewModelScope.launch { scheduler.scheduleAll(preferences.mealReminders.first()) }
        }
    }

    fun setEnabled(mealType: String, enabled: Boolean) = update(mealType) { copy(enabled = enabled) }

    fun setTime(mealType: String, time: LocalTime) = update(mealType) { copy(time = time) }

    fun sendTestNotification() {
        viewModelScope.launch {
            val current = preferences.mealReminders.first()
            val mealType = (current.firstOrNull { it.enabled } ?: current.first()).mealType
            val sent = notifier.notifyMeal(mealType, showWhenEmpty = true)
            if (!sent) _message.value = "Nie udało się wysłać — sprawdź zgodę na powiadomienia."
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private fun update(mealType: String, transform: MealReminder.() -> MealReminder) {
        viewModelScope.launch {
            // Czytamy aktualny stan z DataStore, nie z UI — stateIn może jeszcze nie mieć wartości.
            val reminder = preferences.mealReminders.first()
                .firstOrNull { it.mealType == mealType }
                ?: return@launch
            val updated = reminder.transform()
            preferences.setMealReminder(updated)
            scheduler.schedule(updated)
        }
    }
}
