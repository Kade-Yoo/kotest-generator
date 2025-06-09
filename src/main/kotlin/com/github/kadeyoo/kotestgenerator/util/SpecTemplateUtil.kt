package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.KotestGeneratorSettings
import com.github.kadeyoo.kotestgenerator.common.code.ComponentType
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_CLASS_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.constants.Constants.KT_NAMED_FUNCTION_PACKAGE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GIVEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_BEHAVIOR_SPEC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCKK_EVERY
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.TEST_COMMENT
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.THEN
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.VALID_RESPONSE_STATUS_IS_BAD_REQUEST
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.VALID_RESPONSE_STATUS_IS_OK
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.WHEN
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.intellij.psi.PsiElement

object SpecTemplateUtil {
    private val state = KotestGeneratorSettings.getInstance().state

    fun generateSpec(classInfo: ClassInfo,
                     functionInfos: List<FunctionInfo>,
                     componentType: ComponentType,
                     isPresent: Boolean
    ): String = when (componentType) {
        ComponentType.API -> buildApiSpec(classInfo, functionInfos, isPresent)
        ComponentType.SERVICE -> buildServiceSpec(classInfo, functionInfos, isPresent)
        ComponentType.REPOSITORY -> buildRepositorySpec(classInfo)
        else -> buildDefaultSpec(classInfo, functionInfos)
    }

