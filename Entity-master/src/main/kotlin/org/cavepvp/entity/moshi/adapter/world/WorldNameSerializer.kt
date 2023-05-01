package org.cavepvp.entity.moshi.adapter.world

import com.squareup.moshi.JsonQualifier

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD,AnnotationTarget.FUNCTION,AnnotationTarget.TYPE_PARAMETER,AnnotationTarget.VALUE_PARAMETER)
@JsonQualifier
annotation class WorldNameSerializer
