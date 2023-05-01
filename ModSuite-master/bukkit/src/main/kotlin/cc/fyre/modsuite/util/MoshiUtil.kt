package cc.fyre.modsuite.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.Moshi

import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType

/**
 * @project carnage
 *
 * @date 05/22/21
 * @author xanderume@gmail.com
 */
object MoshiUtil {

    private val builder: Moshi.Builder = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())

    var instance: Moshi = builder
        .build()


    private val FACTORIES_FIELD = Moshi.Builder::class.java.getDeclaredField("factories").also{
        it.isAccessible = true
    }

    private val BASE_TYPE_FIELD = PolymorphicJsonAdapterFactory::class.java.getDeclaredField("baseType").also{
        it.isAccessible = true
    }

    private var factories = getFactories().toMutableList()

    fun rebuild(use: (Moshi.Builder) -> Unit) {
        use.invoke(builder)

        instance = builder.build()
        factories = getFactories().toMutableList()
    }

    fun <T> addToPolymorphic(instance: Class<T>,type: Class<out T>,label: String) {

        val indexedValue = factories
            .withIndex()
            .firstOrNull{it.value is PolymorphicJsonAdapterFactory<*> && BASE_TYPE_FIELD.get(it.value) == instance}
            ?: throw IllegalStateException("Failed to find Polymorphic adapter for ${instance.simpleName}")

        val factory = (indexedValue.value as PolymorphicJsonAdapterFactory<T>).withSubtype(type,label)

        factories[indexedValue.index] = factory
        setFactories(factories)
    }

    private fun setFactories(factories: List<Factory>) {
        FACTORIES_FIELD.set(builder,factories)
        instance = builder.build()
    }

    private fun getFactories():List<Factory> {
        return FACTORIES_FIELD.get(builder) as List<Factory>
    }

    fun isJsonObject(value: String):Boolean {

        if (value.length < 2) {
            return false
        }

        return value[0] == '{' && value[value.lastIndex] == '}'
    }

    const val PRETTY_PRINT_INDENT = "    "
}