package com.jetbrains.cidr.clsi.bindings

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.utils.io.createDirectory
import java.nio.file.Path
import kotlin.io.path.exists

class TSClassServiceTest : BasePlatformTestCase() {
    private val tempDir = Path.of(PathManager.getTempPath(), "class_service_test")

    override fun runInDispatchThread(): Boolean = false

    override fun setUp() {
        super.setUp()

        if (!tempDir.exists()) tempDir.createDirectory()

        clearBindingsCache(tempDir)
    }

    private fun doTest(clazz: Class<*>) {
        createBindingsFor(clazz, tempDir)

        val file = requireNotNull(LocalFileSystem.getInstance().findFileByNioFile(tempDir))
        assertNotNull(file.findChild("${clazz.canonicalName}.d.ts"))

        runReadAction {
            for (child in file.children) {
                val psiFile = psiManager.findFile(child)
                assertEmpty(PsiTreeUtil.findChildrenOfType(psiFile, PsiErrorElement::class.java))
            }
        }
    }

    fun `test Object`() = doTest(Object::class.java)

    fun `test Message`() = doTest(com.intellij.openapi.ui.Messages::class.java)

    fun `test KeymapManager`() = doTest(com.intellij.openapi.keymap.KeymapManager::class.java)
}