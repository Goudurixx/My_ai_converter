package com.goudurixx.myaiconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.goudurixx.myaiconverter.presentation.ui.ConverterScreen
import com.goudurixx.myaiconverter.presentation.ui.SearchScreen
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme
import dagger.hilt.android.AndroidEntryPoint

typealias scrollToTopSearchScreen = () -> Unit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val pagerState = rememberPagerState(
                initialPage = viewModel.startingPage.collectAsStateWithLifecycle().value.ordinal,
                pageCount = { Destination.entries.size }
            )
            var scrollToTopSearchScreen by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pagerState.isScrollInProgress) 0.9f else 1f
            )
            val radius by animateDpAsState(
                targetValue = if (pagerState.isScrollInProgress) 16.dp else 0.dp
            )
            var currentDestination by remember(pagerState.settledPage) {
                mutableIntStateOf(pagerState.currentPage)
            }

            MyAIConverterTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        snapPosition = SnapPosition.Center,
                        key = {
                            Destination.entries[it]
                        }
                    ) {
                        when (it) {
                            Destination.Converter.ordinal ->
                                ConverterScreen(
                                    modifier = Modifier
                                        .graphicsLayer(
                                            {
                                                scaleY = scale
                                                scaleX = scale
                                                shape = RoundedCornerShape(radius)
                                                shadowElevation = radius.value
                                            }
                                        )
                                        .clip(RoundedCornerShape(radius))
                                )

                            Destination.SearchScreen.ordinal ->
                                SearchScreen(
                                    scrollToTop = scrollToTopSearchScreen,
                                    hasScrolled = {
                                        scrollToTopSearchScreen = false
                                    },
                                    modifier = Modifier
                                        .graphicsLayer(
                                            {
                                                scaleY = scale
                                                scaleX = scale
                                                shape = RoundedCornerShape(radius)
                                                shadowElevation = radius.value
                                            }
                                        )
                                        .clip(RoundedCornerShape(radius))
                                )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                currentDestination = Destination.Converter.ordinal
                            }
                        ) {
                            Icon(
                                imageVector = if (pagerState.currentPage == Destination.Converter.ordinal)
                                    Icons.Filled.Calculate
                                else
                                    Icons.Outlined.Calculate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                            )
                        }
                        IconButton(
                            onClick = {
                                if (pagerState.currentPage != Destination.SearchScreen.ordinal)
                                    currentDestination = Destination.SearchScreen.ordinal
                                else
                                    scrollToTopSearchScreen = true
                            }
                        ) {
                            Icon(
                                imageVector = if (pagerState.currentPage == Destination.SearchScreen.ordinal)
                                    Icons.Filled.PlaylistAddCheckCircle
                                else
                                    Icons.Outlined.PlaylistAddCheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                            )
                        }
                    }

                    LaunchedEffect(currentDestination) {
                        pagerState.animateScrollToPage(currentDestination)
                    }
                }
            }
        }
    }
}

enum class Destination {
    Converter,
    SearchScreen;
}