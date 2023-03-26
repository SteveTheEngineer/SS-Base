package me.ste.stevesseries.base.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.RedirectModifier
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import me.ste.stevesseries.base.api.command.CommandSource
import me.ste.stevesseries.base.api.util.ReflectionUtil
import java.lang.reflect.Modifier
import java.util.function.Predicate

object CommandNodeConversions {
    fun convertSource(source: Any): CommandSource {
        if (source is CommandSource) {
            return source
        }

        return CommandSource(source)
    }

    fun convertContext(context: CommandContext<Any>): CommandContext<CommandSource> {
        val source = this.convertSource(context.source)
        return context.copyFor(source) as CommandContext<CommandSource>
    }

    fun convertCommand(command: Command<CommandSource>) =
        Command { command.run(this.convertContext(it)) }

    fun convertRequirement(predicate: Predicate<CommandSource>) =
        Predicate<Any> { predicate.test(this.convertSource(it)) }

    fun convertRedirectModifier(modifier: RedirectModifier<CommandSource>) =
        RedirectModifier { modifier.apply(this.convertContext(it)) as Collection<Any> }

    fun convertSuggestionProvider(provider: SuggestionProvider<CommandSource>) =
        SuggestionProvider { context, builder -> provider.getSuggestions(this.convertContext(context), builder) }

    private fun <T> convertNodeOrBuilder(obj: T) {
        val clazz = (obj ?: return)::class.java

        val commandField = ReflectionUtil.getField(clazz, "command")
        val requirementField = ReflectionUtil.getField(clazz, "requirement")
        val modifierField = ReflectionUtil.getField(clazz, "modifier")
        val customSuggestionsField = ReflectionUtil.getField(clazz, "customSuggestions")

        commandField?.get(obj)?.let { commandField.set(obj, this.convertCommand(it as Command<CommandSource>)) }
        requirementField?.get(obj)?.let { requirementField.set(obj, this.convertRequirement(it as Predicate<CommandSource>)) }
        modifierField?.get(obj)?.let { modifierField.set(obj, this.convertRedirectModifier(it as RedirectModifier<CommandSource>)) }
        customSuggestionsField?.get(obj)?.let { customSuggestionsField.set(obj, this.convertSuggestionProvider(it as SuggestionProvider<CommandSource>)) }
    }

    fun convertNode(node: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<Any> {
        this.convertNodeOrBuilder(node)

        for (child in node.getArguments()) {
            this.convertNode(child)
        }

        return node as LiteralArgumentBuilder<Any>
    }

    fun convertNode(node: CommandNode<CommandSource>): CommandNode<Any> {
        this.convertNodeOrBuilder(node)

        for (child in node.children) {
            this.convertNode(child)
        }

        return node as CommandNode<Any>
    }
}