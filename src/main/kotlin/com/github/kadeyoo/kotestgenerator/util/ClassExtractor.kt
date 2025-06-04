package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

object ClassExtractor {

    fun extractClassInfoByReflection(psiClass: PsiElement, importNames: List<String>): ClassInfo {
        val name = (psiClass as? PsiNamedElement)?.name ?: "className"
        val packageName = JavaDirectoryService.getInstance().getPackage(psiClass.containingFile.containingDirectory)?.qualifiedName
        val primaryConstructorMethod = psiClass.javaClass.methods.firstOrNull { it.name == "getPrimaryConstructor" }
        val primaryConstructor = primaryConstructorMethod?.invoke(psiClass)
        val valueParametersMethod = primaryConstructor?.javaClass?.methods?.firstOrNull { it.name == "getValueParameters" }
        val valueParameters = valueParametersMethod?.invoke(primaryConstructor) as? List<*>
        val parameters = valueParameters?.map { param ->
            val parameterName = param?.javaClass?.getMethod("getName")?.invoke(param) as? String ?: "param"
            val typeRef = param?.javaClass?.getMethod("getTypeReference")?.invoke(param)
            val typeText = typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Any"
            parameterName to typeText
        } ?: emptyList()
        return ClassInfo(name, parameters, importNames, packageName ?: "com.example")
    }
}