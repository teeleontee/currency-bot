package bot.currencyApi

import bot.enums.Currency

interface CurrencyRepository {

    suspend fun convert(value: Double, from: Currency, to: Currency): Double

}