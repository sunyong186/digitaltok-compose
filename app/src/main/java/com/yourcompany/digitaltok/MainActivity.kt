package com.yourcompany.digitaltok

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.IntentCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.auth.AuthStartScreen
import com.yourcompany.digitaltok.ui.auth.PasswordResetScreen
import com.yourcompany.digitaltok.ui.auth.SignupScreen
import com.yourcompany.digitaltok.ui.device.NfcViewModel
import com.yourcompany.digitaltok.ui.home.HomeScreen
import com.yourcompany.digitaltok.ui.onboarding.OnboardingPrefs
import com.yourcompany.digitaltok.ui.onboarding.OnboardingScreen
import com.yourcompany.digitaltok.ui.theme.DigitalTokTheme
import kotlinx.coroutines.delay
import com.yourcompany.digitaltok.ui.theme.*

class MainActivity : AppCompatActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val mainUiViewModel: MainUiViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        enableEdgeToEdge()

        setContent {
            DigitalTokTheme {
                AppEntry(
                    mainViewModel = mainViewModel,
                    mainUiViewModel = mainUiViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TAG_DISCOVERED
        ) {
            Log.d("NFC", "NFC Tag Intent received")
            val tag = IntentCompat.getParcelableExtra(intent, NfcAdapter.EXTRA_TAG, Tag::class.java)
            if (tag != null) {
                nfcViewModel.onTagDiscovered(tag)
            }
        }
    }
}

@Composable
private fun AppEntry(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel
) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1800)
        showSplash = false
    }

    if (showSplash) {
        SplashLanding()
    } else {
        AppNavHost(
            mainViewModel = mainViewModel,
            mainUiViewModel = mainUiViewModel
        )
    }
}

@Composable
private fun SplashLanding() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = painterResource(id = R.drawable.diringlogo),
                contentDescription = null,
                modifier = Modifier.size(width = 48.dp, height = 75.dp)
            )

            Spacer(modifier = Modifier.height(37.dp))

            Text(
                text = "DiRing",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "내 마음대로 꾸미는 나만의 키링",
                fontSize = 20.sp,
                color = DtTextGray2
            )
        }
    }
}

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val isOnboardingDone = OnboardingPrefs.isDone(context)

    AppNavHostContent(
        navController = navController,
        isOnboardingDone = isOnboardingDone,
        onOnboardingFinish = {
            // 온보딩 완료 저장
            OnboardingPrefs.setDone(context)

            navController.navigate("login") {
                popUpTo("onboarding") { inclusive = true }
            }
        },
        homeScreen = {
            HomeScreen(mainViewModel = mainViewModel, mainUiViewModel = mainUiViewModel)
        }
    )
}

@Composable
private fun AppNavHostContent(
    navController: NavHostController,
    isOnboardingDone: Boolean,
    onOnboardingFinish: () -> Unit,
    homeScreen: @Composable () -> Unit
) {
    // 온보딩 1회만: 완료했으면 login부터 시작
    val startDestination = if (isOnboardingDone) "login" else "onboarding"

    NavHost(navController, startDestination = startDestination) {

        composable("onboarding") {
            OnboardingScreen(
                onFinish = onOnboardingFinish
            )
        }

        composable("login") {
            AuthStartScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate("signup") },
                onPasswordResetClick = { navController.navigate("password_reset") }
            )
        }

        composable("signup") {
            SignupScreen(
                onBackClick = { navController.popBackStack() },
                onSignupSuccess = { navController.popBackStack() }
            )
        }

        composable("password_reset") {
            PasswordResetScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("home") {
            homeScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashLandingPreview() {
    DigitalTokTheme {
        SplashLanding()
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavHostOnboardingPreview() {
    DigitalTokTheme {
        AppNavHostContent(
            navController = rememberNavController(),
            isOnboardingDone = false,
            onOnboardingFinish = {},
            homeScreen = { Text("Home Screen Preview") }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavHostLoginPreview() {
    DigitalTokTheme {
        AppNavHostContent(
            navController = rememberNavController(),
            isOnboardingDone = true,
            onOnboardingFinish = {},
            homeScreen = { Text("Home Screen Preview") }
        )
    }
}
