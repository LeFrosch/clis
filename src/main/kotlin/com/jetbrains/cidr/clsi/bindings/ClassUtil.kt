package com.jetbrains.cidr.clsi.bindings

import com.intellij.ide.plugins.PluginManagerCore

object ClassUtil {
    private fun allClassloaders(): Sequence<ClassLoader> = sequence {
        yield(ClassLoader.getSystemClassLoader())
        yield(ClassLoader.getPlatformClassLoader())

        @Suppress("UnstableApiUsage")
        for (plugin in PluginManagerCore.getPluginSet().enabledPlugins) {
            yield(plugin.classLoader)
        }
    }

    fun loadClass(classPath: String): Class<*>? {
        return allClassloaders().firstNotNullOfOrNull { loader ->
            try {
                loader.loadClass(classPath)
            } catch (e: ClassNotFoundException) {
                null
            }
        }
    }
}