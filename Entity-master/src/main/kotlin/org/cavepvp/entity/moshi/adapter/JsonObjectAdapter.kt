package org.cavepvp.entity.moshi.adapter

import org.cavepvp.entity.moshi.MoshiUtil
import org.cavepvp.entity.moshi.json.JsonObject
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object JsonObjectAdapter {

    @ToJson
    fun toJson(json: JsonObject):String {
        return MoshiUtil.instance.adapter<Map<String,Any>>(MoshiUtil.STRING_TO_ANY_MAP_TYPE).toJson(json.get())
    }

    @FromJson
    fun fromJson(json: String): JsonObject {
        return JsonObject(MoshiUtil.instance.adapter<Map<String,Any>>(MoshiUtil.STRING_TO_ANY_MAP_TYPE).fromJson(json)!!)
    }

}