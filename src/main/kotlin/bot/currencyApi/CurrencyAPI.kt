package bot.currencyApi

import bot.enums.Currency
import java.time.LocalDateTime

interface CurrencyAPI {

    suspend fun get(): Map<Currency, Double>

    var timeStamp: LocalDateTime
}