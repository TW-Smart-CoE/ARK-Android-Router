package com.thoughtworks.ark.router.compiler

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import com.thoughtworks.ark.router.annotation.Scheme
import com.thoughtworks.ark.router.annotation.Service
import com.thoughtworks.ark.router.annotation.ServiceImpl
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
class Compiler : AbstractProcessor() {
    private var packageName = ""
    private var className = ""

    private val schemeMap = mutableMapOf<String, String>()
    private val serviceMap = mutableMapOf<String, String>()
    private val serviceImplMap = mutableMapOf<String, ServiceImplInfo>()

    private val composableList = mutableListOf<ComposableInfo>()

    private lateinit var processingEnv: ProcessingEnvironment
    private lateinit var typeUtils: Types
    private lateinit var viewModelType: TypeMirror
    private lateinit var bundleType: TypeMirror

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.processingEnv = processingEnv
        this.typeUtils = processingEnv.typeUtils
        this.viewModelType = processingEnv.elementUtils.getTypeElement(VIEW_MODEL_CLASS_NAME).asType()
        this.bundleType = processingEnv.elementUtils.getTypeElement(BUNDLE_CLASS_NAME).asType()

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        if (kaptKotlinGeneratedDir != null) {
            className = getModuleName(kaptKotlinGeneratedDir)
            packageName = DEFAULT_PACKAGE
        }
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val types = LinkedHashSet<String>()
        types.add(Scheme::class.java.canonicalName)
        types.add(Service::class.java.canonicalName)
        types.add(ServiceImpl::class.java.canonicalName)
        return types
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processSchemeElements(roundEnv)
        processServiceElements(roundEnv)
        processServiceImplElements(roundEnv)

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val composableGenerator = ComposableGenerator(composableList)
        if (kaptKotlinGeneratedDir != null) {
            composableGenerator.generate(File(kaptKotlinGeneratedDir))
        }

        val generator = Generator(packageName, className, schemeMap, serviceMap, serviceImplMap)
        if (kaptKotlinGeneratedDir != null) {
            generator.generate().writeTo(File(kaptKotlinGeneratedDir))
        }

        return false
    }

    private fun processSchemeElements(roundEnv: RoundEnvironment) {
        val schemeElements = roundEnv.getElementsAnnotatedWith(Scheme::class.java)
        schemeElements.forEach {
            processComposeScheme(it)
            processNormalScheme(it)
        }
    }

    private fun processNormalScheme(it: Element) {
        if (it.kind == ElementKind.CLASS) {
            val annotation = it.getAnnotation(Scheme::class.java)
            val scheme = annotation.scheme
            schemeMap[scheme] = it.toString()
        }
    }

    private fun processComposeScheme(it: Element) {
        if (it.kind == ElementKind.METHOD && it is ExecutableElement) {
            val packageName = it.enclosingElement.toString().getPackageName()
            val methodName = it.simpleName.toString()
            if (it.parameters.isNotEmpty()) {
                when (it.parameters.size) {
                    1 -> {
                        val firstParameterType = (it.parameters[0] as VariableElement).asType()
                        val isViewModel = typeUtils.isSubtype(firstParameterType, viewModelType)
                        val isBundle = typeUtils.isSameType(firstParameterType, bundleType)
                        if (isViewModel) {
                            val viewModelName = firstParameterType.asTypeName().toString()
                            composableList.add(ComposableInfo(packageName, methodName, false, viewModelName))
                        } else if (isBundle) {
                            composableList.add(ComposableInfo(packageName, methodName, true, ""))
                        } else {
                            "Composable parameters only support Bundle or ViewModel type! -> ${it.simpleName}".loge()
                        }
                    }
                    2 -> {
                        val firstParameterType = (it.parameters[0] as VariableElement).asType()
                        val secondParameterType = (it.parameters[1] as VariableElement).asType()
                        val isFirstBundle = typeUtils.isSameType(firstParameterType, bundleType)
                        val isSecondViewModel = typeUtils.isSubtype(secondParameterType, viewModelType)
                        if (isFirstBundle && isSecondViewModel) {
                            val viewModelName = secondParameterType.asTypeName().toString()
                            composableList.add(ComposableInfo(packageName, methodName, true, viewModelName))
                        } else {
                            if (!isFirstBundle) {
                                "Composable first parameters must be Bundle! -> ${it.simpleName}".loge()
                            } else {
                                "Composable second parameters must be ViewModel! -> ${it.simpleName}".loge()
                            }
                        }
                    }
                    else -> {
                        "Composable parameters only support Bundle and ViewModel! -> ${it.simpleName}".loge()
                    }
                }
            } else {
                composableList.add(ComposableInfo(packageName, methodName, false, ""))
            }

            val annotation = it.getAnnotation(Scheme::class.java)
            val scheme = annotation.scheme
            schemeMap[scheme] = "$COMPOSABLE_PACKAGE_NAME.${it.simpleName}Composable"
        }
    }

    private fun processServiceImplElements(roundEnv: RoundEnvironment) {
        val serviceImplElements = roundEnv.getElementsAnnotatedWith(ServiceImpl::class.java)
        serviceImplElements.forEach {
            if (it.kind != ElementKind.CLASS) {
                "@ServiceImpl must be annotated at Class!".loge()
            } else {
                val annotation = it.getAnnotation(ServiceImpl::class.java)
                val singleton = annotation.singleton
                val name = it.simpleName
                val identity = annotation.identity
                if (identity.isEmpty() && !name.endsWith("Impl")) {
                    "@ServiceImpl class name must end with Impl!".loge()
                }
                val realKey = identity.ifEmpty {
                    val index = name.lastIndexOf("Impl")
                    name.substring(0, index)
                }.toString()
                serviceImplMap[realKey] = ServiceImplInfo(it.toString(), singleton)
            }
        }
    }

    private fun processServiceElements(roundEnv: RoundEnvironment) {
        val serviceElements = roundEnv.getElementsAnnotatedWith(Service::class.java)
        serviceElements.forEach {
            if (it.kind != ElementKind.INTERFACE) {
                "@Service must be annotated at Interface!".loge()
            } else {
                val annotation = it.getAnnotation(Service::class.java)
                val name = it.simpleName
                val identity = annotation.identity
                val realKey = identity.ifEmpty { name }.toString()
                serviceMap[realKey] = it.toString()
            }
        }
    }

    private fun String.loge() {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, this)
    }
}