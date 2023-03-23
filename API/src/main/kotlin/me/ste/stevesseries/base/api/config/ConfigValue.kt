package me.ste.stevesseries.base.api.config

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

class ConfigValue<T>(
    val type: Class<T>,
    var value: T,
    val comments: List<String>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.value
    }
}
