package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GIVEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.THEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.WHEN
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.DEFAULT_INDENT
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.DOUBLE_INDENT
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.SERVICE_VARIABLE
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo
import com.github.kadeyoo.kotestgenerator.util.CodeGeneratorUtil
import com.github.kadeyoo.kotestgenerator.util.TemplateUtils

class ServiceTemplateGenerator : TemplateGenerator {

    override fun generate(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        if (isPresent) {
            functionInfos.forEach { appendLine(buildServiceTestBody(it, classInfo.parameters)) }
            return@buildString
        }
        val body = buildString {
            appendLine(TemplateUtils.buildMockDeclarations(classInfo.parameters))
            appendLine("${DEFAULT_INDENT}val $SERVICE_VARIABLE = ${classInfo.name}(${classInfo.parameters.joinToString(", ") { it.name }})")
            functionInfos.forEach { appendLine(buildServiceTestBody(it, classInfo.parameters)) }
        }
        append(TemplateUtils.buildTestClass(
            classInfo = classInfo,
            additionalImports = CodeGeneratorUtil.apiImportStatements(),
            body = body
        ))
    }

    private fun buildServiceTestBody(fn: FunctionInfo, classParameter: List<ClassInfo.ParameterInfo>): String = buildString {
        val testData = prepareServiceTestData(fn, classParameter)
        val hasParams = fn.parameters.isNotEmpty()
        val paramExample = if (hasParams)
            fn.parameters.joinToString("\n") { "${DOUBLE_INDENT}val ${it.name} : ${it.type} = ${CodeGeneratorUtil.dummyValue(it.type)}" }
        else "{}"
        val expectedExample = if (hasParams) CodeGeneratorUtil.dummyValue(fn.returnType) else ""
        appendLine("${DEFAULT_INDENT}$GIVEN(\"${fn.name} 요청 할 시\") {")
        appendLine("${DOUBLE_INDENT}// given: 파라미터/expected 값 준비")
        if (hasParams) {
            appendLine(paramExample)
            appendLine("${DOUBLE_INDENT}val expected = $expectedExample")
            appendLine()
        }
        testData.everyStatements.forEach { appendLine("${DOUBLE_INDENT}$it") }
        appendLine()
        appendLine("$DOUBLE_INDENT$WHEN(\"정상 호출 시\") {")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}val result = $SERVICE_VARIABLE.${testData.name}(${testData.paramList})")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}$THEN(\"정상 결과 반환\") {")
        if (hasParams) {
            appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}result shouldBe expected")
        } else {
            appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}// TODO: 결과 검증")
        }
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}}")
        appendLine("$DOUBLE_INDENT}")
        appendLine()
        testData.everyStatements.forEach { appendLine("${DOUBLE_INDENT}$it") }
        appendLine("$DOUBLE_INDENT$WHEN(\"존재하지 않는 값 요청 시\") {")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}$THEN(\"예외 발생\") {")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}shouldThrow<Exception> { $SERVICE_VARIABLE.${testData.name}(${testData.notFoundParamList}) }")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}}")
        appendLine("$DOUBLE_INDENT}")
        appendLine("$DEFAULT_INDENT}")
    }

    private fun prepareServiceTestData(fn: FunctionInfo, classParameter: List<ClassInfo.ParameterInfo>): ServiceTestData {
        val (name, parameters, returnType, _, dependencyCall) = fn
        val paramDecl = buildParameterDeclarations(parameters)
        val paramList = parameters.joinToString(", ") { it.name }
        val expected = CodeGeneratorUtil.expectedValue(returnType)
        val notFoundParamList = parameters.joinToString(", ") { CodeGeneratorUtil.badDummyValue(it.type) }
        val everyStatements = CodeGeneratorUtil.generateMockStubsFromClassInfo(classParameter, dependencyCall)

        return ServiceTestData(
            name = name,
            returnType = returnType,
            paramDecl = paramDecl,
            paramList = paramList,
            expected = expected,
            notFoundParamList = notFoundParamList,
            everyStatements = everyStatements
        )
    }

    private fun buildParameterDeclarations(parameters: List<ParameterInfo>): String {
        return parameters.joinToString("\n") { "$DOUBLE_INDENT val ${it.name}:${it.type} = ${CodeGeneratorUtil.dummyValue(it.type)}" }
    }

    private data class ServiceTestData(
        val name: String,
        val returnType: String,
        val paramDecl: String,
        val paramList: String,
        val expected: String,
        val notFoundParamList: String,
        val everyStatements: List<String>
    )
} 