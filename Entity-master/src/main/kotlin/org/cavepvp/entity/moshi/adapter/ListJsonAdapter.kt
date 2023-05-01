package org.cavepvp.entity.moshi.adapter


import java.lang.reflect.Type
import java.util.ArrayList

import com.squareup.moshi.*
import java.io.IOException

/**
 * @project api
 *
 * @date 05/16/21
 * @author xanderume@gmail.com
 */
object ListJsonAdapter : JsonAdapter.Factory {

    private val EMPTY_LIST = emptyList<Nothing>()
    private val SINGLE_TON_LIST = listOf("")

    override fun create(type: Type,annotations: MutableSet<out Annotation>,moshi: Moshi): JsonAdapter<*>? {

        val raw = Types.getRawType(type)

        if (raw == ArrayList::class.java || raw == MutableList::class.java || raw == EMPTY_LIST::class.java || raw == SINGLE_TON_LIST::class.java) {
            return newArrayListAdapter<Any>(type,moshi).nullSafe()
        }

        return null
    }

    private fun <T> newArrayListAdapter(type: Type,moshi: Moshi): JsonAdapter<MutableCollection<T>> {
        return object : ListJsonElementAdapter<MutableCollection<T>, T>(moshi.adapter(Types.collectionElementType(type,MutableCollection::class.java))) {
            override fun newCollection(): MutableCollection<T> {
                return mutableListOf()
            }
        }
    }

    abstract class ListJsonElementAdapter<C : MutableCollection<T>?, T>(private val elementAdapter: JsonAdapter<T>) :JsonAdapter<C>() {

        abstract fun newCollection(): C

        @Throws(IOException::class)
        override fun fromJson(reader: JsonReader): C {
            val result = newCollection()

            reader.beginArray()

            while (reader.hasNext()) {
                result?.add(elementAdapter.fromJson(reader)!!)
            }

            reader.endArray()

            return result
        }

        @Throws(IOException::class)
        override fun toJson(writer: JsonWriter,value: C?) {
            writer.beginArray()

            for (element in value!!) {
                this.elementAdapter.toJson(writer,element)
            }

            writer.endArray()
        }

        override fun toString(): String {
            return "$elementAdapter.collection()"
        }

    }

}