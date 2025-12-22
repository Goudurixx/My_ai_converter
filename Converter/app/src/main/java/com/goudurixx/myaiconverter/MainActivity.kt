package com.goudurixx.myaiconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.goudurixx.myaiconverter.presentation.ui.ConverterScreen
import com.goudurixx.myaiconverter.presentation.ui.SearchScreen
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val pagerState = rememberPagerState(
                initialPage = Destination.SearchScreen.ordinal,
                pageCount = { Destination.entries.size }
            )

            MyAIConverterTheme {
                HorizontalPager(
                    state = pagerState,
                    snapPosition = SnapPosition.Center,
                    key = {
                        Destination.entries[it]
                    }
                ) {
                    when (it) {
                        Destination.Converter.ordinal ->
                            ConverterScreen()

                        Destination.SearchScreen.ordinal ->
                            SearchScreen()
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
