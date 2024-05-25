package com.jetbrains.cidr.clsi.engine

import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory

abstract class GraalScriptEngineFactory : ScriptEngineFactory {
    abstract fun getLanguageId(): String

    override fun getEngineName(): String = "Graal Scripting Engine - ${getLanguageId()}"

    override fun getEngineVersion(): String = "24.0.1"

    override fun getLanguageVersion(): String = "Graal $engineVersion"

    override fun getMimeTypes(): List<String> = listOf() // not used by idea scripting engine?

    override fun getParameter(key: String?): Any? = when (key) {
        ScriptEngine.NAME -> languageName
        ScriptEngine.ENGINE -> engineName
        ScriptEngine.ENGINE_VERSION -> engineVersion
        ScriptEngine.LANGUAGE -> languageName
        ScriptEngine.LANGUAGE_VERSION -> languageVersion
        else -> null
    }

    override fun getScriptEngine(): ScriptEngine = GraalScriptEngine(this, getLanguageId())
}