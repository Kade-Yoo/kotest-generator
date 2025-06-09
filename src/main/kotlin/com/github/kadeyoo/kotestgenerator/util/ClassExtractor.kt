package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement

object ClassExtractor {

    fun extractClassInfoOfClass(psiClass: PsiElement, importNames: List<String>): ClassInfo {
        return generateClassInfo(psiClass, importNames)
    }

    fun extractClassInfoOfMethod(psiElement: PsiElement, importNames: List<String>): ClassInfo {
        val classElement = psiElement.parent.parent
        return generateClassInfo(classElement, importNames)
    }

    private fun generateClassInfo(classElement: PsiElement, importNames: List<String>): ClassInfo {
        val name = (classElement as? PsiNamedElement)?.name ?: "className"
        val packageName = JavaDirectoryService.getInstance().getPackage(classElement.containingFile.containingDirectory)?.qualifiedName
        val annotations = classElement.children.filter { it.text.contains("@RequestMapping") }
        val requestMappingRegex = Regex("@RequestMapping\\(\"([^\"]+)\"\\)")
        val classUrl = annotations.firstNotNullOfOrNull { requestMappingRegex.find(it.text)?.groupValues?.get(1) } ?: ""
        val primaryConstructorMethod = classElement.javaClass.methods.firstOrNull { it.name == "getPrimaryConstructor" }
        val primaryConstructor = primaryConstructorMethod?.invoke(classElement)
        val valueParametersMethod =
            primaryConstructor?.javaClass?.methods?.firstOrNull { it.name == "getValueParameters" }
        val valueParameters = valueParametersMethod?.invoke(primaryConstructor) as? List<*>
        val parameters = valueParameters?.map { param ->
            val parameterName = param?.javaClass?.getMethod("getName")?.invoke(param) as? String ?: "param"
            val typeRef = param?.javaClass?.getMethod("getTypeReference")?.invoke(param)
            val typeText = typeRef?.javaClass?.getMethod("getText")?.invoke(typeRef) as? String ?: "Any"
            ClassInfo.ParameterInfo(name = parameterName, type = typeText)
        } ?: emptyList()
        return ClassInfo(name, parameters, importNames, packageName ?: "com.example", classUrl)
    }

    fun extractImportNamesFromFile(psiFile: PsiFile): List<String> {
        val importListObj = extractImportsFromFile(psiFile) ?: return emptyList()
        val getImportsMethod = importListObj.javaClass.methods.firstOrNull { it.name == "getImports" }
        val importDirectives = getImportsMethod?.invoke(importListObj) as? List<*>
        return importDirectives?.mapNotNull { importDirective ->
            val getFqNameMethod = importDirective?.javaClass?.methods?.firstOrNull { it.name == "getImportedFqName" }
            val fqNameObj = getFqNameMethod?.invoke(importDirective)
            fqNameObj?.toString()
        } ?: emptyList()
    }

    private fun extractImportsFromFile(psiFile: PsiFile): Any? {
        val getImportListMethod = psiFile.javaClass.methods.firstOrNull { it.name == "getImportList" }
        return getImportListMethod?.invoke(psiFile)
    }
}