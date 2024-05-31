package com.jetbrains.cidr.clsi.codeinsight

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.javascript.index.JSSymbolUtil
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PlatformPatterns.psiFile
import com.intellij.psi.PsiElement
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import com.jetbrains.cidr.clsi.bindings.ClassUtil

private object IsJavaTypeArgumentPattern : PatternCondition<PsiElement>("isJavaTypeArgumentPattern") {
    override fun accepts(element: PsiElement, context: ProcessingContext?): Boolean {
        val expression = element.parent as? JSLiteralExpression ?: return false
        if (!expression.isStringLiteral) return false

        val argumentList = expression.parent as? JSArgumentList ?: return false
        if (argumentList.arguments.size > 1) return false

        val callExpression = argumentList.parent as? JSCallExpression ?: return false
        return JSSymbolUtil.isAccurateReferenceExpressionName(callExpression.methodExpression, "Java", "type")
    }
}

@JvmInline
private value class PackageTree(private val elements: MutableMap<String, PackageTree> = mutableMapOf()) {
    fun insert(path: List<String>) {
        val element = path.firstOrNull() ?: return

        val elementTree = elements.computeIfAbsent(element) { PackageTree() }
        elementTree.insert(path.subList(1, path.size))
    }

    fun lookup(path: List<String>): Set<String> {
        val element = path.firstOrNull() ?: return elements.keys

        val elementTree = elements[element] ?: return emptySet()
        return elementTree.lookup(path.subList(1, path.size))
    }
}

private object JavaLiteralCompletionProvider : CompletionProvider<CompletionParameters>() {
    private const val PACKAGE_TYPE_TEXT = "package"
    private const val CLASS_TYPE_TEXT = "class"

    private val packageTree: PackageTree by lazy {
        val root = PackageTree()

        for (loader in ClassUtil.allClassloaders()) {
            for (pkg in loader.definedPackages) {
                root.insert(pkg.name.split('.'))
            }
        }

        root
    }

    private fun pathToPackageName(path: List<String>): String? {
        return if (path.isEmpty()) {
            null
        } else {
            path.joinToString(separator = ".")
        }
    }

    private fun createPackageLookupElement(parentPackage: String?, packageName: String): LookupElement {
        var lookupString = packageName

        if (parentPackage != null) {
            lookupString = "$parentPackage.$lookupString"
        }

        return LookupElementBuilder.create(lookupString)
            .withTypeText(PACKAGE_TYPE_TEXT)
            .withIcon(PlatformIcons.PACKAGE_ICON)
            .withInsertHandler { context, _ ->
                context.document.insertString(context.selectionEndOffset, ".")
                context.editor.caretModel.moveToOffset(context.selectionEndOffset)
                AutoPopupController.getInstance(context.project).scheduleAutoPopup(context.editor)
            }
    }

    private fun createClassLookupElement(parentPackage: String?, className: String): LookupElement {
        val lookupString = "$parentPackage.$className"

        return LookupElementBuilder.create(lookupString)
            .withTypeText(CLASS_TYPE_TEXT)
            .withIcon(PlatformIcons.CLASS_ICON)
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val text = parameters.position.text
            .trim('\'', '"', ' ')
            .removeSuffix(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)

        val path = text.split('.')
            .dropLast(1)
            .filter { it.isNotEmpty() }

        val parentPackage = pathToPackageName(path)

        for (packageName in packageTree.lookup(path)) {
            result.addElement(createPackageLookupElement(parentPackage, packageName))
        }

        if (parentPackage != null) {
            for (className in ClassUtil.classesInPackage(parentPackage)) {
               result.addElement(createClassLookupElement(parentPackage, className))
            }
        }
    }
}

class JSCompletionContributor : CompletionContributor() {
    init {
        val pattern = psiElement()
            .inFile(psiFile(JSFile::class.java))
            .with(IsJavaTypeArgumentPattern)

        extend(CompletionType.BASIC, pattern, JavaLiteralCompletionProvider)
    }
}