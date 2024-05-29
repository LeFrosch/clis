package com.jetbrains.cidr.clsi.engine

import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory

class GraalJsScriptEngineFactory : ScriptEngineFactory {
    override fun getEngineName(): String = "Graal Scripting Engine"

    override fun getEngineVersion(): String = "23.0.4"

    override fun getLanguageVersion(): String = "Graal $engineVersion"

    override fun getExtensions(): List<String> = listOf("js")

    override fun getNames(): List<String> = listOf("js", "javascript")

    override fun getLanguageName(): String = "javascript"

    override fun getMimeTypes(): List<String> = listOf() // not used by idea scripting engine?

    override fun getParameter(key: String?): Any? = when (key) {
        ScriptEngine.NAME -> languageName
        ScriptEngine.ENGINE -> engineName
        ScriptEngine.ENGINE_VERSION -> engineVersion
        ScriptEngine.LANGUAGE -> languageName
        ScriptEngine.LANGUAGE_VERSION -> languageVersion
        else -> null
    }

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

    override fun getScriptEngine(): ScriptEngine = GraalScriptEngine(this, GraalScriptEngine.JAVA_SCRIPT_LANGUAGE_ID)
}