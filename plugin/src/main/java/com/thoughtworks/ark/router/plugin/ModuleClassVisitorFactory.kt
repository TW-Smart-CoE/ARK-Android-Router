package com.thoughtworks.ark.router.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM7
import org.objectweb.asm.Opcodes.GETSTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import java.util.concurrent.*

/**
 * Save all module name
 */
object ModuleHolder {
    private val modulesMap = ConcurrentHashMap<String, String>()

    fun addModule(moduleName: String) {
        modulesMap[moduleName] = moduleName
    }

    fun forEach(block: (String) -> Unit) {
        modulesMap.values.forEach {
            block(it)
        }
    }

    fun clearModule() {
        modulesMap.clear()
    }
}

abstract class ModuleClassVisitorFactory : AsmClassVisitorFactory<ModuleClassVisitorFactory.ModuleInstrumentation> {
    interface ModuleInstrumentation : InstrumentationParameters {
        @get:Input
        @get:Optional
        val invalidate: Property<Long>
    }

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return ModuleClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        if (classData.interfaces.contains("com.thoughtworks.ark.router.module.Module")) {
            ModuleHolder.addModule(classData.className)
        }

        return classData.superClasses.contains("android.app.Application")
    }
}

class ModuleClassVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(ASM7, nextClassVisitor) {
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "onCreate") {
            mv = ModuleMethodVisitor(mv)
        }
        return mv
    }
}

class ModuleMethodVisitor(methodVisitor: MethodVisitor) : MethodVisitor(ASM7, methodVisitor) {
    override fun visitCode() {
        super.visitCode()

        ModuleHolder.forEach {
            mv.visitFieldInsn(
                GETSTATIC,
                "com/thoughtworks/ark/router/RouterCore",
                "INSTANCE",
                "Lcom/thoughtworks/ark/router/RouterCore;"
            )
            mv.visitLdcInsn(it)
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/thoughtworks/ark/router/RouterCore",
                "addModuleName",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }
}