package bot.messages

class Messages {
    companion object {

        fun helpMessage(): String {
            val sb = StringBuilder()
            sb.append("/help  -  shows all possible commands")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("/convert  {value}  {current}  {next}" )
            sb.append(System.lineSeparator())
            sb.append("  -  converts value in current currency to next currency")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("/setTime  {time}  -  sets time for reminder")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("/list  -  lists available currencies")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("/printReminders  -  prints all reminders")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("/deleteReminder  {order}  -  cancels reminder from remindersList")
            sb.append(", order  -  number of reminder in printReminders")
            return sb.toString()
        }

        fun startMessage(): String {
            val sb = StringBuilder()
            sb.append("Welcome to the currency exchange bot! Commands:")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append(helpMessage())
            return sb.toString()
        }

        fun currencyList(): String {
            val sb = StringBuilder()
            sb.append("Here are all the currencies that are available for use")
            sb.append(System.lineSeparator())
            sb.append(System.lineSeparator())
            sb.append("RUB, USD, EUR, GBP, JPY, AUD CAD, CHF, CNH, HKD, NZD")
            return sb.toString()
        }

        fun addedReminder(time: String): String = "Added reminder for $time"
        fun deletedReminder(time: String): String = "Deleted reminder at $time"
        fun invalidTime(time: String): String = "Invalid time : $time"

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