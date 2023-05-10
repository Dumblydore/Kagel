package me.mauricee.kagel.plugin.generation

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import me.mauricee.kagel.plugin.ClassNames
import me.mauricee.kagel.plugin.generation.parameter.DefaultParameterProviders

class ViewGenerator(private val writer: CodeGenWriter) {
    fun generateView(function: Function) {
        /* Pull function declaration */
        val className = function.name
        val parameters = function.parameters

        /* Generate Views based on function declaration information*/
        val viewClass = ClassName(function.packageName, "${className}View")
        val fileSpec = FileSpec.builder(viewClass)
            .addImport(ClassNames.mutableStateOf, "")
            .addType(
                TypeSpec.classBuilder(viewClass)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addAnnotation(JvmOverloads::class.asTypeName())
                            .addParameter("context", ClassNames.androidContext)
                            .addParameter("attrs", ClassNames.attributeSet.copy(nullable = true))
                            .addParameter(
                                ParameterSpec.builder("defStyleAttr", Int::class.asTypeName())
                                    .defaultValue("%L", 0)
                                    .build(),
                            )
                            .build(),
                    )
                    .superclass(ClassNames.composeView)
                    .addSuperclassConstructorParameter(
                        "%L, %L, %L",
                        "context",
                        "attrs",
                        "defStyleAttr",
                    )
                    .addFunction(
                        FunSpec.builder("Content")
                            .addAnnotation(ClassNames.composable)
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement(
                                "$className(%L)",
                                buildCodeBlock {
                                    for (parameter in parameters) {
                                        add(
                                            "%N = _%N.value",
                                            parameter.name,
                                            parameter.name,
                                        )
                                        if (parameter != parameters.last()) {
                                            add(", ")
                                        }
                                    }
                                },
                            ) // TODO: Add parameters
                            .build(),
                    )
                    .apply {
                        for (parameter in parameters) {
                            /* Add backing state property */
                            val initialValue = parameter.provider
                                ?: DefaultParameterProviders[parameter.type]!!.get()
                            addProperty(
                                PropertySpec.builder(
                                    "_${parameter.name}",
                                    ClassNames.mutableState.parameterizedBy(
                                        parameter.type,
                                    ),
                                    KModifier.PRIVATE,
                                ).initializer("mutableStateOf(%L)", initialValue).mutable().build(),
                            ).build()
                            /* Add public property */
                            addProperty(
                                PropertySpec.builder(
                                    parameter.name,
                                    parameter.type,
                                ).getter(
                                    FunSpec.getterBuilder()
                                        .addStatement("return _${parameter.name}.value")
                                        .build(),
                                ).setter(
                                    FunSpec.setterBuilder()
                                        .addParameter(
                                            ParameterSpec.builder(
                                                "value",
                                                parameter.type,
                                            ).build(),
                                        )
                                        .addStatement("_${parameter.name}.value = value")
                                        .build(),
                                )
                                    .mutable().build(),
                            ).build()
                        }
                    }.build(),
            ).build()

        writer.write(fileSpec, function.containingFile)
    }
}
