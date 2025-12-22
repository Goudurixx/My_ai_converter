package com.goudurixx.myaiconverter.data.repository

import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.goudurixx.myaiconverter.domain.model.CurrencyInfo
import com.goudurixx.myaiconverter.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableMap
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {
    val py = Python.getInstance()
    val module = py.getModule("currency_service")
    private val currencyDbMutex = Mutex()

    override suspend fun syncCurrencies(): Result<Boolean> = withContext(Dispatchers.IO) {
        currencyDbMutex.withLock {
            val success = module.callAttr("populate_database").toInt()
            Log.e("CurrencyRepoImp", "Success is $success")
            if (success > 0) Result.success(true) else Result.failure(Exception("Sync failed"))
        }
    }

    override suspend fun getCurrencyName(code: String): String {
        return module.callAttr("get_currency_name", code).toString()
    }

    override suspend fun getFilteredCurrencies(query: String?): Result<List<CurrencyInfo>> =
        withContext(Dispatchers.IO) {
            currencyDbMutex.withLock {
                try {
                    // Pass the query string directly to Python
                    val pyList = module.callAttr("get_currencies", query).asList()
                    val currencies = pyList.map { item ->
                        Log.e("CurrRepImpl", "Mapping  : $item")
                        val currencyData = item.asMap().toImmutableMap()
                        CurrencyInfo(
                            code = currencyData.getValue(PyObject.fromJava("code")).toString(),
                            name = currencyData[PyObject.fromJava("name")].toString(),
                        )
                    }
                    Log.e("CurrRepImpl", "Success : $currencies")
                    Result.success(currencies)
                } catch (e: Exception) {
                    Log.e("CurrRepImpl", "Error : $e")
                    Result.failure(e)
                }
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

}
