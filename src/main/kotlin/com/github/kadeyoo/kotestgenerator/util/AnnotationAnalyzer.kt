package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.annotation.SpringAnnotation
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_CLASS_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.exception.TemplateGenerationException
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo
import com.intellij.psi.PsiElement

/**
 * PSI 요소에서 어노테이션을 분석하는 유틸리티 클래스
 */
class AnnotationAnalyzer {
    
    /**
     * PSI 요소에서 어노테이션 목록을 추출합니다.
     * 예외 발생 시 TemplateGenerationException을 던집니다.
     */
    fun findAnnotations(element: PsiElement): List<String> = try {
        when (element.javaClass.name) {
            KT_CLASS_PACKAGE -> findAnnotation(element)
            KT_NAMED_FUNCTION_PACKAGE -> findAnnotationFromParent(element)
            else -> throw TemplateGenerationException("지원하지 않는 PSI 타입: ${element.javaClass.name}")
        }
    } catch (e: Exception) {
        throw TemplateGenerationException("어노테이션 추출 실패: ${e.message}")
    }

    /**
     * 클래스에서 어노테이션을 찾습니다.
     */
    private fun findAnnotation(element: PsiElement): List<String> = try {
        val method = element::class.java.methods.find { it.name == "getAnnotationEntries" }
        (method?.invoke(element) as? List<*>)
            ?.mapNotNull { it?.javaClass?.getMethod("getShortName")?.invoke(it)?.toString() }
            ?: emptyList()
    } catch (e: Exception) {
        throw TemplateGenerationException("클래스 어노테이션 추출 실패: ${e.message}")
    }
    
    /**
     * 메서드의 부모 클래스에서 어노테이션을 찾습니다.
     */
    private fun findAnnotationFromParent(element: PsiElement): List<String> {
        var parent = element.parent
        while (parent != null && parent.javaClass.name != KT_CLASS_PACKAGE) {
            parent = parent.parent
        }
        return parent?.let { findAnnotation(it) } ?: emptyList()
    }
    
    /**
     * 특정 어노테이션이 포함되어 있는지 확인합니다.
     */
    fun hasAnnotation(annotations: List<String>, annotationName: String): Boolean =
        annotations.any { it.contains(annotationName) }

    /**
     * 특정 SpringAnnotation을 가진 파라미터를 필터링합니다.
     */
    fun filterParametersBySpringAnnotation(
        parameters: List<ParameterInfo>,
        annotation: SpringAnnotation
    ): List<ParameterInfo> =
        parameters.filter { it.springAnnotations().contains(annotation) }

    /**
     * Request 관련 어노테이션을 가진 파라미터를 필터링합니다.
     */
    fun filterRequestParameters(
        parameters: List<ParameterInfo>
    ): List<ParameterInfo> =
        parameters.filter { it.springAnnotations().any { SpringAnnotation.isRequestAnnotation(it) } }
} 