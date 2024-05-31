package com.jetbrains.cidr.clsi

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.cidr.clsi.engine.GraalScriptEngine
import javax.script.ScriptEngineManager
import kotlin.io.path.readText

class ScriptTest : BasePlatformTestCase() {
    private val resourcePath = CLSI_TEST_DATA_PATH.resolve("scripts")

    private fun doTest(validate: (Any) -> Unit) {
        val script = resourcePath.resolve("${getCLSITestName()}.js").readText()

        val engine = ScriptEngineManager().getEngineByExtension("js")
        assertInstanceOf(engine, GraalScriptEngine::class.java)

        val result = engine.eval(script)
        validate(result)
    }

    fun `test extend AnAction`() = doTest { result ->
       assertInstanceOf(result, AnAction::class.java)
    }
}