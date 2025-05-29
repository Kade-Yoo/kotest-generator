package com.github.kadeyoo.kotestgenerator.service

import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.github.kadeyoo.kotestgenerator.util.FunctionExtractor
import com.github.kadeyoo.kotestgenerator.util.SpecTemplateUtil
import com.intellij.openapi.components.Service
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

@Service(Service.Level.PROJECT)
class ClassService : CodeSpecGenerator {

    override fun supports(psiElement: PsiElement): Boolean =
        psiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtClass"

    override fun generateSpec(psiElement: PsiElement, componentType: ComponentType, isPresent: Boolean): String {
        val className = (psiElement as? PsiNamedElement)?.name ?: "className"
        val packageName = JavaDirectoryService.getInstance().getPackage(psiElement.containingFile.containingDirectory)?.qualifiedName
        val functionInfos = FunctionExtractor.extractFromClass(psiElement)
        return SpecTemplateUtil.generateSpec(packageName, className, functionInfos, componentType, isPresent)
    }
}