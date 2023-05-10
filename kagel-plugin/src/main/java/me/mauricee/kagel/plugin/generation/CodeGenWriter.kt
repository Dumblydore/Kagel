package me.mauricee.kagel.plugin.generation

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec

interface CodeGenWriter {
    fun write(file: FileSpec, containingFile: KSFile?)
}
