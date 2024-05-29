package com.jetbrains.cidr.clsi.bindings

import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.LocalTimeCounter
import com.jetbrains.cidr.clsi.CLSI_TEST_DATA_PATH
import com.jetbrains.cidr.clsi.getCLSITestName
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.writeText

class TsModuleBuilderTest : BasePlatformTestCase() {
    private val resourcePath = CLSI_TEST_DATA_PATH.resolve("bindings")

    private fun compareWithFixture(actual: String) {
        val path = resourcePath.resolve("${getCLSITestName()}.d.ts")

        if (path.exists()) {
            assertSameLinesWithFile(path.absolutePathString(), actual)
        } else {
            path.writeText(actual)
            fail("File ${path.pathString} did not exist. Created new fixture.")
        }
    }

    private fun doTest(clazz: Class<*>) {
        val content = TSModuleBuilder(clazz.packageName).apply {
            classDefinitionFrom(clazz, sortForTesting = true)
        }.build()

        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "file.d.ts",
            TypeScriptFileType.INSTANCE,
            content,
            LocalTimeCounter.currentTime(),
            false,
            true
        )

        // format the file before comparing to fixture
        CodeStyleManager.getInstance(project).reformat(file)

        // write and compare to fixture
        compareWithFixture(file.text)

        // assert no errors in generated file
        assertEmpty(PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java))
    }

    fun `test Predicate`() = doTest(java.util.function.Predicate::class.java)

    fun `test AnAction`() = doTest(com.intellij.openapi.actionSystem.AnAction::class.java)

    fun `test PsiManager`() = doTest(com.intellij.psi.PsiManager::class.java)

    fun `test Keymap`() = doTest(com.intellij.openapi.keymap.Keymap::class.java)
}