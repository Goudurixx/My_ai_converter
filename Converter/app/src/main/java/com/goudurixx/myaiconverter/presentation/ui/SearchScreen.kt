package com.goudurixx.myaiconverter.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.presentation.viewmodel.SearchViewModel
import com.goudurixx.myaiconverter.ui.theme.MyAIConverterTheme

@Composable
fun SearchScreen() {

    val viewModel: SearchViewModel = hiltViewModel()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        SearchScreen(
            currencies = currencies,
            query = query,
            onQueryChange = viewModel::onQueryChanged,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    currencies: List<CurrencyInfo>,
    query: String?,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary,
            )
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        stickyHeader {
            MySearchTextField(
                value = query ?: " ",
                onValueChange = onQueryChange,
                onFocused = { },
                modifier = Modifier.padding(16.dp),
            )
        }
        items(
            items = currencies,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                MyConverterTextField(
                    value = it.name ?: "",
                    prefix = it.code,
                    onValueChange = {},
                    onFocused = {},
                    label = "",
                    currency = "TODO",
                    enabled = false,
                )
            }
        }
    }
}

@Composable
@Preview
private fun SearchScreenPreview() {
    MyAIConverterTheme() {
        SearchScreen(
            currencies = listOf<CurrencyInfo>(
                CurrencyInfo(
                    "yolo", "yolo"
                )
            ),
            onQueryChange = {},
            query = "None",
            modifier = Modifier.fillMaxSize(),
        )
    }
}