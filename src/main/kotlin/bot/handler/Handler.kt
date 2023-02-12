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
import java.util.*
import kotlin.concurrent.timerTask

class Handler(private val currencyBot: CurrencyBot): HandlerInterface {

    companion object {
        val rep: CurrencyRepository = CurrencyRepositoryImpl()
        val tasks = hashMapOf<String, MutableList<TimerTask>>()
        val tasksTime = hashMapOf<String, MutableList<String>>()
    }

    override fun help(chatId: String) = sendMessage(chatId, Messages.helpMessage())

    override fun start(chatId: String) = sendMessage(chatId, Messages.startMessage())

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
                convertCurrency(chatId, 1.0, "USD", "GBP")
            }

            if (!tasks.contains(chatId)) {
                tasks[chatId] = mutableListOf(tTask)
                tasksTime[chatId] = mutableListOf(time)
            } else {
                tasks[chatId]?.add(tTask)
                tasksTime[chatId]?.add(time)
            }
            Timer().schedule(tTask, delayMilli)
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
            sendMessage(chatId, Messages.invalidTime(time))
        }
        sendMessage(chatId, Messages.addedReminder(time))
    }

    private fun updateRemindersList(chatId: String): Boolean {
        try {
            if (tasks[chatId]!!.isEmpty()) {
                sendMessage(chatId, Messages.emptyTasks)
                return false
            }
            for (i in 0 until tasksTime[chatId]!!.size) {
                val timeMilli = getTimeZoned(tasksTime[chatId]!![i])
                val curTimeMilli = getCurrentTimeZoned()
                val delayMilli = Duration.between(curTimeMilli, timeMilli)
                    .toMillis()
                if (delayMilli < 0) {
                    tasks[chatId]?.removeAt(i)
                    tasksTime[chatId]?.removeAt(i)
                }
            }
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
        }
        return true
    }

    override fun deleteReminder(chatId: String, number: Int) {
        try {
            if (!tasks.contains(chatId)) {
                sendMessage(chatId, Messages.cannotDelete)
                return
            }
            if (number < 1 || number > tasks[chatId]!!.size) {
                sendMessage(chatId, Messages.invalidNumberForDeletion)
                return
            }
            tasks[chatId]!![number - 1].cancel()
            tasks[chatId]?.removeAt(number - 1)
            sendMessage(chatId, Messages.deletedReminder(tasksTime[chatId]!![number - 1]))
            tasksTime[chatId]?.removeAt(number - 1)
            if (tasks[chatId]!!.isNotEmpty()) {
                sendMessage(chatId, Messages.remindersLeft)
            }
            printReminders(chatId)
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
        }
    }

    override fun printReminders(chatId: String) {
        if (!tasks.contains(chatId)) {
            sendMessage(chatId, Messages.emptyTasks)
            return
        }
        val flag = updateRemindersList(chatId)
        if (!flag) { return }
        try {
            val sb = StringBuilder()
            for (i in 0 until tasks[chatId]!!.size) {
                sb.append(i + 1)
                sb.append(": ")
                sb.append(tasksTime[chatId]!![i].split(" ")[1])
                sb.append(System.lineSeparator())
            }
            sendMessage(chatId, sb.toString())
        } catch (e: Exception) {
            System.err.println(e.message)
            e.printStackTrace()
        }
    }

    override fun convertCurrency(chatId: String, value: Double, cur1: String, cur2: String) {
        try {
            sendMessage(
                chatId, runBlocking {
                    rep.convert(value, Currency.valueOf(cur1.uppercase()), Currency.valueOf(cur2.uppercase()))
                }.toString() + " ${cur2.uppercase()} = $value ${cur1.uppercase()}"
            )
        } catch (e: Exception) {
            sendMessage(chatId, Messages.unknownCurrency)
            System.err.println(e.message)
            e.printStackTrace()
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
