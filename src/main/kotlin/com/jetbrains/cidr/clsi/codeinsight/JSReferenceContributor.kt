package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.lang.javascript.index.JSSymbolUtil
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSTypeUtils
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns.*
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import com.intellij.util.asSafely

private object IsJavaTypeArgument : PatternCondition<JSLiteralExpression>("isJavaTypeArgument") {
    override fun accepts(element: JSLiteralExpression, context: ProcessingContext?): Boolean {
        if (!element.isStringLiteral)  return false

        val argumentList = element.parent as? JSArgumentList ?: return false
        val callExpression = argumentList.parent as? JSCallExpression ?: return false
        return JSSymbolUtil.isAccurateReferenceExpressionName(callExpression.methodExpression, "Java", "type")
    }
}

private object JavaReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (element !is JSLiteralExpression) return emptyArray()

        val classPath = element.stringValue ?: return emptyArray()
        val range = TextRange.from(1, classPath.length) // TODO: are there other strings with multichar quotes?

        return arrayOf(JSJavaReference(classPath, element, range))
    }
}

class JSReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            psiElement(JSLiteralExpression::class.java).inFile(psiFile(JSFile::class.java)).with(IsJavaTypeArgument),
            JavaReferenceProvider,
        )
    }
}