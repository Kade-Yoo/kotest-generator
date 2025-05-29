package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo
import com.github.kadeyoo.kotestgenerator.common.ComponentType
import com.github.kadeyoo.kotestgenerator.common.KotestGeneratorSettings

object SpecTemplateUtil {
    fun generateSpec(
        packageName: String?, className: String, functionInfos: List<FunctionInfo>,
        componentType: ComponentType, isPresent: Boolean
    ): String = when (componentType) {
        ComponentType.API -> buildApiSpec(packageName, className, functionInfos, isPresent)
        ComponentType.SERVICE -> buildServiceSpec(packageName, className, functionInfos, isPresent)
        ComponentType.REPOSITORY -> buildRepositorySpec(packageName, className)
        else -> buildDefaultSpec(packageName, className, functionInfos)
    }

    private fun buildApiSpec(packageName: String?, className: String, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        if (isPresent) {
            functionInfos.forEach { appendLine(buildApiTestBody(it)) }
            return@buildString
        }
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine(CodeGeneratorUtil.apiImportStatements())
        appendLine(CodeGeneratorUtil.commentStatements())
        appendLine("@WebMvcTest($className::class)")
        appendLine("@AutoConfigureMockMvc")
        appendLine("class ${className}ApiTest(")
        appendLine("    @Autowired val mockMvc: MockMvc")
        appendLine(") : BehaviorSpec({")
        functionInfos.forEach { appendLine(buildApiTestBody(it)) }
        appendLine("})")
    }

    fun buildApiTestBody(fn: FunctionInfo): String {
        val (name, parameters, _, mappingInfo) = fn
        val url = mappingInfo?.url ?: "/TODO"
        val httpMethod = mappingInfo?.method ?: "get"
        val urlParams = CodeGeneratorUtil.buildUrlParams(parameters)
        val paramDecl = CodeGeneratorUtil.buildParamDecl(parameters)
        val fullUrl = if (urlParams.isNotBlank()) "\"$url?$urlParams\"" else "\"$url\""
        val notFoundParamDecl = CodeGeneratorUtil.buildBadParamDecl(parameters)
        val notFoundUrlParams = CodeGeneratorUtil.buildBadUrlParams(parameters)
        val notFoundUrl = if (notFoundUrlParams.isNotBlank()) "\"$url?$notFoundUrlParams\"" else "\"$url\""
        return buildString {
            appendLine("    given(\"$name API 호출 시\") {")
            if (paramDecl.isNotBlank()) appendLine("        $paramDecl")
            appendLine("        `when`(\"정상적인 요청을 보낼 경우\") {")
            appendLine("            then(\"200 OK와 정상 결과를 반환한다\") {")
            appendLine("                val result = mockMvc.perform(${httpMethod}($fullUrl)) // TODO: post/put 등도 지원")
            appendLine("                    .andExpect(status().isOk)")
            appendLine("                    .andReturn()")
            appendLine("                println(result.response.contentAsString) // TODO: 실제 응답 값 검증 코드 추가")
            appendLine("            }")
            appendLine("        }")
            appendLine()
            appendLine("        // 예외 케이스: 존재하지 않는 값으로 요청")
            appendLine("        `when`(\"존재하지 않는 값으로 요청하면\") {")
            if (notFoundParamDecl.isNotBlank()) appendLine("            $notFoundParamDecl")
            appendLine("            then(\"404 Not Found가 반환된다\") {")
            appendLine("                mockMvc.perform(${httpMethod}($notFoundUrl))")
            appendLine("                    .andExpect(status().isNotFound) // TODO: 실제 예외/응답 코드에 맞게 수정")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
        }
    }

    private fun buildServiceSpec(packageName: String?, className: String, functionInfos: List<FunctionInfo>, isPresent: Boolean): String = buildString {
        val state = KotestGeneratorSettings.getInstance().state
        if (isPresent) {
            functionInfos.forEach { appendLine(buildServiceTestBody(it)) }
            return@buildString
        }
        appendLine(CodeGeneratorUtil.commentStatements())
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine(state.getMockLibImport())
        appendLine()
        appendLine("class ${className}Test : BehaviorSpec({")
        functionInfos.forEach { appendLine(buildServiceTestBody(it)) }
        appendLine("})")
    }

    fun buildServiceTestBody(fn: FunctionInfo): String {
        val (name, parameters, returnType) = fn
        val paramDecl = parameters.joinToString(", ") { (n, t) -> "val $n = ${CodeGeneratorUtil.dummyValue(t)}" }
        val paramList = parameters.joinToString(", ") { it.first }
        val expected = CodeGeneratorUtil.expectedValue(returnType)
        val notFoundParamList = parameters.joinToString(", ") { (n, t) ->
            when (t) {
                "Long", "Int" -> "-1"
                "String" -> "\"\""
                else -> CodeGeneratorUtil.dummyValue(t)
            }
        }
        return buildString {
            appendLine("// 정상 시나리오: 정상 파라미터 전달")
            appendLine("given(\"$name 호출 시\"){")
            if (paramDecl.isNotBlank()) appendLine("        $paramDecl")
            if (expected.isNotBlank()) appendLine("        $expected")
            if (expected.isNotBlank()) appendLine("        every { repository.$name($paramList) } returns expected // TODO: mock 반환값/로직 실제에 맞게 수정")
            appendLine()
            appendLine("        `when`(\"정상적인 파라미터가 주어지면\") {")
            val result = if (returnType == "Unit") "service.$name($paramList)" else "    val result = service.$name($paramList)"
            appendLine("            then(\"정상 결과가 반환되어야 한다\") {")
            appendLine("                $result")
            if (expected.isNotBlank()) appendLine("                result shouldBe expected // TODO: 실제 값에 맞게 검증")
            appendLine("            }")
            appendLine("        }")
            appendLine()
            appendLine("        `when`(\"존재하지 않는 ID 등 예외 상황이 주어지면\") {")
            appendLine("            then(\"예외가 발생해야 한다\") {")
            appendLine("                every { repository.$name($notFoundParamList) } returns null // 또는 throws Exception // TODO: 실제 로직에 맞게 mock 세팅")
            appendLine("                shouldThrow<Exception /* TODO: 실제 예외 타입 */> {")
            appendLine("                    service.$name($notFoundParamList)")
            appendLine("                }")
            appendLine("            }")
            appendLine("        }")
            appendLine("    }")
        }
    }

    private fun buildRepositorySpec(packageName: String?, className: String): String = buildString {
        val state = KotestGeneratorSettings.getInstance().state
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine(state.getMockLibImport())
        appendLine()
        appendLine("class ${className}Test : BehaviorSpec({")
        appendLine("""${state.getIndentString()}given("Repository $className 호출 시") {""")
        appendLine("""${state.getIndentString()}    When("데이터 접근이 수행되면") {""")
        appendLine("""            Then("DB와 정상적으로 상호작용해야 한다") { }""")
        appendLine("        }")
        appendLine("${state.getIndentString()}}")
        appendLine("})")
    }

    private fun buildDefaultSpec(packageName: String?, className: String, functionInfos: List<FunctionInfo>): String = buildString {
        if (!packageName.isNullOrBlank()) appendLine("package $packageName")
        appendLine()
        appendLine("import io.kotest.core.spec.style.BehaviorSpec")
        appendLine("import io.mockk.every")
        appendLine()
        appendLine("class ${className}Test : BehaviorSpec({")
        for (fn in functionInfos) {
            appendLine("""    given("${fn.name}") {""")
            appendLine("""        then("should do something") { }""")
            appendLine("    }")
        }
        appendLine("})")
    }
}