    fun findAnnotations(element: PsiElement): List<String> = try {
        when (element.javaClass.name) {
            KT_CLASS_PACKAGE -> {
                findAnnotation(element)
            }

            KT_NAMED_FUNCTION_PACKAGE -> {
                var parent = element.parent
                while (parent != null && parent.javaClass.name != KT_CLASS_PACKAGE) {
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

    private fun buildApiSpec(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        if (isPresent) {
            functionInfos.forEach { appendLine(buildApiTestBody(it, classInfo.requestMappingUrl)) }
            return@buildString
        }
        if (classInfo.packageName.isNotBlank()) appendLine("package ${classInfo.packageName}")
        appendLine()
        appendLine("import ${classInfo.packageName}.${classInfo.name}")
        appendLine(CodeGeneratorUtil.apiImportStatements())
        classInfo.importNames.forEach { appendLine("import $it") }
        appendLine(TEST_COMMENT)
        appendLine("@WebMvcTest(${classInfo.name}::class)")
        appendLine("@AutoConfigureMockMvc")
        appendLine("class ${classInfo.name}Test : BehaviorSpec({")
        appendLine()
        appendLine("    val mapper= jacksonObjectMapper()")
        classInfo.parameters.forEach {
            appendLine("    val ${it.name}: ${it.type} = mockk()")
        }
        appendLine("    lateinit var mockMvc: MockMvc")
        appendLine()
        appendLine("    beforeTest {")
        appendLine("    mockMvc = MockMvcBuilders")
        appendLine("        .standaloneSetup(${classInfo.name}(${classInfo.parameters.joinToString(",") { it.name }})).build()")
        appendLine("    }")
        appendLine()
        functionInfos.forEach { appendLine(buildApiTestBody(it, classInfo.requestMappingUrl)) }
        appendLine("})")
    }

    private fun buildApiTestBody(fn: FunctionInfo, prefixUrl: String): String {
        val (name, parameters, returnType, mappingInfo) = fn
        val httpMethod = mappingInfo.method
        val paramDecl = CodeGeneratorUtil.buildParamDecl(parameters)
        val badRequestParamDecl = CodeGeneratorUtil.buildBadParamDecl(parameters)

        val requestParams = parameters.filter { it.annotations.contains("RequestParam") }
        val paramAssignments = requestParams.joinToString("&") {
            "${it.name}=${CodeGeneratorUtil.dummyValue(it.type).replace("\"", "")}"
        }.let { if (it.isNotEmpty()) "?$it" else "" }

        val badParamAssignments = requestParams.joinToString("&") {
            "${it.name}=${CodeGeneratorUtil.badDummyValue(it.type).replace("\"", "")}"

        }.let { if (it.isNotEmpty()) "?$it" else "" }

        val pathVars = parameters.filter { it.annotations.contains("PathVariable") }
        val goodUrl = generateFullUrl(prefixUrl, mappingInfo.url, pathVars.map { it.type }, true,  paramAssignments)
        val badUrl = generateFullUrl(prefixUrl, mappingInfo.url, pathVars.map { it.type }, false,  badParamAssignments)

        val requestBody = parameters.find { it.annotations.contains("RequestBody") }
        val bodyContent =
            requestBody?.let {
                buildString {
                    appendLine("                    header { \"Content-Type\" to MediaType.APPLICATION_JSON }")
                    appendLine("                    content { mapper.writeValueAsString(${it.name}) }")
                }
            } ?: ""

        return buildString {
            appendLine("    $GIVEN(\"$name API 호출 시\") {")
            if (paramDecl.isNotBlank()) appendLine(paramDecl)
            appendLine("        // TODO: mock 반환값/로직 실제에 맞게 수정해주세요.")
            appendLine("        val expected: $returnType = ${CodeGeneratorUtil.dummyValue(returnType).replace("\"", "")} // TODO: 실제 값으로 변경")
            appendLine()
            appendLine("        // TODO: service mock 반환값/로직 실제에 맞게 수정해주세요.")
            appendLine("        every {  }")
            appendLine("        $WHEN(\"정상적인 요청을 보낼 경우\") {")
            appendLine("            // TODO: 실제 URL에 맞게 수정해주세요.")
            appendLine("            val result = mockMvc.${httpMethod}($goodUrl)")
            appendLine("                .andExpect { ")
            appendLine("                    status { isOk() } ")
            if (bodyContent.isNotEmpty()) append(bodyContent)
            appendLine("                }")
            appendLine("                .andReturn()")
            appendLine()
            appendLine("            // TODO: 실제 응답 코드에 맞게 수정해주세요.")
            appendLine("            $THEN(\"200 OK를 반환한다\") {")
            appendLine("                $VALID_RESPONSE_STATUS_IS_OK")
            appendLine("            }")
            appendLine()
            appendLine("            // TODO: 실제 응답 값에 맞게 수정해주세요.")
            appendLine("            $THEN(\"정상 결과를 반환한다\") {")
            appendLine("                val responseData = mapper.readValue<${returnType}>(result.response.contentAsByteArray)")
            appendLine("                responseData shouldBe expected // TODO: 실제 값에 맞게 검증")
            appendLine("            }")
            appendLine("        }")
            appendLine()
            appendLine("        $WHEN(\"존재하지 않는 값으로 요청하면\") {")
            if (badRequestParamDecl.isNotBlank()) appendLine(badRequestParamDecl)
            appendLine("            // TODO: 실제 URL에 맞게 수정해주세요.")
            appendLine("            val result = mockMvc.${httpMethod}($badUrl)")
            appendLine("                .andExpect { ")
            appendLine("                    status { isBadRequest() } ")
            if (bodyContent.isNotEmpty()) append(bodyContent)
            appendLine("                 }")
            appendLine("                .andReturn()")
            appendLine()
            appendLine("            $THEN(\"400 Bad Request 가 반환된다\") {")
            appendLine("                $VALID_RESPONSE_STATUS_IS_BAD_REQUEST")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
        }
    }

    private fun buildServiceSpec(classInfo: ClassInfo, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        if (isPresent) {
            functionInfos.forEach { appendLine(buildServiceTestBody(it, classInfo.parameters)) }
            return@buildString
        }
        appendLine(TEST_COMMENT)
        if (classInfo.packageName.isNotBlank()) appendLine("package ${classInfo.packageName}")
        appendLine()
        appendLine(CodeGeneratorUtil.apiImportStatements())
        classInfo.importNames.forEach { appendLine("import $it") }
        appendLine()
        appendLine("class ${classInfo.name}Test : BehaviorSpec({")
        classInfo.parameters.forEach { appendLine("    val ${it.name}: ${it.type} = mockk(relaxed = true)") }
        appendLine("    val service = ${classInfo.name}(${classInfo.parameters.joinToString(", ") { it.name }})")
        functionInfos.forEach { appendLine(buildServiceTestBody(it, classInfo.parameters)) }
        appendLine("})")
    }

    private fun buildServiceTestBody(fn: FunctionInfo, classParameter: List<ClassInfo.ParameterInfo>): String {
        val (name, parameters, returnType, _, dependencyCall) = fn
        val paramDecl = parameters.joinToString("\n") { "        val ${it.name}:${it.type} = ${CodeGeneratorUtil.dummyValue(it.type)}" }
        val paramList = parameters.joinToString(", ") { it.name }
        val expected = CodeGeneratorUtil.expectedValue(returnType)
        val notFoundParamList = parameters.joinToString(", ") { CodeGeneratorUtil.badDummyValue(it.type) }
        val everyStatements = CodeGeneratorUtil.generateMockStubsFromClassInfo(classParameter, dependencyCall)

        return buildString {
            appendLine("    $GIVEN(\"$name 에 유효한 정보를 입력하고\") {")
            if (paramDecl.isNotBlank()) appendLine(paramDecl)
            if (expected.isNotBlank()) appendLine("        $expected")
            if (expected.isNotBlank()) appendLine("        // TODO: mock 반환값/로직 실제에 맞게 수정해주세요.")
            everyStatements.forEach { appendLine("        $it") }
            appendLine()
            appendLine("        $WHEN(\"조회하면\") {")
            appendLine("            $THEN(\"정상 결과가 반환되어야 한다\") {")
            val result = if (returnType == "Unit") "service.$name($paramList)" else "val result = service.$name($paramList)"
            appendLine("                $result")
            if (expected.isNotBlank()) appendLine("                result shouldBe expected // TODO: 실제 값에 맞게 검증")
            appendLine("            }")
            appendLine("        }")
            appendLine()
            everyStatements.forEach { appendLine("        $it") }
            appendLine("        $WHEN(\"존재하지 않는 ID로 인해\") {")
            appendLine("            $THEN(\"예외가 발생해야 한다\") {")
            appendLine("                // TODO: 예외 타입에 맞게 수정")
            appendLine("                shouldThrow<Exception> {")
            appendLine("                    service.$name($notFoundParamList)")
            appendLine("                }")
            appendLine("            }")
            appendLine("        }")
//            for ((paramName, type) in parameters) {
//                append(buildParameterScenarioBlock(paramName, type))
//            }
            appendLine("    }")
        }
    }

    private fun buildRepositorySpec(classInfo: ClassInfo): String = buildString {
        if (classInfo.packageName.isNotBlank()) appendLine("package ${classInfo.packageName}")
        appendLine()
        appendLine(IMPORT_BEHAVIOR_SPEC)
        appendLine(IMPORT_MOCKK_EVERY)
        appendLine(state.getMockLibImport())
        appendLine()
        appendLine("class ${classInfo.name}Test : BehaviorSpec({")
        appendLine("""${state.getIndentString()}$GIVEN("Repository ${classInfo.name} 호출 시") {""")
        appendLine("""${state.getIndentString()}    $WHEN("데이터 접근이 수행되면") {""")
        appendLine("""${state.getIndentString()}        $$THEN("DB와 정상적으로 상호작용해야 한다") { }""")
        appendLine("        }")
        appendLine("    ")
        appendLine("})")
    }

    private fun buildDefaultSpec(classInfo: ClassInfo, functionInfos: List<FunctionInfo>): String = buildString {
        if (classInfo.packageName.isNotBlank()) appendLine("package ${classInfo.packageName}")
        appendLine()
        appendLine(IMPORT_BEHAVIOR_SPEC)
        appendLine(IMPORT_MOCKK_EVERY)
        appendLine()
        appendLine("class ${classInfo.name}Test : BehaviorSpec({")
        for (fn in functionInfos) {
            appendLine("""    $GIVEN("${fn.name}") {""")
            appendLine("""        $$THEN("should do something") { }""")
            appendLine("    }")
        }
        appendLine("})")
    }

    private fun scenarioCasesForType(type: String): List<Pair<String, String>> = when (type) {
        "Long", "Int" -> listOf(
            "정상값" to CodeGeneratorUtil.dummyValue(type),
            "음수" to CodeGeneratorUtil.badDummyValue(type),
        )
        "String" -> listOf(
            "정상값" to CodeGeneratorUtil.dummyValue(type),
            "빈값" to CodeGeneratorUtil.badDummyValue(type),
        )
        else -> listOf("기본값" to CodeGeneratorUtil.dummyValue(type))
    }

    private fun buildParameterScenarioBlock(paramName: String, type: String): String = buildString {
        val cases = scenarioCasesForType(type)
        for ((desc, value) in cases) {
            appendLine("        $WHEN(\"$paramName: $desc\") {")
            appendLine("            $THEN(\"적절한 응답이 반환된다\") {")
            appendLine("                val $paramName = $value")
            appendLine("                // TODO: 실제 메소드 호출 및 검증")
            appendLine("            }")
            appendLine("        }")
        }
    }

    private fun findAnnotation(element: PsiElement): List<String> {
        val method = element::class.java.methods.find { it.name == "getAnnotationEntries" }
        return (method?.invoke(element) as? List<*>)
            ?.mapNotNull { it?.javaClass?.getMethod("getShortName")?.invoke(it)?.toString() }
            ?: emptyList()
    }

    private fun generateUrl(url: String, pathVars: List<String>, isGoodUrl: Boolean): String {
        val regex = Regex("\\{\\w+}")
        val variableNames = regex.findAll(url).map { it.groupValues[0] }.toList()
        val variables = pathVars.map {
            if (isGoodUrl) CodeGeneratorUtil.dummyValue(it).replace("\"", "")
            else CodeGeneratorUtil.badDummyValue(it).replace("\"", "") }.toList()
        return variableNames.zip(variables).fold(url) { acc, (key, value) -> acc.replace(key, value) }
    }

    private fun generateFullUrl(
        prefixUrl: String, url: String, pathVars: List<String>, isGoodUrl: Boolean, paramAssignments: String
    ): String =
        buildString {
            append("\"$prefixUrl")
            append(generateUrl(url, pathVars.map { it }, isGoodUrl))
            append(paramAssignments)
            append("\"")
        }
}