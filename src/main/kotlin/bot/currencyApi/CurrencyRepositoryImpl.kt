package bot.currencyApi

import bot.enums.Currency
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class CurrencyRepositoryImpl(base: Currency = Currency.USD): CurrencyRepository {
    private var rates: Map<Currency, Double> = mapOf()
    private val api = CurrencyAPIImpl(base)

    override suspend fun convert(value: Double, from: Currency, to: Currency): Double {
        val timeStamp = api.timeStamp
        if (ChronoUnit.HOURS.between(timeStamp, LocalDateTime.now()) >= 4) {
            rates = api.get()
            println("Updated")
        }
        return value / rates[from]!! * rates[to]!!
    }
}