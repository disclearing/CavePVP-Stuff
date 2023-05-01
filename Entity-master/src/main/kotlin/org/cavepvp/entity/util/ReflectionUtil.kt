package org.cavepvp.entity.util

object ReflectionUtil {

    @JvmStatic
    fun getField(instance: Any,fieldName: String):Any? {
        return try {
            instance::class.java.getField(fieldName).get(instance)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getDeclaredField(instance: Any, fieldName: String): Any? {

        val field = try {
            instance::class.java.getDeclaredField(fieldName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        field.isAccessible = true

        return field[instance]
    }

    @JvmStatic
    fun setField(instance: Any,fieldName: String,value: Any) {

        val field = try {
            instance::class.java.getField(fieldName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }

        field.isAccessible = true
        field.set(instance,value)
    }

    @JvmStatic
    fun setDeclaredField(instance: Any,fieldName: String,value: Any) {

        val field = try {
            instance::class.java.getDeclaredField(fieldName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }

        field.isAccessible = true
        field.set(instance,value)
    }

}