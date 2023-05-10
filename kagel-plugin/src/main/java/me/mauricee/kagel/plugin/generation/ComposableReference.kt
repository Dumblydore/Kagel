package me.mauricee.kagel.plugin.generation

import me.mauricee.kagel.plugin.generation.parameter.Parameter

/**
 * A data class that represents a reference to a Composable function.
 * This class is used to store information about the Composable, such as its name, package name, and parameters.
 *
 * The `ComposableReference` class stores information about a Composable function, such as its name, package name,
 * and parameters. The `containingFile` property is optional and can be used to specify the file that contains the
 * Composable function. This can be useful for tools that need to generate code for Composable functions across multiple
 * files or modules.
 */
data class ComposableReference<T>(
    val name: String,
    val packageName: String,
    val containingFile: T?,
    val parameters: List<Parameter>
)