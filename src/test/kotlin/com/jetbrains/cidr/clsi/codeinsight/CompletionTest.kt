package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class CompletionTest : BasePlatformTestCase() {
    private fun doTest(@Language("JavaScript") text: String, vararg expected: String) {
        myFixture.configureByText("file.js", text)

        val result = myFixture.completeBasic()!!
        assertContainsElements(result.map { it.lookupString }, *expected)
    }

    fun `test works in empty string`() = doTest(
        "Java.type('<caret>')",
        "com", "net",
    )

    fun `test works with incomplete package`() = doTest(
        "Java.type('com.jetbrains.c<caret>')",
        "com.jetbrains.cidr",
    )

    fun `test finds jetbrains`() = doTest(
        "Java.type('com.<caret>')",
        "com.jetbrains",
    )

    fun `test finds AnAction`() = doTest(
        "Java.type('com.intellij.openapi.actionSystem.<caret>')",
        "com.intellij.openapi.actionSystem.AnAction",
    )

    fun `test finds clis`() = doTest(
        "Java.type('com.jetbrains.cidr.<caret>')",
        "com.jetbrains.cidr.clsi",
    )

    fun `test finds a clis class`() = doTest(
        "Java.type('com.jetbrains.cidr.clsi.bindings.<caret>')",
        "com.jetbrains.cidr.clsi.bindings.ClassUtil",
    )
}