package com.jetbrains.cidr.clsi.bindings

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import io.github.classgraph.ClassGraph

object ClassUtil {
    private val PLUGIN_ID: PluginId = PluginId.getId("com.jetbrains.cidr.clsi")

    val allClassLoaders: List<ClassLoader> by lazy {
        buildList {
            add(ClassLoader.getSystemClassLoader())
            add(ClassLoader.getPlatformClassLoader())

            @Suppress("UnstableApiUsage")
            val pluginClassLoader = PluginManagerCore.getPlugin(PLUGIN_ID)?.classLoader
            if (pluginClassLoader != null) add(pluginClassLoader)
        }
    }

    fun classesInPackage(packageName: String): List<String> {
        // TODO: this is expensive, worth caching? Or something less expensive?
        val result = ClassGraph()
            .overrideClassLoaders(*allClassLoaders.toTypedArray())
            .acceptPackagesNonRecursive(packageName)
            .enableClassInfo()
            .scan()

        return result.allClasses
            .filter { info -> info.isPublic && !info.isAnonymousInnerClass }
            .map { classInfo -> classInfo.simpleName }
    }

    fun loadClass(classPath: String): Class<*>? {
        return try {
            this::class.java.classLoader.loadClass(classPath)
        } catch (e: ClassNotFoundException) {
            null
        }
    }
}