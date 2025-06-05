package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.common.code.ComponentType
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_CLASS_PACKAGE
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.github.kadeyoo.kotestgenerator.util.ClassExtractor
import com.github.kadeyoo.kotestgenerator.util.FunctionExtractor
import com.github.kadeyoo.kotestgenerator.util.SpecTemplateUtil
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElement

@Service(Service.Level.PROJECT)
class ClassService : CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean =
        psiElement.javaClass.name == KT_CLASS_PACKAGE

    override fun generateSpec(psiElement: PsiElement, importNames: List<String>, isPresent: Boolean): String {
        val classInfo = ClassExtractor.extractClassInfoOfClass(psiElement, importNames)
        val annotationNames = SpecTemplateUtil.findAnnotations(psiElement)
        val componentType = ComponentType.determineByAnnotations(annotationNames)
        val functionInfo = FunctionExtractor.extractFromClass(psiElement, importNames)
        return SpecTemplateUtil.generateSpec(classInfo, functionInfo, componentType, isPresent)
    }
}