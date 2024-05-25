package com.jetbrains.cidr.clsi.bindings

import com.google.common.reflect.ClassPath
import com.intellij.ide.plugins.PluginManagerCore
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

private fun getClassesInPackage(name: String): Sequence<Class<*>> {
    val classLoaders = sequence {
        yield(ClassLoader.getSystemClassLoader())
        yield(ClassLoader.getPlatformClassLoader())

        @Suppress("UnstableApiUsage")
        for (plugin in PluginManagerCore.getPluginSet().enabledPlugins) {
            yield(plugin.classLoader)
        }
    }

    return classLoaders.flatMap { loader -> ClassPath.from(loader).getTopLevelClasses(name) }.map { it.load() }
}

private fun isValidClass(clazz: Class<*>): Boolean {
    return Modifier.isPublic(clazz.modifiers) && clazz.simpleName.isNotEmpty()
}

fun generateBindings(packageName: String) {
    val imports = mutableListOf<Package>()
    val bindings = mutableListOf<ClassBinding>()

    val classes = getClassesInPackage(packageName).filter(::isValidClass)

    for (clazz in classes) buildClassBinding(clazz.simpleName) {
        for (method in clazz.methods) addMethod(method.name) {
            parameterTypes.addAll(method.parameterTypes)
            returnType = method.returnType
        }
    }
}

class PyFileBuilder(private val packageName: String) {
    private val imports: MutableSet<String> = mutableSetOf()

    private val builder: StringBuilder = StringBuilder()

    private fun resolveType(clazz: Class<*>): String {
        assert(clazz.simpleName.isNotEmpty()) { "class ${clazz.packageName}.${clazz.name} is has no simple name" }

        if (clazz.packageName == packageName) {
            return clazz.simpleName
        } else {
            imports.add(clazz.packageName)
            return "${clazz.packageName}.${clazz.simpleName}"
        }
    }

    private fun classDeclaration(clazz: Class<*>) {
        val superTypes = sequence {
            yield(clazz.superclass)
            yieldAll(clazz.interfaces.iterator())
        }.map(::resolveType)

        builder.append("class ${clazz.simpleName}(")
        builder.append(superTypes.joinToString(separator = ","))
        builder.append("):\n")

        for (method in clazz.declaredMethods) {
            methodDeclaration(method)
        }

        for (field in clazz.declaredFields) {

        }
    }

    private fun methodDeclaration(method: Method) {
        if (Modifier.isStatic(method.modifiers)) {
            builder.append("  @staticmethod\n")
        }

        val paramTypes = method.parameterTypes.map(::resolveType).mapIndexed { index, type -> "p$index: $type" }

        builder.append("  def ${method.name}(")
        builder.append(paramTypes.joinToString(separator = ","))
        builder.append(")->")

        if (method.returnType != Void.TYPE) {
            builder.append("None")
        } else {
            builder.append(resolveType(method.returnType))
        }

        builder.append(":\n    pass\n")
    }

    private fun fieldDeclaration(field: Field) {
        val builder = StringBuilder("  ${field.name}:")
        builder.append(resolveType(field.type))
    }
}
