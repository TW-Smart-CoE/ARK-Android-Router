package com.thoughtworks.ark.router.compiler

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.typeNameOf
import com.thoughtworks.ark.router.annotation.ServiceData
import com.thoughtworks.ark.router.module.Module

@OptIn(ExperimentalStdlibApi::class)
internal class Generator(
    private val packageName: String,
    private val className: String,
    private val schemeMap: Map<String, String>,
    private val serviceMap: Map<String, String>,
    private val serviceImplMap: Map<String, ServiceImplInfo>,
) {
    private val moduleClass = Module::class.asClassName()

    private val mapClass = typeNameOf<HashMap<String, Class<*>>>()
    private val mapDataClass =
        HashMap::class.asClassName().parameterizedBy(String::class.asClassName(), ServiceData::class.asClassName())

    fun generate(): FileSpec {
        val companion = TypeSpec.companionObjectBuilder()
            .addFunction(
                FunSpec.builder("doNothing")
                    .returns(moduleClass)
                    .addStatement("return $className()")
                    .build()
            )
            .build()
        val classBuilder = TypeSpec.classBuilder(className)
            .addSuperinterface(moduleClass)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addProperty(
                PropertySpec.builder("schemeMap", mapClass)
                    .initializer("hashMapOf<String,  Class<*>>()")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("serviceMap", mapClass)
                    .initializer("hashMapOf<String, Class<*>>()")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("serviceImplMap", mapDataClass)
                    .initializer("hashMapOf<String, ServiceData>()")
                    .build()
            )
            .addInitializerBlock(
                generateSchemeMapBlock()
            )
            .addInitializerBlock(
                generateServiceMapBlock()
            )
            .addInitializerBlock(
                generateServiceImplMapBlock()
            )
            .addFunction(
                FunSpec.builder("getScheme")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return schemeMap")
                    .returns(mapClass)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getService")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return serviceMap")
                    .returns(mapClass)
                    .build()
            )
            .addFunction(
                FunSpec.builder("getServiceImpl")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return serviceImplMap")
                    .returns(mapDataClass)
                    .build()
            )
            .addType(companion)

        return FileSpec.builder(packageName, className)
            .addType(classBuilder.build())
            .build()
    }

    private fun generateSchemeMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        schemeMap.forEach { (k, v) ->
            builder.addStatement("""schemeMap["$k"] = $v::class.java """)
        }
        return builder.build()
    }

    private fun generateServiceMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        serviceMap.forEach { (k, v) ->
            builder.addStatement("""serviceMap["$k"] = $v::class.java """)
        }
        return builder.build()
    }

    private fun generateServiceImplMapBlock(): CodeBlock {
        val builder = CodeBlock.Builder()
        serviceImplMap.forEach { (k, v) ->
            builder.addStatement(
                """serviceImplMap["$k"] = ServiceData(cls=${(v.className)}::class.java, singleton=${v.singleton}) """
            )
        }
        return builder.build()
    }
}