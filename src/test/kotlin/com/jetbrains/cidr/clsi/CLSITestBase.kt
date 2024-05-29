package com.jetbrains.cidr.clsi

import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.UsefulTestCase
import java.nio.file.Path
import java.util.*

val CLSI_TEST_DATA_PATH: Path = Path.of("src", "test", "resources")

fun UsefulTestCase.getCLSITestName(): String {
    return PlatformTestUtil.getTestName(name, false)
        .replace(Regex("[#@_.,-]+"), " ")
        .toPascalCase()
}

private fun String.toPascalCase(): String {
    val capitalize = { word: String -> word.replaceFirstChar { it.titlecase(Locale.getDefault()) } }

    return split(' ').joinToString("", transform = capitalize)
}
