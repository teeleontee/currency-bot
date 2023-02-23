package bot

import bot.handler.Handler
import bot.messages.Messages
import bot.recources.Resources
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class CurrencyBot : TelegramLongPollingBot() {

    private val handler = Handler(this)

    companion object {
        private const val botName = "Currency Exchange"
        private val botTokenTelegram = Resources().getProperties("botTokenTelegram")
    }

    override fun getBotToken(): String = botTokenTelegram

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        update ?: return
        val chatId = update.message.chatId.toString()
        try {
            if (update.hasMessage() && update.message.hasText()) {
                val command = update.message.text.split(" ")
                when (command[0]) {
                    "/help" ->  handler.help(chatId)
                    "/start" ->  handler.start(chatId)
                    "/list" -> handler.listCurrencies(chatId)
                    "/convert" -> handler.convertCurrency(chatId, command[1].toDouble(), command[2], command[3])
                    "/setTime" -> handler.setReminder(chatId, command[1] + " " + command[2])
                    "/printReminders" -> handler.printReminders(chatId)
                    "/deleteReminder" -> handler.deleteReminder(chatId, command[1].toInt())
                    else -> handler.notValidCommand(chatId)
                }
            }
        } catch (e: Exception) {
            handler.sendMessage(chatId, Messages.incorrectInput)
            System.err.println(e.message)
            e.printStackTrace()
        }
    }
}