package com.github.kadeyoo.kotestgenerator.dispatcher

import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.psi.PsiElement

class SpecGeneratorDispatcher(
    private val generators: List<CodeSpecGenerator>
) {
    fun generateSpec(psiElement: PsiElement): String {
        val element = findElement(psiElement)
            ?: error("No valid element found for generation: ${psiElement::class.simpleName}")
        val generator = generators.firstOrNull { it.supports(element) }
            ?: error("No suitable generator found for element: ${element::class.simpleName}")
        return generator.generateSpec(element)
    }

    private fun findElement(psiElement: PsiElement): PsiElement? {
        var tempPsiElement = psiElement
        while (tempPsiElement.parent != null) {
            if (tempPsiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtClass") {
                return tempPsiElement
            }

            if (tempPsiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction") {
                return tempPsiElement
            }

            tempPsiElement = tempPsiElement.parent
        }

        return null
    }
}