package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.DEFAULT_INDENT
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo

object TemplateUtils {
    // importNames -> import 문자열 캐싱
    private val importCache = mutableMapOf<List<String>, String>()

    /**
     * 패키지 선언을 생성합니다.
     */
    private fun buildPackageDeclaration(packageName: String): String =
        if (packageName.isNotBlank()) "package $packageName" else ""
    
    /**
     * import 문들을 생성합니다. (캐싱)
     */
    fun buildImports(importNames: List<String>): String =
        importCache.getOrPut(importNames) {
            importNames.joinToString("\n") { "import $it" }
        }
    
    /**
     * 클래스 선언부를 생성합니다.
     */
    private fun buildClassDeclaration(className: String): String =
        "class ${className}Test : BehaviorSpec({"
    
    /**
     * 클래스 종료부를 생성합니다.
     */
    private fun buildClassEnd(): String = "})"
    
    /**
     * Mock 객체 선언들을 생성합니다.
     */
    fun buildMockDeclarations(parameters: List<ClassInfo.ParameterInfo>): String =
        parameters.joinToString("\n") { "${DEFAULT_INDENT}val ${it.name}: ${it.type} = mockk(relaxed = true)" }
    
    /**
     * 공통 헤더 (패키지 + 빈 줄)를 생성합니다.
     */
    private fun buildCommonHeader(classInfo: ClassInfo): String = buildString {
        if (classInfo.packageName.isNotBlank()) {
            appendLine(buildPackageDeclaration(classInfo.packageName))
            appendLine()
        }
    }
    
    /**
     * 공통 import 문들을 생성합니다. (캐싱)
     */
    private fun buildCommonImports(classInfo: ClassInfo, additionalImports: String = ""): String = buildString {
        if (additionalImports.isNotBlank()) {
            appendLine(additionalImports)
        }
        if (classInfo.importNames.isNotEmpty()) {
            appendLine(buildImports(classInfo.importNames))
        }
    }
    
    /**
     * 공통 클래스 선언부를 생성합니다.
     */
    private fun buildCommonClassDeclaration(className: String): String = buildString {
        appendLine(buildClassDeclaration(className))
    }

    /**
     * 공통 테스트 클래스 전체를 생성합니다. (헤더+임포트+클래스 선언+본문+종료)
     */
    fun buildTestClass(
        classInfo: ClassInfo,
        additionalImports: String = "",
        body: String
    ): String = buildString {
        append(buildCommonHeader(classInfo))
        append(buildCommonImports(classInfo, additionalImports))
        append(buildCommonClassDeclaration(classInfo.name))
        append(body)
        append(buildClassEnd())
    }
} 