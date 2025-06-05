package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

object FunctionExtractor {
    fun extractFromClass(psiClassElement: PsiElement, importNames: List<String>): List<FunctionInfo> =
        PsiTreeUtil.findChildrenOfType(psiClassElement, PsiElement::class.java)
            .filter { it.javaClass.name == KT_NAMED_FUNCTION_PACKAGE }
            .mapNotNull { extractFromFunction(it, importNames) }

    fun extractFromFunction(psiElement: PsiElement, importNames: List<String>): FunctionInfo? {
        val name = (psiElement as? PsiNamedElement)?.name ?: return null
        val params = parseParameters(psiElement, importNames)
        val returnType = parseReturnType(psiElement)
        val mappingInfo = CodeGeneratorUtil.extractMappingInfo(psiElement)
        return FunctionInfo(name, params, returnType, mappingInfo)
    }

    private fun parseParameters(psiElement: PsiElement, importNames: List<String>): List<ParameterInfo> {
        val params = mutableListOf<ParameterInfo>()
        val valueParametersMethod = psiElement.javaClass.getMethod("getValueParameters")
        val valueParameters = valueParametersMethod.invoke(psiElement) as? List<*>
        valueParameters?.forEach { param ->
            val paramName = param?.javaClass?.getMethod("getName")?.invoke(param) as? String ?: "unknown"
            val typeRef = param?.javaClass?.getMethod("getTypeReference")?.invoke(param)
            val typeText = typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Any"
            params.add(ParameterInfo(
                name = paramName,
                type = typeText,
                importName = importNames.firstOrNull { it.contains(typeText) } ?: "",
                annotations = this.findParameterAnnotation(param as PsiElement)
            ))
        }

        return params
    }

    private fun parseReturnType(psiElement: PsiElement): String {
        val typeRef = psiElement.javaClass.getMethod("getTypeReference").invoke(psiElement)
        return typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Unit"
    }

    private fun findParameterAnnotation(psiElement: PsiElement): List<String> {
        val method = psiElement.javaClass.getMethod("getAnnotationEntries").invoke(psiElement) as? List<*>
        return method?.mapNotNull { it?.javaClass?.getMethod("getShortName")?.invoke(it)?.toString() } ?: emptyList()
    }
}