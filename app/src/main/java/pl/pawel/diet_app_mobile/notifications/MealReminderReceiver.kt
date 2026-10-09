package pl.pawel.diet_app_mobile.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.preferences.UserPreferencesRepository

/** Odpala się o godzinie posiłku: pokazuje powiadomienie i planuje alarm na kolejny dzień. */
@AndroidEntryPoint
class MealReminderReceiver : BroadcastReceiver() {
    @Inject
    lateinit var preferences: UserPreferencesRepository

    @Inject
    lateinit var scheduler: MealReminderScheduler

    @Inject
    lateinit var notifier: MealReminderNotifier

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_MEAL_REMINDER) return
        val mealType = intent.getStringExtra(EXTRA_MEAL_TYPE) ?: return

        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                val reminder = preferences.mealReminders.first()
                    .firstOrNull { it.mealType == mealType }
                    ?: return@launch
                if (reminder.enabled) {
                    runCatching { notifier.notifyMeal(mealType) }
                }
                scheduler.schedule(reminder)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_MEAL_REMINDER = "pl.pawel.diet_app_mobile.action.MEAL_REMINDER"
        const val EXTRA_MEAL_TYPE = "meal_type"
    }
}

/** Przywraca alarmy po restarcie urządzenia, zmianie czasu lub zgody na dokładne alarmy. */
@AndroidEntryPoint
class MealReminderRescheduleReceiver : BroadcastReceiver() {
    @Inject
    lateinit var preferences: UserPreferencesRepository

    @Inject
    lateinit var scheduler: MealReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                scheduler.scheduleAll(preferences.mealReminders.first())
            } finally {
                pendingResult.finish()
            }
        }
    }
}

private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
