package com.jetbrains.cidr.clsi.bindings

import com.google.common.reflect.ClassPath

object ClassUtil {
    fun allClassloaders(): Sequence<ClassLoader> = sequence {
        yield(ClassLoader.getSystemClassLoader())
        yield(ClassLoader.getPlatformClassLoader())
    }

    fun classesInPackage(packageName: String): Sequence<String> = sequence {
        for (loader in allClassloaders()) {
            yieldAll(ClassPath.from(loader).getTopLevelClasses(packageName).map { it.simpleName })
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