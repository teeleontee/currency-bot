package bot.recources

import java.util.*

class Resources {
    fun getProperties(str: String): String {
        val props = javaClass.classLoader.getResourceAsStream(
            "config.properties").use {
            Properties().apply {
                load(it)
            }
        }
        return props.getProperty(str) ?: throw RuntimeException("no such property")
    }
}