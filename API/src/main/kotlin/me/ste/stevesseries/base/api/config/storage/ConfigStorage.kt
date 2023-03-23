package me.ste.stevesseries.base.api.config.storage

interface ConfigStorage {
    fun save()

    fun <T> getValue(path: String, type: Class<T>): T?
    fun <T> setValue(path: String, type: Class<T>, value: T, comments: List<String>)
}