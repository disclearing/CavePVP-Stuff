package net.frozenorb.foxtrot.brewer

import com.mongodb.client.model.DeleteOneModel
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOneModel
import com.mongodb.client.model.WriteModel
import net.frozenorb.foxtrot.Foxtrot
import net.frozenorb.foxtrot.util.MongoUtil
import org.bson.Document

object FancyBrewerRepository {

    private val collection = Foxtrot.instance.mongoPool.getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("brewers")

    fun findAll():List<FancyBrewer> {
        return this.collection.find().mapNotNull{Foxtrot.GSON.fromJson(it.toJson(MongoUtil.RELAXED_WRITE_SETTING),FancyBrewer::class.java)}
    }

    fun saveAll() {

        val iterator = FancyBrewerHandler.getAllUpdates().iterator()
        val bulkWrite = mutableListOf<WriteModel<Document>>()

        while (iterator.hasNext()) {

            val brewer = FancyBrewerHandler.getBrewerById(iterator.next())

            if (brewer != null) {
                bulkWrite.add(ReplaceOneModel(
                    Filters.eq("_id",brewer.id.toString()),
                    Document.parse(Foxtrot.GSON.toJson(brewer)),
                    MongoUtil.REPLACE_OPTIONS
                ))
            }

            iterator.remove()
        }

        if (bulkWrite.isEmpty()) {
            return
        }

        this.collection.bulkWrite(bulkWrite)
    }

    fun deleteAll() {

        val iterator = FancyBrewerHandler.getAllDeletes().iterator()
        val bulkWrite = mutableListOf<DeleteOneModel<Document>>()

        while (iterator.hasNext()) {
            bulkWrite.add(DeleteOneModel(Filters.eq("_id",iterator.next().toString())))
            iterator.remove()
        }

        if (bulkWrite.isEmpty()) {
            return
        }

        this.collection.bulkWrite(bulkWrite)
    }


}