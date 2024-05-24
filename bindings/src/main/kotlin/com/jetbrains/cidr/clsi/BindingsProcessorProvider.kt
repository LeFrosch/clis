package com.jetbrains.cidr.clsi

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier

class BindingsProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val declarations = resolver.getAllFiles().flatMap { it.declarations }
        logger.warn("SIze: ${declarations.count()}")

        for (clazz in declarations.filterIsInstance<KSClassDeclaration>()) {

            for (function in clazz.getAllFunctions()) {
                logger.warn("${clazz.qualifiedName?.getShortName()}.${function.qualifiedName?.getShortName()}")

                for (param in function.parameters) {
                    logger.warn("${param.name?.asString()} : ${param.type.resolve().declaration.qualifiedName?.getShortName()}")
                }
            }
        }

        return emptyList()
    }
}

class BindingsProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val sources = environment.options["sources"]
        environment.logger.warn("SOURCES: $sources")
        return BindingsProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}