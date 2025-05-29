package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.intellij.psi.PsiElement

interface CodeSpecGenerator {
    fun supports(psiElement: PsiElement): Boolean
    fun generateSpec(psiElement: PsiElement, componentType: ComponentType, isPresent: Boolean): String
}