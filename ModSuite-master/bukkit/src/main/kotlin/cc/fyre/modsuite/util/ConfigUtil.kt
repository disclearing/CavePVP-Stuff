package cc.fyre.modsuite.util

import com.squareup.moshi.JsonReader
import okhttp3.internal.io.FileSystem
import okio.buffer
import java.io.File
import java.io.FileWriter

object ConfigUtil {

    private val cache = hashMapOf<String,HashMap<String, JsonConfig>>()
    private val byClass = hashMapOf<Class<*>, JsonConfig>()

    fun getByClass(clazz: Class<*>): JsonConfig? {
        return byClass[clazz]
    }

    fun getByPlugin(plugin: String):List<JsonConfig> {
        return cache[plugin.toLowerCase()]?.values?.toList() ?: listOf()
    }

    fun getByPluginAndName(plugin: String, name: String): JsonConfig? {

        if (!cache.containsKey(plugin.toLowerCase())) {
            return null
        }

        return cache[plugin.toLowerCase()]!![name.toLowerCase()]
    }

    fun <T> register(name: String,plugin: String,dataFolder: File,config: JsonConfig):T {

        if (!JsonConfig::class.java.isAssignableFrom(config::class.java)) {
            throw IllegalStateException("${config::class.java.simpleName} is not a JsonConfig!")
        }

        if (dataFolder.exists() && !dataFolder.isDirectory) {
            throw IllegalStateException("Failed to load ${name}.json: ${dataFolder.name} is not a directory!")
        }

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val file = File("${dataFolder.absolutePath}/$name.json")

        val toReturn: T = if (!file.exists()) {

            val writer = FileWriter(file)

            try {
                writer.write(MoshiUtil.instance.adapter(config.javaClass).setPrettyPrinting().toJson(config))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            writer.close()

            config as T
        } else {
            MoshiUtil.instance.adapter(config::class.java).setPrettyPrinting().fromJson(JsonReader.of(FileSystem.SYSTEM.source(file).buffer())) as T
        }

        if (toReturn !is JsonConfig) {
            throw IllegalStateException("?")
        }

        toReturn.file = file

        cache.putIfAbsent(plugin.toLowerCase(),hashMapOf())
        cache[plugin.toLowerCase()]!![name] = toReturn
        byClass[config::class.java] = toReturn

        return toReturn
    }

    fun save(config: JsonConfig) {

        if (config.file.exists()) {
            config.file.delete()
        }

        val writer = FileWriter(config.file)

        try {
            writer.write(MoshiUtil.instance.adapter(config.javaClass).setPrettyPrinting().toJson(config))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        writer.close()
    }

}