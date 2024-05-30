package com.jetbrains.cidr.clsi.bindings

import java.lang.reflect.Modifier

interface TSClassBuilderResult {
    val importedClasses: Set<Class<*>>
    val fileContent: String
}

private class TSClassBuilder(private val className: String, private val packageName: String) : TSClassBuilderResult {
    private val imports: MutableSet<Class<*>> = mutableSetOf()
    private val builder: StringBuilder = StringBuilder()

    override val importedClasses: Set<Class<*>> get() = imports
    override val fileContent: String get() = builder.toString()

    var extends: String? = null
    var implements: List<String> = emptyList()
    var abstract: Boolean = false

    private fun typeMappings(jvmType: Class<*>): String? {
        return when (jvmType) {
            Void.TYPE -> "void"
            String::class.java -> "string"
            Character::class.java, Char::class.java -> "character"
            Int::class.java, Byte::class.java, Short::class.java, Long::class.java, Float::class.java, Double::class.java -> "number"
            Boolean::class.java -> "boolean"
            else -> null
        }
    }

    fun resolve(jvmType: Class<*>): String {
        assert(jvmType.simpleName.isNotEmpty()) { "class $jvmType has no simple name" }

        if (jvmType.isArray) return "${resolve(jvmType.componentType)}[]"

        val mapped = typeMappings(jvmType)
        if (mapped != null) return mapped

        assert(!jvmType.isPrimitive) { "primitive class $jvmType was not mapped" }

        if (jvmType.packageName != packageName || jvmType.simpleName != className) {
            imports.add(jvmType)
        }

        return jvmType.simpleName
    }

    private fun StringBuilder.statement(block: StringBuilder.() -> Unit) {
        append("\n    ")
        block(this)
        append(";\n")
    }

    private fun StringBuilder.appendParameters(paramTypes: List<String>) {
        append(paramTypes.mapIndexed { i, type -> "p$i: $type" }.joinToString(separator = ", "))
    }

    fun StringBuilder.method(
        name: String,
        paramTypes: List<String> = emptyList(),
        returnType: String = "void",
        static: Boolean = false,
    ) = statement {
        if (static) append("static ")

        append("$name(")
        appendParameters(paramTypes)
        append("): $returnType")
    }

    fun StringBuilder.constructor(paramTypes: List<String> = emptyList()) = statement {
        append("constructor(")
        appendParameters(paramTypes)
        append(")")
    }

    fun StringBuilder.field(
        name: String,
        type: String,
        final: Boolean = false,
        static: Boolean = false,
    ) = statement {
        if (static) append("static ")
        if (final) append("readonly ")

        append("$name: $type")
    }

    fun body(build: StringBuilder.() -> Unit) {
        val classBuilder = StringBuilder().apply {
            append("export ")

            if (abstract) append("abstract ")
            append("class $className ")

            if (extends != null) {
                append("extends $extends ")
            }

            if (implements.isNotEmpty()) {
                append("implements ")
                append(implements.joinToString(", "))
            }

            append("{\n")
            build(this)
            append("}")
        }

        for (import in imports) {
            builder.append("import { ${import.simpleName} } from \"./${import.canonicalName}\";\n")
        }

        builder.append("\n")
        builder.append(classBuilder)
    }
}

private inline fun <reified T> Array<T>.sortBySignature(testing: Boolean): Iterator<T> {
    if (!testing) return this.iterator()

    val toString = T::class.java.getMethod("toGenericString")
    return sortedBy { toString.invoke(it) as String }.iterator()
}

fun createTsClass(clazz: Class<*>, sortForTesting: Boolean = false): TSClassBuilderResult {
    //assert(Modifier.isPublic(clazz.modifiers)) { "class $clazz is not public" }
    assert(clazz.simpleName.isNotEmpty()) { "class $clazz has no simple name" }

    return TSClassBuilder(clazz.simpleName, clazz.packageName).apply {
        extends = clazz.superclass?.let(::resolve)
        implements = clazz.interfaces.map(::resolve)
        abstract = Modifier.isAbstract(clazz.modifiers)

        body {
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
}
