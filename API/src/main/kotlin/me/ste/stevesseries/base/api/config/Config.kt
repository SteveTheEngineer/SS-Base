package me.ste.stevesseries.base.api.config

import me.ste.stevesseries.base.api.config.storage.ConfigStorage

abstract class Config {
    val values = mutableMapOf<String, ConfigValue<*>>()

    protected fun <T : Config> group(path: String, config: T): T {
        for ((key, value) in config.values) {
            this.values["${path}.${key}"] = value
        }

        return config
    }

    protected fun <T> value(type: Class<T>, path: String, default: T, comments: List<String> = emptyList()): ConfigValue<T> {
        val value = ConfigValue(type, default, comments)
        this.values[path] = value
        return value
    }

    protected inline fun <reified T> value(path: String, default: T, comments: List<String> = emptyList()) =
        this.value(T::class.java, path, default, comments)

    fun sync(storage: ConfigStorage) {
        for ((path, value) in this.values) {
            this.syncValue(storage, path, value)
        }

        storage.save()
    }

    private fun <T> syncValue(storage: ConfigStorage, path: String, value: ConfigValue<T>) {
        val storageValue = storage.getValue(path, value.type)

        if (storageValue != null) {
            value.value = storageValue
        }

        storage.setValue(path, value.type, value.value, value.comments)
    }
}
