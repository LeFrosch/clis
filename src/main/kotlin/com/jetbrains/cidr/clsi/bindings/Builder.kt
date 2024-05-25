package com.jetbrains.cidr.clsi.bindings

fun buildClassBinding(name: String, build: ClassBindingBuilder.() -> Unit): ClassBinding {
    return ClassBindingBuilder(name).apply(build)
}

class ClassBindingBuilder(override val name: String) : ClassBinding {
    override val fields: MutableList<FieldBinding> = mutableListOf()
    override val methods: MutableList<MethodBinding> = mutableListOf()

    fun addMethod(name: String, build: MethodBindingBuilder.() -> Unit) {
        methods.add(MethodBindingBuilder(name).also(build))
    }

    fun addField(name: String, type: Class<*>) {
        fields.add(FieldBinding(name, type))
    }
}

class MethodBindingBuilder(override val name: String) : MethodBinding {
    override var static: Boolean = false
    override var returnType: Class<*>? = null

    override val parameterTypes: MutableList<Class<*>> = mutableListOf()
}