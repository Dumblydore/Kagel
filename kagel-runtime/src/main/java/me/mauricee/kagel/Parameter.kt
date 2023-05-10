package me.mauricee.kagel

import kotlin.reflect.KClass

//annotation class Annotated<C: Contract>(val contract: KClass<C>)
annotation class ParameterInfo<P: Any>(
    val name: String,
    val provider: KClass<out ParameterProvider<P>>
)

interface ParameterProvider<P: Any> {
    fun get(): P
}