package bot.currencyApi


import bot.enums.Currency
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyRequest(
    val base: String,
    val date: String,
    val rates: Map<Currency, Double>,
    val success: Boolean,
)