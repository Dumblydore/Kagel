package me.mauricee.kagel

import kotlin.reflect.KClass

annotation class ParameterInfo<P : Any>(
    val name: String,
    val provider: KClass<out ParameterProvider<P>>,
)

// TODO We should create a resource based ParameterProvider
interface ParameterProvider<P : Any> {
    fun get(): P
}
