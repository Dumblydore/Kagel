package me.mauricee.kagel.plugin.generation

import com.google.devtools.ksp.symbol.KSFile
import me.mauricee.kagel.plugin.generation.parameter.Parameter

data class Function(
    val name: String,
    val packageName: String,
    val containingFile: KSFile?,
    val parameters: List<Parameter>
)