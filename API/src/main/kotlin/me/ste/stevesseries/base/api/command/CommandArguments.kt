package me.ste.stevesseries.base.api.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import me.ste.stevesseries.base.api.extensions.i18n
import me.ste.stevesseries.base.api.util.ReflectionUtil
import me.ste.stevesseries.base.api.util.vector.Vector2D
import me.ste.stevesseries.base.api.util.vector.Vector2I
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.TranslatableComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.apache.commons.lang.math.FloatRange
import org.apache.commons.lang.math.IntRange
import org.bukkit.Axis
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Objective
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

object CommandArguments {
    // Classes, Methods
    private fun getArgTypeClass(name: String) = ReflectionUtil.getNMSClass("commands.arguments.$name")

    private val bukkitServer = Bukkit.getServer()
    private val server = this.bukkitServer::class.java.getDeclaredMethod("getServer").invoke(this.bukkitServer)
    private val reloadableResources = this.server::class.java.superclass.getDeclaredField("at").get(this.server)
    private val dataPackResources =
        ReflectionUtil.getField(this.reloadableResources::class.java, "b")!!.get(this.reloadableResources)
    private val commandBuildContext =
        ReflectionUtil.getField(this.dataPackResources::class.java, "c")!!.get(this.dataPackResources)

    private val chatBaseComponentClass = ReflectionUtil.getNMSClass("network.chat.IChatBaseComponent")
    private val chatSerializerClass = this.chatBaseComponentClass.declaredClasses.find { it.simpleName == "ChatSerializer" }!!

    private val entityClass = this.getArgTypeClass("ArgumentEntity")
    private val profileClass = this.getArgTypeClass("ArgumentProfile")
    private val positionClass = this.getArgTypeClass("coordinates.ArgumentPosition")
    private val vec2IClass = this.getArgTypeClass("coordinates.ArgumentVec2I")
    private val vec3Class = this.getArgTypeClass("coordinates.ArgumentVec3")
    private val vec2Class = this.getArgTypeClass("coordinates.ArgumentVec2")
    private val itemStackClass = this.getArgTypeClass("item.ArgumentItemStack")
    private val itemPredicateClass = this.getArgTypeClass("item.ArgumentItemPredicate")
    private val chatFormatClass = this.getArgTypeClass("ArgumentChatFormat")
    private val chatComponentClass = this.getArgTypeClass("ArgumentChatComponent")
    private val chatClass = this.getArgTypeClass("ArgumentChat")
    private val scoreboardObjectiveClass = this.getArgTypeClass("ArgumentScoreboardObjective")
    private val angleClass = this.getArgTypeClass("ArgumentAngle")
    private val scoreHolderClass = this.getArgTypeClass("ArgumentScoreholder")
    private val rotationAxisClass = this.getArgTypeClass("coordinates.ArgumentRotationAxis")
    private val scoreboardTeamClass = this.getArgTypeClass("ArgumentScoreboardTeam")
    private val inventorySlotClass = this.getArgTypeClass("ArgumentInventorySlot")
    private val minecraftKeyRegisteredClass = this.getArgTypeClass("ArgumentMinecraftKeyRegistered")
    private val uuidClass = this.getArgTypeClass("ArgumentUUID")
    private val timeClass = this.getArgTypeClass("ArgumentTime")
    private val dimensionClass = this.getArgTypeClass("ArgumentDimension")
    private val criterionValueClass = this.getArgTypeClass("ArgumentCriterionValue")
    private val criterionValueFloatClass = this.criterionValueClass.declaredClasses.find { it.simpleName == "a" }!!
    private val criterionValueIntClass = this.criterionValueClass.declaredClasses.find { it.simpleName == "b" }!!

    // Utility Methods
    private fun convertContextBack(context: CommandContext<CommandSource>) =
        (context as CommandContext<Any>).copyFor((context as CommandContext<CommandSource>).source.obj)

    private fun <T> getResult(
        argumentClass: Class<*>,
        method: String,
        context: CommandContext<CommandSource>,
        name: String,
        convert: Function<Any, T>
    ): T {
        val listener = context.source.obj
        val vanillaContext = this.convertContextBack(context)

        val result: Any
        try {
            result = argumentClass.getDeclaredMethod(method, CommandContext::class.java, String::class.java)
                .invoke(null, vanillaContext, name)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }

        return convert.apply(result)
    }

    private fun getEntity(obj: Any) =
        ReflectionUtil.getMethod(obj::class.java, "getBukkitEntity")!!.invoke(obj) as Entity

    private fun getBlockVector(obj: Any): BlockVector {
        val x = ReflectionUtil.getFirstMethod(obj::class.java, "getX", "u")!!.invoke(obj) as Int
        val y = ReflectionUtil.getFirstMethod(obj::class.java, "getY", "v")!!.invoke(obj) as Int
        val z = ReflectionUtil.getFirstMethod(obj::class.java, "getZ", "w")!!.invoke(obj) as Int

        return BlockVector(x, y, z)
    }

