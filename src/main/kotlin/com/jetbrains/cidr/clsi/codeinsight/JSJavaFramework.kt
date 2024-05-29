package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.lang.javascript.frameworks.JSFrameworkSpecificHandler
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSExpectedTypeKind
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSType
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.util.asSafely
import com.jetbrains.cidr.clsi.bindings.ClassUtil
import com.jetbrains.cidr.clsi.bindings.TSClassService

class JSJavaFramework : JSFrameworkSpecificHandler {
    override fun findExpectedType(
        element: PsiElement,
        parent: PsiElement?,
        expectedTypeKind: JSExpectedTypeKind
    ): JSType? {
        if (element !is JSCallExpression) return null

        val arg = element.argumentList?.arguments?.singleOrNull()?.asSafely<JSLiteralExpression>() ?: return null
        if (!arg.isStringLiteral) return null

        val classPath = arg.stringValue ?: return null
        val clazz = ClassUtil.loadClass(classPath) ?: return null

        val moduleService = service<TSClassService>()
        val binding = moduleService.getBinding(element.project, clazz) ?: return null

        return binding.staticJSType
    }
}