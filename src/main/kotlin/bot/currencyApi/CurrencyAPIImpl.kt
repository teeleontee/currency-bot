package bot.currencyApi

import bot.enums.Currency
import bot.recources.Resources
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class CurrencyAPIImpl(private val base: Currency = Currency.USD) : CurrencyAPI {

    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override var timeStamp: LocalDateTime = LocalDateTime.MIN

    override suspend fun get(): Map<Currency, Double> {
        val res: CurrencyRequest = client.get(
            "https://api.apilayer.com/exchangerates_data/latest?symbols=${
                Currency.values().joinToString(",")
            }&base=$base"
        ) {
            headers.append("apikey", API_KEY)
        }.body()
        timeStamp = LocalDateTime.now()
        return res.rates
    }

    companion object {
        private val API_KEY = Resources().getProperties("API_KEY")
    }
}