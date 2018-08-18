package nuke.discord.util

import nuke.discord.CONFIG_AUTO_SAVE_PERIOD
import nuke.discord.LOGGER
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class Config(filename: String) {

    private val file = File(filename)
    private val properties = Properties()

    private val hasChanged = AtomicBoolean(false)

    init {
        // create file if it doesn't exist
        if (!file.exists()) {
            LOGGER.warn("Config file does not exist, creating...")
            try {
                if (!(file.mkdirs() && file.delete() && file.createNewFile())) {
                    LOGGER.error("Could not create file.")
                }
            } catch (e: IOException) {
                LOGGER.error("Could not create file.", e)
            }
        }

        // then load first time
        load()

        // finally, start auto-saving
        object : TimerTask() {
            override fun run() {
                if (hasChanged.compareAndSet(true, false)) {
                    save()
                }
            }
        }.also {
            Timer("config-auto-save", true)
                    .schedule(it, CONFIG_AUTO_SAVE_PERIOD, CONFIG_AUTO_SAVE_PERIOD)
        }
    }

    fun load() {
        try {
            FileReader(file).use { properties.load(it) }
            LOGGER.info("Loaded config.")
        } catch (e: IOException) {
            LOGGER.error("Could not load config.", e)
        }
    }

    fun save() {
        try {
            FileWriter(file).use { properties.store(it, "please do not edit this manually") }
            LOGGER.info("Saved config.")
        } catch (e: IOException) {
            LOGGER.error("Could not load config.", e)
        }
    }

    operator fun get(key: String): String {
        return if (properties.containsKey(key)) {
            properties.getProperty(key)
        } else {
            val envKey = key.toUpperCase(Locale.US)
            val envValue = System.getenv(envKey)

            if (envValue != null) {
                LOGGER.warn("Key '$key' did not exist, using environment variable '$envKey'.")
                envValue
            } else {
                LOGGER.warn("Key '$key' did not exist, creating empty entry.")
                ""
            }.also {
                properties[key] = it
            }
        }
    }

    operator fun set(key: String, value: Any?) {
        properties[key] = value.toString()
        // we've changed config, update the changed flag
        hasChanged.set(true)
    }

}