package com.jetbrains.cidr.clsi.engine

class GraalPythonScriptEngineFactory : GraalScriptEngineFactory() {
    override fun getLanguageId(): String = GraalScriptEngine.PYTHON_LANGUAGE_ID

    override fun getExtensions(): List<String> = listOf("py")

    override fun getNames(): List<String> = listOf("py", "python")

    override fun getLanguageName(): String = "python"

    override fun getMethodCallSyntax(obj: String, method: String, vararg args: String): String {
        return buildString {
            append("$obj.$method(")
            append(args.joinToString(separator = ","))
            append(")")
        }
    }

    override fun getOutputStatement(toDisplay: String): String {
        return "print($toDisplay)"
    }

    override fun getProgram(vararg statements: String): String {
        return statements.joinToString(separator = "\n")
    }
}