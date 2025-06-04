package com.github.kadeyoo.kotestgenerator.dispatcher

import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_CLASS_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.psi.PsiElement

class SpecGeneratorDispatcher(
    private val generators: List<CodeSpecGenerator>
) {
    fun generateSpec(psiElement: PsiElement, importNames: List<String>, isPresent: Boolean): String {
        val element = this.findElement(psiElement) ?: error("유효한 클래스 엘리먼트를 찾을 수 없습니다.: ${psiElement::class.simpleName}")
        val generator = generators.firstOrNull { it.supports(element) } ?: error("적합한 생성기를 찾을 수 없습니다. : ${element::class.simpleName}")
        return generator.generateSpec(element, importNames, isPresent)
    }

    private fun findElement(psiElement: PsiElement): PsiElement? {
        var tempPsiElement = psiElement
        while (tempPsiElement.parent != null) {
            if (tempPsiElement.javaClass.name == KT_CLASS_PACKAGE) {
                return tempPsiElement
            }

            if (tempPsiElement.javaClass.name == KT_NAMED_FUNCTION_PACKAGE) {
                return tempPsiElement
            }

            tempPsiElement = tempPsiElement.parent
        }

        return null
    }
}