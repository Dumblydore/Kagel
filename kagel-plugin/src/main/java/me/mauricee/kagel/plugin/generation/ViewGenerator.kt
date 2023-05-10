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
import me.mauricee.kagel.plugin.generation.parameter.Parameter

/**
 * Generates Kotlin code for a Composable function and its parameters, and sends the results to CodeGenWriter.
 * This class is responsible for converting a Composable into a wrapped custom View.
 *
 * The `ViewGenerator` class uses KotlinPoet to generate Kotlin code for the Composable function and its parameters,
 * and sends the results to a CodeGenWriter. The CodeGenWriter is responsible for writing the generated code to a file.
 * By default, the generated code includes a Custom View class that wraps the Composable function, along with any necessary
 * imports and constructor parameters.
 */
class ViewGenerator<T>(private val writer: CodeGenWriter<T>) {

    /**
     * Generates a custom view from the given Composable function reference and writes it to a custom file.
     */
    fun generateView(ref: ComposableReference<T>) {
        /* Pull function declaration */
        val className = ref.name
        val parameters = ref.parameters

        /* Generate Views based on function declaration information*/
        val viewClass = ClassName(ref.packageName, "${className}View")

        val customView = createClass(viewClass)
            .addProperties(parameters)
            .addContent(ref.name, parameters)
            .build()

        val fileSpec = FileSpec.builder(viewClass)
            .addImport(ClassNames.mutableStateOf, "")
            .addType(customView)
            .build()

        writer.write(fileSpec, ref.containingFile)
    }
}

// TODO allow for custom attrs
private fun createClass(viewClass: ClassName): TypeSpec.Builder =
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

/*
 * TODO: Eventually we need to convert things like compose.Color to their Legacy View counterparts
 * Generation should be something like this:
 *
 *
 * private val _color: MutableState<Color> = mutableStateOf(initialValue)
 * @ColorInt
 * var color: Int
 * get() = _color.toArgb()
 * set(value) {
 *      _color.value = Color(value)
 * }
 *
 *  I'm not exactly sure if we should do this with dimensions.
 */
private fun TypeSpec.Builder.addProperties(parameters: List<Parameter>): TypeSpec.Builder = apply {
    for (parameter in parameters) {
        val initialValue = when {
            parameter.provider != null -> parameter.provider
            DefaultParameterProviders.containsKey(parameter.type) -> DefaultParameterProviders[parameter.type]!!.get()
            else -> throw IllegalStateException("No provider available for Parameter: ${parameter.name}: ${parameter.type}!")
        }

        /* Add backing state property */
        addProperty(
            PropertySpec.builder(
                "_${parameter.name}",
                ClassNames.mutableState.parameterizedBy(parameter.type),
                KModifier.PRIVATE,
            ).initializer("mutableStateOf(%L)", initialValue).mutable().build(),
        ).addProperty(
            PropertySpec.builder(parameter.name, parameter.type)
                .mutable()
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement("return _${parameter.name}.value")
                        .build(),
                )
                .setter(
                    FunSpec.setterBuilder()
                        .addParameter(ParameterSpec.builder("value", parameter.type).build())
                        .addStatement("_%L.value = value", parameter.name)
                        .build(),
                ).build(),
        )
    }
}

private fun TypeSpec.Builder.addContent(
    className: String,
    parameters: List<Parameter>,
): TypeSpec.Builder = addFunction(
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
        ).build(),
)
