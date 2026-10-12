package co.edu.iudigital.radio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.iudigital.radio.ui.screens.PremiumMainScreen
import co.edu.iudigital.radio.ui.screens.PremiumProfileScreen
import co.edu.iudigital.radio.ui.theme.IUDigitalRadioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IUDigitalRadioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") { PremiumMainScreen(navController) }
                        composable("profile") { PremiumProfileScreen(navController) }
                    }
                }
            }
        }
    }
}
