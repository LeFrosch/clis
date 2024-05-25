package com.jetbrains.cidr.clsi

import com.google.common.reflect.ClassPath
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.QualifiedName
import com.intellij.util.LocalTimeCounter
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.impl.PyImportResolver
import com.jetbrains.python.psi.resolve.PyQualifiedNameResolveContext

class PyJvwImportResolver : PyImportResolver {
    private fun getClassesInPackage(name: String): Sequence<Class<*>> {
        val classLoaders = sequence {
            yield(ClassLoader.getSystemClassLoader())
            yield(ClassLoader.getPlatformClassLoader())

            @Suppress("UnstableApiUsage")
            for (plugin in PluginManagerCore.getPluginSet().enabledPlugins) {
                yield(plugin.classLoader)
            }
        }

        return classLoaders.flatMap { loader -> ClassPath.from(loader).getTopLevelClasses(name) }
            .map { it.load() }
    }

    private fun createFile(project: Project, content: String): PsiFile {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "file.py",
            PythonFileType.INSTANCE,
            content,
            LocalTimeCounter.currentTime(),
            false,
            true
        )
    }

    override fun resolveImportReference(
        name: QualifiedName,
        context: PyQualifiedNameResolveContext,
        withRoots: Boolean
    ): PsiElement? {
        return null
    }
}
