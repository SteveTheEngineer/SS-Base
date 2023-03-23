package me.ste.stevesseries.base.util

import java.util.function.Consumer

@Deprecated("For backwards compatibility only.")
class LoreBuilder {
    private val lore: MutableList<String> = ArrayList()

    fun line(line: String, amount: Int = 1): LoreBuilder {
        for(i in 1..amount) {
            this.lore += line
        }
        return this
    }

    fun line() = this.line("")

    fun condition(condition: Boolean, consumerTrue: Consumer<LoreBuilder>, consumerFalse: Consumer<LoreBuilder>? = null): LoreBuilder {
        if (condition) {
            consumerTrue.accept(this)
        } else {
            consumerFalse?.accept(this)
        }
        return this
    }

    fun build() = this.lore
}