    private fun getBaseComponent(obj: Any): Array<BaseComponent> {
        val jsonString = this.chatSerializerClass.getDeclaredMethod("a", this.chatBaseComponentClass).invoke(null, obj) as String
        return ComponentSerializer.parse(jsonString)
    }

    private fun getObjective(obj: Any): Objective {
        val name = obj::class.java.getField("b").get(obj) as String
        return this.bukkitServer.scoreboardManager!!.mainScoreboard.getObjective(name)!!
    }

    // Public
    fun translationMessage(translation: String, vararg args: Object) =
        this.componentMessage(
            arrayOf(
                TranslatableComponent(translation, *args)
            )
        )

    fun literalMessage(literal: String) =
        this.componentMessage(
            arrayOf(
                TextComponent(literal)
            )
        )

    fun i18nMessage(player: Player, key: NamespacedKey, args: Map<String, String> = emptyMap()) =
        this.literalMessage(player.i18n(key, args))

    fun componentMessage(components: Array<BaseComponent>): Message {
        val serialized = ComponentSerializer.toString(*components)
        return this.chatSerializerClass.getDeclaredMethod("a", String::class.java).invoke(null, serialized) as Message
    }

    fun entity() = this.entityClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun entities() =
        this.entityClass.getDeclaredMethod("multipleEntities").invoke(null) as ArgumentType<Collection<Any>>

    fun player() = this.entityClass.getDeclaredMethod("c").invoke(null) as ArgumentType<Any>

    fun players() = this.entityClass.getDeclaredMethod("d").invoke(null) as ArgumentType<Collection<Any>>

    fun getEntity(context: CommandContext<CommandSource>, name: String): Entity =
        this.getResult(this.entityClass, "a", context, name, this::getEntity)

    fun getEntities(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.entityClass, "c", context, name) { (it as Collection<Any>).map(this::getEntity) }

    fun getEntitiesNonEmpty(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.entityClass, "b", context, name) { (it as Collection<Any>).map(this::getEntity) }

    fun getPlayer(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.entityClass, "e", context, name) { this.getEntity(it) as Player }

    fun getPlayers(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.entityClass, "d", context, name) {
            (it as Collection<Any>).map { entity ->
                this.getEntity(entity) as Player
            }
        }

    fun getPlayersNonEmpty(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.entityClass, "f", context, name) {
            (it as Collection<Any>).map { entity ->
                this.getEntity(entity) as Player
            }
        }

    fun offlinePlayers() =
        this.profileClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Collection<Any>>

    fun getOfflinePlayers(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.profileClass, "a", context, name) {
            (it as Collection<Any>).map { profile ->
                val id = it::class.java.getDeclaredMethod("getId").invoke(profile) as UUID
                Bukkit.getOfflinePlayer(id)
            }
        }

    fun blockVector() = this.positionClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getBlockVector(context: CommandContext<CommandSource>, name: String): BlockVector =
        this.getResult(this.positionClass, "b", context, name, this::getBlockVector)

    fun getBlockVectorLoaded(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.positionClass, "a", context, name, this::getBlockVector)

    fun vector2I() = this.vec2IClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getVector2I(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.vec2IClass, "a", context, name) {
            val x = it::class.java.getDeclaredField("a").get(it) as Int
            val y = it::class.java.getDeclaredField("b").get(it) as Int

            Vector2I(x, y)
        }

    fun vector(centerIntegers: Boolean = true) =
        this.vec3Class.getDeclaredMethod("a", Boolean::class.java).invoke(null, centerIntegers) as ArgumentType<Vector>

    fun getVector(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.vec3Class, "a", context, name) {
            val x = ReflectionUtil.getFirstMethod(it::class.java, "getX", "a")!!.invoke(it) as Double
            val y = ReflectionUtil.getFirstMethod(it::class.java, "getY", "b")!!.invoke(it) as Double
            val z = ReflectionUtil.getFirstMethod(it::class.java, "getZ", "c")!!.invoke(it) as Double

            Vector(x, y, z)
        }

    fun vector2D(centerIntegers: Boolean = true) = this.vec2Class.getDeclaredMethod("a", Boolean::class.java)
        .invoke(null, centerIntegers) as ArgumentType<Vector2D>

    fun getVector2D(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.vec2Class, "a", context, name) {
            val x = it::class.java.getDeclaredField("i").get(it) as Float
            val y = it::class.java.getDeclaredField("j").get(it) as Float

            Vector2D(x.toDouble(), y.toDouble())
        }

    // TODO block state ???

    // TODO block predicate ???

    fun itemStack() = this.itemStackClass.getDeclaredMethod("a", this.commandBuildContext::class.java.enclosingClass)
        .invoke(null, this.commandBuildContext) as ArgumentType<ItemStack>

