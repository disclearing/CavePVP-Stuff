package org.cavepvp.entity

import com.squareup.moshi.JsonReader
import okhttp3.internal.io.FileSystem
import okio.buffer
import org.cavepvp.entity.moshi.MoshiUtil
import org.cavepvp.entity.moshi.setPrettyPrinting
import java.io.File
import java.io.FileWriter
import java.io.IOException

object EntityRepository {

    lateinit var container: File

    fun findAll():List<Entity> {
        return (this.container.listFiles() ?: arrayOf())
            .filterNotNull()
            .mapNotNull{JsonReader.of(FileSystem.SYSTEM.source(it).buffer())}
            .mapNotNull{MoshiUtil.instance.adapter(Entity::class.java).fromJson(it)}
    }

    fun onLoad() {
        this.container = File("${EntityPlugin.instance.dataFolder.absolutePath}/entities")
        this.container.mkdir()
    }

    fun updateById(entity: Entity) {

        val writer = FileWriter(entity.file)

        try {
            writer.write(MoshiUtil.instance.adapter(Entity::class.java).setPrettyPrinting().toJson(entity))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }

        writer.close()
    }

    fun deleteById(entity: Entity) {

        if (!entity.file.exists()) {
            return
        }

        entity.file.delete()
    }

}