package me.mauricee.kagel.plugin

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toTypeName
import me.mauricee.kagel.BindView
import me.mauricee.kagel.ParameterInfo
import me.mauricee.kagel.plugin.generation.ComposableReference
import me.mauricee.kagel.plugin.generation.ViewGenerator
import me.mauricee.kagel.plugin.generation.parameter.Parameter
import me.mauricee.kagel.plugin.ksp.KspFileWriter
import kotlin.reflect.KClass

class KagelProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val fileWriter = KspFileWriter(environment.codeGenerator)
    private val viewGenerator = ViewGenerator(fileWriter)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val listedFunctions: Sequence<KSFunctionDeclaration> =
            resolver.findAnnotations(BindView::class)
        if (!listedFunctions.iterator().hasNext()) return emptyList()
        listedFunctions
            .map(KSFunctionDeclaration::toFunction)
            .forEach(viewGenerator::generateView)

        return (listedFunctions).filterNot { it.validate() }.toList()
    }
}

private fun KSFunctionDeclaration.toFunction(): ComposableReference<KSFile> {
    val parameters = parameters.map(KSValueParameter::toParameter)
    return ComposableReference(
        name = simpleName.asString(),
        packageName = packageName.asString(),
        containingFile = containingFile,
        parameters = parameters,
    )
}

@OptIn(KspExperimental::class)
fun KSValueParameter.toParameter(): Parameter {
    return Parameter(
        name = name!!.asString(),
        type = type.toTypeName(),
        provider = if (type.isAnnotationPresent(ParameterInfo::class)) {
            type.getAnnotationsByType(ParameterInfo::class)
                ?.firstOrNull()
                ?.provider
                ?.constructors
                ?.firstOrNull()
                ?.call()
        } else {
            null
        },
    )
}

private fun Resolver.findAnnotations(kClass: KClass<*>) =
    getSymbolsWithAnnotation(kClass.qualifiedName.toString())
        .filterIsInstance<KSFunctionDeclaration>()
