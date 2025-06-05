package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GET_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTOWIRED
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTO_CONFIGURE_MOCK_MVC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_BEHAVIOR_SPEC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_DELETE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_GET
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_HTTP_STATUS
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_JACKSON_OBJECT_MAPPTER
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_MEDIA_TYPE
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
import com.github.kadeyoo.kotestgenerator.dto.MappingInfo
import com.github.kadeyoo.kotestgenerator.dto.ParameterInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object CodeGeneratorUtil {
    fun dummyValue(type: String): String =
        when {
            type.startsWith("ResponseEntity") -> {
                "ResponseEntity.ok().build()" // or use a dummy body if needed
            }
            type.startsWith("List") -> {
                "emptyList()"
            }
            type == "String" -> {
                "\"expected\""
            }
            type == "Int" || type == "Long" -> {
                "0"
            }
            type == "Boolean" -> {
                "true"
            }
            type.startsWith("ApiResponse") -> {
                "ApiResponse(data = null)"
            }
            else -> {
                // fallback
                "mockk<$type>()"
            }
        }

    fun extractMappingInfo(method: PsiElement): MappingInfo {
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

    fun <T> List<T>.toJoinedString(delimiter: String, transform: (T) -> String): String =
        this.joinToString(delimiter, transform = transform)

    fun buildParamDecl(parameters: List<ParameterInfo>): String =
        parameters.toJoinedString("\n") { (n, t, _) -> "        val $n = ${dummyValue(t)}" }

    fun buildBadParamDecl(parameters: List<ParameterInfo>): String =
        parameters.toJoinedString("\n") { (n, t, _) ->
            val badVal = when (t) {
                "Long", "Int" -> "-1"
                "String" -> "\"\""
                else -> dummyValue(t)
            }
            "           val $n = $badVal"
        }

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
    """.trimIndent()

    fun extractImportNamesFromFile(psiFile: PsiFile): List<String> {
        val importListObj = extractImportsFromFile(psiFile) ?: return emptyList()
        val getImportsMethod = importListObj.javaClass.methods.firstOrNull { it.name == "getImports" }
        val importDirectives = getImportsMethod?.invoke(importListObj) as? List<*>
        return importDirectives?.mapNotNull { importDirective ->
            val getFqNameMethod = importDirective?.javaClass?.methods?.firstOrNull { it.name == "getImportedFqName" }
            val fqNameObj = getFqNameMethod?.invoke(importDirective)
            fqNameObj?.toString()
        } ?: emptyList()
    }

    private fun extractImportsFromFile(psiFile: PsiFile): Any? {
        val getImportListMethod = psiFile.javaClass.methods.firstOrNull { it.name == "getImportList" }
        return getImportListMethod?.invoke(psiFile)
    }

    fun extractDataClassProperties(psiElement: PsiElement): List<Pair<String, String?>> {
        val parameters = psiElement.javaClass.methods.firstOrNull { it.name == "getPrimaryConstructorParameters" }
            ?.invoke(psiElement) as? List<*>
        val properties = parameters?.map { it ->
            (it?.javaClass?.methods?.firstOrNull { it.name == "getName" }?.invoke(it)?.toString() ?: "unknown") to
                    it?.javaClass?.methods?.firstOrNull { it.name == "getTypeReference" }?.invoke(it)?.javaClass
                        ?.methods?.firstOrNull { it.name == "getText" }?.invoke(it)?.toString()
        } ?: emptyList()

        val body = psiElement.javaClass.methods.firstOrNull { it.name == "getBody" }
            ?.invoke(psiElement) as? PsiElement
        val bodyProperties = body?.javaClass?.methods?.firstOrNull { it.name == "getProperties" }?.invoke(body) as? List<*>
        val pairs = bodyProperties?.map { prop ->
            val name = prop?.javaClass?.methods?.firstOrNull { it.name == "getName" }?.invoke(prop)?.toString() ?: "unknown"
            val typeRef = prop?.javaClass?.methods?.firstOrNull { it.name == "getTypeReference" }?.invoke(prop)
            val typeText = typeRef?.javaClass?.methods?.firstOrNull { it.name == "getText" }?.invoke(typeRef)?.toString()
            name to typeText
        } ?: emptyList()

        return properties + pairs
    }
}
