package com.jetbrains.cidr.clsi

import com.intellij.ide.script.IdeScriptEngineManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class TestAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val b = 3
        val engine = IdeScriptEngineManager.getInstance().getEngineByFileExtension("kts", null)

        engine!!.eval(
            """
import com.intellij.openapi.ui.Messages.showInfoMessage

showInfoMessage("test", "test")
        """.trimIndent()
        )
    }
}

fun testRunnable(r: Runnable) {
    r.run()
}

fun testLambda(r: () -> Unit) {
    r()
}