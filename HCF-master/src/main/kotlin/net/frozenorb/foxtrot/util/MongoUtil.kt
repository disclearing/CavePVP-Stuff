package net.frozenorb.foxtrot.util

import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings

object MongoUtil {

    val UPDATE_OPTIONS: UpdateOptions = UpdateOptions().upsert(true)
    val REPLACE_OPTIONS: ReplaceOptions = ReplaceOptions().upsert(true)
    val RELAXED_WRITE_SETTING: JsonWriterSettings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build()

}