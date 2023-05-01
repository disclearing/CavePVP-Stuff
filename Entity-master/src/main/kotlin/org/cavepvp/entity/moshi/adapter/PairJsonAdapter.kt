package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object PairJsonAdapter : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>,moshi: Moshi): JsonAdapter<*>? {

        if (type !is ParameterizedType) {
            return null
        }

        if (type.rawType != Pair::class.java) {
            return null
        }

        return PairAdapter(
            moshi.adapter(type.actualTypeArguments[0]),
            moshi.adapter(type.actualTypeArguments[1]),
            moshi.adapter(Types.newParameterizedType(List::class.java,String::class.java))
        )
    }

    private class PairAdapter(
        private val firstAdapter: JsonAdapter<Any>,
        private val secondAdapter: JsonAdapter<Any>,
        private val listAdapter: JsonAdapter<List<String>>
    ) : JsonAdapter<Pair<Any, Any>>() {

        override fun toJson(writer: JsonWriter,value: Pair<Any, Any>?) {
            writer.beginArray()
            this.firstAdapter.toJson(writer,value?.first)
            this.secondAdapter.toJson(writer,value?.second)
            writer.endArray()
        }

        override fun fromJson(reader: JsonReader): Pair<Any, Any>? {

            val list = this.listAdapter.fromJson(reader) ?: return null

            require(list.size == 2) {
                "pair with more or less than two elements: $list"
            }

            val first = this.firstAdapter.fromJsonValue(list[0])!!
            val second = this.secondAdapter.fromJsonValue(list[1])!!

            return first to second
        }

    }
}