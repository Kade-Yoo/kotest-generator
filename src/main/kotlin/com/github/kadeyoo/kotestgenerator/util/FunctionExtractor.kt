package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

object FunctionExtractor {
    fun extractFromClass(psiClassElement: PsiElement): List<FunctionInfo> =
        PsiTreeUtil.findChildrenOfType(psiClassElement, PsiElement::class.java)
            .filter { it.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction" }
            .mapNotNull { extractFromFunction(it) }

    fun extractFromFunction(psiElement: PsiElement): FunctionInfo? {
        val name = (psiElement as? PsiNamedElement)?.name ?: return null
        val params = parseParameters(psiElement)
        val returnType = parseReturnType(psiElement)
        val mappingInfo = CodeGeneratorUtil.extractMappingInfo(psiElement)
        return FunctionInfo(name, params, returnType, mappingInfo)
    }

    private fun parseParameters(psiElement: PsiElement): List<Pair<String, String>> {
        val params = mutableListOf<Pair<String, String>>()
        val valueParametersMethod = psiElement.javaClass.getMethod("getValueParameters")
        val valueParameters = valueParametersMethod.invoke(psiElement) as? List<*>
        valueParameters?.forEach { param ->
            val paramName = param?.javaClass?.getMethod("getName")?.invoke(param) as? String ?: "unknown"
            val typeRef = param?.javaClass?.getMethod("getTypeReference")?.invoke(param)
            val typeText = typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Any"
            params.add(paramName to typeText)
        }
        return params
    }

    private fun parseReturnType(psiElement: PsiElement): String {
        val typeRef = psiElement.javaClass.getMethod("getTypeReference").invoke(psiElement)
        return typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Unit"
    }
}