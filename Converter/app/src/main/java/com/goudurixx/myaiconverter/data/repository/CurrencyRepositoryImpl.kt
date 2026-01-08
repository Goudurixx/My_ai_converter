package com.goudurixx.myaiconverter.data.repository

import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.goudurixx.myaiconverter.core.DEFAULT_CURRENCY
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {
    val py = Python.getInstance()
    val module = py.getModule("currency_service_v2").apply {
        callAttr("initialize", DEFAULT_CURRENCY)
    }
    val emojiModule = py.getModule("code_emoji_service")
    val symbolsModule = py.getModule("code_symbols_service")
    private val _isSyncing = MutableStateFlow(false)
    override val isSyncing = _isSyncing.asStateFlow()

    private val _currenciesFlow = MutableStateFlow<List<CurrencyInfo>>(emptyList())
    override val currenciesFlow = _currenciesFlow.asStateFlow()

    private val _selectedCurrenciesFlow = MutableStateFlow<List<CurrencyInfo>>(emptyList())
    override val selectedCurrenciesFlow = _selectedCurrenciesFlow.asStateFlow()


    init {
        // Redirect Python stdout/stderr to Android logcat
        try {
            py.getModule("android.util")
            val sys = py.getModule("sys")
            val androidLog = py.getModule("android.util").get("Log")

            // Create a custom writer class in Python
            py.getModule("io")
            sys["stdout"] = py.builtins.callAttr(
                "type", "LogWriter",
                py.builtins.callAttr("tuple", listOf(py.builtins.get("object"))),
                py.builtins.callAttr("dict")
            ).callAttr("__new__")
        } catch (e: Exception) {
            Log.e("CurrencyRepImpl", "Failed to setup Python logging: ${e.message}")
        }
    }

    override suspend fun syncIfNeeded() {
        withContext(Dispatchers.IO) {
            // Check if we already have labels in the DB to avoid unnecessary network calls
            val currentCount = module.callAttr("get_db_count").toInt()

            if (currentCount == 0) {
                _isSyncing.value = true
                try {
                    // Call your Python populate function
                    module.callAttr("populate_database")
                } finally {
                    _isSyncing.value = false
                }
            }
            filterCurrencies(null)
        }
    }

    override suspend fun filterCurrencies(query: String?) {
        withContext(Dispatchers.IO) {
            try {
                val pyList = module.callAttr("get_filtered_currencies", query).asList()
                val currencies = pyList.map { item ->
                    val map = item.asMap()
                    CurrencyInfo(
                        code = map[PyObject.fromJava("code")].toString(),
                        name = map[PyObject.fromJava("name")].toString(),
                        rate = map[PyObject.fromJava("rate")]?.toDouble(),
                        selected = map[PyObject.fromJava("selected")]?.toInt() == 1,
                    )
                }
                _currenciesFlow.update {
                    currencies
                }
            } catch (e: Exception) {
                Log.e("CurrencyRepImpl", "${e.message}")
            }
        }
    }

    override suspend fun getSelectedCurrencies(): Result<Int> {
        try {
            val pyList = module.callAttr("get_selected_currencies").asList()
            val currencies = pyList.map { item ->
                val map = item.asMap()
                CurrencyInfo(
                    code = map[PyObject.fromJava("code")].toString(),
                    name = map[PyObject.fromJava("name")].toString(),
                    rate = map[PyObject.fromJava("rate")]?.toDouble(),
                    selected = map[PyObject.fromJava("selected")]?.toInt() == 1,
                )
            }
            _selectedCurrenciesFlow.update {
                currencies
            }
            return Result.success(currencies.size)
        } catch (e: Exception) {
            Log.e("CurrRepImpl", "Error : $e")
            return Result.failure(e)
        }
    }

    override suspend fun selectCurrency(code: String, isSelected: Boolean): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                // SET Selected attribute to code
                module.callAttr("toggle_currency_selection", code, isSelected)
                filterCurrencies(null)
                Result.success(isSelected)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getEuroRate(target: String): Result<Double> {
        return withContext(Dispatchers.IO) {
            try {

                // Call the Python function with the target currency (e.g., "USD")
                val rate = module.callAttr("fetch_rate_from_eur", target).toDouble()

                if (rate > 0) {
                    Result.success(rate)
                } else {
                    Result.failure(Exception("Currency '$target' not found or network error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getCurrencyEmoji(code: String): Result<String> {
        try {
            return Result.success(emojiModule.callAttr("get_flag_dynamic", code).toString())
        } catch (e: Exception) {
            Log.e("Impl", "${e.message}")
            return Result.failure(e)
        }
    }

    override fun getCurrencySymbol(code: String): Result<String> {
        try {
            return Result.success(symbolsModule.callAttr("get_symbols_from_code", code.toUpperCase()).toString())
        } catch (e: Exception) {
            Log.e("Impl", "${e.message}")
            return Result.failure(e)
        }
    }
}
