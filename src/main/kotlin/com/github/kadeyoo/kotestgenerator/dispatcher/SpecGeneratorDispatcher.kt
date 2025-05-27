package com.github.kadeyoo.kotestgenerator.dispatcher

import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.github.kadeyoo.kotestgenerator.generator.CodeSpecGenerator
import com.intellij.psi.PsiElement

class SpecGeneratorDispatcher(
    private val generators: List<CodeSpecGenerator>
) {
    fun generateSpec(psiElement: PsiElement): String {
        val element = findElement(psiElement) ?: error("No valid element found for generation: ${psiElement::class.simpleName}")
        val annotationNames = getAnnotations(element)
        val componentType = determineComponentType(annotationNames)
        val generator = generators.firstOrNull { it.supports(element) } ?: error("No suitable generator found for element: ${element::class.simpleName}")
        return generator.generateSpec(element, componentType)
    }

    private fun getAnnotations(element: PsiElement) = try {
        when (element.javaClass.name) {
            "org.jetbrains.kotlin.psi.KtClass" -> {
                findAnnotation(element)
            }

            "org.jetbrains.kotlin.psi.KtNamedFunction" -> {
                var parent = element.parent
                while (parent != null && parent.javaClass.name != "org.jetbrains.kotlin.psi.KtClass") {
                    parent = parent.parent
                }

                if (parent != null) {
                    findAnnotation(parent)
                } else {
                    emptyList()
                }
            }

            else -> emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

    private fun findAnnotation(element: PsiElement): List<String> {
        val method = element::class.java.methods.find { it.name == "getAnnotationEntries" }
        return (method?.invoke(element) as? List<*>)
            ?.mapNotNull { it?.javaClass?.getMethod("getShortName")?.invoke(it)?.toString() }
            ?: emptyList()
    }

    private fun findElement(psiElement: PsiElement): PsiElement? {
        var tempPsiElement = psiElement
        while (tempPsiElement.parent != null) {
            if (tempPsiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtClass") {
                return tempPsiElement
            }

            if (tempPsiElement.javaClass.name == "org.jetbrains.kotlin.psi.KtNamedFunction") {
                return tempPsiElement
            }

            tempPsiElement = tempPsiElement.parent
        }

        return null
    }

    private fun determineComponentType(annotations: List<String>): ComponentType {
        return when {
            annotations.any { it.endsWith("RestController") || it.endsWith("Controller") } -> ComponentType.API
            annotations.any { it.endsWith("Service") } -> ComponentType.SERVICE
            annotations.any { it.endsWith("Repository") } -> ComponentType.REPOSITORY
            else -> ComponentType.UNKNOWN
        }
    }
}