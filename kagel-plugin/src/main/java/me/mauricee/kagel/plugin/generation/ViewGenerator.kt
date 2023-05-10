package me.mauricee.kagel.plugin.generation

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

/*
* public class TestComposableView(context: Context) : AbstractComposeView(context) {
    private var _text: MutableState<String?> = mutableStateOf(null)
    public var text: String?
        get() = _text.value
        set(value) {
            _text.value = value
        }

    @Composable
    override fun Content() {
        TestComposable()
    }
}
*
* */

class ViewGenerator(private val environment: SymbolProcessorEnvironment) {
    fun generateView(function: KSFunctionDeclaration) {
        /* Pull function declaration */
        val className = function.simpleName.asString()
        val parameters = function.parameters

        /* Generate Views based on function declaration information*/
        val viewClass = ClassName(function.packageName.asString(), "${className}View")
        FileSpec.builder(viewClass)
            .addType(
                TypeSpec.classBuilder(viewClass)
                    .apply {
                        for (parameter in parameters) {
                            assert(parameter.name != null) { "function $className has a parameter with a missing name!" }
                            addProperty(
                                PropertySpec.builder(
                                    parameter.name!!.asString(),
                                    parameter.type.toTypeName(),
                                ).build(),
                            ).build()
                        }
                    }.build(),
            )
            .build().writeTo(
                environment.codeGenerator,
                Dependencies(
                    false,
                    *listOfNotNull(function.containingFile).toTypedArray(),
                ),
            )

//        val file = environment.codeGenerator.createNewFile(
//            Dependencies(
//                false,
//                *sourceFiles.toList().toTypedArray(),
//            ),
//            "your.generated.file.package",
//            "GeneratedLists",
//        )
//
//        file.write(fileText.toByteArray())
    }
}
