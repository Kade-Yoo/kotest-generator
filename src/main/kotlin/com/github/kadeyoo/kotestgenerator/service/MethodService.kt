package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.common.code.ComponentType
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.github.kadeyoo.kotestgenerator.util.ClassExtractor
import com.github.kadeyoo.kotestgenerator.util.FunctionExtractor
import com.github.kadeyoo.kotestgenerator.util.SpecTemplateUtil
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement

@Service(Service.Level.PROJECT)
class MethodService: CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean =
        psiElement.javaClass.name == KT_NAMED_FUNCTION_PACKAGE

    override fun generateSpec(psiElement: PsiElement, importNames: List<String>, isPresent: Boolean): String {
        val classInfo = ClassExtractor.extractClassInfoByReflection(psiElement, importNames)
        val functionInfo = FunctionExtractor.extractFromFunction(psiElement) ?: error("Not a function")
        val annotationNames = SpecTemplateUtil.findAnnotations(psiElement)
        val componentType = ComponentType.determineByAnnotations(annotationNames)
        return SpecTemplateUtil.generateSpec(classInfo, listOf(functionInfo), componentType, isPresent)
    }
}