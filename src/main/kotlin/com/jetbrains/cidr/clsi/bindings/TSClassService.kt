package com.jetbrains.cidr.clsi.bindings

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.asSafely
import com.intellij.util.concurrency.ThreadingAssertions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.system.measureTimeMillis

private const val TS_LIBRARY_DIR = "clsi_ts_lib"

@Service(Service.Level.APP)
class TSClassService(coroutineScope: CoroutineScope) {
    private val logger: Logger = thisLogger()
    private val cachePath: Path by lazy { PathManager.getSystemDir().resolve(TS_LIBRARY_DIR) }

    private val queue: Channel<Class<*>> = Channel(capacity = 100, onBufferOverflow = BufferOverflow.DROP_LATEST)

    init {
        coroutineScope.launch(Dispatchers.IO) {
            queue.consumeEach { clazz ->
                logger.info("bindings [${clazz.simpleName}]: start creation")

                val duration = measureTimeMillis {
                   try {
                      createFileFor(clazz)
                   } catch (e: IOException) {
                       logger.error("bindings [${clazz.simpleName}]: error", e)
                   }
                }

                logger.info("bindings [${clazz.simpleName}]: took ${duration}ms")
            }
        }
    }

    private fun fileNameFor(clazz: Class<*>): String {
        return "${clazz.canonicalName}.d.ts"
    }

    fun getCacheFile(): VirtualFile {
        if (!cachePath.exists()) cachePath.createDirectory()

        return LocalFileSystem.getInstance().findFileByNioFile(cachePath)
            ?: throw IOException("could not find virtual file for cache: $cachePath")
    }

    private fun createFileFor(clazz: Class<*>) {
        ThreadingAssertions.assertNoReadAccess()
        ThreadingAssertions.assertBackgroundThread()

        if (!cachePath.exists()) cachePath.createDirectory()
        if (cachePath.resolve(fileNameFor(clazz)).exists()) return

        val frontier = Stack<Class<*>>()
        frontier.push(clazz)

        while (frontier.isNotEmpty()) {
            val current = frontier.pop()

            // check if the file already exits
            val path = cachePath.resolve(fileNameFor(current))
            if (path.exists()) continue

            // generate the ts class and push all imported classes
            val result = createTsClass(current)
            result.importedClasses.forEach { frontier.push(it) }

            path.writeText(result.fileContent)
        }

        getCacheFile().refresh(false, false)
    }

    private fun getOrCreateFile(clazz: Class<*>): VirtualFile? {
        val file = getCacheFile().findChild(fileNameFor(clazz))
        if (file != null) return file

        queue.trySend(clazz)
        return null
    }

    fun getBinding(project: Project, clazz: Class<*>): TypeScriptClass? {
        val virtualFile = getOrCreateFile(clazz) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null

        val result = JSStubBasedPsiTreeUtil.resolveLocally(clazz.simpleName, psiFile) ?: return null

        return result.asSafely<TypeScriptClass>()
    }
}