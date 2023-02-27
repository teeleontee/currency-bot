package bot.messages

import bot.enums.Currency

class Messages {

    companion object {
        fun currencyList(): String {
            val sb = StringBuilder()
            sb.append("Here are all the currencies that are available for use:")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            Currency.values()
                .forEach {
                    sb.append(it)
                    sb.append(", ")
                }
            return sb.toString()
        }

        fun addedReminder(time: String): String = "Added reminder for $time"
        fun deletedReminder(time: String): String = "Deleted reminder at $time"
        fun invalidTime(time: String): String = "Invalid time : $time"

        const val helpMessage: String =
            "/help  -  shows all possible commands\n\n" +
                    "/convert  {value}  {current}  {next}\n" +
                    "  -  converts value in current currency to next currency\n\n" +
                    "/setTime  {time}  -  sets time for reminder\n\n" +
                    "/list  -  lists available currencies\n\n" +
                    "/printReminders  -  prints all reminders\n\n" +
                    "/deleteReminder  {order}  -  cancels reminder from remindersList" +
                    ", order  -  number of reminder in printReminders"

        const val startMessage: String =
            "Welcome to the currency exchange bot! Commands:\n\n$helpMessage"

        const val notValidCommand = "This command is not valid, if stuck please use /help"
        const val incorrectInput = "Input is incorrect..."
        const val emptyTasks = "You have no reminders..."
        const val invalidDate = "Invalid time given..."
        const val remindersLeft = "These are the reminders you have left: "
        const val cannotDelete = "Cannot delete Reminder, there are no reminders..."
        const val invalidNumberForDeletion = "No such reminder with this number, " +
                "to see which reminders are available please use /printReminders"
        const val unknownCurrency = "unknown currency..."
    }
}