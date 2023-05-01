package org.cavepvp.entity.animation

object EntityAnimationRegistry {

    private val animations = mutableMapOf<String,EntityAnimation>()

    fun register(animation: EntityAnimation) {
        this.animations[animation.getName()] = animation
    }

    fun getAllAnimations():List<EntityAnimation> {
        return this.animations.values.toList()
    }

    fun getAnimationByName(name: String):EntityAnimation? {
        return this.animations[name]
    }

}