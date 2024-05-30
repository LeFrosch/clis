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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.io.IOException
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.system.measureTimeMillis

private const val TS_LIBRARY_DIR = "clsi_ts_lib"

private fun fileNameFor(clazz: Class<*>): String {
    return "${clazz.canonicalName}.d.ts"
}

private fun virtualFileFor(path: Path): VirtualFile {
    if (!path.exists()) path.createDirectory()

    return LocalFileSystem.getInstance().findFileByNioFile(path)
        ?: throw IOException("could not find virtual file for cache: $path")
}

@VisibleForTesting
fun createBindingsFor(clazz: Class<*>, path: Path) {
    ThreadingAssertions.assertNoReadAccess()
    ThreadingAssertions.assertBackgroundThread()

    if (!path.exists()) path.createDirectory()
    if (path.resolve(fileNameFor(clazz)).exists()) return

    val frontier = Stack<Class<*>>()
    frontier.push(clazz)

    while (frontier.isNotEmpty()) {
        val current = frontier.pop()

        // check if the file already exits
        val filePath = path.resolve(fileNameFor(current))
        if (filePath.exists()) continue

        // generate the ts class and push all imported classes
        val result = createTsClass(current)
        result.importedClasses.forEach { frontier.push(it) }

        filePath.writeText(result.fileContent)
    }

    virtualFileFor(path).refresh(false, false)
}

@Service(Service.Level.APP)
class TSClassService(coroutineScope: CoroutineScope) {
    private val logger: Logger = thisLogger()
    private val cachePath: Path by lazy { PathManager.getSystemDir().resolve(TS_LIBRARY_DIR) }

    private val queue: ConcurrentUniqueQueue<Class<*>> = ConcurrentUniqueQueue()

    init {
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                ensureActive()

                val clazz = queue.current()
                logger.info("bindings [${clazz.simpleName}]: start creation")

                val duration = measureTimeMillis {
                    try {
                        createBindingsFor(clazz, cachePath)
                    } catch (e: IOException) {
                        logger.error("bindings [${clazz.simpleName}]: error", e)
                    }
                }

                queue.remove()
                logger.info("bindings [${clazz.simpleName}]: took ${duration}ms")
            }
        }
    }

    fun getCacheFile(): VirtualFile = virtualFileFor(cachePath)

    private fun getOrCreateBinding(clazz: Class<*>): VirtualFile? {
        val file = getCacheFile().findChild(fileNameFor(clazz))
        if (file != null) return file

        queue.send(clazz)
        return null
    }

    fun getBinding(project: Project, clazz: Class<*>): TypeScriptClass? {
        val virtualFile = getOrCreateBinding(clazz) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null

        val result = JSStubBasedPsiTreeUtil.resolveLocally(clazz.simpleName, psiFile) ?: return null

        return result.asSafely<TypeScriptClass>()
    }
}