    fun getItemStack(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.itemStackClass, "a", context, name) {
            val mcStack = it::class.java.getDeclaredMethod(
                "a",
                Int::class.javaPrimitiveType!!,
                Boolean::class.javaPrimitiveType!!
            ).invoke(it, 1, false)
            mcStack::class.java.getDeclaredMethod("getBukkitStack").invoke(mcStack) as ItemStack
        }

    fun itemStackPredicate() =
        this.itemPredicateClass.getDeclaredMethod("a", this.commandBuildContext::class.java.enclosingClass)
            .invoke(null, this.commandBuildContext) as ArgumentType<Predicate<ItemStack>>

    fun getItemStackPredicate(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.itemPredicateClass, "a", context, name) {
            val predicate = it as Predicate<Any>

            Predicate<ItemStack> {
                val mcStack = it::class.java.getDeclaredField("handle").get(it)
                predicate.test(mcStack)
            }
        }

    fun chatColor() = this.chatFormatClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getChatColor(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.chatFormatClass, "a", context, name) {
            val code = it::class.java.getDeclaredMethod("a").invoke(it) as Char
            ChatColor.getByChar(code)
        }

    fun baseComponents() =
        this.chatComponentClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getBaseComponents(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.chatComponentClass, "a", context, name, this::getBaseComponent)

    fun message() = this.chatClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getMessage(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.chatClass, "a", context, name, this::getBaseComponent)

    // TODO signed chat messages ???

    // TODO NBT tag, base, path ???

    fun objective() = this.scoreboardObjectiveClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getObjective(context: CommandContext<CommandSource>, name: String): Objective =
        this.getResult(this.chatClass, "a", context, name, this::getObjective)

    fun getObjectiveWritable(context: CommandContext<CommandSource>, name: String): Objective =
        this.getResult(this.chatClass, "b", context, name, this::getObjective)

    // TODO objective criteria ???

    // TODO math operation ???

    // TODO particles ???

    fun angle() = this.angleClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>
    fun getAngle(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.angleClass, "a", context, name) { it as Float }

    // TODO rotation ???

    // TODO scoreboard display slot ???

    fun scoreHolder() = this.scoreHolderClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>
    fun scoreHolders() = this.scoreHolderClass.getDeclaredMethod("b").invoke(null) as ArgumentType<Collection<Any>>

    fun getScoreHolder(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.scoreHolderClass, "a", context, name) { it as String }

    fun getScoreHolders(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.scoreHolderClass, "c", context, name) { it as Collection<String> }

    // TODO last score holder method ???

    fun axes() = this.rotationAxisClass.getDeclaredMethod("a").invoke(null) as ArgumentType<EnumSet<*>>

    fun getAxes(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.rotationAxisClass, "a", context, name) {
            val axisName = it::class.java.getDeclaredMethod("a").invoke(it) as String
            Axis.values().find { axis -> axis.name == axisName.uppercase() }!!
        }

    fun team() = this.scoreboardTeamClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getTeam(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.scoreboardTeamClass, "a", context, name) {
            val teamName = it::class.java.getDeclaredMethod("b").invoke(it) as String
            this.bukkitServer.scoreboardManager!!.mainScoreboard.getTeam(teamName)!!
        }

    fun slot() = this.inventorySlotClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getSlot(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.inventorySlotClass, "a", context, name) { it as Int }

    fun namespacedKey() = this.minecraftKeyRegisteredClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getNamespacedKey(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.minecraftKeyRegisteredClass, "e", context, name) { NamespacedKey.fromString(it.toString()) }

    // TODO functions ???

    // TODO entity anchor ???

    fun floatRange() = this.criterionValueClass.getDeclaredMethod("b").invoke(null) as ArgumentType<Any>

    fun getFloatRange(context: CommandContext<CommandSource>, name: String): FloatRange {
        val valueType = this.criterionValueFloatClass.getDeclaredMethod("parse", StringReader::class.java).returnType
        val value = context.getArgument(name, valueType)

        val min = value::class.java.getDeclaredField("f").get(value) as Float
        val max = value::class.java.getDeclaredField("g").get(value) as Float

        return FloatRange(min, max)
    }

    fun intRange() = this.criterionValueClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getIntRange(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.criterionValueIntClass, "a", context, name) {
            val min = it::class.java.getDeclaredField("f").get(it) as Long
            val max = it::class.java.getDeclaredField("g").get(it) as Long

            IntRange(min.toInt(), max.toInt())
        }

    fun world() = this.dimensionClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getWorld(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.dimensionClass, "a", context, name) {
            it::class.java.superclass.getDeclaredMethod("getWorld").invoke(it) as World
        }

    fun ticks() = this.timeClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getTicks(context: CommandContext<CommandSource>, name: String) =
        context.getArgument(name, Int::class.javaObjectType)

    fun uuid() = this.uuidClass.getDeclaredMethod("a").invoke(null) as ArgumentType<Any>

    fun getUUID(context: CommandContext<CommandSource>, name: String) =
        this.getResult(this.uuidClass, "a", context, name) { it as UUID }
}