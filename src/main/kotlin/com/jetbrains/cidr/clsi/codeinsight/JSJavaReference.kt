package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.cidr.clsi.bindings.ClassUtil
import com.jetbrains.cidr.clsi.bindings.TSClassService

class JSJavaReference(
    private val classPath: String,
    element: PsiElement,
    rangeInElement: TextRange,
) : PsiReferenceBase<PsiElement>(element, rangeInElement) {
    override fun resolve(): PsiElement? {
        val clazz = ClassUtil.loadClass(classPath) ?: return null

        val classService = service<TSClassService>()
        return classService.getBinding(element.project, clazz)
    }
}

