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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.auth.AuthStartScreen
import com.yourcompany.digitaltok.ui.auth.SignupActivity
import com.yourcompany.digitaltok.ui.device.NfcViewModel
import com.yourcompany.digitaltok.ui.home.HomeScreen
import com.yourcompany.digitaltok.ui.onboarding.OnboardingPrefs
import com.yourcompany.digitaltok.ui.onboarding.OnboardingScreen
import com.yourcompany.digitaltok.ui.theme.DigitalTokTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val mainUiViewModel: MainUiViewModel by viewModels()

    private val signupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 회원가입 성공했다면, 여기서 토스트/처리 가능
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        enableEdgeToEdge()

        setContent {
            DigitalTokTheme {
                AppEntry(
                    mainViewModel = mainViewModel,
                    mainUiViewModel = mainUiViewModel,
                    onOpenSignUp = {
                        signupLauncher.launch(Intent(this, SignupActivity::class.java))
                    }
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
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                nfcViewModel.onTagDiscovered(tag)
            }
        }
    }
}

@Composable
private fun AppEntry(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel,
    onOpenSignUp: () -> Unit
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
            mainUiViewModel = mainUiViewModel,
            onOpenSignUp = onOpenSignUp
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
                color = Color(0xFF6B6B6B)
            )
        }
    }
}

@Composable
fun AppNavHost(
    mainViewModel: MainViewModel,
    mainUiViewModel: MainUiViewModel,
    navController: NavHostController = rememberNavController(),
    onOpenSignUp: () -> Unit = {}
) {
    val context = LocalContext.current

    // 온보딩 1회만: 완료했으면 login부터 시작
    val startDestination =
        if (OnboardingPrefs.isDone(context)) "login" else "onboarding"

    NavHost(navController, startDestination = startDestination) {

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    // 온보딩 완료 저장
                    OnboardingPrefs.setDone(context)

                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            AuthStartScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignupClick = { onOpenSignUp() }
            )
        }

        composable("home") {
            HomeScreen(mainViewModel = mainViewModel, mainUiViewModel = mainUiViewModel)
        }
    }
}
