package com.github.kadeyoo.kotestgenerator.util

import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.DELETE_MAPPING_ANNOTATION
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.DELETE_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GET_MAPPING_ANNOTATION
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.GET_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTOWIRED
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_AUTO_CONFIGURE_MOCK_MVC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_BEHAVIOR_SPEC
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_DELETE
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_GET
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_HTTP_STATUS
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.IMPORT_JACKSON_OBJECT_MAPPTER
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
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.POST_MAPPING_ANNOTATION
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.POST_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.PUT_MAPPING_ANNOTATION
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.PUT_METHOD_NAME
import com.github.kadeyoo.kotestgenerator.common.constants.SpecTemplateConstants.REQUEST_MAPPING_ANNOTATION
import com.github.kadeyoo.kotestgenerator.dto.MappingInfo
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object CodeGeneratorUtil {
    fun dummyValue(type: String): String =
        when (type) {
            "Long" -> "1L"
            "Int" -> "1"
            "String" -> "\"example\""
            "Boolean" -> "true"
            "Double" -> "1.0"
            else -> "$type()"
        }

    fun extractMappingInfo(method: PsiElement): MappingInfo? {
        val annotations = method.firstChild.children.filter { it.javaClass.name.contains("Annotation") }
        for (annotation in annotations) {
            val annotationText = annotation.text
            val isGet = annotationText.contains(GET_MAPPING_ANNOTATION)
            val isPost = annotationText.contains(POST_MAPPING_ANNOTATION)
            val isPut = annotationText.contains(PUT_MAPPING_ANNOTATION)
            val isDelete = annotationText.contains(DELETE_MAPPING_ANNOTATION)
            val isRequest = annotationText.contains(REQUEST_MAPPING_ANNOTATION)
            if (isGet || isPost || isRequest || isPut || isDelete) {
                val split = annotationText.split("\"")
                val url = split[1]
                val methodType = when {
                    isGet -> GET_METHOD_NAME
                    isPost -> POST_METHOD_NAME
                    isPut -> PUT_METHOD_NAME
                    isDelete -> DELETE_METHOD_NAME
                    else -> GET_METHOD_NAME
                }
                return MappingInfo(url, methodType)
            }
        }
        return null
    }

    fun <T> List<T>.toJoinedString(delimiter: String, transform: (T) -> String): String =
        this.joinToString(delimiter, transform = transform)

    fun buildParamDecl(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("\n") { (n, t) -> "val $n = ${dummyValue(t)}" }

    fun buildUrlParams(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("&") { (n, _) -> "$n=\${$n}" }

    fun buildBadParamDecl(parameters: List<Pair<String, String>>): String =
        parameters.toJoinedString("\n") { (n, t) ->
            val badVal = when (t) {
                "Long", "Int" -> "-1"
                "String" -> "\"\""
                else -> dummyValue(t)
            }
            "val $n = $badVal"
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
}
