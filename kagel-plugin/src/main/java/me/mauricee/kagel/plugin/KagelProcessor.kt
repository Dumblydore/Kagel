package me.mauricee.kagel.plugin

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import me.mauricee.kagel.BindView
import me.mauricee.kagel.plugin.generation.ViewGenerator
import kotlin.reflect.KClass

class KagelProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val viewGenerator = ViewGenerator(environment)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val listedFunctions: Sequence<KSFunctionDeclaration> =
            resolver.findAnnotations(BindView::class)
        if (!listedFunctions.iterator().hasNext()) return emptyList()
        listedFunctions.forEach(viewGenerator::generateView)

//        val sourceFiles = listedFunctions.mapNotNull { it.containingFile }
//        val fileText = buildString {
//            append("// ")
//            append(functionNames.joinToString(", "))
//        }
//        val file = environment.codeGenerator.createNewFile(
//            Dependencies(
//                false,
//                *sourceFiles.toList().toTypedArray(),
//            ),
//            "your.generated.file.package",
//            "GeneratedLists",
//        )

//        file.write(fileText.toByteArray())
        return (listedFunctions).filterNot { it.validate() }.toList()
    }

    private fun Resolver.findAnnotations(kClass: KClass<*>) =
        getSymbolsWithAnnotation(kClass.qualifiedName.toString())
            .filterIsInstance<KSFunctionDeclaration>()
}
