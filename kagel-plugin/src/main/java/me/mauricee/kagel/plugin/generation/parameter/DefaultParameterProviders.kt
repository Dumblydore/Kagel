package me.mauricee.kagel.plugin.generation.parameter

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import me.mauricee.kagel.ParameterProvider


object DefaultByteParameterProvider : ParameterProvider<Byte> {
    override fun get(): Byte = 0
}

object DefaultShortParameterProvider : ParameterProvider<Short> {
    override fun get(): Short = 0
}

object DefaultIntParameterProvider : ParameterProvider<Int> {
    override fun get(): Int = 0
}

object DefaultLongParameterProvider : ParameterProvider<Long> {
    override fun get(): Long = 0
}

object DefaultFloatParameterProvider : ParameterProvider<Float> {
    override fun get(): Float = 0f
}

object DefaultDoubleParameterProvider : ParameterProvider<Double> {
    override fun get(): Double = 0.0
}

object DefaultBooleanParameterProvider : ParameterProvider<Boolean> {
    override fun get(): Boolean = false
}

object DefaultCharParameterProvider : ParameterProvider<Char> {
    override fun get(): Char = ' '
}

object DefaultStringParameterProvider : ParameterProvider<String> {
    override fun get(): String = "\"\""
}

class DefaultListParameterProvider<A : Any>() : ParameterProvider<List<A>> {
    override fun get(): List<A> = emptyList()
}

class DefaultSetParameterProvider<A : Any>() : ParameterProvider<Set<A>> {
    override fun get(): Set<A> = emptySet()
}

class DefaultMapParameterProvider<K : Any, V : Any>() : ParameterProvider<Map<K, V>> {
    override fun get(): Map<K, V> = emptyMap()
}

val DefaultParameterProviders: Map<ClassName, ParameterProvider<out Any>> = mapOf(
    Byte::class.asTypeName() to DefaultByteParameterProvider,
    Short::class.asTypeName() to DefaultShortParameterProvider,
    Int::class.asTypeName() to DefaultIntParameterProvider,
    Long::class.asTypeName() to DefaultLongParameterProvider,
    Float::class.asTypeName() to DefaultFloatParameterProvider,
    Double::class.asTypeName() to DefaultDoubleParameterProvider,
    Boolean::class.asTypeName() to DefaultBooleanParameterProvider,
    Char::class.asTypeName() to DefaultCharParameterProvider,
    String::class.asTypeName() to DefaultStringParameterProvider,
)
