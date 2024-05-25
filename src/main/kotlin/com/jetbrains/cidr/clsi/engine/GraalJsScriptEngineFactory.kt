package com.jetbrains.cidr.clsi.engine

class GraalJsScriptEngineFactory : GraalScriptEngineFactory() {
    override fun getLanguageId(): String = GraalScriptEngine.JAVA_SCRIPT_LANGUAGE_ID

    override fun getExtensions(): List<String> = listOf("js")

    override fun getNames(): List<String> = listOf("js", "javascript")

    override fun getLanguageName(): String = "javascript"

    override fun getMethodCallSyntax(obj: String, method: String, vararg args: String): String {
        return buildString {
            append("$obj.$method(")
            append(args.joinToString(separator = ","))
            append(")")
        }
    }

    override fun getOutputStatement(toDisplay: String?): String {
        return "print($toDisplay)"
    }

    override fun getProgram(vararg statements: String?): String {
        return buildString {
            for (statement in statements) {
                append(statement)
                append(";")
            }
        }
    }
}