package me.ste.stevesseries.base.util

import java.util.function.Consumer

class CustomStringBuilder {
    private var string: String = ""

    fun append(string: String): CustomStringBuilder {
        this.string += string
        return this
    }

    fun condition(condition: Boolean, consumerTrue: Consumer<CustomStringBuilder>, consumerFalse: Consumer<CustomStringBuilder>? = null): CustomStringBuilder {
        if (condition) {
            consumerTrue.accept(this)
        } else {
            consumerFalse?.accept(this)
        }
        return this
    }

    fun build() = this.string
}