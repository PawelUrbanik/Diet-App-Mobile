package pl.pawel.diet_app_mobile.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.pawel.diet_app_mobile.domain.model.MealReminder
import pl.pawel.diet_app_mobile.domain.model.MealReminderDefaults

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class ShoppingRangeDates(val start: LocalDate, val end: LocalDate)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val lastShoppingRange: Flow<ShoppingRangeDates?> = context.dataStore.data.map { prefs ->
        val start = prefs[KEY_SHOPPING_RANGE_START]?.let(LocalDate::parse)
        val end = prefs[KEY_SHOPPING_RANGE_END]?.let(LocalDate::parse)
        if (start != null && end != null) ShoppingRangeDates(start, end) else null
    }

    suspend fun setLastShoppingRange(start: LocalDate, end: LocalDate) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOPPING_RANGE_START] = start.toString()
            prefs[KEY_SHOPPING_RANGE_END] = end.toString()
        }
    }

    suspend fun clearLastShoppingRange() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_SHOPPING_RANGE_START)
            prefs.remove(KEY_SHOPPING_RANGE_END)
        }
    }

    val mealReminders: Flow<List<MealReminder>> = context.dataStore.data.map { prefs ->
        MealReminderDefaults.ALL.map { default ->
            val key = REMINDER_KEYS.getValue(default.mealType)
            MealReminder(
                mealType = default.mealType,
                enabled = prefs[booleanPreferencesKey("reminder_${key}_enabled")] ?: default.enabled,
                time = prefs[intPreferencesKey("reminder_${key}_minute")]
                    ?.let { minute -> LocalTime.of(minute / 60, minute % 60) }
                    ?: default.time,
            )
        }
    }

    suspend fun setMealReminder(reminder: MealReminder) {
        val key = REMINDER_KEYS.getValue(reminder.mealType)
        context.dataStore.edit { prefs ->
            prefs[booleanPreferencesKey("reminder_${key}_enabled")] = reminder.enabled
            prefs[intPreferencesKey("reminder_${key}_minute")] =
                reminder.time.hour * 60 + reminder.time.minute
        }
    }

    private companion object {
        val KEY_SHOPPING_RANGE_START = stringPreferencesKey("shopping_range_start")
        val KEY_SHOPPING_RANGE_END = stringPreferencesKey("shopping_range_end")

        // Stabilne klucze ASCII — nazwy typów posiłków mogą się zmienić w UI.
        val REMINDER_KEYS = mapOf(
            "Śniadanie" to "breakfast",
            "Drugie śniadanie" to "second_breakfast",
            "Obiad" to "lunch",
            "Podwieczorek" to "snack",
            "Kolacja" to "dinner",
        )
    }
}
