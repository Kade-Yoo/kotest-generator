package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.code.ComponentType
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.generator.ApiTemplateGenerator
import com.github.kadeyoo.kotestgenerator.generator.DefaultTemplateGenerator
import com.github.kadeyoo.kotestgenerator.generator.RepositoryTemplateGenerator
import com.github.kadeyoo.kotestgenerator.generator.ServiceTemplateGenerator
import com.github.kadeyoo.kotestgenerator.generator.TemplateGenerator
import com.intellij.psi.PsiElement

object SpecTemplateUtil {
    
    private val templateGenerators: Map<ComponentType, TemplateGenerator> = mapOf(
        ComponentType.API to ApiTemplateGenerator(),
        ComponentType.SERVICE to ServiceTemplateGenerator(),
        ComponentType.REPOSITORY to RepositoryTemplateGenerator(),
        ComponentType.UNKNOWN to DefaultTemplateGenerator()
    )
    
    private val annotationAnalyzer = AnnotationAnalyzer()

    fun generateSpec(classInfo: ClassInfo,
                     functionInfos: List<FunctionInfo>,
                     componentType: ComponentType,
                     isPresent: Boolean
    ): String {
        val generator = templateGenerators[componentType] ?: DefaultTemplateGenerator()
        return generator.generate(classInfo, functionInfos, isPresent)
    }

    fun findAnnotations(element: PsiElement): List<String> = 
        annotationAnalyzer.findAnnotations(element)

}