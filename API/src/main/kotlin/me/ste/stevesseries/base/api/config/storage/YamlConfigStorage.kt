package me.ste.stevesseries.base.api.config.storage

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Path
import kotlin.io.path.isRegularFile

class YamlConfigStorage(private val file: Path) : ConfigStorage {
    private val configuration = YamlConfiguration()

    init {
        if (this.file.isRegularFile()) {
            this.configuration.load(this.file.toFile())
        }
    }

    override fun save() {
        this.configuration.save(this.file.toFile())
    }

    override fun <T> getValue(path: String, type: Class<T>) = this.configuration.getObject(path, type)

    override fun <T> setValue(path: String, type: Class<T>, value: T, comments: List<String>) {
        this.configuration.set(path, value)

        try {
            val setComments = ConfigurationSection::class.java.getDeclaredMethod("setComments", String::class.java, List::class.java)
            setComments.invoke(this.configuration, path, comments)
        } catch (_: Throwable) {}
    }
}