package com.jetbrains.cidr.clsi.bindings

import com.google.common.reflect.ClassPath
import com.intellij.ide.plugins.PluginManagerCore
import org.jetbrains.annotations.VisibleForTesting
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

private inline fun <reified T> Array<T>.sortBySignature(testing: Boolean): Iterator<T> {
    if (!testing) return this.iterator()

    val toString = T::class.java.getMethod("toGenericString")
    return sortedBy { toString.invoke(it) as String }.iterator()
}

@VisibleForTesting
fun TSModuleBuilder.classDefinitionFrom(clazz: Class<*>, sortForTesting: Boolean = false) {
    if (!Modifier.isPublic(clazz.modifiers)) return

    classDefinition(
        name = clazz.simpleName,
        extends = clazz.superclass?.let(::resolve),
        implements = clazz.interfaces.map(::resolve),
        abstract = Modifier.isAbstract(clazz.modifiers),
    ) {
        for (field in clazz.declaredFields.sortBySignature(sortForTesting)) {
            if (!Modifier.isPublic(field.modifiers)) continue

            field(
                name = field.name,
                type = resolve(field.type),
                final = Modifier.isFinal(field.modifiers),
                static = Modifier.isStatic(field.modifiers),
            )
        }

        for (constructor in clazz.constructors.sortBySignature(sortForTesting)) {
           if (!Modifier.isPublic(constructor.modifiers)) continue

           constructor(
               paramTypes = constructor.parameterTypes.map(::resolve),
           )
        }

        for (method in clazz.declaredMethods.sortBySignature(sortForTesting)) {
            if (!Modifier.isPublic(method.modifiers)) continue

            method(
                name = method.name,
                paramTypes = method.parameterTypes.map(::resolve),
                returnType = resolve(method.returnType),
                static = Modifier.isStatic(method.modifiers),
            )
        }
    }
}

fun createTsModule(packageName: String): String {
    val builder = TSModuleBuilder(packageName)

    for (clazz in getClassesInPackage(packageName)) {
        if (!Modifier.isPublic(clazz.modifiers)) continue

        builder.classDefinitionFrom(clazz)
    }

    return builder.build()
}