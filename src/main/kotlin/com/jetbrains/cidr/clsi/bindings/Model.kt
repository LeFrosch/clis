package com.jetbrains.cidr.clsi.bindings

interface ClassBinding {
    val name: String

    val fields: List<FieldBinding>
    val methods: List<MethodBinding>
}

interface MethodBinding {
    val name: String
    val static: Boolean

    val parameterTypes: List<Class<*>>
    val returnType: Class<*>?
}

data class FieldBinding(val name: String, val type: Class<*>)
