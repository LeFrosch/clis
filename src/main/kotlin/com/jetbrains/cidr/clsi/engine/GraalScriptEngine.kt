package com.jetbrains.cidr.clsi.engine

import com.intellij.util.asSafely
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import java.io.Reader
import javax.script.*

class GraalScriptEngine(
    private val factory: ScriptEngineFactory,
    private val languageId: String,
) : AbstractScriptEngine(), AutoCloseable {

    companion object {
        const val JAVA_SCRIPT_LANGUAGE_ID = "js"

        private const val GRAAL_CONTEXT = "graal.context"
    }

    override fun getFactory(): ScriptEngineFactory = factory

    override fun createBindings(): Bindings = SimpleBindings()

    private fun getOrCreateContext(scriptContext: ScriptContext): Context {
        return scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).computeIfAbsent(GRAAL_CONTEXT) {
            Context.newBuilder(JAVA_SCRIPT_LANGUAGE_ID)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup { true }
                .hostClassLoader(this::class.java.classLoader)
                .build()
        } as Context
    }

    override fun eval(reader: Reader, context: ScriptContext): Any? {
        val script = reader.readText()
        reader.close()

        return eval(script, context)
    }

    override fun eval(script: String, context: ScriptContext): Any? {
        val graalContext = getOrCreateContext(context)
        return graalContext.eval(languageId, script).`as`(Object::class.java)
    }

    override fun close() {
        getBindings(ScriptContext.ENGINE_SCOPE)[GRAAL_CONTEXT].asSafely<Context>()?.close()
    }
}