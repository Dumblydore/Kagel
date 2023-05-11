@file:OptIn(KotlinPoetMetadataPreview::class)

package me.mauricee.kagel.plugin.apt

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.classinspectors.ElementsClassInspector
import com.squareup.kotlinpoet.metadata.specs.ClassInspector
import me.mauricee.kagel.BindView
import me.mauricee.kagel.plugin.api.ComposableReference
import me.mauricee.kagel.plugin.api.ViewGenerator
import me.mauricee.kagel.plugin.api.parameter.Parameter
import me.mauricee.kagel.plugin.apt.AnnotationProcessor.Companion.KAPT_KOTLIN_GENERATED_OPTION_NAME
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

@OptIn(KotlinPoetMetadataPreview::class)
@AutoService(Processor::class)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
class AnnotationProcessor : AbstractProcessor() {
    private lateinit var codeGenWriter: AptFileWriter
    private lateinit var generator: ViewGenerator<File>
    private lateinit var classInspector: ClassInspector

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        codeGenWriter = AptFileWriter(processingEnv)
        generator = ViewGenerator(codeGenWriter)
        classInspector =
            ElementsClassInspector.create(processingEnv.elementUtils, processingEnv.typeUtils)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        mutableSetOf(BindView::class.java.name)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment,
    ): Boolean {
        roundEnv.getElementsAnnotatedWith(BindView::class.java)
            .asSequence()
            .filterIsInstance<ExecutableElement>()
            .map { it.toComposableReference(processingEnv) }
            .forEach(generator::generateView)
        return false
    }
}

private fun ExecutableElement.toComposableReference(environment: ProcessingEnvironment): ComposableReference<File> {
    return ComposableReference(
        name = simpleName.toString(),
        packageName = environment.elementUtils.getPackageOf(this).toString(),
        containingFile = environment.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let(::File),
        parameters = parameters.filterIsInstance<VariableElement>()
            .map(VariableElement::toParameter),
    )
}

@OptIn(KotlinPoetMetadataPreview::class)
private fun VariableElement.toParameter(): Parameter = Parameter(
    name = simpleName.toString(),
    type = Class.forName(asType().asTypeName().toString()).kotlin.asTypeName(),
    provider = null, // TODO
)
