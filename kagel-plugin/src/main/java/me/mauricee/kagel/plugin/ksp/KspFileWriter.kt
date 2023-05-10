package me.mauricee.kagel.plugin.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import me.mauricee.kagel.plugin.generation.CodeGenWriter

class KspFileWriter(private val codeGenerator: CodeGenerator) : CodeGenWriter {
    override fun write(file: FileSpec, containingFile: KSFile?) {
        val dependencies = Dependencies(false, *listOfNotNull(containingFile).toTypedArray())
        file.writeTo(codeGenerator, dependencies)
    }
}
