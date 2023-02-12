import bot.CurrencyBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    try {
        val bot = TelegramBotsApi(DefaultBotSession::class.java)
        bot.registerBot(CurrencyBot())
    } catch (e: TelegramApiException) {
        System.err.println(e.message)
    }
}
