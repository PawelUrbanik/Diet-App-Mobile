package pl.pawel.diet_app_mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pl.pawel.diet_app_mobile.data.local.seed.MealSeeder

@HiltAndroidApp
class DietAppApplication : Application() {
    @Inject
    lateinit var mealSeeder: MealSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Seedowanie bazą startową niezależnie od tego, który ekran użytkownik otworzy
        // jako pierwszy (seedMissingMeals seeduje też produkty). Jest idempotentne.
        applicationScope.launch {
            runCatching { mealSeeder.seedMissingMeals() }
        }
    }
}
