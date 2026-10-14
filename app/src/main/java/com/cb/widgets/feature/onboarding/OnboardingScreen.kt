package com.cb.widgets.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.SkeuButton
import com.cb.widgets.core.design.skeuRaised
import com.cb.widgets.core.design.skeuRecessed
import kotlinx.coroutines.launch

private data class OnboardingPage(val icon: ImageVector, val title: String, val body: String)

private val pages = listOf(
    OnboardingPage(
        Icons.Outlined.Widgets,
        "Browse widgets",
        "Clocks, battery, network speed, storage and more, each with real information from your phone."
    ),
    OnboardingPage(
        Icons.Outlined.Tune,
        "Customize",
        "Choose a material, accent color, transparency and corner radius, and see the result live."
    ),
    OnboardingPage(
        Icons.Outlined.AddToHomeScreen,
        "Add to your home screen",
        "Touch and hold your home screen, open Widgets and drag CB Widgets onto it. Android always places widgets for you."
    ),
    OnboardingPage(
        Icons.Outlined.CheckCircle,
        "Enjoy",
        "Your widgets update on their own and work without internet wherever Android allows."
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val palette = LocalSkeu.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val last = pagerState.currentPage == pages.lastIndex

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            if (!last) {
                SkeuButton(text = "Skip", onClick = onFinish)
            } else {
                Spacer(Modifier.height(52.dp))
            }
        }

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
            val page = pages[index]
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .size(168.dp)
                        .skeuRaised(radius = 84.dp, depth = 14.dp, gloss = true),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(116.dp)
                            .skeuRecessed(radius = 58.dp, depth = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(page.icon, contentDescription = null, tint = palette.accent, modifier = Modifier.size(52.dp))
                    }
                }
                Spacer(Modifier.height(40.dp))
                Text(
                    page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = palette.text,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    page.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = palette.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { i ->
                val active = i == pagerState.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 5.dp)
                        .size(12.dp)
                        .skeuRecessed(radius = 6.dp, depth = 3.dp)
                ) {
                    if (active) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(palette.accent)
                        )
                    }
                }
            }
        }

        SkeuButton(
            text = if (last) "Get started" else "Next",
            primary = true,
            onClick = {
                if (last) onFinish()
                else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
