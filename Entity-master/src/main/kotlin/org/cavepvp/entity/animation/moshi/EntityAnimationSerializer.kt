package org.cavepvp.entity.animation.moshi

import com.squareup.moshi.JsonQualifier

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD,AnnotationTarget.FUNCTION,AnnotationTarget.TYPE_PARAMETER,AnnotationTarget.VALUE_PARAMETER)
@JsonQualifier
annotation class EntityAnimationSerializer