package com.github.kadeyoo.kotestgenerator.generator

import com.intellij.psi.PsiElement

interface CodeSpecGenerator {
    fun supports(psiElement: PsiElement): Boolean
    fun generateSpec(psiElement: PsiElement): String
}