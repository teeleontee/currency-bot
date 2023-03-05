package bot.handler

interface HandlerInterface {

    fun help(chatId: String)

    fun start(chatId: String)

    fun setReminder(chatId: String, time: String)

    fun deleteReminder(chatId: String, number: Int)

    fun printReminders(chatId: String)

    fun convertCurrency(chatId: String, value: Double, from: String, to: String)

    fun notValidCommand(chatId: String)

    fun listCurrencies(chatId: String)

    fun sendMessage(chatId: String, text: String)
}
