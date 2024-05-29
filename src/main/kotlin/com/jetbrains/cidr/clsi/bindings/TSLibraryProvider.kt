package com.jetbrains.cidr.clsi.bindings

import com.intellij.lang.javascript.library.JSPredefinedLibraryProvider
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.webcore.libraries.ScriptingLibraryModel
import java.io.IOException

private const val LIBRARY_NAME = "CLion Platform SDK Type Definitions"
private const val PRELUDE_PATH = "prelude"

class TSLibraryProvider : JSPredefinedLibraryProvider() {
    private fun getPreludeFile(): VirtualFile {
        val url = this::class.java.classLoader.getResource(PRELUDE_PATH)
            ?: throw IOException("prelude: could not get url to resource")

        return VfsUtil.findFileByURL(url)
            ?: throw IOException("prelude: could not get virtual file")
    }

    override fun getPredefinedLibraries(project: Project): Array<ScriptingLibraryModel> {
        val cacheRoot = service<TSClassService>().getCacheFile()
        val preludeRoot = getPreludeFile()

        val library = ScriptingLibraryModel.createPredefinedLibrary(
            LIBRARY_NAME,
            arrayOf(cacheRoot, preludeRoot),
            true,
        )

        return arrayOf(library)
    }

    override fun getRequiredLibraryFilesForResolve(): Set<VirtualFile> {
       return getPreludeFile().children.toSet()
    }
}