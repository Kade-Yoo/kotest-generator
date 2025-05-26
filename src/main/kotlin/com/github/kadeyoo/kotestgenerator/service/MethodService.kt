package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

@Service(Service.Level.PROJECT)
class MethodService: CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean {
        return psiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction"
    }

    override fun generateSpec(psiElement: PsiElement): String {
        val name = (psiElement as? PsiNamedElement)?.name
        return buildString {
            appendLine("class Main : BehaviorSpec({")
            appendLine("    given(\"${name}\") {")
            appendLine("        `when`(\"it is called\") {")
            appendLine("            then(\"it should behave correctly\") {")
            appendLine("                // TODO: Add test logic")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
            appendLine("})")
        }
    }
}