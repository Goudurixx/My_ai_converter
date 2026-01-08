package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goudurixx.myaiconverter.core.MAX_SELECTION
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.presentation.viewmodel.SearchViewModel
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme
import kotlin.math.max
import kotlin.math.min

@Composable
fun SearchScreen(
    scrollToTop: Boolean,
    hasScrolled: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: SearchViewModel = hiltViewModel()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        AnimatedContent(
            targetState = loading,
            modifier = Modifier.fillMaxSize()
        ) {
            if (it) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                )
            } else {
                SearchScreen(
                    currencies = currencies,
                    query = query,
                    onQueryChange = viewModel::onQueryChanged,
                    onCurrencyClicked = viewModel::selectCurrency,
                    getCurrencyEmoji = viewModel::getCurrencyEmoji,
                    getCurrencySymbol = viewModel::getCurrencySymbol,
                    scrollToTop = scrollToTop,
                    hasScrolled = hasScrolled,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    currencies: List<CurrencyInfo>,
    query: String?,
    onQueryChange: (String) -> Unit,
    onCurrencyClicked: (CurrencyInfo) -> Unit,
    getCurrencyEmoji: (String) -> String,
    getCurrencySymbol: (String) -> String,
    scrollToTop: Boolean,
    hasScrolled: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected by remember(currencies) {
        mutableIntStateOf(currencies.count { it.selected })
    }
    var selectedList by remember(currencies) {
        mutableStateOf(
            currencies.subList(0, min(currencies.size, MAX_SELECTION)).filter { it.selected }
        )
    }
    val scrollState = rememberLazyListState()
    var searchFocused by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(scrollToTop) {
        if (scrollToTop) {
            scrollState.scrollToItem(0)
            hasScrolled()
        }
    }
    LazyColumn(
        modifier = modifier
            .clickable(
                onClick = {
                    // Clear focus and hide keyboard when the Box is clicked
                    focusManager.clearFocus()
                    searchFocused = false
                    keyboardController?.hide()
                },
                indication = null,
                interactionSource = null,
            )
            .background(
                MaterialTheme.colorScheme.primary,
            ),
        state = scrollState,
    ) {
        stickyHeader {
            Column(
                modifier =
                    Modifier
                        .clip(
                            shape = MaterialTheme.shapes.medium
                                .copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                )
                        )
                        .shadow(
                            elevation = 16.dp,
                            shape = MaterialTheme.shapes.medium
                                .copy(
                                    topStart = CornerSize(0.dp),
                                    topEnd = CornerSize(0.dp),
                                )
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                        .statusBarsPadding()
            ) {
                MySearchTextField(
                    value = query ?: String(),
                    onValueChange = onQueryChange,
                    onFocused = {
                        searchFocused = it
                        if(it.not())
                            focusManager.clearFocus()
                    },
                    modifier = Modifier.padding(16.dp),
                )
                AnimatedVisibility((scrollState.isScrollInProgress.not() && scrollState.canScrollBackward.not()) && searchFocused.not()) {
                    Column {
                        selectedList.forEach {
                            CurrencySelector(
                                onCurrencyClicked = onCurrencyClicked,
                                getCurrencyEmoji = getCurrencyEmoji,
                                getCurrencySymbol = getCurrencySymbol,
                                info = it,
                                enabled = selected < MAX_SELECTION || it.selected
                            )
                        }
                    }
                }
                AnimatedVisibility((scrollState.isScrollInProgress.not() && scrollState.canScrollBackward.not() && searchFocused.not()).not()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        selectedList.forEach {
                            Text(getCurrencyEmoji(it.code))
                        }
//                        Spacer(
//                            modifier = Modifier.weight(1f)
//                        )
                        Text("/5", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.size(4.dp))
        }
        items(
            items = currencies.subList(max(0, selectedList.size), max(0, currencies.size)),
        ) {
            CurrencySelector(
                onCurrencyClicked = onCurrencyClicked,
                getCurrencyEmoji = getCurrencyEmoji,
                getCurrencySymbol = getCurrencySymbol,
                info = it,
                enabled = selected < MAX_SELECTION || it.selected
            )
        }
        item {
            Spacer(
                modifier = Modifier
                    .size(16.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SearchScreenPreview() {
    MyAIConverterTheme() {
        SearchScreen(
            currencies = listOf<CurrencyInfo>(
                CurrencyInfo(
                    "yili", "yili", rate = 1.220, selected = true,
                ),
                CurrencyInfo(
                    "yolo", "yolo", rate = 122.0,
                ),
                CurrencyInfo(
                    "yala", "yala", rate = 12.20,
                ),
            ),
            getCurrencyEmoji = { _ -> "°-°" },
            getCurrencySymbol = { _ -> "€" },
            onQueryChange = {},
            onCurrencyClicked = {},
            query = "None",
            scrollToTop = true,
            hasScrolled = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}