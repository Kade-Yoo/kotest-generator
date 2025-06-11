package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_ASSERT_THROW
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTOWIRED
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTO_CONFIGURE_MOCK_MVC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_BEHAVIOR_SPEC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_DELETE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_GET
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_HTTP_STATUS
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_JACKSON_OBJECT_MAPPTER
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MEDIA_TYPE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCKITO
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCKK
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCKK_EVERY
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MOCK_MVC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MVC_BUILDER
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_POST
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_PUT
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_READ_VALUE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_REQUEST_BUILDERS_GET
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_RESULT_MATCHERS_STATUS
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_SHOULD_BE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_SHOULD_BE_EQUAL
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_WEB_MVC_TEST
import com.github.kadeyoo.kotestgenerator.dto.ClassInfo
import com.github.kadeyoo.kotestgenerator.dto.FunctionInfo.DependencyCall
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo

object CodeGeneratorUtil {
    fun dummyValue(type: String): String =
        when {
            type == "String" -> {
                "\"expected\""
            }
            type == "Int" -> {
                "1"
            }
            type == "Long" -> {
                "1L"
            }
            type == "Boolean" -> {
                "true"
            }
            type == "Double" || type == "Float" -> {
                "1.0"
            }
            type.startsWith("ResponseEntity") -> {
                "ResponseEntity.ok().build()"
            }
            type.startsWith("List") -> {
                "emptyList()"
            }
            type.startsWith("ApiResponse") -> {
                "ApiResponse(data = null)"
            }
            else -> {
                "mockk<$type>()"
            }
        }

    fun badDummyValue(type: String): String =
        when {
            type == "String" -> {
                "\"badValue\""
            }
            type == "Int" -> {
                "-1"
            }
            type == "Long" -> {
                "-1L"
            }
            type == "Boolean" -> {
                "false"
            }
            type == "Double" || type == "Float" -> {
                "-1.0"
            }
            type.startsWith("ResponseEntity") -> {
                "ResponseEntity.badRequest().build()"
            }
            type.startsWith("List") -> {
                "listOf()"
            }
            type.startsWith("ApiResponse") -> {
                "ApiResponse(data = null)"
            }
            else -> {
                "mockk<$type>()"
            }
        }

    private fun <T> List<T>.toJoinedString(delimiter: String, transform: (T) -> String): String =
        this.joinToString(delimiter, transform = transform)

    fun buildParamDecl(parameters: List<ParameterInfo>): String =
        parameters.toJoinedString("\n") { "        val ${it.name} = ${dummyValue(it.type)}" }

    fun buildBadParamDecl(parameters: List<ParameterInfo>): String =
        parameters.toJoinedString("\n") { "            val ${it.name} = ${badDummyValue(it.type)}" }

    fun expectedValue(returnType: String): String =
        if (returnType == "Unit") "" else "val expected = ${dummyValue(returnType)} // TODO: 실제 값으로 변경"

    fun apiImportStatements() = """
        $IMPORT_WEB_MVC_TEST
        $IMPORT_AUTO_CONFIGURE_MOCK_MVC
        $IMPORT_AUTOWIRED
        $IMPORT_MOCK_MVC
        $IMPORT_REQUEST_BUILDERS_GET
        $IMPORT_RESULT_MATCHERS_STATUS
        $IMPORT_BEHAVIOR_SPEC
        $IMPORT_MEDIA_TYPE
        $IMPORT_HTTP_STATUS
        $IMPORT_JACKSON_OBJECT_MAPPTER
        $IMPORT_READ_VALUE
        $IMPORT_SHOULD_BE
        $IMPORT_SHOULD_BE_EQUAL
        $IMPORT_MVC_BUILDER
        $IMPORT_GET
        $IMPORT_POST
        $IMPORT_PUT
        $IMPORT_DELETE
        $IMPORT_MOCKK_EVERY
        $IMPORT_MOCKK
        $IMPORT_MOCKITO
        $IMPORT_ASSERT_THROW
    """.trimIndent()

    fun generateMockStubsFromClassInfo(classParameters: List<ClassInfo.ParameterInfo>, dependencyCallInfos: List<DependencyCall>): List<String> {
        val dependencyCallMap = dependencyCallInfos.associateBy { it.name }
        return classParameters.flatMap { classParameter ->
            dependencyCallMap.entries
                .filter { (key) -> key.startsWith(prefix = "${classParameter.name}.") }
                .map { (key, value) ->
                    val paramExpr = value.parameters.joinToString(", ") { "any()" }
                    val callExpr = "${key}($paramExpr)"
                    "every { $callExpr } returns mockk()"
                }
        }
    }
}