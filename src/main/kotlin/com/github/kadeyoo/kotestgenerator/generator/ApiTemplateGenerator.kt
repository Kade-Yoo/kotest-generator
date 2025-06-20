package com.github.kadeyoo.kotestgenerator.generator

import com.github.kadeyoo.kotestgenerator.common.annotation.SpringAnnotation
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GIVEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.THEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.WHEN
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.DEFAULT_INDENT
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.DOUBLE_INDENT
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.HTTP_BAD_REQUEST
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.HTTP_OK
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.MAPPER_VARIABLE
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.MEDIA_TYPE_JSON
import com.github.kadeyoo.kotestgenerator.common.constants.TemplateConstants.MOCK_MVC_VARIABLE
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo
import com.github.kadeyoo.kotestgenerator.util.AnnotationAnalyzer
import com.github.kadeyoo.kotestgenerator.util.CodeGeneratorUtil
import com.github.kadeyoo.kotestgenerator.util.TemplateUtils
import com.github.kadeyoo.kotestgenerator.util.UrlGenerator

class ApiTemplateGenerator : TemplateGenerator {

    private val urlGenerator = UrlGenerator()
    private val annotationAnalyzer = AnnotationAnalyzer()

    override fun generate(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        if (isPresent) {
            functionInfos.forEach { appendLine(buildApiTestBody(it, classInfo.requestMappingUrl)) }
            return@buildString
        }
        val body = buildString {
            appendLine()
            appendLine("${DEFAULT_INDENT}val $MAPPER_VARIABLE = jacksonObjectMapper()")
            appendLine(TemplateUtils.buildMockDeclarations(classInfo.parameters))
            appendLine("${DEFAULT_INDENT}lateinit var $MOCK_MVC_VARIABLE: MockMvc")
            appendLine()
            appendLine("${DEFAULT_INDENT}beforeTest {")
            appendLine("${DOUBLE_INDENT}$MOCK_MVC_VARIABLE = MockMvcBuilders")
            appendLine("${DOUBLE_INDENT}${DEFAULT_INDENT}.standaloneSetup(${classInfo.name}(${classInfo.parameters.joinToString(",") { it.name }})).build()")
            appendLine("${DEFAULT_INDENT}}")
            appendLine()
            functionInfos.forEach { appendLine(buildApiTestBody(it, classInfo.requestMappingUrl)) }
        }
        append(TemplateUtils.buildTestClass(
            classInfo = classInfo,
            additionalImports = "import ${classInfo.packageName}.${classInfo.name}\n" + CodeGeneratorUtil.apiImportStatements(),
            body = body
        ))
    }

    private fun buildApiTestBody(fn: FunctionInfo, prefixUrl: String): String = buildString {
        val testData = prepareTestData(fn, prefixUrl)
        val hasParams = fn.parameters.isNotEmpty()
        val requestExample = if (hasParams)
            fn.parameters.joinToString("\n") { "${DOUBLE_INDENT}val ${it.name} : ${it.type} = ${CodeGeneratorUtil.dummyValue(it.type)}" }
        else "{}"
        val expectedExample = if (hasParams) CodeGeneratorUtil.dummyValue(fn.returnType) else ""
        appendLine("${DEFAULT_INDENT}$GIVEN(\"${fn.name} API 호출 시 \") {")
        appendLine("${DOUBLE_INDENT}// given: 요청/파라미터/expected 값 준비")
        if (hasParams) {
            appendLine(requestExample)
            appendLine("${DOUBLE_INDENT}val expected = $expectedExample")
            appendLine()
        }
        appendLine("$DOUBLE_INDENT$WHEN(\"정상 데이터로 요청하면\") {")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}val result = $MOCK_MVC_VARIABLE.${testData.httpMethod}(${testData.goodUrl}) {")
        if (testData.bodyContent.isNotEmpty()) append(testData.bodyContent)
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}}")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}.andExpect { status { $HTTP_OK } }")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}.andReturn()")
        appendLine()
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}$THEN(\"정상 응답 반환\") {")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}val response = $MAPPER_VARIABLE.readValue<${testData.returnType}>(result.response.contentAsByteArray)")
        if (hasParams) {
            appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}response shouldBe expected")
        } else {
            appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}// TODO: 결과 검증")
        }
        appendLine("$DOUBLE_INDENT$DEFAULT_INDENT}")
        appendLine("$DOUBLE_INDENT}")
        appendLine()
        appendLine("$DOUBLE_INDENT$WHEN(\"잘못된 요청 하면\") {")
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}val result = $MOCK_MVC_VARIABLE.${testData.httpMethod}(${testData.badUrl}) {")
        if (testData.bodyContent.isNotEmpty()) append(testData.bodyContent)
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}}")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}.andExpect { status { $HTTP_BAD_REQUEST } }")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}.andReturn()")
        appendLine()
        appendLine("$DOUBLE_INDENT${DEFAULT_INDENT}$THEN(\"400 반환\") {")
        appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}result.response.status shouldBe 400")
        appendLine("$DOUBLE_INDENT$DEFAULT_INDENT}")
        appendLine("$DOUBLE_INDENT}")
        appendLine("$DEFAULT_INDENT}")
    }

    private fun prepareTestData(fn: FunctionInfo, prefixUrl: String): ApiTestData {
        val (name, parameters, returnType, mappingInfo) = fn
        val httpMethod = mappingInfo.method
        val paramDecl = CodeGeneratorUtil.buildParamDecl(parameters)
        val badRequestParamDecl = CodeGeneratorUtil.buildBadParamDecl(parameters)

        val requestParams = annotationAnalyzer.filterParametersBySpringAnnotation(parameters, SpringAnnotation.REQUEST_PARAM)
        val paramAssignments = urlGenerator.buildParamAssignments(requestParams, true)
        val badParamAssignments = urlGenerator.buildParamAssignments(requestParams, false)

        val pathVars = annotationAnalyzer.filterParametersBySpringAnnotation(parameters, SpringAnnotation.PATH_VARIABLE)
        val goodUrl = urlGenerator.generateFullUrl(prefixUrl, mappingInfo.url, pathVars.map { it.type }, true, paramAssignments)
        val badUrl = urlGenerator.generateFullUrl(prefixUrl, mappingInfo.url, pathVars.map { it.type }, false, badParamAssignments)

        val requestBody = parameters.find { 
            annotationAnalyzer.hasAnnotation(it.annotations, SpringAnnotation.REQUEST_BODY.value)
        }
        val bodyContent = buildRequestBodyContent(requestBody)

        return ApiTestData(
            name = name,
            httpMethod = httpMethod,
            returnType = returnType,
            paramDecl = paramDecl,
            badRequestParamDecl = badRequestParamDecl,
            goodUrl = goodUrl,
            badUrl = badUrl,
            bodyContent = bodyContent
        )
    }

    private fun buildRequestBodyContent(requestBody: ParameterInfo?): String {
        return requestBody?.let {
            buildString {
                appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}accept = $MEDIA_TYPE_JSON")
                appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}contentType = $MEDIA_TYPE_JSON")
                appendLine("$DOUBLE_INDENT${DOUBLE_INDENT}content = $MAPPER_VARIABLE.writeValueAsString(${it.name})")
            }
        } ?: ""
    }

    private data class ApiTestData(
        val name: String,
        val httpMethod: String,
        val returnType: String,
        val paramDecl: String,
        val badRequestParamDecl: String,
        val goodUrl: String,
        val badUrl: String,
        val bodyContent: String
    )
} 