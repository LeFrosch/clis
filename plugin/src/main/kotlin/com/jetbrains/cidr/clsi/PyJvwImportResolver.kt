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

        return classLoaders.flatMap { loader -> ClassPath.from(loader).getTopLevelClasses(name) }.map { it.load() }
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
        val builder = PyModuleBuilder()

        for (clazz in getClassesInPackage(name.toString()).toList()) builder.addClass(clazz.simpleName) {
            for (method in clazz.methods) addMethod(method.name) {
                for (param in method.parameters) {
                    addArg(param.name, param.type.simpleName)
                }
            }
        }

        val file = createFile(context.project, builder.build())

        return file
    }
}

class PyFunctionBuilder(private val name: String, private val self: Boolean) {
    private data class Arg(val name: String, val type: String)

    private val args: MutableList<Arg> = mutableListOf()

    fun addArg(name: String, type: String) = args.add(Arg(name, type))

    fun build(builder: StringBuilder) {
        val indentation = if (self) 1 else 0

        builder.appendIndent(indentation)
        builder.append("def $name(")

        if (self) {
            builder.append("self,")
        }
        for (arg in args) {
            builder.append("${arg.name}: ${arg.type},")
        }

        builder.appendLine("):")
        builder.appendIndentedLine(indentation + 1, "pass")
    }
}

class PyClassBuilder(private val name: String) {
    private val methods: MutableList<PyFunctionBuilder> = mutableListOf()

    fun addMethod(name: String, build: PyFunctionBuilder.() -> Unit) {
        methods.add(PyFunctionBuilder(name, true).apply(build))
    }

    fun build(builder: StringBuilder) {
        builder.appendLine("class $name:")

        for (method in methods) {
            method.build(builder)
        }
    }
}

class PyModuleBuilder {
    private val classes: MutableList<PyClassBuilder> = mutableListOf()

    fun addClass(name: String, build: PyClassBuilder.() -> Unit) {
        classes.add(PyClassBuilder(name).also(build))
    }

    fun build(): String {
        val builder = StringBuilder()

        for (clazz in classes) {
            clazz.build(builder)
        }

        return builder.toString()
    }
}

private fun StringBuilder.appendIndent(indentation: Int) {
    for (i in 0 until indentation) {
        append("  ")
    }
}

private fun StringBuilder.appendIndentedLine(indentation: Int, value: String) {
    appendIndent(indentation)
    append(value)
    append("\n")
}