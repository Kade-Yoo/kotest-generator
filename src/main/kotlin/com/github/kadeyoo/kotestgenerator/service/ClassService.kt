package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

@Service(Service.Level.PROJECT)
class ClassService : CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean {
        return psiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtClass"
    }

    override fun generateSpec(psiElement: PsiElement): String {
        val className = (psiElement as? PsiNamedElement)?.name ?: "UnknownClass"
        val methodNames = getMethodNames(psiElement)
        return buildString {
            appendLine("class $className : BehaviorSpec({")
            for (fn in methodNames) {
                appendLine("""    given("$fn") {""")
                appendLine("""        then("should do something") { }""")
                appendLine("    }")
            }
            appendLine("})")
        }
    }

    private fun getMethodNames(psiElement: PsiElement) : List<String> = extractFunctionNamesFromKtClass(psiElement)

    private fun extractFunctionNamesFromKtClass(psiClassElement: PsiElement): List<String> {
        return PsiTreeUtil.findChildrenOfType(psiClassElement, PsiElement::class.java)
            .filter { it.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction" }
            .mapNotNull { (it as? PsiNamedElement)?.name }
    }
}