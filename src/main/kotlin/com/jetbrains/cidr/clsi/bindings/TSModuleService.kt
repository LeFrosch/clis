package com.jetbrains.cidr.clsi.bindings

import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import com.intellij.util.concurrency.ThreadingAssertions
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
class TSModuleService(private val project: Project) {
    private val cache: ConcurrentHashMap<String, PsiFile> = ConcurrentHashMap()

    private fun createPsiFile(name: String, content: String): PsiFile {
        return PsiFileFactory.getInstance(project).createFileFromText(
            "$name.d.ts",
            TypeScriptFileType.INSTANCE,
            content,
            LocalTimeCounter.currentTime(),
            false,
            false,
        )
    }

    fun getBinding(packageName: String): PsiFile {
        ThreadingAssertions.assertBackgroundThread()

        val cached = cache[packageName]
        if (cached != null) return cached

        val created = createPsiFile(
            name = packageName,
            content = createTsModule(packageName),
        )
        cache.putIfAbsent(packageName, created)

        return created
    }
}