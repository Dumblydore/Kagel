package me.mauricee.kagel.plugin.generation.parameter

import com.squareup.kotlinpoet.TypeName
import me.mauricee.kagel.ParameterProvider

data class Parameter(
    val name: String,
    val type: TypeName,
    val provider: ParameterProvider<*>?
)