package pl.pawel.diet_app_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pl.pawel.diet_app_mobile.ui.AppRoot
import pl.pawel.diet_app_mobile.ui.theme.DietAppMobileTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DietAppMobileTheme {
                AppRoot()
            }
        }
    }
}
