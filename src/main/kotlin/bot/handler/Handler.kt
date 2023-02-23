package bot.handler

import bot.CurrencyBot
import bot.currencyApi.CurrencyRepository
import bot.currencyApi.CurrencyRepositoryImpl
import bot.enums.Currency
import bot.messages.Messages
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.concurrent.timerTask

class Handler(private val currencyBot: CurrencyBot): HandlerInterface {

    data class Times(val tasksList: MutableList<TimerTask>, val tasksTime: MutableList<String>)

    companion object {
        const val dayMilli: Long = 86400000
        val rep: CurrencyRepository = CurrencyRepositoryImpl()
        val tasks = hashMapOf<String, Times>()
    }

    override fun help(chatId: String) = sendMessage(chatId, Messages.helpMessage)

    override fun start(chatId: String) = sendMessage(chatId, Messages.startMessage)

    private fun getTimeZoned(time: String): ZonedDateTime =
        LocalDateTime
            .parse(
                time
                    .replace(" ", "T")
                    .replace("/", "-")
            )
            .atZone(
                ZoneId.of("Europe/Moscow")
            )

    private fun getCurrentTimeZoned(): ZonedDateTime = LocalDateTime.now()
        .atZone(
            ZoneId.of("Europe/Moscow")
        )

    override fun setReminder(chatId: String, time: String) {
        try {
            val timeMilli = getTimeZoned(time)
            val curTimeMilli = getCurrentTimeZoned()
            val delayMilli = Duration.between(curTimeMilli, timeMilli).toMillis()

            if (delayMilli < 0) {
                sendMessage(chatId, Messages.invalidDate)
                return
            }
            val tTask = timerTask {
                convertCurrency(chatId, 1.0, "USD", "RUB")
            }
            if (!tasks.contains(chatId)) {
                tasks[chatId] = Times(mutableListOf(tTask), mutableListOf(time))
            } else {
                tasks[chatId]?.tasksList?.add(tTask)
                tasks[chatId]?.tasksTime?.add(time)
            }

            try {
                Timer().schedule(tTask, delayMilli, dayMilli)
            } catch (e: IllegalArgumentException) {
                System.err.println(e.message)
            } catch (e: IllegalStateException) {
                System.err.println(e.message)
            }

        } catch (e: DateTimeParseException) {
            System.err.println(e.message)
            sendMessage(chatId, Messages.invalidTime(time))
        } catch (e: Exception) {
            System.err.println(e.message)
            sendMessage(chatId, Messages.invalidTime(time))
        }
        sendMessage(chatId, Messages.addedReminder(time))
    }

    override fun deleteReminder(chatId: String, number: Int) {
        try {
            if (!tasks.contains(chatId)) {
                sendMessage(chatId, Messages.cannotDelete)
                return
            }
            if (number < 1 || number > tasks[chatId]!!.tasksList.size) {
                sendMessage(chatId, Messages.invalidNumberForDeletion)
                return
            }
            tasks[chatId]!!.tasksList[number - 1].cancel()
            tasks[chatId]?.tasksList?.removeAt(number - 1)
            sendMessage(chatId, Messages.deletedReminder(tasks[chatId]?.tasksTime!![number - 1]))
            tasks[chatId]?.tasksTime?.removeAt(number - 1)
            if (tasks[chatId]!!.tasksList.isNotEmpty()) {
                sendMessage(chatId, Messages.remindersLeft)
            }
            printReminders(chatId)
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
        }
    }

    override fun printReminders(chatId: String) {
        if (!tasks.contains(chatId) || tasks[chatId]?.tasksList!!.isEmpty()) {
            sendMessage(chatId, Messages.emptyTasks)
            return
        }
        try {
            val sb = StringBuilder()
            for (i in 0 until tasks[chatId]!!.tasksTime.size) {
                sb.append(i + 1)
                sb.append(": ")
                sb.append(tasks[chatId]?.tasksTime!![i].split(" ")[1])
                sb.append(System.lineSeparator())
            }
            sendMessage(chatId, sb.toString())
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
        }
    }

    override fun convertCurrency(chatId: String, value: Double, from: String, to: String) {
        try {
            sendMessage(
                chatId, runBlocking {
                    rep.convert(
                        value,
                        Currency.valueOf(from.uppercase()),
                        Currency.valueOf(to.uppercase())
                    )
                }.toString() + " ${to.uppercase()} = $value ${from.uppercase()}"
            )
        } catch (e: IllegalArgumentException) {
            sendMessage(chatId, Messages.unknownCurrency)
            System.err.println(e.message)
        } catch (e: Exception) {
            System.err.println(e.message)
        }
    }
    override fun notValidCommand(chatId: String) = sendMessage(chatId, Messages.notValidCommand)

    override fun listCurrencies(chatId: String) = sendMessage(chatId, Messages.currencyList())

    override fun sendMessage(chatId: String, text: String) {
        try {
            val message = SendMessage()
            message.chatId = chatId
            message.text = text
            currencyBot.execute(message)
        } catch (e: TelegramApiException) {
            System.err.println(e.message)
            e.printStackTrace()
        }
    }
}
