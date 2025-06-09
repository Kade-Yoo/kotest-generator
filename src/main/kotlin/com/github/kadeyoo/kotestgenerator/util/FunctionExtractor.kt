package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GET_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo.DependencyCall
import com.github.kadeyoo.kotestgenerator.dto.MappingInfo
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
        val isPrivateMethod = extractPrivateFunctionsByReflection(psiElement).isNotEmpty()
        if (isPrivateMethod) {
            return null
        }
        val name = (psiElement as? PsiNamedElement)?.name ?: return null
        val params = parseParameters(psiElement, importNames)
        val returnType = parseReturnType(psiElement)
        val mappingInfo = extractMappingInfo(psiElement)
        val dependencyCallList = extractDependencyCallInfoByReflection(psiElement)
        return FunctionInfo(name, params, returnType, mappingInfo, dependencyCallList)
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

    private fun extractMappingInfo(method: PsiElement): MappingInfo {
        val pattern = Regex("@(GetMapping|PostMapping|PutMapping|DeleteMapping)\\(\"([^\"]+)\"\\)")
        // method의 자식 중 어노테이션이 포함된 요소만 필터링
        val annotationElements = method.children.filter {
            it.children.any { child -> child.javaClass.name.contains("Annotation") }
        }

        for (annotation in annotationElements) {
            val match = pattern.find(annotation.text) ?: continue
            val httpMethod = match.groupValues[1]
            val url = match.groupValues[2]

            return MappingInfo(url, httpMethod.removeSuffix("Mapping").lowercase())
        }

        return MappingInfo("", GET_METHOD_NAME)
    }

    private fun extractDependencyCallInfoByReflection(psiElement: PsiElement): List<DependencyCall> {
        val bodyExpr = try {
            psiElement.javaClass.getMethod("getBodyExpression").invoke(psiElement)
        } catch (e: Exception) {
            return emptyList()
        } ?: return emptyList()

        val result = mutableListOf<DependencyCall>()
        findCallExpressions(bodyExpr, result)
        return result
    }

    // 재귀적으로 children 을 탐색하면서 KtCallExpression을 찾는다
    private fun findCallExpressions(element: Any?, result: MutableList<DependencyCall>) {
        if (element == null) return

        val className = element.javaClass.name
        if ("KtCallExpression" in className) {
            val call = element as PsiElement
            val methodCall = call.text

            // callee (메소드 이름)
            val callee = try {
                element.javaClass.getMethod("getCalleeExpression").invoke(element)
            } catch (_: Exception) { null }

            val methodName = callee?.javaClass?.getMethod("getText")?.invoke(callee) as? String ?: return

            // 인자 추출
            val argList = try {
                element.javaClass.getMethod("getValueArguments").invoke(element) as? List<*>
            } catch (_: Exception) { null }

            val parameters = argList?.mapNotNull {
                try {
                    val expr = it?.javaClass?.getMethod("getArgumentExpression")?.invoke(it)
                    expr?.javaClass?.getMethod("getText")?.invoke(expr) as? String
                } catch (_: Exception) { null }
            } ?: emptyList()

            // 수신 객체 (예: userRepository) 추출
            val parent = (element as? PsiElement)?.parent
            val receiver = try {
                parent?.javaClass?.getMethod("getReceiverExpression")?.invoke(parent)
            } catch (_: Exception) { null }

            val receiverText = try {
                receiver?.javaClass?.getMethod("getText")?.invoke(receiver) as? String
            } catch (_: Exception) { null }

            val name = if (receiverText != null) "$receiverText.$methodName" else methodName

            result.add(
                DependencyCall(
                    name = name,
                    methodCall = methodCall,
                    parameters = parameters,
                    returnType = "Any" // 정확한 타입 추론은 어려우므로 Any 또는 TODO
                )
            )
        }

        // 하위 children 재귀 탐색
        val children = try {
            (element as? PsiElement)?.javaClass?.getMethod("getChildren")?.invoke(element) as? Array<*>
        } catch (_: Exception) { null }

        children?.forEach { findCallExpressions(it, result) }
    }

    private fun extractPrivateFunctionsByReflection(root: PsiElement): List<String> {
        val result = mutableListOf<String>()

        fun visit(element: Any?) {
            if (element == null) return

            val className = element.javaClass.name
            // KtNamedFunction 인지 확인
            if ("KtNamedFunction" in className) {
                val isPrivate = try {
                    val modifierList = element.javaClass.getMethod("getModifierList").invoke(element)
                    val text = modifierList?.javaClass?.getMethod("getText")?.invoke(modifierList) as? String
                    text?.contains("private") == true
                } catch (_: Exception) {
                    false
                }

                if (isPrivate) {
                    // 함수 이름 가져오기
                    val name = try {
                        element.javaClass.getMethod("getName").invoke(element) as? String
                    } catch (_: Exception) {
                        "<unknown>"
                    }
                    result.add(name ?: "<unknown>")
                }
            }

            // 재귀 탐색: 하위 children 호출
            val children = try {
                (element as? PsiElement)?.javaClass?.getMethod("getChildren")?.invoke(element) as? Array<*>
            } catch (_: Exception) { null }

            children?.forEach { visit(it) }
        }

        visit(root)
        return result
    }
}