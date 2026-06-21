package pl.pawel.diet_app_mobile.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    private companion object {
        val KEY_SHOPPING_RANGE_START = stringPreferencesKey("shopping_range_start")
        val KEY_SHOPPING_RANGE_END = stringPreferencesKey("shopping_range_end")
    }
}
