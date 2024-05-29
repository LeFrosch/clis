package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.lang.javascript.frameworks.JSFrameworkSpecificHandler
import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.resolve.CachingPolyReferenceBase
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext
import com.intellij.lang.javascript.psi.resolve.JSResolveResult
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.util.asSafely
import com.jetbrains.cidr.clsi.bindings.TSModuleService

class JSJavaReference(
    private val classPath: String,
    element: JSLiteralExpression,
    rangeInElement: TextRange,
) : CachingPolyReferenceBase<JSLiteralExpression>(element, rangeInElement) {

    override fun resolveInner(): Array<ResolveResult> {
        val index = classPath.lastIndexOf('.')
        if (index == -1) return emptyArray()

        val packageName = classPath.substring(0, index)
        val className = classPath.substring(index + 1)

        val moduleService = element.project.service<TSModuleService>()
        val file = moduleService.getBinding(packageName)

        val result = JSStubBasedPsiTreeUtil.resolveLocally(className, file) ?: return emptyArray()

        return arrayOf(JSResolveResult(result))
    }
}

class JSJavaFramework : JSFrameworkSpecificHandler {
    override fun getAdditionalTypes(
        context: PsiElement,
        qualifier: JSReferenceItem,
        evaluateContext: JSEvaluateContext
    ): MutableList<JSType> {
        return super.getAdditionalTypes(context, qualifier, evaluateContext)
    }

    override fun findExpectedType(
        element: PsiElement,
        parent: PsiElement?,
        expectedTypeKind: JSExpectedTypeKind
    ): JSType? {
        if (element !is JSCallExpression) return null

        val arg = element.argumentList?.arguments?.singleOrNull()?.asSafely<JSLiteralExpression>() ?: return null
        if (!arg.isStringLiteral) return null

        val classPath = arg.stringValue ?: return null

        val index = classPath.lastIndexOf('.')
        if (index == -1) return null

        val packageName = classPath.substring(0, index)
        val className = classPath.substring(index + 1)

        val moduleService = element.project.service<TSModuleService>()
        val file = moduleService.getBinding(packageName)

        val result = JSStubBasedPsiTreeUtil.resolveLocally(className, file) ?: return null
        val type = JSTypeUtils.getTypeOfElement(result)

        return type
    }
}