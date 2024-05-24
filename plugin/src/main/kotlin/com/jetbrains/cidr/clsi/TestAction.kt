package com.jetbrains.cidr.clsi

import com.intellij.ide.script.IdeScriptEngineManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.jetbrains.python.psi.impl.PyImportResolver

class TestAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        for (method in PyClassBuilder::class.java.methods) {
            println(method.parameters.joinToString { it.name })
        }
        val extensions = PyImportResolver.EP_NAME.extensionList
        val b = 3
        /*val engine = IdeScriptEngineManager.getInstance().getEngineByFileExtension("kts", null)

        engine!!.eval("""
import com.intellij.openapi.ui.Messages.showInfoMessage

showInfoMessage("test", "test")
        """.trimIndent())
         */
    }
}