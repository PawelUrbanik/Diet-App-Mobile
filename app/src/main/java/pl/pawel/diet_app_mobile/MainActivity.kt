package pl.pawel.diet_app_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import pl.pawel.diet_app_mobile.ui.AppRoot
import pl.pawel.diet_app_mobile.ui.BrandSplash
import pl.pawel.diet_app_mobile.ui.theme.DietAppMobileTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DietAppMobileTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
                }
                Crossfade(targetState = showSplash, label = "app_splash") { splash ->
                    if (splash) BrandSplash() else AppRoot()
                }
            }
        }
    }
}
