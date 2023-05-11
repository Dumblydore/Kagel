package me.mauricee.kagel.plugin.api.parameter

import com.squareup.kotlinpoet.TypeName
import me.mauricee.kagel.ParameterProvider

/**
 * A data class that represents a parameter of a Composable function.
 * It can also store additional information, such as a provider function or a resource ID.
 **/
data class Parameter(
    val name: String,
    val type: TypeName,
    val provider: ParameterProvider<*>?
)
