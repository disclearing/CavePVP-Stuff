package org.cavepvp.entity.moshi

import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.Moshi

import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.addAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.cavepvp.entity.moshi.adapter.*
import org.cavepvp.entity.moshi.adapter.world.WorldJsonAdapter
import org.cavepvp.entity.moshi.adapter.world.WorldNameJsonAdapter
import org.cavepvp.entity.util.ReflectionUtil
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType

/**
 * @project carnage
 *
 * @date 05/22/21
 * @author xanderume@gmail.com
 */
@OptIn(ExperimentalStdlibApi::class)
object MoshiUtil {

    private val builder: Moshi.Builder = Moshi.Builder()
        .add(UUIDJsonAdapter)
        .add(ListJsonAdapter)
        .add(PairJsonAdapter)
        .add(JsonObjectAdapter)
        .add(SerializeNullAdapter)
        .add(AtomicIntegerJsonAdapter)
        .add(WorldJsonAdapter)
        .add(WorldNameJsonAdapter)
        .add(LegacyItemStackJsonAdapter)
        .add(FireworkEffectJsonAdapter)
        .addAdapter(PotionEffectJsonAdapter)
        .addAdapter(LocationJsonAdapter)
        .add(LocalDateTimeJsonAdapter)
        .addLast(KotlinJsonAdapterFactory())

    var instance: Moshi = builder
        .build()

    private var factories = getFactories().toMutableList()

    fun rebuild(use: (Moshi.Builder) -> Unit) {
        use.invoke(builder)

        instance = builder.build()
        factories = getFactories().toMutableList()
    }

    fun <T> addToPolymorphic(instance: Class<T>,type: Class<out T>,label: String) {

        val indexedValue = factories
            .withIndex()
            .firstOrNull{it.value is PolymorphicJsonAdapterFactory<*> && ReflectionUtil.getDeclaredField(it.value,"baseType") == instance}
            ?: throw IllegalStateException("Failed to find Polymorphic adapter for ${instance.simpleName}")

        val factory = (indexedValue.value as PolymorphicJsonAdapterFactory<T>).withSubtype(type,label)

        factories[indexedValue.index] = factory
        setFactories(factories)
    }

    private fun setFactories(factories: List<Factory>) {
        ReflectionUtil.setDeclaredField(builder,"factories",factories)
        instance = builder.build()
    }

    private fun getFactories():List<Factory> {
        return ReflectionUtil.getDeclaredField(builder,"factories") as List<Factory>
    }

    val LIST_STRING_TYPE: ParameterizedType = Types.newParameterizedType(List::class.java,String::class.java)
    val STRING_TO_ANY_MAP_TYPE: ParameterizedType = Types.newParameterizedType(Map::class.java,String::class.java,Any::class.java)

    const val PRETTY_PRINT_INDENT = "    "
}