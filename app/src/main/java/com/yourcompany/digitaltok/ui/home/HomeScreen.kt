package com.yourcompany.digitaltok.ui.home

import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.MainUiViewModel
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.components.BottomNavBar
import com.yourcompany.digitaltok.ui.decorate.DecorateFragment
import com.yourcompany.digitaltok.ui.device.DeviceConnectFragment
import com.yourcompany.digitaltok.ui.faq.HelpFragment

private object Variables {
    val Gray1 = Color(0xFFA0A0A0)
    val Point = Color(0xFF3AADFF)
}

@Composable
private fun ComposableFragmentContainer(modifier: Modifier = Modifier, fragment: () -> Fragment) {
    val containerId = remember { View.generateViewId() }
    val context = LocalContext.current

    AndroidView(
        factory = { FragmentContainerView(it).apply { id = containerId } },
        modifier = modifier,
        update = {
            val fm = (context as? FragmentActivity)?.supportFragmentManager
            if (fm != null && fm.findFragmentById(containerId) == null) {
                fm.commit {
                    setReorderingAllowed(true)
                    add(containerId, fragment())
                }
            }
        }
    )
}

@Composable
fun HomeScreen(mainViewModel: MainViewModel, mainUiViewModel: MainUiViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 하단바 가시성 / 연결 상태는 MainViewModel (단일 소스)
    val isBottomNavVisible by mainViewModel.isBottomNavVisible.observeAsState(initial = true)
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    // Fragment에서 요청한 탭 이동 처리
    val navigateTo by mainUiViewModel.navigateTo.collectAsState()
    LaunchedEffect(navigateTo) {
        navigateTo?.let { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
            mainUiViewModel.consumeNavigate()
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (isBottomNavVisible) {
                BottomNavBar(navController = navController, onItemClick = { route ->
                    if (route == "device" && isDeviceConnected) {
                        Toast.makeText(context, "이미 기기가 연결되어 있습니다.", Toast.LENGTH_SHORT).show()
                        false
                    } else true
                })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(
                start = 0.dp,
                end = 0.dp,
                top = innerPadding.calculateTopPadding(),
                bottom = if (isBottomNavVisible) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable("home") {
                HomeTab(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }


            composable("device") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DeviceConnectFragment() }
            }

            composable("decorate") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { DecorateFragment() }
            }

            composable("settings") {
                ComposableFragmentContainer(modifier = Modifier.fillMaxSize()) { HelpFragment() }
            }
        }
    }
}

@Composable
private fun HomeTab(mainViewModel: MainViewModel, navController: NavHostController) {
    val isDeviceConnected by mainViewModel.isDeviceConnected.observeAsState(initial = false)

    if (!isDeviceConnected) {
        HomeNoConnection()
    } else {
        HomeConnected(
            mainViewModel = mainViewModel,
            navController = navController
        )
    }
}

@Composable
private fun HomeNoConnection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단 타이틀
        Text(
            text = "Di-ring",
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF121212)
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 18.dp, start = 16.dp)
        )

        // 중앙 아이콘
        Image(
            painter = painterResource(id = R.drawable.ic_no_connection),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 217.dp)
                .size(width = 198.dp, height = 156.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = (-50).dp)          // ✅ 왼쪽/오른쪽 이동 (여기만 조절)
                    .padding(bottom = 0.dp),      // ✅ 아래에서 띄우기 (여기만 조절: 탭바 위)
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Variables.Point,
                                fontWeight = FontWeight(700)
                            )
                        ) { append("기기 연결") }
                        append("에서\n디지털톡을 찾아주세요")
                    },
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                        color = Variables.Gray1,
                        textAlign = TextAlign.Center
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "▼",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(800),
                        color = Variables.Point
                    )
                )
            }
        }

    }
}


@Composable
private fun HomeConnected(mainViewModel: MainViewModel, navController: NavController) {
    val lastImageUrl by mainViewModel.lastTransferredImageUrl.observeAsState()

    val navigateToDecorate = {
        navController.navigate("decorate")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "DigitalTok",
            style = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF121212)
            )
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 150.dp),
            contentAlignment = Alignment.TopCenter


        ) {
            Box(
                modifier = Modifier
                    .shadow(20.dp, RoundedCornerShape(12.dp))
                    .size(288.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .clickable { navigateToDecorate() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF4F4F4))
                ) {
                    AsyncImage(
                        model = lastImageUrl ?: R.drawable.rectangle_95,
                        contentDescription = "Last transferred image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.rectangle_95),
                        placeholder = painterResource(id = R.drawable.rectangle_95)
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "터치하여 이미지 변경",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Variables.Point
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { navigateToDecorate() }
        )
    }
}
