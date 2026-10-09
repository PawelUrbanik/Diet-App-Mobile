package pl.pawel.diet_app_mobile.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import pl.pawel.diet_app_mobile.domain.model.MealReminder
import pl.pawel.diet_app_mobile.domain.model.MealReminderDefaults

/**
 * Planuje codzienne alarmy przypomnień. Każdy typ posiłku ma jeden alarm na najbliższe
 * wystąpienie godziny; po jego odpaleniu [MealReminderReceiver] planuje kolejny dzień.
 */
@Singleton
class MealReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)

    /** Dokładne alarmy wymagają zgody użytkownika; bez niej system może je opóźnić. */
    fun canScheduleExactAlarms(): Boolean = alarmManager.canScheduleExactAlarms()

    fun scheduleAll(reminders: List<MealReminder>) {
        reminders.forEach(::schedule)
    }

    fun schedule(reminder: MealReminder) {
        val pendingIntent = pendingIntentFor(reminder.mealType)
        if (!reminder.enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }
        val triggerAtMillis = nextTriggerTime(reminder.time, ZonedDateTime.now())
            .toInstant()
            .toEpochMilli()
        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } else {
            // setAndAllowWhileIdle dostaje od systemu okno do godziny — setWindow trzyma się
            // ~10 min (minimum systemowe), kosztem możliwego opóźnienia w trybie Doze.
            alarmManager.setWindow(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                INEXACT_WINDOW_MILLIS,
                pendingIntent,
            )
        }
    }

    private fun pendingIntentFor(mealType: String): PendingIntent {
        val intent = Intent(context, MealReminderReceiver::class.java)
            .setAction(MealReminderReceiver.ACTION_MEAL_REMINDER)
            .putExtra(MealReminderReceiver.EXTRA_MEAL_TYPE, mealType)
        return PendingIntent.getBroadcast(
            context,
            reminderId(mealType),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val INEXACT_WINDOW_MILLIS = 10 * 60 * 1000L
    }
}

/** Stały identyfikator typu posiłku — używany jako requestCode alarmu i id powiadomienia. */
internal fun reminderId(mealType: String): Int =
    MealReminderDefaults.ALL.indexOfFirst { it.mealType == mealType }.coerceAtLeast(0) + 1

/** Najbliższe wystąpienie [time] ściśle po [now] (dziś albo jutro). */
internal fun nextTriggerTime(time: LocalTime, now: ZonedDateTime): ZonedDateTime {
    val today = now.with(time).withSecond(0).withNano(0)
    return if (today.isAfter(now)) today else today.plusDays(1)
}
