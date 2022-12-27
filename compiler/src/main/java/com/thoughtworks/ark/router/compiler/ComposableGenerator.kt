package com.thoughtworks.ark.router.compiler

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName.Companion.get
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.thoughtworks.ark.router.compiler.ComposableHelper.composableLambdaType
import com.thoughtworks.ark.router.compiler.ComposableHelper.paramsComposableLambdaType
import com.thoughtworks.ark.router.compiler.ComposableHelper.paramsViewModelComposableLambdaType
import com.thoughtworks.ark.router.compiler.ComposableHelper.viewModelComposableLambdaType
import java.io.File

internal object ComposableHelper {
    private val composeAnnotationCls = ClassName("androidx.compose.runtime", "Composable")
    private val composeAnnotation = AnnotationSpec.builder(composeAnnotationCls).build()

    private val bundleCls = ClassName("android.os", "Bundle")
    private val bundleParams = ParameterSpec.unnamed(bundleCls)

    private val anyParams = ParameterSpec.unnamed(Any::class)

    private val unitType = Unit::class.asTypeName()

    val composableLambdaType =
        get(returnType = unitType).copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val viewModelComposableLambdaType = get(parameters = listOf(anyParams), returnType = unitType)
        .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val paramsComposableLambdaType = get(parameters = listOf(bundleParams), returnType = unitType)
        .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val paramsViewModelComposableLambdaType =
        get(parameters = listOf(bundleParams, anyParams), returnType = unitType)
            .copy(annotations = arrayListOf(composeAnnotation), nullable = true)

    val superCls = ClassName("com.thoughtworks.ark.router.compose", "SchemeComposable")
}

internal class ComposableGenerator(private val composableList: List<ComposableInfo>) {
    fun generate(file: File) {
        composableList.forEach {
            createFileSpec(it).writeTo(file)
        }
    }

    private fun createFileSpec(composableInfo: ComposableInfo): FileSpec {
        val classBuilder = TypeSpec.classBuilder("${composableInfo.methodName}Composable")
            .superclass(ComposableHelper.superCls)
            .apply {
                if (composableInfo.hasBundle) {
                    if (composableInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("paramsViewModelComposable", paramsViewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle, viewModel -> ${composableInfo.methodName}(bundle, viewModel as ${composableInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composableInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("paramsComposable", paramsComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { bundle -> ${composableInfo.methodName}(bundle) }""".trimIndent()
                                )
                                .build()
                        )
                    }
                } else {
                    if (composableInfo.viewModelName.isNotEmpty()) {
                        addProperty(
                            PropertySpec.builder("viewModelComposable", viewModelComposableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { viewModel -> ${composableInfo.methodName}(viewModel as ${composableInfo.viewModelName}) }""".trimIndent()
                                )
                                .build()
                        )
                        addProperty(
                            PropertySpec.builder("viewModelClass", String::class)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """ "${composableInfo.viewModelName}" """.trimIndent()
                                )
                                .build()
                        )
                    } else {
                        addProperty(
                            PropertySpec.builder("composable", composableLambdaType)
                                .addModifiers(KModifier.OVERRIDE)
                                .initializer(
                                    """@Composable { ${composableInfo.methodName}() }""".trimIndent()
                                )
                                .build()
                        )
                    }
                }
            }

        return FileSpec.builder(COMPOSABLE_PACKAGE_NAME, "${composableInfo.methodName}Composable")
            .addType(classBuilder.build())
            .addImport(ClassName(composableInfo.packageName, composableInfo.methodName), "")
            .addImport(ComposableHelper.superCls, "")
            .build()
    }
}