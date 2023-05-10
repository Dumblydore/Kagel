package me.mauricee.kagel.plugin.generation

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.mauricee.kagel.ParameterInfo
import me.mauricee.kagel.plugin.ClassNames
import me.mauricee.kagel.plugin.parameter.DefaultParameterProviders
import java.lang.IllegalStateException
import javax.lang.model.element.ExecutableElement

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
    @OptIn(KspExperimental::class)
    fun generateView(function: KSFunctionDeclaration) {
        /* Pull function declaration */
        val className = function.simpleName.asString()
        val parameters = function.parameters

        /* Generate Views based on function declaration information*/
        val viewClass = ClassName(function.packageName.asString(), "${className}View")
        FileSpec.builder(viewClass)
            .addImport(ClassNames.mutableStateOf, "")
            .addType(
                TypeSpec.classBuilder(viewClass)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("context", ClassNames.androidContext)
                            .build()
                    )
                    .superclass(ClassNames.composeView)
                    .addSuperclassConstructorParameter("context")
                    .addFunction(
                        FunSpec.builder("Content")
                            .addAnnotation(ClassNames.composable)
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement("${className}(%L)", buildCodeBlock {
                                for (parameter in parameters) {
                                    add(
                                        "%N = _%N.value",
                                        parameter.name!!.asString(),
                                        parameter.name!!.asString()
                                    )
                                    if (parameter != parameters.last()) {
                                        add(", ")
                                    }
                                }
                            }) //TODO: Add parameters
                            .build()
                    )
                    .apply {
                        for (parameter in parameters) {
                            assert(parameter.name != null) { "function $className has a parameter with a missing name!" }
                            /* Add backing state property */
                            val initialValue =
                                if (parameter.type.isAnnotationPresent(ParameterInfo::class)) {
                                    parameter.type.getAnnotationsByType(ParameterInfo::class)
                                        .first()
                                        .provider.constructors.first().call().get()
                                } else {
                                    DefaultParameterProviders[parameter.type.toTypeName()]!!.get()
//                                 throw IllegalStateException("thing: ${parameter.type.toTypeName().javaClass}")
                                }
                            addProperty(
                                PropertySpec.builder(
                                    "_${parameter.name!!.asString()}",
                                    ClassNames.mutableState.parameterizedBy(
                                        parameter.type.toTypeName(),
                                    ),
                                    KModifier.PRIVATE
                                ).initializer("mutableStateOf(%L)",initialValue).mutable().build(),
                            ).build()
                            /* Add public property */
                            addProperty(
                                PropertySpec.builder(
                                    parameter.name!!.asString(),
                                    parameter.type.toTypeName()
                                ).getter(
                                    FunSpec.getterBuilder()
                                        .addStatement("return _${parameter.name!!.asString()}.value")
                                        .build()
                                ).setter(
                                    FunSpec.setterBuilder()
                                        .addParameter(
                                            ParameterSpec.builder("value", parameter.type.toTypeName()).build()
                                        )
                                        .addStatement("_${parameter.name!!.asString()}.value = value")
                                        .build()
                                )
                                    .mutable().build(),
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
