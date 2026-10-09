package pl.pawel.diet_app_mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.local.seed.MealSeeder
import pl.pawel.diet_app_mobile.data.preferences.UserPreferencesRepository
import pl.pawel.diet_app_mobile.notifications.MealReminderNotifier
import pl.pawel.diet_app_mobile.notifications.MealReminderScheduler

@HiltAndroidApp
class DietAppApplication : Application() {
    @Inject
    lateinit var mealSeeder: MealSeeder

    @Inject
    lateinit var preferences: UserPreferencesRepository

    @Inject
    lateinit var reminderScheduler: MealReminderScheduler

    @Inject
    lateinit var reminderNotifier: MealReminderNotifier

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        reminderNotifier.createChannel()
        // Seedowanie bazą startową niezależnie od tego, który ekran użytkownik otworzy
        // jako pierwszy (seedMissingMeals seeduje też produkty). Jest idempotentne.
        applicationScope.launch {
            runCatching { mealSeeder.seedMissingMeals() }
        }
        // Alarmy znikają m.in. po wymuszonym zatrzymaniu aplikacji — odtwórz je przy starcie.
        applicationScope.launch {
            runCatching { reminderScheduler.scheduleAll(preferences.mealReminders.first()) }
        }
    }
}
