package me.mauricee.kagel.plugin.apt

import com.squareup.kotlinpoet.FileSpec
import me.mauricee.kagel.plugin.api.CodeGenWriter
import java.io.File
import javax.annotation.processing.ProcessingEnvironment

class AptFileWriter(private val processingEnv: ProcessingEnvironment) : CodeGenWriter<File> {
    override fun write(file: FileSpec, containingFile: File?) {
        val kaptKotlinGeneratedDir = processingEnv.options[AnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}