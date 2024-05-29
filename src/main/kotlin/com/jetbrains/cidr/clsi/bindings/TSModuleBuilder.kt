package com.jetbrains.cidr.clsi.bindings

class TSModuleBuilder(private val modulePackage: String) {
    private val imports: MutableMap<String, String> = mutableMapOf()
    private val definitions: MutableList<String> = mutableListOf()

    private fun typeMappings(jvmType: Class<*>): String? {
        return when (jvmType) {
            Void.TYPE -> "void"
            String::class.java, Character::class.java -> "string"
            Int::class.java, Byte::class.java, Short::class.java, Long::class.java, Float::class.java, Double::class.java -> "number"
            Boolean::class.java -> "boolean"
            else -> null
        }
    }

    fun resolve(jvmType: Class<*>): String {
        assert(jvmType.simpleName.isNotEmpty()) { "class $jvmType has no simple name" }

        val mapped = typeMappings(jvmType)
        if (mapped != null) {
            return mapped
        }

        if (jvmType.packageName == modulePackage) {
            return jvmType.simpleName
        }

        val prefix = imports.computeIfAbsent(jvmType.packageName) { "i${imports.size}" }
        return "$prefix.${jvmType.simpleName}"
    }

    fun classDefinition(
        name: String,
        extends: String? = null,
        implements: List<String> = emptyList(),
        abstract: Boolean = false,
        body: StringBuilder.() -> Unit,
    ) {
        val builder = StringBuilder().apply {
            append("export ")

            if (abstract) append("abstract ")
            append("class $name ")

            if (extends != null) {
                append("extends $extends ")
            }

            if (implements.isNotEmpty()) {
                append("implements ")
                append(implements.joinToString(","))
            }

            append("{")
            body(this)
            append("}")
        }

        definitions.add(builder.toString())
    }

    fun build(): String {
        val builder = StringBuilder()

        for ((path, name) in imports) {
            builder.append("import * as $name from '$path';")
        }

        for (definition in definitions) {
            builder.append(definition)
        }

        return builder.toString()
    }
}

fun StringBuilder.method(
    name: String,
    paramTypes: List<String> = emptyList(),
    returnType: String = "void",
    static: Boolean = false,
) {
    if (static) append("static ")

    append("$name(")
    paramTypes.forEachIndexed { i, it -> append("p$i:$it,") }
    append("):$returnType;")
}

fun StringBuilder.constructor(paramTypes: List<String> = emptyList()) {
    append("constructor(")
    paramTypes.forEachIndexed { i, it -> append("p$i:$it,") }
    append(");")
}

fun StringBuilder.field(
    name: String,
    type: String,
    final: Boolean = false,
    static: Boolean = false,
) {
    if (static) append("static ")
    if (final) append("readonly ")

    append("$name:$type;")
}