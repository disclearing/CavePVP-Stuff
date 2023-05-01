package org.cavepvp.entity.moshi.adapter

import org.cavepvp.entity.moshi.annotation.SerializeNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * @project carnage
 *
 * @date 05/22/21
 * @author xanderume@gmail.com
 */
object SerializeNullAdapter : JsonAdapter.Factory {

    override fun create(type: Type,annotations: MutableSet<out Annotation>,moshi: Moshi): JsonAdapter<Any>? {

        val annotation = Types.nextAnnotations(annotations, SerializeNull::class.java)

        if (annotation == null || annotation.isEmpty()) {
            return null
        }

        return moshi.nextAdapter<Any>(this,type,annotation).serializeNulls()
    }

}