package pl.pawel.diet_app_mobile.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import pl.pawel.diet_app_mobile.MainActivity
import pl.pawel.diet_app_mobile.R
import pl.pawel.diet_app_mobile.domain.model.PlannedMeal
import pl.pawel.diet_app_mobile.domain.repository.MealPlanRepository

@Singleton
class MealReminderNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val mealPlanRepository: MealPlanRepository,
) {
    fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Przypomnienia o posiłkach",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Powiadomienia o godzinie posiłku z dzisiejszego planu"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun canPostNotifications(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED &&
            NotificationManagerCompat.from(context).areNotificationsEnabled()

    /**
     * Pokazuje przypomnienie z posiłkami zaplanowanymi na [date] w slocie [mealType].
     * Pusty slot i slot oznaczony „jem na mieście" są pomijane, chyba że [showWhenEmpty]
     * (powiadomienie testowe z ustawień). Zwraca true, jeśli powiadomienie zostało wysłane.
     */
    suspend fun notifyMeal(
        mealType: String,
        date: LocalDate = LocalDate.now(),
        showWhenEmpty: Boolean = false,
    ): Boolean {
        if (!canPostNotifications()) return false

        val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val plannedMeals = mealPlanRepository.observeWeekPlan(weekStart).first()
            .days
            .firstOrNull { it.date == date }
            ?.mealsForCategory(mealType)
            ?.filterNot { it.skipped }
            .orEmpty()
        if (plannedMeals.isEmpty() && !showWhenEmpty) return false

        val title = "Czas na: ${mealType.lowercase(POLISH)}"
        val lines = plannedMeals.map { it.describe() }
        val text = lines.joinToString("\n").ifEmpty { "Na dziś nic nie zaplanowano w tym posiłku." }

        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_meal)
            .setContentTitle(title)
            .setContentText(lines.firstOrNull() ?: text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(reminderId(mealType), notification)
        } catch (_: SecurityException) {
            return false
        }
        return true
    }

    private fun PlannedMeal.describe(): String {
        val name = meal.name.lowercase(POLISH).replaceFirstChar { it.titlecase(POLISH) }
        val servingsLabel = if (servings == 1.0) "" else " (${servings.formatServings()} porcji)"
        return "$name$servingsLabel · ${nutrition.calories.toInt()} kcal"
    }

    private fun Double.formatServings(): String =
        if (this % 1.0 == 0.0) toInt().toString() else toString().replace('.', ',')

    private companion object {
        const val CHANNEL_ID = "meal_reminders"
        val POLISH: Locale = Locale.forLanguageTag("pl")
    }
}
